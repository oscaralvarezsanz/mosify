package com.mosify.domain.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public enum TaskFrequency {
    DAILY,
    WEEKLY,
    MONTHLY;

    public DateTimeRange calculateRange(LocalDateTime referenceDateTime) {
        LocalDate today = referenceDateTime.toLocalDate();
        return switch (this) {
            case DAILY -> new DateTimeRange(
                    today.atStartOfDay(),
                    today.atTime(LocalTime.MAX)
            );
            case WEEKLY -> {
                LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                yield new DateTimeRange(
                        monday.atStartOfDay(),
                        sunday.atTime(LocalTime.MAX)
                );
            }
            case MONTHLY -> {
                LocalDate firstDay = today.withDayOfMonth(1);
                LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());
                yield new DateTimeRange(
                        firstDay.atStartOfDay(),
                        lastDay.atTime(LocalTime.MAX)
                );
            }
        };
    }

    public record DateTimeRange(LocalDateTime start, LocalDateTime end) {}
}
