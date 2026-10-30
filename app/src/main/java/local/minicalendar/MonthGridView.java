package local.minicalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A reusable six-row page. Binding never removes or recreates its 42 cells. */
final class MonthGridView extends LinearLayout {
    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("EEEE, d MMMM uuuu", Locale.forLanguageTag("ru"));
    private final Cell[] cells = new Cell[CalendarMath.CELL_COUNT];
    private final int foreground, accent, onAccent;
    private final int preferredCircle;
    private final String todaySuffix;

    MonthGridView(Context context, int rowHeight, int foreground, int accent, int onAccent) {
        super(context);
        this.foreground = foreground; this.accent = accent; this.onAccent = onAccent;
        setOrientation(VERTICAL); setLayoutDirection(LAYOUT_DIRECTION_LTR);
        TextView measure = new TextView(context); measure.setTextSize(17);
        android.graphics.Paint.FontMetricsInt metrics = measure.getPaint().getFontMetricsInt();
        preferredCircle = Math.max(dp(40), metrics.descent - metrics.ascent + dp(8));
        todaySuffix = context.getString(R.string.current_date_suffix);
        for (int row = 0; row < 6; row++) {
            LinearLayout line = new LinearLayout(context);
            for (int col = 0; col < 7; col++) {
                Cell cell = new Cell(context); cells[row * 7 + col] = cell;
                line.addView(cell, new LinearLayout.LayoutParams(0, rowHeight, 1));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, rowHeight);
            if (row > 0) params.topMargin = dp(4);
            addView(line, params);
        }
    }
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    void bindMonth(YearMonth month, LocalDate today) {
        for (int i = 0; i < cells.length; i++) {
            int day = CalendarMath.cellDay(month, i); Cell cell = cells[i];
            if (day == 0) { cell.label.setVisibility(View.INVISIBLE); continue; }
            LocalDate date = month.atDay(day); boolean current = date.equals(today);
            cell.label.setVisibility(View.VISIBLE); cell.label.setText(Integer.toString(day));
            cell.label.setTextColor(current ? onAccent : foreground);
            cell.label.setTypeface(current ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            cell.circle.setColor(current ? accent : Color.TRANSPARENT);
            cell.label.setContentDescription(DATE_LABEL.format(date) + (current ? todaySuffix : ""));
        }
    }
    private final class Cell extends FrameLayout {
        final TextView label;
        final GradientDrawable circle = new GradientDrawable();
        Cell(Context context) {
            super(context);
            label = new TextView(context); label.setGravity(Gravity.CENTER); label.setIncludeFontPadding(false);
            label.setMaxLines(1); label.setHorizontallyScrolling(false); label.setTextSize(17); label.setAutoSizeTextTypeUniformWithConfiguration(12, 17, 1, TypedValue.COMPLEX_UNIT_SP);
            circle.setShape(GradientDrawable.OVAL); circle.setColor(Color.TRANSPARENT); label.setBackground(circle);
            addView(label, new FrameLayout.LayoutParams(dp(40), dp(40), Gravity.CENTER));
        }
        @Override protected void onSizeChanged(int w, int h, int oldW, int oldH) {
            super.onSizeChanged(w, h, oldW, oldH);
            int side = Math.max(1, Math.min(preferredCircle, Math.min(w - dp(4), h - dp(4))));
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) label.getLayoutParams();
            if (params.width != side || params.height != side) { params.width = side; params.height = side; label.setLayoutParams(params); }
        }
    }
}
