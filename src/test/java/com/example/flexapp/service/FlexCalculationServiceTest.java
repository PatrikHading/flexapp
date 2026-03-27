package com.example.flexapp.service;

import com.example.flexapp.entity.TimeEntry;
import com.example.flexapp.entity.WorkSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class FlexCalculationServiceTest {

    private FlexCalculationService service;
    private WorkSchedule schedule;

    @BeforeEach
    void setUp() {
        service = new FlexCalculationService();

        schedule = new WorkSchedule();
        schedule.setPlannedStartTime(LocalTime.of(8, 0));
        schedule.setPlannedEndTime(LocalTime.of(16, 0));
        schedule.setPaidLunchMinutes(30);
        // planned worked minutes = 8h - 30min = 450 minutes
    }

    // --- calculateLunchMinutes ---

    @Test
    void lunchMinutes_returnsZero_whenNoLunch() {
        TimeEntry entry = makeEntry("08:00", null, null, "16:00");
        assertEquals(0, service.calculateLunchMinutes(entry));
    }

    @Test
    void lunchMinutes_calculatesCorrectly() {
        TimeEntry entry = makeEntry("08:00", "12:00", "12:30", "16:00");
        assertEquals(30, service.calculateLunchMinutes(entry));
    }

    // --- calculateExtraLunchMinutes ---

    @Test
    void extraLunchMinutes_returnsZero_whenLunchWithinPaid() {
        assertEquals(0, service.calculateExtraLunchMinutes(30, 30));
    }

    @Test
    void extraLunchMinutes_returnsZero_whenLunchShorterThanPaid() {
        assertEquals(0, service.calculateExtraLunchMinutes(20, 30));
    }

    @Test
    void extraLunchMinutes_returnsOverage_whenLunchExceedsPaid() {
        assertEquals(15, service.calculateExtraLunchMinutes(45, 30));
    }

    // --- calculateWorkedMinutes ---

    @Test
    void workedMinutes_subtractsLunch() {
        // 8h total - 30min lunch = 450 min
        assertEquals(450, service.calculateWorkedMinutes(
                dateTime("08:00"), dateTime("16:00"), 30));
    }

    @Test
    void workedMinutes_noLunch() {
        assertEquals(480, service.calculateWorkedMinutes(
                dateTime("08:00"), dateTime("16:00"), 0));
    }

    // --- calculateFlexMinutes ---

    @Test
    void flexMinutes_zero_whenWorkedExactlyPlanned() {
        // 08:00-16:00 with 30min lunch = exactly 450 worked = 0 flex
        TimeEntry entry = makeEntry("08:00", "12:00", "12:30", "16:00");
        assertEquals(0, service.calculateFlexMinutes(schedule, entry));
    }

    @Test
    void flexMinutes_positive_whenWorkedMore() {
        // stays 30 min late → +30 flex
        TimeEntry entry = makeEntry("08:00", "12:00", "12:30", "16:30");
        assertEquals(30, service.calculateFlexMinutes(schedule, entry));
    }

    @Test
    void flexMinutes_negative_whenWorkedLess() {
        // leaves 30 min early → -30 flex
        TimeEntry entry = makeEntry("08:00", "12:00", "12:30", "15:30");
        assertEquals(-30, service.calculateFlexMinutes(schedule, entry));
    }

    @Test
    void flexMinutes_negative_whenLongLunch() {
        // 60min lunch instead of 30min paid → -30 flex
        TimeEntry entry = makeEntry("08:00", "12:00", "13:00", "16:00");
        assertEquals(-30, service.calculateFlexMinutes(schedule, entry));
    }

    @Test
    void flexMinutes_positive_whenArrivesEarly() {
        // arrives 30 min early → +30 flex
        TimeEntry entry = makeEntry("07:30", "12:00", "12:30", "16:00");
        assertEquals(30, service.calculateFlexMinutes(schedule, entry));
    }

    @Test
    void flexMinutes_combinedEarlyAndLate() {
        // arrives 15 min early, leaves 15 min late → +30 flex
        TimeEntry entry = makeEntry("07:45", "12:00", "12:30", "16:15");
        assertEquals(30, service.calculateFlexMinutes(schedule, entry));
    }

    // --- Helpers ---

    private TimeEntry makeEntry(String checkIn, String lunchOut, String lunchIn, String checkOut) {
        TimeEntry entry = new TimeEntry();
        LocalDate date = LocalDate.of(2025, 1, 1);
        entry.setWorkDate(date);
        entry.setCheckInTime(LocalDateTime.of(date, LocalTime.parse(checkIn)));
        entry.setCheckOutTime(LocalDateTime.of(date, LocalTime.parse(checkOut)));
        if (lunchOut != null)
            entry.setLunchOutTime(LocalDateTime.of(date, LocalTime.parse(lunchOut)));
        if (lunchIn != null)
            entry.setLunchInTime(LocalDateTime.of(date, LocalTime.parse(lunchIn)));
        return entry;
    }

    private LocalDateTime dateTime(String time) {
        return LocalDateTime.of(LocalDate.of(2025, 1, 1), LocalTime.parse(time));
    }
}