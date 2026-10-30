package local.minicalendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Pure calendar logic: no Android dependencies, so it can be tested on the JVM. */
final class CalendarMath {
    static final int MIN_YEAR = 1;
    static final int MAX_YEAR = 9999;
    static final int MONTH_COUNT = MAX_YEAR * 12;
    static final int CELL_COUNT = 42;
    static final int DEFAULT_INTERVAL_SECONDS = 5;
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("d.MM:yy", Locale.ROOT);

    private CalendarMath() { }

    static YearMonth restoreMonth(int year, int month, YearMonth fallback) {
        if (year < MIN_YEAR || year > MAX_YEAR || month < 1 || month > 12) {
            return YearMonth.of(Math.max(MIN_YEAR, Math.min(MAX_YEAR, fallback.getYear())), fallback.getMonthValue());
        }
        return YearMonth.of(year, month);
    }
    static int toIndex(YearMonth month) {
        if (month.getYear() < MIN_YEAR || month.getYear() > MAX_YEAR) throw new IllegalArgumentException("Month outside supported range");
        return (month.getYear() - 1) * 12 + month.getMonthValue() - 1;
    }
    static YearMonth fromIndex(int index) {
        int safe = Math.max(0, Math.min(MONTH_COUNT - 1, index));
        return YearMonth.of(safe / 12 + 1, safe % 12 + 1);
    }
    static int cellDay(YearMonth month, int cell) {
        if (cell < 0 || cell >= CELL_COUNT) return 0;
        int day = cell - (month.atDay(1).getDayOfWeek().getValue() - 1) + 1;
        return day >= 1 && day <= month.lengthOfMonth() ? day : 0;
    }
    static int intervalSeconds(int value) { return Math.max(1, Math.min(60, value)); }
    static String formatToday(LocalDate date) { return SHORT_DATE.format(date); }
}
