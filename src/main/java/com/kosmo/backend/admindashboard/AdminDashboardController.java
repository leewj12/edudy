package com.kosmo.backend.admindashboard;

import com.kosmo.backend.admindashboard.dto.DashboardSummaryResponse;
import com.kosmo.backend.admindashboard.dto.LectureAttendanceRateResponse;
import com.kosmo.backend.banner.BannerRepository;
import com.kosmo.backend.lecture.repository.LectureRepository;
import com.kosmo.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final BannerRepository bannerRepository;

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/admin/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = adminDashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/admin/dashboard/attendance/today")
    public ResponseEntity<List<LectureAttendanceRateResponse>> getTodayAttendanceRates() {
        return ResponseEntity.ok(adminDashboardService.getTodayAttendanceRates());
    }

}