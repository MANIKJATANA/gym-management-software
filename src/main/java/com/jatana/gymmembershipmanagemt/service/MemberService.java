package com.jatana.gymmembershipmanagemt.service;

import com.jatana.gymmembershipmanagemt.model.Member;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberRequest;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberUpdateRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberDocumentResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberSummaryResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MembershipResponse;
import com.jatana.gymmembershipmanagemt.model.enums.Gender;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    public MemberResponse createMember(MemberRequest memberRequest) {
       Member member = getMemberFromMemberRequest(memberRequest);
       // save member to repo
        return getMemberResponseFromMember(member).withMembershipHistory(List.of()).withDocuments(List.of());
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

        List<Member> members = new ArrayList<>(); // TODO: Update this
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
        return LocalDate.now().plusMonths(1); //TODO : Get this from repo
    }

    public MemberResponse getMember(String memberId) {
        Member member = null;
        return getMemberResponseWithMembershipAndDocDetailFromMember(member);
    }

    private List<MemberDocumentResponse> getMemberShipDocuments(String memberId) {
        return null;
    }

    private List<MembershipResponse> getMembershipResponse(String memberId) {
        return null;
    }

    public MemberResponse updateMember(MemberUpdateRequest memberUpdateRequest, String memberId) {
        Member member = new Member(); //Todo: Get from repo

        member.setFirstName(memberUpdateRequest.firstName());
        member.setLastName(memberUpdateRequest.lastName());
        member.setFullName(memberUpdateRequest.firstName() + " " + memberUpdateRequest.lastName());
        member.setDateOfBirth(memberUpdateRequest.dateOfBirth());
        member.setGender(memberUpdateRequest.gender().toString());
        member.setPhoneNumber(memberUpdateRequest.phoneNumber());
        member.setEmail(memberUpdateRequest.email());
        member.setAddress(memberUpdateRequest.address());

        // TODO: save member


        return getMemberResponseWithMembershipAndDocDetailFromMember(member);


    }

    private MemberResponse getMemberResponseWithMembershipAndDocDetailFromMember(Member member) {
        MemberResponse memberResponse = getMemberResponseFromMember(member);
        List<MembershipResponse> membershipHistory = getMembershipResponse(member.getMemberId());
        List<MemberDocumentResponse> documents = getMemberShipDocuments(member.getMemberId());
        return memberResponse.withMembershipHistory(membershipHistory).withDocuments(documents);
    }

    public MemberResponse updateMemberStatus(String memberId, MemberStatus memberStatus) {
        Member member = new Member();
        // Todo: Get it from repo

        member.setMemberStatus(memberStatus.toString());

        // Todo: save member

        return getMemberResponseWithMembershipAndDocDetailFromMember(member);
    }
}
