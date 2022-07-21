package utils;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;

/**
 * Кастомный класс для работы с датой и временем
 */
public class GDateTimeUtil {

    /**
     * Возвращает текущее время в секундах, прошедших с начала эпохи Unix
     */
    public static long timestampAsSeconds() {
        return Calendar.getInstance().toInstant().getLong(ChronoField.INSTANT_SECONDS);
    }

    /**
     * Возвращает переданное время в секундах, прошедших с начала эпохи Unix
     */
    public static long isoFormatTimeToSeconds(String dateTimeInIso) {
        return DateTimeFormatter.ISO_INSTANT.parse(dateTimeInIso).getLong(ChronoField.INSTANT_SECONDS);
    }
}
