package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.MemberDocumentUploadRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberDocumentResponse;
import com.jatana.gymmembershipmanagemt.service.MemberDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
public class MemberDocumentController {

    @Autowired
    private MemberDocumentService memberDocumentService;

    @PostMapping("/member/upload/document")
    public ResponseEntity<MemberDocumentResponse> uploadDocument(
            @RequestParam String memberId, 
            @ModelAttribute MemberDocumentUploadRequest memberDocumentUploadRequest) {
        
        MultipartFile file = memberDocumentUploadRequest.file();
        String fileName = file != null ? file.getOriginalFilename() : "unknown";
        long fileSize = file != null ? file.getSize() : 0;
        
        log.info("Received document upload request - member ID: {}, doc type: {}, file name: {}, file size: {} bytes", 
                memberId, memberDocumentUploadRequest.docType(), fileName, fileSize);
        
        try {
            // Validate file is not empty
            if (file == null || file.isEmpty()) {
                log.error("Upload failed - Empty or null file for member ID: {}, doc type: {}", 
                        memberId, memberDocumentUploadRequest.docType());
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            // Log file details for security monitoring
            String contentType = file.getContentType();
            log.debug("File details - member ID: {}, doc type: {}, content type: {}, size: {} bytes", 
                    memberId, memberDocumentUploadRequest.docType(), contentType, fileSize);
            
            MemberDocumentResponse documentResponse = 
                    memberDocumentService.uploadDocument(memberId, memberDocumentUploadRequest);
            
            log.info("Successfully uploaded document - member ID: {}, doc type: {}, document ID: {}, file name: {}", 
                    memberId, memberDocumentUploadRequest.docType(), 
                    documentResponse.documentId(), fileName);
            
            return new ResponseEntity<>(documentResponse, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            log.error("Bad request while uploading document - member ID: {}, doc type: {}, file name: {}. Error: {}", 
                    memberId, memberDocumentUploadRequest.docType(), fileName, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            
        } catch (RuntimeException e) {
            // Check if it's a Cloudinary upload error
            if (e.getMessage() != null && e.getMessage().contains("Failed to upload document")) {
                log.error("Cloudinary upload failed - member ID: {}, doc type: {}, file name: {}. Error: {}", 
                        memberId, memberDocumentUploadRequest.docType(), fileName, e.getMessage(), e);
                return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
            }
            
            log.error("Runtime error while uploading document - member ID: {}, doc type: {}, file name: {}. Error: {}", 
                    memberId, memberDocumentUploadRequest.docType(), fileName, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            
        } catch (Exception e) {
            log.error("Unexpected error while uploading document - member ID: {}, doc type: {}, file name: {}. Error: {}", 
                    memberId, memberDocumentUploadRequest.docType(), fileName, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}