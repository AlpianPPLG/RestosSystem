package com.restos.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.sql.Timestamp;

/**
 * Utility class for date and time formatting and manipulation
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class DateTimeUtil {

    // Common date/time formatters
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter TIME_FORMAT_FULL = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATETIME_FORMAT_FULL = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Indonesian day names
    private static final String[] DAY_NAMES = {
        "Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"
    };

    // Indonesian month names
    private static final String[] MONTH_NAMES = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    /**
     * Format date to standard format (dd/MM/yyyy)
     * @param date LocalDate to format
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : "";
    }

    /**
     * Format time to standard format (HH:mm)
     * @param time LocalTime to format
     * @return Formatted time string
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMAT) : "";
    }

    /**
     * Format datetime to standard format (dd/MM/yyyy HH:mm)
     * @param dateTime LocalDateTime to format
     * @return Formatted datetime string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : "";
    }

    /**
     * Format datetime to full format (dd/MM/yyyy HH:mm:ss)
     * @param dateTime LocalDateTime to format
     * @return Formatted datetime string
     */
    public static String formatDateTimeFull(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT_FULL) : "";
    }

    /**
     * Convert Timestamp to LocalDateTime
     * @param timestamp SQL Timestamp
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * Convert LocalDateTime to Timestamp
     * @param dateTime LocalDateTime
     * @return SQL Timestamp
     */
    public static Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }

    /**
     * Get current date
     * @return Current LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Get current datetime
     * @return Current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Get Indonesian day name
     * @param date LocalDate
     * @return Indonesian day name
     */
    public static String getDayName(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        // Java DayOfWeek: Monday = 1, Sunday = 7
        // Adjust for our array: Sunday = 0
        int index = dayOfWeek == 7 ? 0 : dayOfWeek;
        return DAY_NAMES[index];
    }

    /**
     * Get Indonesian month name
     * @param month Month number (1-12)
     * @return Indonesian month name
     */
    public static String getMonthName(int month) {
        if (month < 1 || month > 12) {
            return "";
        }
        return MONTH_NAMES[month - 1];
    }

    /**
     * Format date in Indonesian long format
     * Example: Senin, 17 Januari 2026
     * @param date LocalDate to format
     * @return Indonesian formatted date
     */
    public static String formatIndonesian(LocalDate date) {
        if (date == null) return "";
        
        String dayName = getDayName(date);
        String monthName = getMonthName(date.getMonthValue());
        return String.format("%s, %d %s %d", 
            dayName, date.getDayOfMonth(), monthName, date.getYear());
    }

    /**
     * Format datetime in Indonesian format
     * Example: Senin, 17 Januari 2026 14:30
     * @param dateTime LocalDateTime to format
     * @return Indonesian formatted datetime
     */
    public static String formatIndonesian(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        
        return formatIndonesian(dateTime.toLocalDate()) + " " + formatTime(dateTime.toLocalTime());
    }

    /**
     * Get relative time string (e.g., "5 menit lalu", "2 jam lalu")
     * @param dateTime DateTime to compare
     * @return Relative time string in Indonesian
     */
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();
        
        if (seconds < 0) {
            // Future time
            seconds = Math.abs(seconds);
            if (seconds < 60) {
                return "dalam beberapa detik";
            } else if (seconds < 3600) {
                return "dalam " + (seconds / 60) + " menit";
            } else if (seconds < 86400) {
                return "dalam " + (seconds / 3600) + " jam";
            } else {
                return "dalam " + (seconds / 86400) + " hari";
            }
        }

        if (seconds < 60) {
            return "baru saja";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " menit lalu";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " jam lalu";
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return days + " hari lalu";
        } else {
            return formatDate(dateTime.toLocalDate());
        }
    }

    /**
     * Get elapsed time string (e.g., "05:30" for 5 minutes 30 seconds)
     * @param startTime Start time
     * @return Elapsed time string in mm:ss format
     */
    public static String getElapsedTime(LocalDateTime startTime) {
        if (startTime == null) return "00:00";

        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long totalSeconds = duration.getSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Get elapsed time in minutes
     * @param startTime Start time
     * @return Elapsed minutes
     */
    public static long getElapsedMinutes(LocalDateTime startTime) {
        if (startTime == null) return 0;
        return ChronoUnit.MINUTES.between(startTime, LocalDateTime.now());
    }

    /**
     * Check if datetime is today
     * @param dateTime DateTime to check
     * @return true if today, false otherwise
     */
    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) return false;
        return dateTime.toLocalDate().equals(LocalDate.now());
    }

    /**
     * Get start of day for a given date
     * @param date LocalDate
     * @return LocalDateTime at 00:00:00
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Get end of day for a given date
     * @param date LocalDate
     * @return LocalDateTime at 23:59:59
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
}
