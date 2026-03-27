package com.hp.hospin.member.domain;

import com.hp.hospin.member.domain.entity.Member;
import com.hp.hospin.member.domain.port.MemberRepository;
import com.hp.hospin.member.exception.MemberException.InvalidIdentiferPolicy;
import com.hp.hospin.member.exception.MemberException.MemberNotFoundException;
import com.hp.hospin.member.exception.MemberException.PasswordMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class MemberDomainServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private MemberDomainServiceImpl memberDomainService;

    // -------------------------------------------------------------------------
    // validatePolicy()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("validatePolicy - 아이디 정책 검증")
    class ValidatePolicy {

        // 아이디 정책(길이, 시작 문자, 허용 문자, 반복, 금지어)이 각각 독립적으로 동작하는지 확인하기 위함
        // 케이스마다 @Test를 분리하면 과잉이고 한 메서드에 나열하면 첫 실패 이후 케이스가 실행되지 않아 @ParameterizedTest 선택.
        @ParameterizedTest(name = "[{index}] 유효하지 않은 식별자: \"{0}\"")
        @ValueSource(strings = {
                "",           // 빈 문자열
                " ",          // 공백만 있는 경우
                "ab cd",      // 중간 공백 포함
                "abc",        // 6자 미만
                "a12345678901234567890", // 20자 초과
                "1abc12",      // 숫자로 시작
                "valid@id",    // 특수문자 포함 (@)
                "valid-id1",   // 허용되지 않는 특수문자 (-)
                "aaaaaaa",     // 동일 문자 6회 이상 반복 (a가 7개)
                "adminUser",   // 금지어 admin 포함
                "rootLogin",   // 금지어 root 포함
                "masterKey1"   // 금지어 master 포함
        })
        @DisplayName("정책 위반 식별자는 InvalidIdentiferPolicy 예외를 발생")
        void validatePolicy_invalidIdentifiers_throwsException(String invalidIdentifier) {
            // when & then
            assertThatThrownBy(() -> memberDomainService.validatePolicy(invalidIdentifier))
                    .isInstanceOf(InvalidIdentiferPolicy.class);
        }
        // 정책 위반 케이스 12개가 각각 독립적으로 실패/성공을 보고하므로 어느 조건이 깨졌는지 즉시 파악 가능.
        // 향후 정책이 추가되거나 정규식이 변경될 때 회귀 방어망 역할을 한다.

        // 잘못된 케이스만 검증하면 메서드가 항상 예외를 던지도록 구현되어 있어도 테스트가 통과해버린다
        // 정상 경로도 함께 검증해야 로직이 실제로 의도대로 동작한다는 것을 보장할 수 있어 작성
        @ParameterizedTest(name = "[{index}] 유효한 식별자: \"{0}\"")
        @ValueSource(strings = {
                "valid1",       // 정확히 6자, 문자 시작
                "user_name1",   // 언더스코어 허용
                "MyUser2025",   // 대문자 혼합
                "abcdef",       // 소문자 6자
                "a1b2c3d4e5f6"  // 영숫자 혼합 12자
        })
        @DisplayName("정책을 준수하는 식별자는 예외 없이 통과한다")
        void validatePolicy_validIdentifiers_noException(String validIdentifier) {
            // when & then
            assertThatCode(() -> memberDomainService.validatePolicy(validIdentifier))
                    .doesNotThrowAnyException();
        }
        // 정책을 준수하는 정상 케이스가 실수로 막히는 상황 방지
    }

    // -------------------------------------------------------------------------
    // resetPassword()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("resetPassword - 비밀번호 초기화")
    class ResetPassword {

        private static final String TEST_EMAIL = "user@test.com";
        private static final String NEW_PASSWORD = "newPass123!";
        private static final String CONFIRM_PASSWORD = "newPass123!";
        private static final String MISMATCH_PASSWORD = "differentPass!";

        // 비밀번호 불일치 시 DB 조회 없이 즉시 차단되는지 확인하기 위해 작성
        // 불일치 상태에서 DB를 조회하면 불필요한 I/O가 발생하고, 로직 순서 버그로 이어질 가능성 존재
        @Test
        @DisplayName("새 비밀번호와 확인 비밀번호가 다르면 PasswordMismatchException 예외를 던진다")
        void resetPassword_passwordMismatch_throwsException() {
            // given - 저장소 호출 없이 로직 내에서 즉시 실패해야 한다

            // when & then
            assertThatThrownBy(() ->
                    memberDomainService.resetPassword(TEST_EMAIL, NEW_PASSWORD, MISMATCH_PASSWORD))
                    .isInstanceOf(PasswordMismatchException.class);

            // 비밀번호 불일치 시 저장소에 접근하지 않아야 한다
            then(memberRepository).shouldHaveNoInteractions();
        }
        // 예외 발생 + 저장소 미호출을 동시에 검증하므로, 나중에 로직 순서가 바뀌어도 이 테스트가 잡아낸다.

        // 비밀번호 일치 여부와 회원 존재 여부를 검증하는 두 조건이 독립적으로 동작하는지 확인하기 위해 작성.
        @Test
        @DisplayName("이메일에 해당하는 회원이 없으면 MemberNotFoundException 예외를 던진다")
        void resetPassword_memberNotFound_throwsException() {
            // given
            given(memberRepository.getByEmail(TEST_EMAIL)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    memberDomainService.resetPassword(TEST_EMAIL, NEW_PASSWORD, CONFIRM_PASSWORD))
                    .isInstanceOf(MemberNotFoundException.class);

            then(memberRepository).should().getByEmail(TEST_EMAIL);
            then(memberRepository).shouldHaveNoMoreInteractions();
        }
        // 회원이 없을 때 updatePassword가 호출되지 않음을 보장해 존재하지 않는 계정의 비밀번호가 변경되는 사고를 방지한다.

        // 두 조건(비밀번호 일치 + 회원 존재)이 모두 충족될 때 실제로 비밀번호가 변경되는지 검증하기 위해 작성.
        @Test
        @DisplayName("비밀번호가 일치하고 회원이 존재하면 비밀번호가 정상적으로 변경된다")
        void resetPassword_success() {
            // given
            Member existingMember = Member.builder()
                    .id(1L)
                    .identifier("validUser1")
                    .password("oldEncodedPassword")
                    .name("홍길동")
                    .phoneNumber("010-1234-5678")
                    .email(TEST_EMAIL)
                    .role(2)
                    .gender(1)
                    .birth(LocalDate.of(1990, 1, 1))
                    .build();

            String encodedNewPassword = "$2a$10$encodedNewPassword";

            given(memberRepository.getByEmail(TEST_EMAIL)).willReturn(Optional.of(existingMember));
            given(bCryptPasswordEncoder.encode(NEW_PASSWORD)).willReturn(encodedNewPassword);

            // when
            memberDomainService.resetPassword(TEST_EMAIL, NEW_PASSWORD, CONFIRM_PASSWORD);

            // then
            then(memberRepository).should().getByEmail(TEST_EMAIL);
            then(bCryptPasswordEncoder).should().encode(NEW_PASSWORD);
            then(memberRepository).should().updatePassword(existingMember.getId(), encodedNewPassword);
        }
        // 인코딩된 비밀번호가 저장되는지, 원본 비밀번호가 그대로 저장되지 않는지를 보장
    }
}
