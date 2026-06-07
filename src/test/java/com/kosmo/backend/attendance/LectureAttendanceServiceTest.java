package com.kosmo.backend.attendance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GPS 거리 판정 로직(isWithinDistance) 단위 테스트.
 *
 * - DB·외부 의존이 전혀 없는 순수 계산 메서드라, 의존성은 모두 null로 주입해 인스턴스만 만든다.
 * - 강의실 고정 좌표(submitAttendance 기준): 위도 37.488264, 경도 126.982749
 * - 허용 반경: 200m (메서드 내부에 하드코딩됨)
 */
class LectureAttendanceServiceTest {

    // isWithinDistance는 어떤 의존성도 사용하지 않으므로 null로 채워 인스턴스 생성
    private final LectureAttendanceService service =
            new LectureAttendanceService(null, null, null, null, null, null);

    // 강의실 기준 좌표
    private static final double BASE_LAT = 37.488264;
    private static final double BASE_LON = 126.982749;

    @Test
    @DisplayName("같은 위치면 거리 0m → 출석 허용(true)")
    void sameLocation_isWithin() {
        boolean result = service.isWithinDistance(BASE_LAT, BASE_LON, BASE_LAT, BASE_LON);
        assertTrue(result);
    }

    @Test
    @DisplayName("약 100m 떨어진 위치(반경 200m 이내) → 출석 허용(true)")
    void within200m_isWithin() {
        // 위도 +0.0009 ≈ 북쪽으로 약 100m
        boolean result = service.isWithinDistance(BASE_LAT, BASE_LON, BASE_LAT + 0.0009, BASE_LON);
        assertTrue(result);
    }

    @Test
    @DisplayName("약 300m 떨어진 위치(반경 200m 초과) → 출석 거부(false)")
    void beyond200m_isNotWithin() {
        // 위도 +0.0027 ≈ 북쪽으로 약 300m
        boolean result = service.isWithinDistance(BASE_LAT, BASE_LON, BASE_LAT + 0.0027, BASE_LON);
        assertFalse(result);
    }

    @Test
    @DisplayName("멀리 떨어진 위치(서울시청, 약 8.7km) → 출석 거부(false)")
    void farAway_isNotWithin() {
        boolean result = service.isWithinDistance(BASE_LAT, BASE_LON, 37.5665, 126.9780);
        assertFalse(result);
    }
}
