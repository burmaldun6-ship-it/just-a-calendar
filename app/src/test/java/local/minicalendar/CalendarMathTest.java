package local.minicalendar;

import org.junit.Test;
import java.time.LocalDate;
import java.time.YearMonth;
import static org.junit.Assert.*;

public final class CalendarMathTest {
    @Test public void everySupportedMonthRoundTrips() {
        for (int i = 0; i < CalendarMath.MONTH_COUNT; i++) assertEquals(i, CalendarMath.toIndex(CalendarMath.fromIndex(i)));
    }
    @Test public void restoredStateIsValidated() {
        YearMonth fallback = YearMonth.of(2026, 10);
        assertEquals(fallback, CalendarMath.restoreMonth(0, 1, fallback));
        assertEquals(fallback, CalendarMath.restoreMonth(10000, 1, fallback));
        assertEquals(fallback, CalendarMath.restoreMonth(2026, 0, fallback));
        assertEquals(fallback, CalendarMath.restoreMonth(2026, 13, fallback));
        assertEquals(YearMonth.of(1, 1), CalendarMath.restoreMonth(1, 1, fallback));
        assertEquals(YearMonth.of(9999, 12), CalendarMath.restoreMonth(9999, 12, fallback));
    }
    @Test public void navigationClampsAtBothEnds() {
        assertEquals(YearMonth.of(1, 1), CalendarMath.fromIndex(-1));
        assertEquals(YearMonth.of(9999, 12), CalendarMath.fromIndex(Integer.MAX_VALUE));
    }
    @Test public void dateFormatHasUnpaddedDayPaddedMonthAndShortYear() {
        assertEquals("1.01:26", CalendarMath.formatToday(LocalDate.of(2026, 1, 1)));
        assertEquals("30.10:26", CalendarMath.formatToday(LocalDate.of(2026, 10, 30)));
        assertEquals("31.12:99", CalendarMath.formatToday(LocalDate.of(1999, 12, 31)));
    }
    @Test public void intervalNeverCreatesBusyLoop() {
        assertEquals(1, CalendarMath.intervalSeconds(0));
        assertEquals(1, CalendarMath.intervalSeconds(Integer.MIN_VALUE));
        assertEquals(5, CalendarMath.intervalSeconds(5));
        assertEquals(60, CalendarMath.intervalSeconds(Integer.MAX_VALUE));
    }
    @Test public void leapYearAndCenturyRulesAreCorrect() {
        assertDaySequence(YearMonth.of(2028, 2), 29);
        assertDaySequence(YearMonth.of(2100, 2), 28);
        assertDaySequence(YearMonth.of(2000, 2), 29);
    }
    @Test public void allDatesFitInSixRowsAcrossSupportedRange() {
        for (int i = 0; i < CalendarMath.MONTH_COUNT; i++) {
            YearMonth month = CalendarMath.fromIndex(i);
            int last = 0;
            for (int cell = 0; cell < CalendarMath.CELL_COUNT; cell++) {
                int day = CalendarMath.cellDay(month, cell);
                if (day != 0) { assertEquals(last + 1, day); last = day; }
            }
            assertEquals(month.lengthOfMonth(), last);
        }
    }
    @Test public void invalidCellIndexesAreEmpty() {
        assertEquals(0, CalendarMath.cellDay(YearMonth.of(2026, 10), -1));
        assertEquals(0, CalendarMath.cellDay(YearMonth.of(2026, 10), 42));
    }
    private static void assertDaySequence(YearMonth month, int expected) {
        int count = 0;
        for (int cell = 0; cell < CalendarMath.CELL_COUNT; cell++) if (CalendarMath.cellDay(month, cell) != 0) count++;
        assertEquals(expected, count);
    }
}
