package com.jatana.gymmembershipmanagemt.controller;

import com.jatana.gymmembershipmanagemt.model.dto.request.MemberDocumentUploadRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberDocumentResponse;
import com.jatana.gymmembershipmanagemt.service.MemberDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import com.jatana.gymmembershipmanagemt.model.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
public class MemberDocumentController {

    @Autowired
    private MemberDocumentService memberDocumentService;

    @PostMapping("/member/upload/document")
    public ResponseEntity<?> uploadDocument(
            @RequestParam String memberId, 
            @ModelAttribute MemberDocumentUploadRequest memberDocumentUploadRequest,
            HttpServletRequest request) {
        
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
                ErrorResponse err = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("Empty or missing file")
                        .path(request.getRequestURI())
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
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
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(e.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            
        } catch (RuntimeException e) {
            // Check if it's a Cloudinary upload error
            if (e.getMessage() != null && e.getMessage().contains("Failed to upload document")) {
                log.error("Cloudinary upload failed - member ID: {}, doc type: {}, file name: {}. Error: {}", 
                        memberId, memberDocumentUploadRequest.docType(), fileName, e.getMessage(), e);
                ErrorResponse err = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                        .message("Document storage service unavailable")
                        .path(request.getRequestURI())
                        .build();
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
            }
            
            log.error("Runtime error while uploading document - member ID: {}, doc type: {}, file name: {}. Error: {}", 
                    memberId, memberDocumentUploadRequest.docType(), fileName, e.getMessage(), e);
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message("Runtime error during upload")
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
            
        } catch (Exception e) {
            log.error("Unexpected error while uploading document - member ID: {}, doc type: {}, file name: {}. Error: {}", 
                    memberId, memberDocumentUploadRequest.docType(), fileName, e.getMessage(), e);
            ErrorResponse err = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message("Unexpected server error")
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}