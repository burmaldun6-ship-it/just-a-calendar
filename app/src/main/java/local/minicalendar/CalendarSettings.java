package local.minicalendar;

import android.content.SharedPreferences;

/** Only three local preferences; malformed values fall back safely. */
final class CalendarSettings {
    private final SharedPreferences preferences;
    CalendarSettings(SharedPreferences preferences) { this.preferences = preferences; }
    boolean isDark(boolean fallback) { return readBoolean("dark", fallback); }
    void setDark(boolean value) { preferences.edit().putBoolean("dark", value).apply(); }
    boolean autoUpdate() { return readBoolean("auto_update", true); }
    void setAutoUpdate(boolean value) { preferences.edit().putBoolean("auto_update", value).apply(); }
    int intervalSeconds() {
        try { return CalendarMath.intervalSeconds(preferences.getInt("interval_seconds", CalendarMath.DEFAULT_INTERVAL_SECONDS)); }
        catch (ClassCastException wrongType) { return CalendarMath.DEFAULT_INTERVAL_SECONDS; }
    }
    void setIntervalSeconds(int value) { preferences.edit().putInt("interval_seconds", CalendarMath.intervalSeconds(value)).apply(); }
    private boolean readBoolean(String key, boolean fallback) {
        try { return preferences.getBoolean(key, fallback); }
        catch (ClassCastException wrongType) { return fallback; }
    }
}
