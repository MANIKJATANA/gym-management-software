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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MembershipRepo membershipRepo;

    @Autowired
    private MemberDocumentService memberDocumentService;

    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) {
        log.info("Creating new member - ID: {}, name: {} {}, email: {}", 
                memberRequest.memberId(), 
                memberRequest.firstName(), 
                memberRequest.lastName(),
                memberRequest.email());
        
        try {
            // Check if member already exists
            if (memberRepo.existsById(memberRequest.memberId())) {
                log.error("Cannot create member - Member already exists with ID: {}", 
                        memberRequest.memberId());
                throw new IllegalArgumentException("Member already exists with ID: " + memberRequest.memberId());
            }
            
            log.debug("Mapping member request to entity - ID: {}", memberRequest.memberId());
            Member member = getMemberFromMemberRequest(memberRequest);
            
            Member response = memberRepo.save(member);
            log.info("Successfully created member - ID: {}, name: {}, status: {}", 
                    response.getMemberId(), 
                    response.getFullName(),
                    response.getMemberStatus());
            
            return getMemberResponseFromMember(response)
                    .withMembershipHistory(List.of())
                    .withDocuments(List.of());
                    
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create member - ID: {}. Error: {}", 
                    memberRequest.memberId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create member with ID: " + memberRequest.memberId(), e);
        }
    }

    private MemberResponse getMemberResponseFromMember(Member member) {
        log.trace("Converting member entity to response - ID: {}", member.getMemberId());
        
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
        if (dateOfBirth == null) {
            log.warn("Attempted to calculate age with null date of birth");
            return 0;
        }
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
        log.info("Fetching members with filter: {}, search key: '{}'", filter, searchKey);
        
        try {
            List<Member> members = memberRepo.findMembersUsingFilterAndSearchKeyword(
                    filter.toString(), searchKey);
            
            log.debug("Found {} member(s) matching filter: {}, search key: '{}'", 
                    members.size(), filter, searchKey);
            
            List<MemberSummaryResponse> memberSummaryResponses = members.stream()
                    .map(this::getMemberSummaryResponseFromMember)
                    .collect(Collectors.toList());
            
            log.info("Successfully retrieved {} member(s) with filter: {}", 
                    memberSummaryResponses.size(), filter);
            
            return memberSummaryResponses;
            
        } catch (Exception e) {
            log.error("Failed to fetch members with filter: {}, search key: '{}'. Error: {}", 
                    filter, searchKey, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch members", e);
        }
    }

    private MemberSummaryResponse getMemberSummaryResponseFromMember(Member member) {
        log.trace("Creating summary response for member ID: {}", member.getMemberId());
        
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
        log.trace("Fetching latest membership end date for member ID: {}", memberId);
        
        try {
            Optional<Membership> latestMembershipOptional = 
                    membershipRepo.findTopByMemberIdOrderByEndDateDesc(memberId);
            
            LocalDate endDate = latestMembershipOptional
                    .map(Membership::getEndDate)
                    .orElse(LocalDate.of(2000, 1, 1));
            
            if (latestMembershipOptional.isEmpty()) {
                log.debug("No membership found for member ID: {}, using default date", memberId);
            }
            
            return endDate;
            
        } catch (Exception e) {
            log.error("Failed to fetch end date for member ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            return LocalDate.of(2000, 1, 1);
        }
    }

    public MemberResponse getMember(String memberId) {
        log.info("Fetching member details for ID: {}", memberId);
        
        try {
            Optional<Member> memberOptional = memberRepo.findById(memberId);
            
            if (memberOptional.isEmpty()) {
                log.error("Member not found with ID: {}", memberId);
                throw new IllegalArgumentException("Member not found with ID: " + memberId);
            }
            
            Member member = memberOptional.get();
            log.debug("Found member - ID: {}, name: {}, status: {}", 
                    member.getMemberId(), 
                    member.getFullName(), 
                    member.getMemberStatus());
            
            MemberResponse response = getMemberResponseWithMembershipAndDocDetailFromMember(member);
            log.info("Successfully retrieved complete member details for ID: {}", memberId);
            
            return response;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch member with ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch member with ID: " + memberId, e);
        }
    }

    private List<MemberDocumentResponse> getMemberShipDocuments(String memberId) {
        log.debug("Fetching documents for member ID: {}", memberId);
        
        try {
            List<MemberDocumentResponse> documents = 
                    memberDocumentService.getMemberShipDocuments(memberId);
            log.debug("Retrieved {} document(s) for member ID: {}", 
                    documents.size(), memberId);
            return documents;
        } catch (Exception e) {
            log.error("Failed to fetch documents for member ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            throw e;
        }
    }

    private List<MembershipResponse> getMembershipResponse(String memberId) {
        log.debug("Fetching membership history for member ID: {}", memberId);
        
        try {
            List<MembershipResponse> memberships = 
                    membershipService.getAllMemberships(memberId);
            log.debug("Retrieved {} membership(s) for member ID: {}", 
                    memberships.size(), memberId);
            return memberships;
        } catch (Exception e) {
            log.error("Failed to fetch memberships for member ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public MemberResponse updateMember(MemberUpdateRequest memberUpdateRequest, String memberId) {
        log.info("Updating member - ID: {}, new name: {} {}", 
                memberId, 
                memberUpdateRequest.firstName(), 
                memberUpdateRequest.lastName());
        
        try {
            Optional<Member> memberOptional = memberRepo.findById(memberId);
            
            if (memberOptional.isEmpty()) {
                log.error("Cannot update - Member not found with ID: {}", memberId);
                throw new IllegalArgumentException("Member not found with ID: " + memberId);
            }

            Member member = memberOptional.get();
            
            log.debug("Updating member details - ID: {}, old name: {}, new name: {} {}", 
                    memberId, 
                    member.getFullName(),
                    memberUpdateRequest.firstName(), 
                    memberUpdateRequest.lastName());

            member.setFirstName(memberUpdateRequest.firstName());
            member.setLastName(memberUpdateRequest.lastName());
            member.setFullName(memberUpdateRequest.firstName() + " " + memberUpdateRequest.lastName());
            member.setDateOfBirth(memberUpdateRequest.dateOfBirth());
            member.setGender(memberUpdateRequest.gender().toString());
            member.setPhoneNumber(memberUpdateRequest.phoneNumber());
            member.setEmail(memberUpdateRequest.email());
            member.setAddress(memberUpdateRequest.address());
            member.setUpdatedAt(LocalDateTime.now());

            Member response = memberRepo.save(member);
            
            log.info("Successfully updated member - ID: {}, new name: {}", 
                    memberId, response.getFullName());

            return getMemberResponseWithMembershipAndDocDetailFromMember(response);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update member with ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            throw new RuntimeException("Failed to update member with ID: " + memberId, e);
        }
    }

    private MemberResponse getMemberResponseWithMembershipAndDocDetailFromMember(Member member) {
        log.debug("Building complete member response with memberships and documents - ID: {}", 
                member.getMemberId());
        
        MemberResponse memberResponse = getMemberResponseFromMember(member);
        List<MembershipResponse> membershipHistory = getMembershipResponse(member.getMemberId());
        List<MemberDocumentResponse> documents = getMemberShipDocuments(member.getMemberId());
        
        return memberResponse
                .withMembershipHistory(membershipHistory)
                .withDocuments(documents);
    }

    @Transactional
    public MemberResponse updateMemberStatus(String memberId, MemberStatus memberStatus) {
        log.info("Updating member status - ID: {}, new status: {}", memberId, memberStatus);
        
        try {
            Optional<Member> memberOptional = memberRepo.findById(memberId);
            
            if (memberOptional.isEmpty()) {
                log.error("Cannot update status - Member not found with ID: {}", memberId);
                throw new IllegalArgumentException("Member not found with ID: " + memberId);
            }

            Member member = memberOptional.get();
            MemberStatus oldStatus = MemberStatus.valueOf(member.getMemberStatus());
            
            log.debug("Changing member status - ID: {}, old status: {}, new status: {}", 
                    memberId, oldStatus, memberStatus);

            member.setMemberStatus(memberStatus.toString());
            member.setUpdatedAt(LocalDateTime.now());

            Member response = memberRepo.save(member);
            
            log.info("Successfully updated member status - ID: {}, status changed from {} to {}", 
                    memberId, oldStatus, memberStatus);

            return getMemberResponseWithMembershipAndDocDetailFromMember(response);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update member status - ID: {}, target status: {}. Error: {}", 
                    memberId, memberStatus, e.getMessage(), e);
            throw new RuntimeException("Failed to update member status for ID: " + memberId, e);
        }
    }

    public List<MemberSummaryResponse> getMembersByDate(LocalDate endDate) {
        log.info("Fetching active members with membership ending on or before: {}", endDate);
        
        try {
            List<Member> members = memberRepo.findMembersUsingFilterAndSearchKeyword(
                    MemberStatus.ACTIVE.toString(), ""
            );
            
            log.debug("Found {} active member(s), filtering by end date: {}", 
                    members.size(), endDate);

            List<MemberSummaryResponse> filteredMembers = members.stream()
                    .filter(member -> {
                        LocalDate memberEndDate = getEndDate(member.getMemberId());
                        boolean matches = !memberEndDate.isAfter(endDate);
                        if (matches) {
                            log.trace("Member ID: {} included - end date: {}", 
                                    member.getMemberId(), memberEndDate);
                        }
                        return matches;
                    })
                    .map(this::getMemberSummaryResponseFromMember)
                    .toList();
            
            log.info("Successfully retrieved {} member(s) with membership ending on or before: {}", 
                    filteredMembers.size(), endDate);
            
            return filteredMembers;
            
        } catch (Exception e) {
            log.error("Failed to fetch members by end date: {}. Error: {}", 
                    endDate, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch members by date: " + endDate, e);
        }
    }
}