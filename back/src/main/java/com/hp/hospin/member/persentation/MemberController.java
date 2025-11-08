package com.hp.hospin.member.persentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.common.MemberDetails;
import com.hp.hospin.global.jwt.CookieUtil;
import com.hp.hospin.global.standard.base.Empty;
import com.hp.hospin.member.application.dto.JoinRequest;
import com.hp.hospin.member.application.dto.MemberResponse;
import com.hp.hospin.member.application.dto.LoginRequest;
import com.hp.hospin.member.exception.MemberException;
import com.hp.hospin.member.persentation.port.AuthenticationService;
import com.hp.hospin.member.persentation.port.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationService authenticationService;

    @GetMapping()
    public ApiResponse<MemberResponse> memberInfo(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(memberService.findByIdentifier(memberDetails.getName()));
    }
    @PostMapping("/join")
    public ApiResponse<Empty> join(@RequestBody @Valid JoinRequest request) {
        memberService.join(request);
        return ApiResponse.created();
    }

    @PostMapping ("/login")
    public ApiResponse<MemberResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                             HttpServletRequest request, HttpServletResponse response) {
//        TODO: 구조변경 필요 (관련 메서드 전제 수정?...
        memberService.login(loginRequest);

        return ApiResponse.ok(
                authenticationService.authenticateAndSetTokens(loginRequest.identifier(), request, response)
        );
    }

    @PostMapping ("/logout")
    public ApiResponse<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        Stream.of("refresh_token", "access_token", "JSESSIONID")
                .forEach(cookieName -> CookieUtil.deleteCookie(request, response, cookieName));

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ApiResponse.ok(memberService.logoutMsg());
    }

    @GetMapping("/check-duplicate")
    public ApiResponse<String> identfierDuplicationCheck(@RequestParam String identifier) {

        Map.Entry<Boolean, String> entry =
                memberService.checkDuplicateIdentifier(identifier).entrySet().iterator().next();

        return entry.getKey()
                ? ApiResponse.ok(entry.getValue())
                : ApiResponse.impossible(entry.getValue());

    }
}
