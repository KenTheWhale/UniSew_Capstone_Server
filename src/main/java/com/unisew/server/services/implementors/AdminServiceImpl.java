package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.requests.AdminAccountStatsRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AdminService;
import com.unisew.server.utils.ResponseBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminServiceImpl implements AdminService {

    AccountRepo accountRepo;

    @Override
    public ResponseEntity<ResponseObject> getAccountStats(AdminAccountStatsRequest request) {
        if (request.getFrom() == null || request.getTo() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "from/to is required", null);
        }
        if (request.getFrom().isAfter(request.getTo())) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "from must be <= to", null);
        }
        if (request.getGroupBy() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "groupBy is required", null);
        }

        List<Account> inRange = accountRepo.findAllByRegisterDateBetween(request.getFrom(), request.getTo());
        List<Account> inRangeNonAdmin = inRange.stream()
                .filter(a -> a.getRole() != Role.ADMIN)
                .toList();

        Map<String, Long> byRole = Arrays.stream(Role.values())
                .filter(r -> r != Role.ADMIN)
                .collect(Collectors.toMap(
                        Enum::name,
                        accountRepo::countByRole,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        long total = byRole.values().stream().mapToLong(Long::longValue).sum();

        Map<String, Long> byStatus = Arrays.stream(Status.values())
                .filter(s -> s.name().startsWith("ACCOUNT_"))
                .collect(Collectors.toMap(
                        Enum::name,
                        accountRepo::countByStatus,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        long inactiveCount = byStatus.getOrDefault(Status.ACCOUNT_INACTIVE.name(), 0L);

        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("total", total);
        overview.put("byRole", byRole);
        overview.put("byStatus", byStatus);
        overview.put("inactiveCount", inactiveCount);

        List<Map<String, Object>> timeSeries = buildTimeSeries(
                inRangeNonAdmin, request.getFrom(), request.getTo(), request.getGroupBy()
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("overview", overview);
        data.put("timeSeries", timeSeries);

        return ResponseBuilder.build(HttpStatus.OK, "Account stats", data);
    }

    private List<Map<String, Object>> buildTimeSeries(
            List<Account> inRangeNonAdmin,
            LocalDate from,
            LocalDate to,
            AdminAccountStatsRequest.GroupBy groupBy
    ) {
        List<Bucket> buckets = generateBuckets(from, to, groupBy);

        Map<LocalDate, List<Account>> grouped = inRangeNonAdmin.stream()
                .collect(Collectors.groupingBy(a -> bucketKey(a.getRegisterDate(), groupBy)));

        return buckets.stream().map(b -> {
            List<Account> accountsInBucket = grouped.getOrDefault(b.start, Collections.emptyList());

            Map<String, Long> byRole = accountsInBucket.stream()
                    .collect(Collectors.groupingBy(
                            a -> a.getRole().name(),
                            LinkedHashMap::new,
                            Collectors.counting()
                    ));

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("bucketStart", b.start);
            m.put("bucketEnd", b.end);
            m.put("count", (long) accountsInBucket.size()); // đã loại ADMIN ở tham số đầu vào
            m.put("byRole", byRole);
            return m;
        }).collect(Collectors.toList());
    }

    private LocalDate bucketKey(LocalDate date, AdminAccountStatsRequest.GroupBy groupBy) {
        return switch (groupBy) {
            case WEEK -> date.with(java.time.DayOfWeek.MONDAY);
            case MONTH -> date.with(TemporalAdjusters.firstDayOfMonth());
            default -> date;
        };
    }

    private static class Bucket {
        LocalDate start;
        LocalDate end;

        Bucket(LocalDate s, LocalDate e) {
            this.start = s;
            this.end = e;
        }
    }

    private List<Bucket> generateBuckets(LocalDate from, LocalDate to, AdminAccountStatsRequest.GroupBy groupBy) {
        List<Bucket> out = new ArrayList<>();
        LocalDate cursor;

        switch (groupBy) {
            case DAY:
                cursor = from;
                while (!cursor.isAfter(to)) {
                    out.add(new Bucket(cursor, cursor));
                    cursor = cursor.plusDays(1);
                }
                break;
            case WEEK:
                cursor = from.with(java.time.DayOfWeek.MONDAY);
                while (!cursor.isAfter(to)) {
                    LocalDate end = cursor.plusDays(6);
                    if (end.isAfter(to)) end = to;
                    out.add(new Bucket(cursor, end));
                    cursor = cursor.plusWeeks(1);
                }
                break;
            case MONTH:
                cursor = from.with(TemporalAdjusters.firstDayOfMonth());
                while (!cursor.isAfter(to)) {
                    LocalDate end = cursor.with(TemporalAdjusters.lastDayOfMonth());
                    if (end.isAfter(to)) end = to;
                    out.add(new Bucket(cursor, end));
                    cursor = cursor.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                }
                break;
        }
        return out;
    }
}
