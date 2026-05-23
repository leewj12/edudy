package com.kosmo.backend.attendance.holiday;

import com.kosmo.backend.attendance.holiday.dto.HolidayResponse;
import com.kosmo.backend.attendance.holiday.dto.HolidayResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HolidayService {

    private final HolidayRepository holidayRepository;

    private final RestTemplate restTemplate;
    private final Map<Integer, List<LocalDate>> holidayCache = new ConcurrentHashMap<>();

    @Value("${holiday.api.service-key:}")
    private String serviceKey;

    private final String url = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo";

    // 특정 날짜가 공휴일인지 여부 확인
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date);
    }

    @Transactional
    public List<LocalDate> getHolidaysForYear(long year) {
        List<HolidayEntity> existing = holidayRepository.findByHolidayYear(year);
        if (!existing.isEmpty()) {
            return existing.stream().map(HolidayEntity::getHolidayDate).toList();
        }

        List<HolidayEntity> holidays = fetchFromPublicAPI(year);
        if (!holidays.isEmpty()) {
            holidayRepository.saveAll(holidays);
        }

        return holidays.stream().map(HolidayEntity::getHolidayDate).toList();
    }

    @Transactional
    public List<HolidayResult> getHolidaysForYearResult(long year) {
        List<HolidayEntity> existing = holidayRepository.findByHolidayYear(year);

        if (!existing.isEmpty()) {
            return existing.stream()
                    .map(HolidayResult::fromEntity)
                    .toList();
        }

        List<HolidayEntity> holidays = fetchFromPublicAPI(year);
        if (!holidays.isEmpty()) {
            holidayRepository.saveAll(holidays);
        }

        return holidays.stream()
                .map(HolidayResult::fromEntity)
                .toList();
    }

    private List<HolidayEntity> fetchFromPublicAPI(long year) {
        if (serviceKey.isEmpty()) {
            log.warn("공휴일 API 키가 설정되지 않았습니다.");
            return Collections.emptyList();
        }

        try {
            String requestUrl = url
                    + "?solYear=" + year
                    + "&ServiceKey=" + serviceKey
                    + "&numOfRows=100"
                    + "&_type=xml";

            ResponseEntity<HolidayResponse> response =
                    restTemplate.getForEntity(requestUrl, HolidayResponse.class);

            HolidayResponse holidayResponse = response.getBody();

            if (holidayResponse == null || holidayResponse.getBody() == null ||
                    holidayResponse.getBody().getItems() == null ||
                    holidayResponse.getBody().getItems().getItem() == null) {
                log.warn("Holiday API 응답이 null입니다. year={}", year);
                return Collections.emptyList();
            }

            List<HolidayResponse.Body.Items.Item> itemList =
                    holidayResponse.getBody().getItems().getItem();

            log.debug("가져온 holiday item 수: {}", itemList.size());

            List<HolidayEntity> result = new ArrayList<>();
            for (HolidayResponse.Body.Items.Item item : itemList) {
                LocalDate date = LocalDate.parse(item.getLocdate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                result.add(HolidayEntity.builder()
                        .holidayDate(date)
                        .holidayName(item.getDateName())
                        .holidayYear((long) date.getYear())
                        .build());
            }

            return result;
        } catch (Exception e) {
            log.error("공휴일 API 호출 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<LocalDate> getHolidaysForYearRange(int startYear, int endYear) {
        List<LocalDate> holidays = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            holidays.addAll(getHolidaysForYear(year));
        }
        return holidays;
    }
}
