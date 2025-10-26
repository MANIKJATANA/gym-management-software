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
    public ResponseEntity<MemberDocumentResponse> uploadDocument(@RequestParam String memberId, @ModelAttribute MemberDocumentUploadRequest memberDocumentUploadRequest) {
        try{
            MemberDocumentResponse documentResponse = memberDocumentService.uploadDocument(memberId,memberDocumentUploadRequest);
            return ResponseEntity.ok(documentResponse);
        } catch (Exception e) {
            log.error("error {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
