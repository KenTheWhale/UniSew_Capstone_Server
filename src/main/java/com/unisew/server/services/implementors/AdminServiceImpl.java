package com.unisew.server.services.implementors;

import com.unisew.server.enums.Role;
import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.models.DesignRequest;
import com.unisew.server.models.Order;
import com.unisew.server.models.Transaction;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.repositories.DesignRequestRepo;
import com.unisew.server.repositories.OrderRepo;
import com.unisew.server.repositories.TransactionRepo;
import com.unisew.server.requests.AdminAccountStatsRequest;
import com.unisew.server.requests.AdminTransactionStatsRequest;
import com.unisew.server.responses.ResponseObject;
import com.unisew.server.services.AdminService;
import com.unisew.server.utils.ResponseBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
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
    TransactionRepo transactionRepo;
    DesignRequestRepo designRequestRepo;
    OrderRepo orderRepo;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class Bucket {
        LocalDate start;
        LocalDate end;
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

    @Override
    public ResponseEntity<ResponseObject> getTransactionStats(AdminTransactionStatsRequest request) {
        if (request.getFrom() == null || request.getTo() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "from/to is required", null);
        }
        if (request.getFrom().isAfter(request.getTo())) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "from must be <= to", null);
        }

        LocalDate from = request.getFrom();
        LocalDate to = request.getTo();

        List<Transaction> txs = transactionRepo.findAllByCreationDateBetween(from, to);

        Map<String, Long> byStatus = txs.stream()
                .collect(Collectors.groupingBy(t -> t.getStatus().name(),
                        LinkedHashMap::new, Collectors.counting()));

        Map<String, Long> byPaymentType = txs.stream()
                .filter(t -> t.getPaymentType() != null)
                .collect(Collectors.groupingBy(t -> t.getPaymentType().name(),
                        LinkedHashMap::new, Collectors.counting()));

        long totalCount = txs.size();
        long totalAmountAll = txs.stream().mapToLong(Transaction::getAmount).sum();

        long totalServiceFee = txs.stream()
                .mapToLong(this::revenueOf)
                .sum();

        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalCount", totalCount);
        overview.put("totalAmount", totalAmountAll);
        overview.put("totalServiceFee", totalServiceFee);
        overview.put("byStatus", byStatus);
        overview.put("byPaymentType", byPaymentType);

        List<Map<String, Object>> daily = buildDailyRevenueSeries(from, to, txs);
        List<Map<String, Object>> monthly = buildMonthlyRevenueSeries(from, to, txs);
        List<Map<String, Object>> yearly = buildYearlyRevenueSeries(from, to, txs);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("overview", overview);
        data.put("dailyRevenue", daily);
        data.put("monthlyRevenue", monthly);
        data.put("yearlyRevenue", yearly);

        return ResponseBuilder.build(HttpStatus.OK, "Transaction stats", data);
    }

    private long revenueOf(Transaction t) {
        if (t.getStatus() == null) return 0L;
        long fee = t.getServiceFee();
        return switch (t.getStatus()) {
            case TRANSACTION_SUCCESS -> fee;
            case TRANSACTION_FAIL -> -fee;
            default -> 0L;
        };
    }

    private List<Map<String, Object>> buildDailyRevenueSeries(LocalDate from, LocalDate to, List<Transaction> txs) {
        Map<LocalDate, List<Transaction>> byDate = txs.stream()
                .collect(Collectors.groupingBy(Transaction::getCreationDate));

        List<Map<String, Object>> out = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            List<Transaction> dayTx = byDate.getOrDefault(cursor, Collections.emptyList());
            long revenue = dayTx.stream().mapToLong(this::revenueOf).sum();
            long completedCount = dayTx.stream().filter(t -> t.getStatus() == Status.TRANSACTION_SUCCESS).count();

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", cursor);
            m.put("revenue", revenue);
            m.put("completedCount", completedCount);
            m.put("txCount", (long) dayTx.size());
            out.add(m);

            cursor = cursor.plusDays(1);
        }
        return out;
    }

    private List<Map<String, Object>> buildMonthlyRevenueSeries(LocalDate from, LocalDate to, List<Transaction> txs) {
        YearMonth start = YearMonth.from(from);
        YearMonth end = YearMonth.from(to);

        Map<YearMonth, List<Transaction>> byYm = txs.stream()
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getCreationDate())));

        List<Map<String, Object>> out = new ArrayList<>();
        YearMonth cursor = start;
        while (!cursor.isAfter(end)) {
            List<Transaction> monthTx = byYm.getOrDefault(cursor, Collections.emptyList());
            long revenue = monthTx.stream().mapToLong(this::revenueOf).sum();
            long completedCount = monthTx.stream().filter(t -> t.getStatus() == Status.TRANSACTION_SUCCESS).count();

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("yearMonth", cursor.toString());
            m.put("revenue", revenue);
            m.put("completedCount", completedCount);
            m.put("txCount", (long) monthTx.size());
            out.add(m);

            cursor = cursor.plusMonths(1);
        }
        return out;
    }

    private List<Map<String, Object>> buildYearlyRevenueSeries(LocalDate from, LocalDate to, List<Transaction> txs) {
        int startYear = from.getYear();
        int endYear = to.getYear();

        Map<Integer, List<Transaction>> byYear = txs.stream()
                .collect(Collectors.groupingBy(t -> t.getCreationDate().getYear()));

        List<Map<String, Object>> out = new ArrayList<>();
        for (int y = startYear; y <= endYear; y++) {
            List<Transaction> yearTx = byYear.getOrDefault(y, Collections.emptyList());
            long revenue = yearTx.stream().mapToLong(this::revenueOf).sum();
            long completedCount = yearTx.stream().filter(t -> t.getStatus() == Status.TRANSACTION_SUCCESS).count();

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("year", y);
            m.put("revenue", revenue);
            m.put("completedCount", completedCount);
            m.put("txCount", (long) yearTx.size());
            out.add(m);
        }
        return out;
    }


}
