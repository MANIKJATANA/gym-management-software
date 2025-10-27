package com.jatana.gymmembershipmanagemt.repo;

import com.jatana.gymmembershipmanagemt.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepo extends JpaRepository<Member, String> {
    @Query("""
    SELECT m FROM Member m
    WHERE
        (:filter IS NULL OR :filter = '' OR LOWER(m.memberStatus) = LOWER(:filter))
        AND (
            :keyword IS NULL OR :keyword = '' OR
            LOWER(m.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(m.memberId) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    ORDER BY m.createdAt DESC
""")
    List<Member> findMembersUsingFilterAndSearchKeyword(@Param("filter") String filter,
                                                        @Param("keyword") String keyword);


}

