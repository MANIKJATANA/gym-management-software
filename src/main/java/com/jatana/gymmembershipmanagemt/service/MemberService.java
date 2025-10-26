package com.jatana.gymmembershipmanagemt.service;

import com.jatana.gymmembershipmanagemt.model.dto.request.MemberRequest;
import com.jatana.gymmembershipmanagemt.model.dto.request.MemberUpdateRequest;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberResponse;
import com.jatana.gymmembershipmanagemt.model.dto.response.MemberSummaryResponse;
import com.jatana.gymmembershipmanagemt.model.enums.MemberStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    public MemberResponse createMember(MemberRequest memberRequest) {
        return null;
    }

    public List<MemberSummaryResponse> getMembers(MemberStatus filter, String searchKey) {
        return List.of();
    }

    public MemberResponse getMember(String id) {
        return null;
    }

    public MemberResponse updateMember(MemberUpdateRequest memberUpdateRequest, String memberId) {
        return null;
    }

    public MemberResponse updateMemberStatus(String memberId, MemberStatus memberStatus) {
        return null;
    }
}
