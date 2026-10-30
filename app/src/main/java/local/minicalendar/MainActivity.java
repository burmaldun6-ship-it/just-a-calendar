package local.minicalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final String[] MONTHS = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private static final String[] DAYS = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};
    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("EEEE, d MMMM uuuu", Locale.forLanguageTag("ru"));
    private CalendarSettings settings;
    private boolean dark, resumed;
    private int background, foreground, muted, accent, onAccent;
    private LocalDate today;
    private YearMonth shown;
    private TextView heading, todayDate;
    private LinearLayout todayButton;
    private ViewPager2 pager;
    private MonthsAdapter adapter;
    private AlertDialog settingsDialog;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!resumed || !settings.autoUpdate()) return;
            refreshToday(); scheduleTick();
        }
    };
    private final ViewPager2.OnPageChangeCallback pageCallback = new ViewPager2.OnPageChangeCallback() {
        @Override public void onPageSelected(int position) { shown = CalendarMath.fromIndex(position); updateHeading(); }
    };

    @Override public void onCreate(Bundle state) {
        settings = new CalendarSettings(getPreferences(MODE_PRIVATE));
        boolean systemDark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        dark = settings.isDark(systemDark);
        setTheme(dark ? R.style.AppTheme_Dark : R.style.AppTheme);
        super.onCreate(state);
        today = LocalDate.now();
        YearMonth fallback = CalendarMath.restoreMonth(today.getYear(), today.getMonthValue(), YearMonth.of(1970, 1));
        shown = state == null ? fallback : CalendarMath.restoreMonth(state.getInt("year", fallback.getYear()), state.getInt("month", fallback.getMonthValue()), fallback);
        buildUi();
        if (state != null && state.getBoolean("settings_open", false)) pager.post(() -> { if (!isFinishing() && !isDestroyed()) showSettings(); });
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        YearMonth selected = pager == null ? shown : CalendarMath.fromIndex(pager.getCurrentItem());
        out.putInt("year", selected.getYear()); out.putInt("month", selected.getMonthValue());
        out.putBoolean("settings_open", settingsDialog != null && settingsDialog.isShowing());
        super.onSaveInstanceState(out);
    }
    @Override protected void onResume() { super.onResume(); resumed = true; refreshToday(); scheduleTick(); }
    @Override protected void onPause() {
        resumed = false; handler.removeCallbacks(tick); stopAndSnap(); super.onPause();
    }
    @Override protected void onDestroy() {
        handler.removeCallbacks(tick);
        if (pager != null) pager.unregisterOnPageChangeCallback(pageCallback);
        if (settingsDialog != null) settingsDialog.dismiss();
        super.onDestroy();
    }
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private LinearLayout.LayoutParams lp(int width, int height) { return new LinearLayout.LayoutParams(width, height); }
    private void gap(LinearLayout parent, int height) { parent.addView(new View(this), lp(1, dp(height))); }
    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this); view.setText(value); view.setTextSize(size); view.setTextColor(color);
        view.setGravity(Gravity.CENTER); view.setIncludeFontPadding(false); return view;
    }
    private int textHeight(int size) {
        TextView measure = text("", size, foreground);
        android.graphics.Paint.FontMetricsInt metrics = measure.getPaint().getFontMetricsInt(); return metrics.descent - metrics.ascent;
    }
    private RippleDrawable ripple() {
        GradientDrawable mask = new GradientDrawable(); mask.setColor(Color.WHITE); mask.setCornerRadius(dp(8));
        return new RippleDrawable(ColorStateList.valueOf(dark ? 0x30FFFFFF : 0x18000000), null, mask);
    }
    private void buildUi() {
        background = Color.parseColor(dark ? "#191919" : "#FFFFFF");
        foreground = Color.parseColor(dark ? "#EEEEEE" : "#292929");
        muted = Color.parseColor(dark ? "#AAAAAA" : "#696969");
        accent = Color.parseColor(dark ? "#9BC8FF" : "#1769BD");
        onAccent = Color.parseColor(dark ? "#102640" : "#FFFFFF");
        getWindow().setBackgroundDrawable(new ColorDrawable(background));
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (Build.VERSION.SDK_INT < 35) { getWindow().setStatusBarColor(Color.TRANSPARENT); getWindow().setNavigationBarColor(Color.TRANSPARENT); }
        if (Build.VERSION.SDK_INT >= 29) getWindow().setNavigationBarContrastEnforced(false);
        FrameLayout root = new FrameLayout(this); root.setBackgroundColor(background); root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            if (view.getPaddingLeft() != bars.left || view.getPaddingTop() != bars.top || view.getPaddingRight() != bars.right || view.getPaddingBottom() != bars.bottom) view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return windowInsets;
        });
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setVerticalScrollBarEnabled(false);
        root.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        FrameLayout container = new FrameLayout(this); scroll.addView(container, new ScrollView.LayoutParams(-1, -1));
        LinearLayout content = new LinearLayout(this) {
            @Override protected void onMeasure(int widthSpec, int heightSpec) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(widthSpec), dp(432)), MeasureSpec.EXACTLY), heightSpec);
            }
        };
        content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(16), dp(16), dp(16), dp(24));
        container.addView(content, new FrameLayout.LayoutParams(-1, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL));
        FrameLayout header = new FrameLayout(this); header.setMinimumHeight(dp(48));
        heading = text("", 24, foreground); heading.setMaxLines(1); heading.setHorizontallyScrolling(false); heading.setAutoSizeTextTypeUniformWithConfiguration(12, 24, 1, android.util.TypedValue.COMPLEX_UNIT_SP); heading.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        ViewCompat.setAccessibilityHeading(heading, true); heading.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        FrameLayout.LayoutParams headingParams = new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER); headingParams.leftMargin = dp(48); headingParams.rightMargin = dp(48);
        header.addView(heading, headingParams);
        ImageButton gear = new ImageButton(this); gear.setImageResource(R.drawable.ic_settings); gear.setImageTintList(ColorStateList.valueOf(muted));
        gear.setPadding(dp(12), dp(12), dp(12), dp(12)); gear.setBackground(ripple()); gear.setContentDescription(getString(R.string.settings)); gear.setTooltipText(getString(R.string.settings)); gear.setOnClickListener(v -> showSettings());
        header.addView(gear, new FrameLayout.LayoutParams(dp(48), dp(48), Gravity.END | Gravity.CENTER_VERTICAL));
        content.addView(header, lp(-1, Math.max(dp(48), textHeight(24) + dp(16)))); gap(content, 24);
        int weekdaysHeight = Math.max(dp(32), textHeight(14) + dp(8));
        LinearLayout weekdays = new LinearLayout(this); weekdays.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        for (String name : DAYS) weekdays.addView(text(name, 14, muted), new LinearLayout.LayoutParams(0, weekdaysHeight, 1));
        content.addView(weekdays, lp(-1, weekdaysHeight)); gap(content, 8);
        int rowHeight = Math.max(dp(48), textHeight(17) + dp(12));
        pager = new ViewPager2(this); pager.setId(R.id.month_pager); pager.setSaveEnabled(false); pager.setSaveFromParentEnabled(false); pager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        adapter = new MonthsAdapter(rowHeight, foreground, accent, onAccent, today); pager.setAdapter(adapter);
        pager.setCurrentItem(CalendarMath.toIndex(shown), false); pager.registerOnPageChangeCallback(pageCallback);
        content.addView(pager, lp(-1, rowHeight * 6 + dp(4) * 5)); gap(content, 24);
        todayButton = new LinearLayout(this); todayButton.setOrientation(LinearLayout.VERTICAL); todayButton.setGravity(Gravity.CENTER);
        todayButton.setPadding(dp(16), dp(12), dp(16), dp(12)); todayButton.setBackground(ripple()); todayButton.setFocusable(true); todayButton.setClickable(true);
        TextView todayTitle = text(getString(R.string.today_title), 22, foreground); todayTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        todayButton.addView(todayTitle, lp(-2, -2)); gap(todayButton, 8);
        todayDate = text("", 20, muted); todayButton.addView(todayDate, lp(-2, -2)); todayButton.setOnClickListener(v -> jumpToToday());
        todayButton.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override public void onInitializeAccessibilityNodeInfo(View host, android.view.accessibility.AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info); info.setClassName("android.widget.Button");
            }
        });
        content.addView(todayButton, lp(-1, -2));
        setContentView(root);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), root);
        controller.setAppearanceLightStatusBars(!dark); controller.setAppearanceLightNavigationBars(!dark);
        ViewCompat.requestApplyInsets(root); updateHeading(); updateTodayLabel();
    }
    private void updateHeading() { if (heading != null) heading.setText(getString(R.string.month_heading, MONTHS[shown.getMonthValue() - 1], shown.getYear())); }
    private void updateTodayLabel() {
        todayDate.setText(CalendarMath.formatToday(today)); todayButton.setContentDescription(getString(R.string.today_description, DATE_LABEL.format(today)));
    }
    private void refreshToday() {
        LocalDate actual = LocalDate.now();
        if (!actual.equals(today)) { today = actual; adapter.setToday(actual); updateTodayLabel(); }
    }
    private void scheduleTick() {
        handler.removeCallbacks(tick);
        if (resumed && settings.autoUpdate()) handler.postDelayed(tick, settings.intervalSeconds() * 1000L);
    }
    private void stopAndSnap() {
        if (pager == null) return;
        pager.setCurrentItem(pager.getCurrentItem(), false);
    }
    private void jumpToToday() {
        refreshToday();
        YearMonth target = CalendarMath.restoreMonth(today.getYear(), today.getMonthValue(), shown);
        navigateTo(CalendarMath.toIndex(target));
    }
    private void navigateTo(int position) {
        pager.setCurrentItem(Math.max(0, Math.min(CalendarMath.MONTH_COUNT - 1, position)), false);
        shown = CalendarMath.fromIndex(pager.getCurrentItem()); updateHeading();
    }
    @Override public boolean onKeyDown(int key, KeyEvent event) {
        if (settingsDialog == null && key == KeyEvent.KEYCODE_DPAD_LEFT) { navigateTo(pager.getCurrentItem() - 1); return true; }
        if (settingsDialog == null && key == KeyEvent.KEYCODE_DPAD_RIGHT) { navigateTo(pager.getCurrentItem() + 1); return true; }
        return super.onKeyDown(key, event);
    }
    private void showSettings() {
        if (settingsDialog != null) return;
        LinearLayout panel = new LinearLayout(this); panel.setOrientation(LinearLayout.VERTICAL); panel.setPadding(dp(24), dp(8), dp(24), dp(8));
        Switch theme = new Switch(this); theme.setText(R.string.dark_theme); theme.setTextSize(16); theme.setTextColor(foreground); theme.setMinHeight(dp(48)); theme.setChecked(dark);
        panel.addView(theme, lp(-1, -2)); gap(panel, 8);
        Switch auto = new Switch(this); auto.setText(R.string.auto_update); auto.setTextSize(16); auto.setTextColor(foreground); auto.setMinHeight(dp(48)); auto.setChecked(settings.autoUpdate());
        panel.addView(auto, lp(-1, -2)); gap(panel, 16);
        TextView intervalLabel = text(getString(R.string.interval_value, settings.intervalSeconds()), 16, foreground); intervalLabel.setGravity(Gravity.START); panel.addView(intervalLabel, lp(-1, -2));
        SeekBar interval = new SeekBar(this); interval.setMin(1); interval.setMax(60); interval.setProgress(settings.intervalSeconds()); interval.setEnabled(settings.autoUpdate()); interval.setContentDescription(getString(R.string.interval_description));
        panel.addView(interval, lp(-1, dp(48)));
        TextView hint = text(getString(settings.autoUpdate() ? R.string.auto_on_hint : R.string.auto_off_hint), 14, muted); hint.setGravity(Gravity.START); panel.addView(hint, lp(-1, -2));
        interval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int value, boolean fromUser) {
                intervalLabel.setText(getString(R.string.interval_value, CalendarMath.intervalSeconds(value)));
                if (fromUser) { settings.setIntervalSeconds(value); scheduleTick(); }
            }
            @Override public void onStartTrackingTouch(SeekBar bar) { }
            @Override public void onStopTrackingTouch(SeekBar bar) { }
        });
        auto.setOnCheckedChangeListener((button, enabled) -> {
            settings.setAutoUpdate(enabled); interval.setEnabled(enabled); hint.setText(enabled ? R.string.auto_on_hint : R.string.auto_off_hint); scheduleTick();
        });
        theme.setOnCheckedChangeListener((button, enabled) -> { if (enabled != dark) { settings.setDark(enabled); recreate(); } });
        ScrollView settingsScroll = new ScrollView(this); settingsScroll.addView(panel, new ScrollView.LayoutParams(-1, -2));
        settingsDialog = new AlertDialog.Builder(this).setTitle(R.string.settings).setView(settingsScroll).setPositiveButton(R.string.done, (dialog, which) -> { }).create();
        settingsDialog.setOnDismissListener(dialog -> { settingsDialog = null; scheduleTick(); }); settingsDialog.show();
    }
}
