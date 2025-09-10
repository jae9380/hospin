package com.hp.hospin.member.persentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.standard.base.Empty;
import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.persentation.port.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ApiResponse<Empty> join(@RequestBody @Valid JoinRequest request) {
        memberService.join(request);
        return ApiResponse.created();
    }
}
