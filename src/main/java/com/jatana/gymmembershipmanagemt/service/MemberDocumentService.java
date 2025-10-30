package com.jatana.gymmembershipmanagemt.service;

import com.jatana.gymmembershipmanagemt.model.Member;
import com.jatana.gymmembershipmanagemt.model.MemberDocument;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberDocumentUploadRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberDocumentResponse;
import com.jatana.gymmembershipmanagemt.model.enums.DocType;
import com.jatana.gymmembershipmanagemt.repo.MemberDocumentRepo;
import com.jatana.gymmembershipmanagemt.repo.MemberRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberDocumentService {
    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private MemberDocumentRepo memberDocumentRepo;

    public MemberDocumentResponse uploadDocument(String memberId, MemberDocumentUploadRequest memberDocumentUploadRequest) {
        DocType docType = memberDocumentUploadRequest.docType();
        Optional<Member> memberOptional = memberRepo.findById(memberId);
        if(memberOptional.isEmpty()){
            log.error("Could not find member with id {}", memberId);
            throw  new IllegalArgumentException("Could not find member with id " + memberId);
        }

        Member member = memberOptional.get();
        String docUrl= docType.name();
        MemberDocumentResponse memberDocumentResponse = getMemberDocumentResponse(memberId, docType, docUrl);

        if(docType == DocType.PHOTO){
            member.setPhotoUrl(docUrl);
            memberRepo.save(member);
        }

        return memberDocumentResponse;
    }

    private MemberDocumentResponse getMemberDocumentResponse(String memberId, DocType docType, String docUrl) {
        MemberDocument memberDocument = getMemberDocument(memberId, docType, docUrl);
        MemberDocument response = memberDocumentRepo.save(memberDocument);

        return new MemberDocumentResponse(
                response.getDocumentId(),
                response.getDocumentType(),
                response.getUrl()
        );
    }

    private static MemberDocument getMemberDocument(String memberId, DocType docType, String docUrl) {
        MemberDocument memberDocument = new MemberDocument();
        memberDocument.setDocumentId(memberId +"-"+ docType.name());
        memberDocument.setMemberId(memberId);
        memberDocument.setDocumentType(docType);
        memberDocument.setUrl(docUrl);
        memberDocument.setUploadedAt(LocalDateTime.now());
        return memberDocument;
    }

    public List<MemberDocumentResponse> getMemberShipDocuments(String memberId) {
        return memberDocumentRepo.findMemberDocumentByMemberId(memberId)
                .stream()
                .map(doc -> new MemberDocumentResponse(
                        doc.getDocumentId(),
                        doc.getDocumentType(),
                        doc.getUrl()
                ))
                .toList();
    }

}
