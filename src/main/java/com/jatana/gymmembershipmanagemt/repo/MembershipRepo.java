package com.jatana.gymmembershipmanagemt.repo;

import com.jatana.gymmembershipmanagemt.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipRepo extends JpaRepository<Membership, String> {
    List<Membership> findMembershipByMemberId(String memberId);
}
