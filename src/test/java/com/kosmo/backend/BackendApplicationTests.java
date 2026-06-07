package com.kosmo.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// 전체 애플리케이션 컨텍스트(JPA·DB·보안 등)를 띄우는 통합 스모크 테스트.
// CI에는 DB가 없어 실행 시 실패하므로, DB가 갖춰진 환경에서만 수동 실행한다.
// 단위 테스트(LectureAttendanceServiceTest, JwtProviderTest)는 DB 없이 항상 CI에서 실행됨.
@Disabled("전체 컨텍스트·DB 필요 — 통합 테스트 환경에서만 실행")
@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
