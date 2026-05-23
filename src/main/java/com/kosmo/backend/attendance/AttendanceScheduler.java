package com.kosmo.backend.attendance;

import com.kosmo.backend.global.config.AttendancePolicyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class AttendanceScheduler implements SchedulingConfigurer {

    private final LectureAttendanceService attendanceService;
    private final AttendancePolicyProperties policy;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (!policy.isEnabled()) {
            return; // ❌ enabled=false면 스케줄러 등록 안 함
        }

        taskRegistrar.setScheduler(taskExecutor());

        // ✅ 결석 자동처리 스케줄 등록
        taskRegistrar.addTriggerTask(
                () -> attendanceService.markAbsentIfNoEntry(),
                (TriggerContext triggerContext) -> {
                    // "0 0 2 * * ?" 같은 형태
                    CronTrigger trigger = new CronTrigger(policy.getCronExpression());
                    Date nextExecutionTime = trigger.nextExecutionTime(triggerContext);
                    return nextExecutionTime.toInstant();
                }
        );

        // ✅ 현재까지 진행한 수업일수 증가 로직 추가
        taskRegistrar.addTriggerTask(
                () -> attendanceService.increaseLectureCurrentCntDaily(),
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(policy.getCronExpression());
                    return trigger.nextExecutionTime(triggerContext).toInstant();
                }
        );
    }

    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}