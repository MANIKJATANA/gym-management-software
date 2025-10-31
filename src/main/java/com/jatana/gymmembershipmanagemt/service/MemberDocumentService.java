package com.jatana.gymmembershipmanagemt.service;

import com.cloudinary.Cloudinary;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MemberDocumentService {
    
    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private MemberDocumentRepo memberDocumentRepo;

    @Autowired
    private Cloudinary cloudinary;

    private String getImageUrl(String memberId, MemberDocumentUploadRequest memberDocumentUploadRequest) {
        DocType docType = memberDocumentUploadRequest.docType();
        String filename = memberId + "-" + docType.name();
        
        log.debug("Uploading document to Cloudinary - member ID: {}, doc type: {}, filename: {}", 
                memberId, docType, filename);
        
        try {
            long fileSize = memberDocumentUploadRequest.file().getSize();
            log.debug("File size: {} bytes for member ID: {}, doc type: {}", 
                    fileSize, memberId, docType);
            
            Map<String, Object> params = Map.of(
                    "public_id", filename,
                    "overwrite", true
            );

            Map<String, Object> uploadedResult = cloudinary.uploader()
                    .upload(memberDocumentUploadRequest.file().getBytes(), params);

            String secureUrl = uploadedResult.get("secure_url").toString();
            log.info("Successfully uploaded document to Cloudinary - member ID: {}, doc type: {}, URL: {}", 
                    memberId, docType, secureUrl);
            
            return secureUrl;

        } catch (IOException e) {
            log.error("Failed to upload document to Cloudinary - member ID: {}, doc type: {}. Error: {}", 
                    memberId, docType, e.getMessage(), e);
            throw new RuntimeException("Failed to upload document for member: " + memberId + ", type: " + docType, e);
        } catch (Exception e) {
            log.error("Unexpected error during document upload - member ID: {}, doc type: {}. Error: {}", 
                    memberId, docType, e.getMessage(), e);
            throw new RuntimeException("Unexpected error uploading document for member: " + memberId, e);
        }
    }

    @Transactional
    public MemberDocumentResponse uploadDocument(String memberId, MemberDocumentUploadRequest memberDocumentUploadRequest) {
        DocType docType = memberDocumentUploadRequest.docType();
        String fileName = memberDocumentUploadRequest.file().getOriginalFilename();
        
        log.info("Starting document upload - member ID: {}, doc type: {}, file name: {}", 
                memberId, docType, fileName);
        
        try {
            // Validate member exists
            Optional<Member> memberOptional = memberRepo.findById(memberId);
            if (memberOptional.isEmpty()) {
                log.error("Cannot upload document - Member not found with ID: {}", memberId);
                throw new IllegalArgumentException("Member not found with ID: " + memberId);
            }

            Member member = memberOptional.get();
            log.debug("Member found - ID: {}, uploading {} document", memberId, docType);
            
            // Upload to Cloudinary
            String docUrl = getImageUrl(memberId, memberDocumentUploadRequest);
            
            // Save document metadata
            log.debug("Saving document metadata to database - member ID: {}, doc type: {}", 
                    memberId, docType);
            MemberDocumentResponse memberDocumentResponse = getMemberDocumentResponse(memberId, docType, docUrl);

            // Update member photo URL if document is a photo
            if (docType == DocType.PHOTO) {
                log.debug("Updating member photo URL - member ID: {}", memberId);
                member.setPhotoUrl(docUrl);
                memberRepo.save(member);
                log.info("Successfully updated member photo URL - member ID: {}", memberId);
            }

            log.info("Successfully uploaded and saved document - member ID: {}, doc type: {}, document ID: {}", 
                    memberId, docType, memberDocumentResponse.documentId());
            
            return memberDocumentResponse;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload document - member ID: {}, doc type: {}. Error: {}", 
                    memberId, docType, e.getMessage(), e);
            throw new RuntimeException("Failed to upload document for member: " + memberId, e);
        }
    }

    private MemberDocumentResponse getMemberDocumentResponse(String memberId, DocType docType, String docUrl) {
        log.debug("Creating member document entity - member ID: {}, doc type: {}", memberId, docType);
        
        try {
            MemberDocument memberDocument = getMemberDocument(memberId, docType, docUrl);
            MemberDocument response = memberDocumentRepo.save(memberDocument);
            
            log.debug("Successfully saved member document - document ID: {}", response.getDocumentId());

            return new MemberDocumentResponse(
                    response.getDocumentId(),
                    response.getDocumentType(),
                    response.getUrl()
            );
        } catch (Exception e) {
            log.error("Failed to save document metadata - member ID: {}, doc type: {}. Error: {}", 
                    memberId, docType, e.getMessage(), e);
            throw new RuntimeException("Failed to save document metadata", e);
        }
    }

    private static MemberDocument getMemberDocument(String memberId, DocType docType, String docUrl) {
        MemberDocument memberDocument = new MemberDocument();
        memberDocument.setDocumentId(memberId + "-" + docType.name());
        memberDocument.setMemberId(memberId);
        memberDocument.setDocumentType(docType);
        memberDocument.setUrl(docUrl);
        memberDocument.setUploadedAt(LocalDateTime.now());
        return memberDocument;
    }

    public List<MemberDocumentResponse> getMemberShipDocuments(String memberId) {
        log.info("Fetching all documents for member ID: {}", memberId);
        
        try {
            // Optional: Validate member exists
            if (!memberRepo.existsById(memberId)) {
                log.warn("Attempting to fetch documents for non-existent member ID: {}", memberId);
                // You can choose to throw an exception or return empty list
                throw new IllegalArgumentException("Member not found with ID: " + memberId);
            }
            
            List<MemberDocument> documents = memberDocumentRepo.findMemberDocumentByMemberId(memberId);
            log.debug("Found {} document(s) for member ID: {}", documents.size(), memberId);
            
            List<MemberDocumentResponse> responses = documents.stream()
                    .map(doc -> {
                        log.trace("Mapping document - ID: {}, type: {}", 
                                doc.getDocumentId(), doc.getDocumentType());
                        return new MemberDocumentResponse(
                                doc.getDocumentId(),
                                doc.getDocumentType(),
                                doc.getUrl()
                        );
                    })
                    .toList();
            
            log.info("Successfully retrieved {} document(s) for member ID: {}", 
                    responses.size(), memberId);
            
            return responses;
            
        } catch (Exception e) {
            log.error("Failed to fetch documents for member ID: {}. Error: {}", 
                    memberId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch documents for member: " + memberId, e);
        }
    }
}