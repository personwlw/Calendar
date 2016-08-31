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
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean startAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView calendarInfo1= (TextView) findViewById(R.id.tv_calendar_info1);
        final TextView calendarInfo2= (TextView) findViewById(R.id.tv_calendar_info2);
        final CalendarView calendarView1= (CalendarView) findViewById(R.id.calendar_view1);
        final CalendarView calendarView2= (CalendarView) findViewById(R.id.calendar_view2);
        calendarView1.addCalendarInfo(new CalendarDay(2016, 8, 1), "建军节");
        calendarView1.addCalendarInfo(new CalendarDay(2016, 8, 15), "中秋节");


        CalendarDay calendarDay = calendarView1.getCalendarDay();
        calendarDay.day=1;
        calendarInfo1.setText(calendarDay.toString());
        final Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendarView2.setCalendarDay(new CalendarDay(calendar.getTimeInMillis()));
        calendarView2.addCalendarInfo(2016, 8, 2, "建军节");
        calendarView2.addCalendarInfo(2016, 8, 5, "劳动节");
        calendarInfo2.setText(calendarView2.getCalendarDay().toString());
        calendar.add(Calendar.MONTH, -1);

        findViewById(R.id.btn_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, -1);
                calendarView1.setCalendarDay(new CalendarDay(calendar.getTimeInMillis()));
                calendarInfo1.setText(calendarView1.getCalendarDay().toString());
                calendar.add(Calendar.MONTH, 1);
                calendarView2.setCalendarDay(new CalendarDay(calendar.getTimeInMillis()));
                calendarInfo2.setText(calendarView2.getCalendarDay().toString());
                calendar.add(Calendar.MONTH, -1);
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                calendarView1.setCalendarDay(new CalendarDay(calendar.getTimeInMillis()));
                calendarInfo1.setText(calendarView1.getCalendarDay().toString());

                calendar.add(Calendar.MONTH, 1);
                calendarView2.setCalendarDay(new CalendarDay(calendar.getTimeInMillis()));
                calendarInfo2.setText(calendarView2.getCalendarDay().toString());
                calendar.add(Calendar.MONTH, -1);
            }
        });

        calendarView1.setOnCalendarItemClickListener(new CalendarView.OnCalendarItemClickListener() {
            @Override
            public void onItemClick(CalendarDay calendarDay1, CalendarDay calendarDay2, boolean cancle) {
                if(cancle){
                    calendarView1.clearCalendarInfo();
                    calendarView2.clearCalendarInfo();
                    calendarView2.clearSelectCalendar();
                    animatorCancel(calendarInfo2,calendarView2);
                } else if(null!=calendarDay1&&null != calendarDay2) {
                    calendarView2.setSelectCalendarDay(calendarDay1);
                    calendarView1.addCalendarInfo(calendarDay2, "离店");
                } else if(null!=calendarDay1){
                    startAnim=true;
                    calendarView2.setSelectCalendarDay(calendarDay1);
                    calendarView1.addCalendarInfo(calendarDay1, "入住");
                }
            }
        });
        calendarView2.setOnCalendarItemClickListener(new CalendarView.OnCalendarItemClickListener() {
            @Override
            public void onItemClick(CalendarDay calendarDay1, CalendarDay calendarDay2, boolean cancle) {
                if(cancle){
                    calendarView1.clearCalendarInfo();
                    calendarView2.clearCalendarInfo();
                    calendarView1.clearSelectCalendar();
                    animatorCancel(calendarInfo2,calendarView2);
                } else if(null!=calendarDay1&&null!=calendarDay2){
                    if(startAnim){
                        int height = calendarInfo2.getHeight();
                        calendarInfo2.animate().alpha(0f).translationY(-height);
                        calendarView2.animate().translationY(-height-calendarView2.getItemHeight());
                    }
                    calendarView1.setSelectCalendarDay(calendarDay2);
                    calendarView2.addCalendarInfo(calendarDay2, "离店");
                } else if(null!=calendarDay1){
                    calendarView1.setSelectCalendarDay(calendarDay1);
                    calendarView2.addCalendarInfo(calendarDay1, "入住");
                }
            }
        });
    }

    private void animatorCancel(View calendarInfo,View calendarView) {
        if(startAnim){
            startAnim=false;
            calendarInfo.animate().alpha(1f).translationY(0);
            calendarView.animate().translationY(0);
        }
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
