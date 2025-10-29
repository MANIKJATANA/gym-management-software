package com.jatana.gymmembershipmanagemt.service;

import com.jatana.gymmembershipmanagemt.model.Member;
import com.jatana.gymmembershipmanagemt.model.Membership;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberRequest;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberUpdateRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberDocumentResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberSummaryResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipResponse;
import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import com.jatana.gymmembershipmanagemt.repo.MemberRepo;
import com.jatana.gymmembershipmanagemt.repo.MembershipRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MembershipRepo membershipRepo;

    public MemberResponse createMember(MemberRequest memberRequest) {
       Member member = getMemberFromMemberRequest(memberRequest);
       Member response = memberRepo.save(member);
        return getMemberResponseFromMember(response).withMembershipHistory(List.of()).withDocuments(List.of());
    }

    private MemberResponse getMemberResponseFromMember(Member member) {

        return MemberResponse
                .builder()
                .memberId(member.getMemberId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .fullName(member.getFullName())
                .dateOfBirth(member.getDateOfBirth())
                .age(calculateAge(member.getDateOfBirth()))
                .gender(Gender.valueOf(member.getGender()))
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .address(member.getAddress())
                .memberStatus(MemberStatus.valueOf(member.getMemberStatus()))
                .photoUrl(member.getPhotoUrl())
                .build();
    }

    private int calculateAge(LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(dateOfBirth, currentDate).getYears();
    }

    private Member getMemberFromMemberRequest(MemberRequest memberRequest) {
        Member member = new Member();
        member.setMemberId(memberRequest.memberId());
        member.setFirstName(memberRequest.firstName());
        member.setLastName(memberRequest.lastName());
        member.setFullName(memberRequest.firstName() + " " + memberRequest.lastName());

        member.setDateOfBirth(memberRequest.dateOfBirth());
        member.setGender(memberRequest.gender().toString());

        member.setPhoneNumber(memberRequest.phoneNumber());
        member.setEmail(memberRequest.email());

        member.setAddress(memberRequest.address());
        member.setMemberStatus(MemberStatus.ACTIVE.toString());
        member.setPhotoUrl("");

        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());

        return member;
    }

    public List<MemberSummaryResponse> getMembers(MemberStatus filter, String searchKey) {

        List<Member> members = memberRepo.findMembersUsingFilterAndSearchKeyword(filter.toString(), searchKey);

        List<MemberSummaryResponse> memberSummaryResponses = new ArrayList<>();
        for(Member member: members){
            MemberSummaryResponse memberSummaryResponse = getMemberSummaryResponseFromMember(member);
            memberSummaryResponses.add(memberSummaryResponse);
        }
        return memberSummaryResponses;
    }

    private MemberSummaryResponse getMemberSummaryResponseFromMember(Member member) {
        return new MemberSummaryResponse(
                member.getMemberId(),
                member.getFullName(),
                calculateAge(member.getDateOfBirth()),
                Gender.valueOf(member.getGender()),
                member.getPhoneNumber(),
                member.getEmail(),
                MemberStatus.valueOf(member.getMemberStatus()),
                member.getPhotoUrl(),
                getEndDate(member.getMemberId())
        );
    }

    private LocalDate getEndDate(String memberId) {

        Optional<Membership> latestMembershipOptional = membershipRepo.findTopByMemberIdOrderByEndDateDesc(memberId);
        return latestMembershipOptional
                .map(Membership::getEndDate)
                .orElse(LocalDate.of(2000, 1, 1));


    }

    public MemberResponse getMember(String memberId) {
        Optional<Member> memberOptional = memberRepo.findById(memberId);
        if (memberOptional.isPresent()) {
            return getMemberResponseWithMembershipAndDocDetailFromMember(memberOptional.get());
        }
        // throw error
        log.error("Member with id {} not found", memberId);
        throw new IllegalArgumentException("Member with id " + memberId + " not found");
    }

    private List<MemberDocumentResponse> getMemberShipDocuments(String memberId) {
        return List.of();
    }

    private List<MembershipResponse> getMembershipResponse(String memberId) {
        return membershipService.getAllMemberships(memberId);
    }

    public MemberResponse updateMember(MemberUpdateRequest memberUpdateRequest, String memberId) {

        Optional<Member> memberOptional = memberRepo.findById(memberId);
        if(memberOptional.isEmpty()){
            throw new IllegalArgumentException("Member with id " + memberId + " not found");
        }

        Member member = memberOptional.get();

        member.setFirstName(memberUpdateRequest.firstName());
        member.setLastName(memberUpdateRequest.lastName());
        member.setFullName(memberUpdateRequest.firstName() + " " + memberUpdateRequest.lastName());
        member.setDateOfBirth(memberUpdateRequest.dateOfBirth());
        member.setGender(memberUpdateRequest.gender().toString());
        member.setPhoneNumber(memberUpdateRequest.phoneNumber());
        member.setEmail(memberUpdateRequest.email());
        member.setAddress(memberUpdateRequest.address());


        Member response = memberRepo.save(member);


        return getMemberResponseWithMembershipAndDocDetailFromMember(response);


    }

    private MemberResponse getMemberResponseWithMembershipAndDocDetailFromMember(Member member) {
        MemberResponse memberResponse = getMemberResponseFromMember(member);
        List<MembershipResponse> membershipHistory = getMembershipResponse(member.getMemberId());
        List<MemberDocumentResponse> documents = getMemberShipDocuments(member.getMemberId());
        return memberResponse.withMembershipHistory(membershipHistory).withDocuments(documents);
    }

    public MemberResponse updateMemberStatus(String memberId, MemberStatus memberStatus) {
        Optional<Member> memberOptional = memberRepo.findById(memberId);
        if(memberOptional.isEmpty()){
            throw new IllegalArgumentException("Member with id " + memberId + " not found");
        }

        Member member = memberOptional.get();

        member.setMemberStatus(memberStatus.toString());

        Member response = memberRepo.save(member);


        return getMemberResponseWithMembershipAndDocDetailFromMember(response);
    }

    public List<MemberSummaryResponse> getMembersByDate(LocalDate endDate) {
        List<Member> members = memberRepo.findMembersUsingFilterAndSearchKeyword(
                MemberStatus.ACTIVE.toString(), ""
        );

        return members.stream()
                .filter(member -> !getEndDate(member.getMemberId()).isAfter(endDate))
                .map(this::getMemberSummaryResponseFromMember)
                .toList();
    }
}
