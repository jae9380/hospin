package com.hp.hospin.schedule.application;

import com.hp.hospin.schedule.application.dto.ScheduleDTO;
import com.hp.hospin.schedule.application.mapper.ScheduleDtoMapper;
import com.hp.hospin.schedule.application.port.ScheduleDomainService;
import com.hp.hospin.schedule.domain.entity.Schedule;
import com.hp.hospin.schedule.domain.form.ScheduleForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleDomainService scheduleDomainService;

    @Mock
    private ScheduleDtoMapper mapper;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    // -------------------------------------------------------------------------
    // createSchedule() - validDateRange() 내부 검증
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("createSchedule - 날짜 범위 유효성 검증")
    class ValidDateRange {

        private static final Long MEMBER_ID = 1L;

        // start > end인 역방향 입력이 실제로 차단되는지 확인하기 위해 작성.
        // 날짜 검증 없이 저장되면 UI에서 종료일이 시작일보다 이른 일정이 노출되는 데이터 정합성 문제가 생긴다.
        @Test
        @DisplayName("start가 end보다 이후이면 IllegalArgumentException 예외를 던진다")
        void createSchedule_startAfterEnd_throwsIllegalArgumentException() {
            // given
            LocalDateTime start = LocalDateTime.of(2026, 3, 26, 12, 0);
            LocalDateTime end   = LocalDateTime.of(2026, 3, 26, 10, 0); // start보다 이전

            ScheduleDTO request = ScheduleDTO.builder()
                    .category(1)
                    .title("검진 예약")
                    .memo("내과")
                    .startDatetime(start)
                    .endDatetime(end)
                    .build();

            // when & then
            assertThatThrownBy(() -> scheduleService.createSchedule(MEMBER_ID, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작일자는 종료일자보다 이후일 수 없습니다.");
        }
        // 역방향 입력이 서비스 레이어에서 차단되어 도메인 레이어까지 도달하지 않음을 보장한다.

        // isAfter만 체크하는 현재 구현에서 start == end가 통과된다는 정책 공백을 명시하기 위해 작성.
        // "통과가 맞는가"에 대한 정책 결정을 테스트 코드로 문서화해 향후 변경 시 수정 지점을 명확히 한다.
        @Test
        @DisplayName("start와 end가 같으면 예외 없이 통과한다 - 현재 코드는 isAfter만 검사하므로 통과")
        void createSchedule_startEqualsEnd_noException() {
            // given
            LocalDateTime sameTime = LocalDateTime.of(2026, 3, 26, 10, 0);

            ScheduleDTO request = ScheduleDTO.builder()
                    .category(1)
                    .title("즉시 완료 일정")
                    .memo("")
                    .startDatetime(sameTime)
                    .endDatetime(sameTime)
                    .build();

            ScheduleForm stubForm = new ScheduleForm(
                    request.getCategory(),
                    request.getTitle(),
                    request.getMemo(),
                    request.getStartDatetime(),
                    request.getEndDatetime()
            );

            Schedule stubSchedule = Schedule.builder()
                    .id(10L)
                    .memberId(MEMBER_ID)
                    .category(1)
                    .title("즉시 완료 일정")
                    .startDatetime(sameTime)
                    .endDatetime(sameTime)
                    .build();

            ScheduleDTO stubResultDto = ScheduleDTO.builder()
                    .id(10L)
                    .memberId(MEMBER_ID)
                    .category(1)
                    .title("즉시 완료 일정")
                    .startDatetime(sameTime)
                    .endDatetime(sameTime)
                    .build();

            given(mapper.toForm(request)).willReturn(stubForm);
            given(scheduleDomainService.createSchedule(MEMBER_ID, stubForm)).willReturn(stubSchedule);
            given(mapper.toDto(stubSchedule)).willReturn(stubResultDto);

            // when & then
            // NOTE: 현재 validDateRange는 isAfter만 체크하므로 start == end 는 통과한다.
            // 향후 isEqual도 거부해야 한다면 이 테스트를 assertThatThrownBy로 변경해야 한다.
            assertThatCode(() -> scheduleService.createSchedule(MEMBER_ID, request))
                    .doesNotThrowAnyException();
        }
        // 정책 공백이 코드에 문서화되어, 나중에 정책이 확정될 때 이 테스트 하나만 수정하면 된다는 것을 보장한다.

        // 정상 범위 입력이 검증을 통과하고 ScheduleDTO를 반환하는 happy path를 확인하기 위해 작성.
        @Test
        @DisplayName("start가 end보다 이전이면 정상적으로 ScheduleDTO를 반환한다")
        void createSchedule_validDateRange_returnsScheduleDTO() {
            // given
            LocalDateTime start = LocalDateTime.of(2026, 3, 26, 9, 0);
            LocalDateTime end   = LocalDateTime.of(2026, 3, 26, 11, 0);

            ScheduleDTO request = ScheduleDTO.builder()
                    .category(1)
                    .title("외래 진료")
                    .memo("정형외과")
                    .startDatetime(start)
                    .endDatetime(end)
                    .build();

            ScheduleForm stubForm = new ScheduleForm(
                    request.getCategory(),
                    request.getTitle(),
                    request.getMemo(),
                    request.getStartDatetime(),
                    request.getEndDatetime()
            );

            Schedule stubSchedule = Schedule.builder()
                    .id(20L)
                    .memberId(MEMBER_ID)
                    .category(1)
                    .title("외래 진료")
                    .startDatetime(start)
                    .endDatetime(end)
                    .build();

            ScheduleDTO expectedDto = ScheduleDTO.builder()
                    .id(20L)
                    .memberId(MEMBER_ID)
                    .category(1)
                    .title("외래 진료")
                    .startDatetime(start)
                    .endDatetime(end)
                    .build();

            given(mapper.toForm(request)).willReturn(stubForm);
            given(scheduleDomainService.createSchedule(MEMBER_ID, stubForm)).willReturn(stubSchedule);
            given(mapper.toDto(stubSchedule)).willReturn(expectedDto);

            // when
            ScheduleDTO result = scheduleService.createSchedule(MEMBER_ID, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(20L);
            assertThat(result.getTitle()).isEqualTo("외래 진료");
            assertThat(result.getStartDatetime()).isEqualTo(start);
            assertThat(result.getEndDatetime()).isEqualTo(end);
        }
        // 날짜 검증 로직이 정상 입력을 잘못 막지 않음을 보장하고, 반환값이 입력과 일치하는지 확인한다.
    }
}
