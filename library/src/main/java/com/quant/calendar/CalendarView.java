package com.quant.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by cz on 8/29/16.
 */
public class CalendarView extends View {
    private static final String TAG = "CalendarView";
    private static final int WEEK_DAY_COUNT=7;
    public static final int LEFT=0x01;
    public static final int TOP=0x02;
    public static final int RIGHT=0x04;
    public static final int BOTTOM=0x08;

    @IntDef(value={LEFT,TOP,RIGHT,BOTTOM})
    public @interface Gravity{
    }

    private final int[] WEEK_DAY=new int[]{7,1,2,3,4,5,6};
    private final GregorianCalendar calendar;
    private final Paint dividePaint;
    private final TextPaint textPaint;
    private CalendarDay calendarItem;
    private int divideGravity;
    private float divideSize;
    private int itemHeight;
    private final Rect textRect;

    public CalendarView(Context context) {
        this(context, null, 0);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textRect = new Rect();
        dividePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        calendarItem=new CalendarDay(System.currentTimeMillis());
        calendar=new GregorianCalendar();
        calendar.setFirstDayOfWeek(calendar.MONDAY);
        calendar.set(calendarItem.year,calendarItem.month,0);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        setItemSelectDrawable(a.getDrawable(R.styleable.CalendarView_cv_itemSelectDrawable));
        setItemHeight((int) a.getDimension(R.styleable.CalendarView_cv_itemHeight, 0));
        setItemTextColor(a.getColor(R.styleable.CalendarView_cv_itemTextColor, Color.BLACK));
        setItemTextSize(a.getDimensionPixelSize(R.styleable.CalendarView_cv_itemTextSize, 0));
        setItemWeekColor(a.getColor(R.styleable.CalendarView_cv_itemWeekColor, Color.DKGRAY));
        setItemDivideColor(a.getColor(R.styleable.CalendarView_cv_itemDivideColor, Color.DKGRAY));
        setItemDivideSize(a.getDimension(R.styleable.CalendarView_cv_itemDivideSize, 0));
        setDivideGravity(a.getInt(R.styleable.CalendarView_cv_divideGravity, LEFT));
        a.recycle();

        GregorianCalendar gc = new GregorianCalendar();
        System.out.println( gc.getActualMinimum(gc.DAY_OF_WEEK) );
        gc.setFirstDayOfWeek(gc.MONDAY);
        gc.set(gc.YEAR, 2016);
        gc.set(gc.MONTH,7-1);
        gc.set(gc.DATE, 1);
        int max = gc.getActualMaximum(gc.DAY_OF_MONTH);  //本月最大日

        System.out.println( max );
        for(int i=0; i<max; i++) {
            Log.e(TAG, gc.get(gc.DAY_OF_MONTH) + "号, 星期" + WEEK_DAY[gc.get(Calendar.DAY_OF_WEEK)-1] );
            gc.add(gc.DAY_OF_MONTH, 1);
        }
    }

    static String week(int i) {
        if(i==GregorianCalendar.MONDAY) return "一";
        if(i==GregorianCalendar.TUESDAY) return "二";
        if(i==GregorianCalendar.WEDNESDAY) return "三";
        if(i==GregorianCalendar.THURSDAY) return "四";
        if(i==GregorianCalendar.FRIDAY) return "五";
        if(i==GregorianCalendar.SATURDAY) return "六";
        if(i==GregorianCalendar.SUNDAY) return "日";
        throw new RuntimeException("非法的星期数:" + i);
    }

    public void setItemDivideSize(float size) {
        this.dividePaint.setStrokeWidth(1);
        invalidate();
    }

    public void setCalendarDay(CalendarDay day){
        calendarItem=day;
        calendar.set(day.year,day.month,0);
        invalidate();
    }

    public void setItemSelectDrawable(Drawable drawable) {

    }

    public void setItemHeight(int height) {
        this.itemHeight=height;
        requestLayout();
    }

    public void setItemTextColor(int color) {
        this.textPaint.setColor(color);
        invalidate();
    }

    public void setItemTextSize(int textSize) {
        this.textPaint.setTextSize(textSize);
        invalidate();
    }

    public void setItemWeekColor(int color) {

    }

    public void setItemDivideColor(int color) {
        dividePaint.setColor(color);
    }

    public void setDivideGravityInner(int gravity) {

    }

    public void setDivideGravity(int gravity) {
        this.divideGravity=gravity;
        invalidate();
    }

    @Override
    public void invalidate() {
        if(hasWindowFocus()) super.invalidate();
    }

    @Override
    public void postInvalidate() {
        if(hasWindowFocus()) super.postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int column = getCalendarColumn();
        setMeasuredDimension(getMeasuredWidth(), resolveSize(itemHeight*column, heightMeasureSpec));
    }

    private int getCalendarColumn() {
        int totalDay= calendar.get(Calendar.DAY_OF_MONTH)+ calendar.get(Calendar.DAY_OF_WEEK);
        return (0==totalDay%WEEK_DAY_COUNT)?totalDay/WEEK_DAY_COUNT:totalDay/WEEK_DAY_COUNT+1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int column = getCalendarColumn();
        drawCalendarText(canvas,column);

        drawDivide(canvas,column);
    }

    private void drawCalendarText(Canvas canvas, int totalColumn) {
        int startDay = WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1];
        int currentDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH, -1);
        int previousDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH, 1);

        Log.e(TAG, calendar.get(Calendar.MONTH)+" cur:" + currentDays + " previous:" + previousDays);

        int itemWidth=getWidth()/WEEK_DAY_COUNT;
        int total=WEEK_DAY_COUNT*totalColumn;
        canvas.drawRect(new Rect(0, 0, itemWidth, itemHeight), dividePaint);

        for(int i=0;i<total;i++){
            String text;
            if(i<startDay){
                //previous month
                text = String.valueOf(previousDays-startDay+i);
            } else if(i>=startDay&&i<startDay+currentDays){
                //current days
                text = String.valueOf(i-startDay+1);
            } else {
                //next days
                text = String.valueOf(i-startDay-currentDays);
            }

            int column=i/WEEK_DAY_COUNT;
            int row=i%WEEK_DAY_COUNT;
            int startX=row*itemWidth;
            int startY=column*itemHeight;

            int textWidth = (int) textPaint.measureText(text, 0, text.length());
            textPaint.getTextBounds(text,0,text.length(),textRect);

            canvas.drawText(text,startX+(itemWidth-textWidth)/2,
                    startY+(itemHeight - (textPaint.descent() + textPaint.ascent())) / 2,textPaint);
        }
    }



    private void drawDivide(Canvas canvas,int column) {
        int width = getWidth();
        int height = getHeight();
        int itemWidth=width/WEEK_DAY_COUNT;
        int itemHeight=height/column;
        //draw horizontal divide
        for(int i=1;i<column;i++){
            canvas.drawLine(0,itemHeight*i,width,itemHeight*i,dividePaint);
        }
        //draw vertical divide
        for(int i=1;i<WEEK_DAY_COUNT;i++){
            canvas.drawLine(itemWidth*i,0,itemWidth*i,height,dividePaint);
        }
        int strokeWidth = (int) dividePaint.getStrokeWidth();
        int start=Math.round(strokeWidth*1.0f/2);
        //draw left divide
        if(divideGravity==(divideGravity|LEFT)){
            canvas.drawLine(start,0,start,height,dividePaint);
        }
        //draw top divide
        if(divideGravity==(divideGravity|TOP)){
            canvas.drawLine(0,start,width,start,dividePaint);
        }
        //draw right divide
        if(divideGravity==(divideGravity|RIGHT)){
            canvas.drawLine(width-start,0,width-start,height,dividePaint);
        }
        //draw bottom divide
        if(divideGravity==(divideGravity|BOTTOM)){
            canvas.drawLine(0,height-start,width,height-start,dividePaint);
        }
    }

    private int getPosition(int x,int y){
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        return super.onTouchEvent(event);
    }
}
