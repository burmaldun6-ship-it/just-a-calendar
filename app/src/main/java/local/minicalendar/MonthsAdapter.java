package local.minicalendar;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.YearMonth;

/** RecyclerView reuses page views; a new date only rebinds affected months. */
final class MonthsAdapter extends RecyclerView.Adapter<MonthsAdapter.Holder> {
    private final int rowHeight, foreground, accent, onAccent;
    private LocalDate today;
    MonthsAdapter(int rowHeight, int foreground, int accent, int onAccent, LocalDate today) {
        this.rowHeight = rowHeight; this.foreground = foreground; this.accent = accent; this.onAccent = onAccent; this.today = today;
        setHasStableIds(true);
    }
    @Override public int getItemCount() { return CalendarMath.MONTH_COUNT; }
    @Override public long getItemId(int position) { return position; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        MonthGridView grid = new MonthGridView(context, rowHeight, foreground, accent, onAccent);
        grid.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
        return new Holder(grid);
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) { holder.grid.bindMonth(CalendarMath.fromIndex(position), today); }
    void setToday(LocalDate value) {
        if (value.equals(today)) return;
        YearMonth oldMonth = YearMonth.from(today), newMonth = YearMonth.from(value); today = value;
        notifyIfSupported(oldMonth);
        if (!newMonth.equals(oldMonth)) notifyIfSupported(newMonth);
    }
    private void notifyIfSupported(YearMonth month) {
        if (month.getYear() >= CalendarMath.MIN_YEAR && month.getYear() <= CalendarMath.MAX_YEAR) notifyItemChanged(CalendarMath.toIndex(month));
    }
    static final class Holder extends RecyclerView.ViewHolder {
        final MonthGridView grid;
        Holder(MonthGridView grid) { super(grid); this.grid = grid; }
    }
}
