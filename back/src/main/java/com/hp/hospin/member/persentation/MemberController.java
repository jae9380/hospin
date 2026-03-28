package com.hp.hospin.member.persentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.common.MemberDetails;
import com.hp.hospin.global.jwt.CookieUtil;
import com.hp.hospin.global.standard.annotations.Monitored;
import com.hp.hospin.global.standard.base.Empty;
import com.hp.hospin.member.application.dto.AuthTokenResult;
import com.hp.hospin.member.application.dto.MemberDTO;
import com.hp.hospin.member.persentation.dto.JoinRequest;
import com.hp.hospin.member.persentation.dto.MemberResponse;
import com.hp.hospin.member.persentation.dto.LoginRequest;
import com.hp.hospin.member.persentation.dto.ResetPasswordRequest;
import com.hp.hospin.member.persentation.mapper.MemberApiMapper;
import com.hp.hospin.member.persentation.port.AuthenticationService;
import com.hp.hospin.member.persentation.port.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final int REFRESH_TOKEN_MAX_AGE = (int) Duration.ofDays(1).toSeconds();
    private static final int ACCESS_TOKEN_MAX_AGE = (int) Duration.ofHours(1).toSeconds();

    private final MemberService memberService;
    private final AuthenticationService authenticationService;
    private final MemberApiMapper mapper;

    @GetMapping()
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "memberInfo"
    )
    public ApiResponse<MemberResponse> memberInfo(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.ok(
                mapper.dtoToResponse(
                        memberService.findByIdentifier(memberDetails.getUsername())
                )
        );
    }

    @PostMapping("/join")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "join"
    )
    public ApiResponse<Empty> join(@RequestBody @Valid JoinRequest request) {
        memberService.join(mapper.joinRequestToDto(request));
        return ApiResponse.created();
    }

    @PostMapping("/login")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "login"
    )
    public ApiResponse<MemberResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                             HttpServletRequest request, HttpServletResponse response) {
        MemberDTO memberDTO = memberService.login(mapper.loginRequestToDto(loginRequest));

        AuthTokenResult tokenResult = authenticationService.authenticateAndIssueTokens(memberDTO);
        setTokenCookies(request, response, tokenResult.accessToken(), tokenResult.refreshToken());

        return ApiResponse.ok(mapper.dtoToResponse(tokenResult.member()));
    }

    @PostMapping("/logout")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "logout"
    )
    public ApiResponse<Map<String, String>> logout(@AuthenticationPrincipal MemberDetails memberDetails,
                                                   HttpServletRequest request, HttpServletResponse response) {
        if (memberDetails != null) {
            authenticationService.deleteRefreshToken(memberDetails.getId());
        }

        boolean secure = CookieUtil.isSecure(request);
        Stream.of(REFRESH_TOKEN_COOKIE, ACCESS_TOKEN_COOKIE, "JSESSIONID")
                .forEach(cookieName -> CookieUtil.deleteCookie(request, response, cookieName, secure));

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ApiResponse.ok(memberService.logoutMsg());
    }

    @PostMapping("/auth/refresh")
    public ApiResponse<Empty> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookies(request, REFRESH_TOKEN_COOKIE);
        String newAccessToken = authenticationService.reissueAccessToken(refreshToken);

        boolean secure = CookieUtil.isSecure(request);
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE, secure);
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE, newAccessToken, ACCESS_TOKEN_MAX_AGE, secure);

        return ApiResponse.noContent();
    }

    @GetMapping("/check-duplicate")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "identfierDuplicationCheck"
    )
    public ApiResponse<String> identfierDuplicationCheck(@RequestParam String identifier) {
        Map.Entry<Boolean, String> entry =
                memberService.checkDuplicateIdentifier(identifier).entrySet().iterator().next();

        return entry.getKey()
                ? ApiResponse.ok(entry.getValue())
                : ApiResponse.impossible(entry.getValue());
    }

    @GetMapping("/findId")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "findId"
    )
    public ApiResponse<String> findId(@RequestParam String name, @RequestParam String email) {
        return ApiResponse.ok(memberService.findId(name, email));
    }

    @PostMapping("/findPw")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "findPw"
    )
    public ApiResponse<String> findPw(@RequestParam String name, @RequestParam String id, @RequestParam String email) {
        return ApiResponse.ok(memberService.verifyAndSendAuthCode(name, id, email));
    }

    @PostMapping("/join/sendAuthEmail")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "sendAuthEmail"
    )
    public ApiResponse<Empty> sendAuthEmail(@RequestParam String email) {
        memberService.sendAuthEmail(email);
        return ApiResponse.noContent();
    }

    @PatchMapping("/join/verifyCode")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "verifyCode"
    )
    public ApiResponse<Empty> verifyCode(@RequestParam String email, @RequestParam String code) {
        memberService.verifyCode(email, code);
        return ApiResponse.noContent();
    }

    @PutMapping("/resetPassword")
    @Monitored(
            domain = "member",
            layer = "presentation",
            api = "resetPassword"
    )
    public ApiResponse<Empty> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        memberService.resetPassword(request.email(), request.newPassword(), request.confirmNewPassword());
        return ApiResponse.noContent();
    }

    private void setTokenCookies(HttpServletRequest request, HttpServletResponse response,
                                 String accessToken, String refreshToken) {
        boolean secure = CookieUtil.isSecure(request);
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_COOKIE, secure);
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE, secure);
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE, accessToken, ACCESS_TOKEN_MAX_AGE, secure);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE, refreshToken, REFRESH_TOKEN_MAX_AGE, secure);
    }

    private String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
