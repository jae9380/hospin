package com.hp.hospin.member.persentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.common.MemberDetails;
import com.hp.hospin.global.jwt.CookieUtil;
import com.hp.hospin.global.standard.base.Empty;
import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.dto.MemberResponse;
import com.hp.hospin.member.persentation.dto.LoginRequest;
import com.hp.hospin.member.persentation.mapper.MemberDtoMapper;
import com.hp.hospin.member.persentation.port.AuthenticationService;
import com.hp.hospin.member.persentation.port.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationService authenticationService;
    private final MemberDtoMapper mapper;

    @GetMapping()
    public ApiResponse<MemberResponse> memberInfo(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(
                mapper.domainToResponse(memberService.findByIdentifier(memberDetails.getName()))
        );
    }
    @PostMapping("/join")
    public ApiResponse<Empty> join(@RequestBody @Valid JoinRequest request) {
        memberService.join(request);
        return ApiResponse.created();
    }

    @PostMapping ("/login")
    public ApiResponse<MemberResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                             HttpServletRequest request, HttpServletResponse response) {
        memberService.login(loginRequest);
        MemberResponse memberResponse = authenticationService.authenticateAndSetTokens(loginRequest.identifier(), request, response);
        return ApiResponse.ok(memberResponse);
    }

    @PostMapping ("/logout")
    public ApiResponse<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        Stream.of("refresh_token", "access_token", "JSESSIONID")
                .forEach(cookieName -> CookieUtil.deleteCookie(request, response, cookieName));

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "로그아웃 되었습니다.");
        return ApiResponse.ok(responseMap);
    }

    @GetMapping("/check-duplicate")
    public ApiResponse<Empty> identfierDuplicationCheck(@RequestParam String identifier) {
        memberService.checkDuplicateIdentifier(identifier);
        return ApiResponse.noContent();
    }
}
