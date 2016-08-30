package quant.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.quant.calendar.CalendarDay;
import com.quant.calendar.CalendarView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView calendarInfo= (TextView) findViewById(R.id.tv_calendar_info);
        final CalendarView calendarView= (CalendarView) findViewById(R.id.calendar_view);
        calendarView.addCalendarInfo(2016,8,24,"入住");
        calendarView.addCalendarInfo(2016,8,28,"离店");
        calendarView.addTodayInfo("今天");
        final CalendarDay calendarDay = calendarView.getCalendarDay();
        final Calendar calendar=Calendar.getInstance();
        calendar.set(calendarDay.year, calendarDay.month,1);
        calendarInfo.setText(calendarView.getCalendarDay().toString());
        findViewById(R.id.btn_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, -1);
                calendarView.setCalendarDay(new CalendarDay(calendar.getTimeInMillis()));
                calendarInfo.setText(calendarView.getCalendarDay().toString());
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                calendarView.setCalendarDay(new CalendarDay(calendar.getTimeInMillis()));
                calendarInfo.setText(calendarView.getCalendarDay().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
