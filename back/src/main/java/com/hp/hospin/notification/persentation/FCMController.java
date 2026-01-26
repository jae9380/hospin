package com.hp.hospin.notification.persentation;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.common.MemberDetails;
import com.hp.hospin.global.standard.base.Empty;
import com.hp.hospin.global.standard.util.FcmTokenUtil;
import com.hp.hospin.notification.infrastructure.FCMSenderImpl;
import com.hp.hospin.notification.persentation.port.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/FCM")
@RequiredArgsConstructor
public class FCMController {
    private final NotificationService notificationService;
//    private final FCMSenderImpl sender;

    @PostMapping("/register")
    public ApiResponse<Empty> register(@AuthenticationPrincipal MemberDetails memberDetails,
                                       @RequestBody String token) {
        notificationService.register(memberDetails.getId(), token);

        return ApiResponse.noContent();
    }

    @PostMapping("/deregister")
    public ApiResponse<Empty> deregister(@AuthenticationPrincipal MemberDetails memberDetails) {
        notificationService.deregister(memberDetails.getId());

        return ApiResponse.noContent();
    }

//    FCM Push test
//    @PostMapping("/testFCM")
//    public ApiResponse<Empty> pushTest(@RequestBody String token) {
//        System.out.println(token);
//        System.out.println(FcmTokenUtil.parseFcmToken(token));
//        sender.send(FcmTokenUtil.parseFcmToken(token),"Title", "Body");
//
//        return ApiResponse.noContent();
//    }
}
