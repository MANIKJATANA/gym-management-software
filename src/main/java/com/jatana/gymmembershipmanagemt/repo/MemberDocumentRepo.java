package com.jatana.gymmembershipmanagemt.repo;

import com.jatana.gymmembershipmanagemt.model.MemberDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberDocumentRepo extends JpaRepository<MemberDocument, String> {
    List<MemberDocument> findMemberDocumentByMemberId(String memberId);
}
