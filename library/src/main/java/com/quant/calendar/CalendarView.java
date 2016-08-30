package com.quant.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by cz on 8/29/16.
 */
public class CalendarView extends View {
    private static final String TAG = "CalendarView";
    private static final int WEEK_DAY_COUNT=7;
    private static final int AUTO_ITEM_HEIGHT=0;
    public static final int LEFT=0x01;
    public static final int TOP=0x02;
    public static final int RIGHT=0x04;
    public static final int BOTTOM=0x08;


    @IntDef(value={LEFT,TOP,RIGHT,BOTTOM})
    public @interface Gravity{
    }
    private final int[] WEEK_DAY=new int[]{7,1,2,3,4,5,6};
    private final HashMap<CalendarDay,String> calendarInfos;
    private final Calendar calendar;
    private final Paint dividePaint;
    private final TextPaint textPaint;
    private IAttacker attacker;
    private CalendarDay calendarDay;
    private CalendarDay today;
    private float itemPadding;
    private int divideGravity;
    private int weekTextColor;
    private int selectTextColor;
    private int textDisableColor;
    private int textColor;
    private float divideSize;
    private int itemHeight;
    private RectF touchRect;
    private int selectPosition;

    public CalendarView(Context context) {
        this(context, null, 0);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectPosition=-1;
        attacker=new HotelAttacker(this);
        calendarInfos=new HashMap<>();
        dividePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        calendarDay = new CalendarDay(System.currentTimeMillis());
        today = new CalendarDay(System.currentTimeMillis());
        calendar=Calendar.getInstance();
        calendar.setFirstDayOfWeek(calendar.MONDAY);
        calendar.set(calendarDay.year, calendarDay.month, 1);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        setItemSelectDrawable(a.getDrawable(R.styleable.CalendarView_cv_itemSelectDrawable));
        setItemHeight(a.getLayoutDimension(R.styleable.CalendarView_cv_itemHeight, "cv_itemHeight"));
        setItemTextColor(a.getColor(R.styleable.CalendarView_cv_itemTextColor, Color.BLACK));
        setItemTextSize(a.getDimensionPixelSize(R.styleable.CalendarView_cv_itemTextSize, 0));
        setItemWeekColor(a.getColor(R.styleable.CalendarView_cv_itemWeekColor, Color.DKGRAY));
        setItemPadding(a.getDimension(R.styleable.CalendarView_cv_itemPadding,0));
        setItemSelectColor(a.getColor(R.styleable.CalendarView_cv_itemSelectColor,Color.WHITE));
        setItemDisableColor(a.getColor(R.styleable.CalendarView_cv_itemDisableColor,Color.GRAY));
        setItemDivideColor(a.getColor(R.styleable.CalendarView_cv_itemDivideColor, Color.DKGRAY));
        setItemDivideSize(a.getDimension(R.styleable.CalendarView_cv_itemDivideSize, 0));
        setDivideGravityInner(a.getInt(R.styleable.CalendarView_cv_divideGravity, LEFT));
        a.recycle();
    }




    public void setItemDivideSize(float size) {
        this.dividePaint.setStrokeWidth(1);
        invalidate();
    }

    public CalendarDay getCalendarDay(){
        return calendarDay;
    }

    public void setCalendarDay(CalendarDay day){
        calendarDay =day;
        calendar.set(day.year,day.month,1);
        requestLayout();
    }

    public void setItemSelectDrawable(Drawable drawable) {

    }

    public void setItemHeight(int height) {
        this.itemHeight=height;
        requestLayout();
    }

    public void setItemPadding(float padding) {
        this.itemPadding=padding;
        invalidate();
    }

    public void setItemSelectColor(int color) {
        this.selectTextColor =color;
        invalidate();
    }

    public void setItemTextColor(int color) {
        this.textColor=color;
        invalidate();
    }

    public void setItemDisableColor(int color) {
        this.textDisableColor=color;
        invalidate();
    }

    public void setItemTextSize(int textSize) {
        this.textPaint.setTextSize(textSize);
        invalidate();
    }

    public void setItemWeekColor(int color) {
        this.weekTextColor=color;
        invalidate();
    }

    public void setItemDivideColor(int color) {
        dividePaint.setColor(color);
    }

    public void setDivideGravityInner(int gravity) {
        this.divideGravity=gravity;
        invalidate();
    }

    public void setDivideGravity(@Gravity  int gravity) {
        setDivideGravity(gravity);
    }

    public void addCalendarInfo(CalendarDay day,String text){
        addCalendarInfo(day.year, day.month - 1, day.day - 1, text);
    }

    public void addCalendarInfo(int year,int month,int day,String text){
        calendarInfos.put(new CalendarDay(year, month - 1, day - 1), text);
        invalidate();
    }

    public void addTodayInfo(String text){
        calendarInfos.put(today,text);
        invalidate();
    }

    public void setCalendarAttacker(IAttacker attacker){
        this.attacker=attacker;
        invalidate();
    }


    public float getItemWidth() {
        return getMeasuredWidth()/WEEK_DAY_COUNT;
    }

    public float getItemHeight(){
        return itemHeight;
    }

    public float getItemPadding(){
        return itemPadding;
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
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int column = getCalendarColumn();
        if(MeasureSpec.EXACTLY==mode){
            //exactly height
            int measuredHeight = getMeasuredHeight();
            itemHeight=measuredHeight/column;
            setMeasuredDimension(getMeasuredWidth(),measuredHeight);
        } else {
            if(AUTO_ITEM_HEIGHT==itemHeight){
                //auto height
                itemHeight=getMeasuredWidth()/WEEK_DAY_COUNT;
            }
            setMeasuredDimension(getMeasuredWidth(), resolveSize(itemHeight*column, heightMeasureSpec));
        }
    }

    private int getCalendarColumn() {
        int totalDay= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+ WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1]-1;
        return (0==totalDay%WEEK_DAY_COUNT)?totalDay/WEEK_DAY_COUNT:totalDay/WEEK_DAY_COUNT+1;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int column = getCalendarColumn();
        drawDivide(canvas,column);
        drawCalendarText(canvas,column);
    }

    private void drawCalendarText(Canvas canvas, int totalColumn) {
        calendar.set(calendarDay.year, calendarDay.month, 1);
        int startDay = WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1];
        int currentDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH, -1);
        int previousDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH, 1);
        int itemWidth=getWidth()/WEEK_DAY_COUNT;
        int total=WEEK_DAY_COUNT*totalColumn;

        for(int i=1;i<=total;i++){
            int column=(i-1)/WEEK_DAY_COUNT;
            int row=(i-1)%WEEK_DAY_COUNT;
            int startX=row*itemWidth;
            int startY=column*itemHeight;
            int month,dayOfMonth;
            if(i<startDay){
                //previous month
                month=calendarDay.month-1;
                textPaint.setColor(textDisableColor);
                dayOfMonth=previousDays-startDay+i+1;
            } else if(i>=startDay&&i<startDay+currentDays){
                //current days
                month=calendarDay.month;
                dayOfMonth=i-startDay+1;
                if(-1!=selectPosition&&i==selectPosition+1||
                        (calendarDay.year==today.year&&calendarDay.month==today.month&&i-startDay==today.day)){
                    textPaint.setColor(selectTextColor);
                } else if(5==row||6==row){
                    //week
                    textPaint.setColor(weekTextColor);
                } else {
                    //default
                    textPaint.setColor(textColor);
                }
            } else {
                //next days
                month=calendarDay.month+1;
                textPaint.setColor(textDisableColor);
                dayOfMonth=i-startDay-currentDays+1;
            }
            String calendarInfo =null;
            calendarDay.day=i-startDay;
            if(calendarInfos.containsKey(calendarDay)){
                calendarInfo = calendarInfos.get(calendarDay);
            }

            if(null!=attacker){
                attacker.onDraw(canvas, textPaint, startX, startY, calendarDay.year, month, dayOfMonth, calendarInfo);
            }
        }
    }

    private void drawDivide(Canvas canvas,int column) {
        int width = getWidth();
        int height = getHeight();
        float itemWidth=width*1.0f/WEEK_DAY_COUNT;
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
        //draw touch rect
        if(null!=touchRect){
            canvas.drawRect(touchRect,dividePaint);
        }
    }

    private RectF getSelectRect(float x,float y){
        float itemWidth=getWidth()*1.0f/WEEK_DAY_COUNT;
        int row= (int) (y/itemHeight);
        int column= (int) (x/itemWidth);
        this.selectPosition=row*WEEK_DAY_COUNT+column;
        return new RectF(column*itemWidth,row*itemHeight,(column+1)*itemWidth,(row+1)*itemHeight);
    }

    private boolean isInWindowRect(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        return 0<=x&&x<=width&&0<=y&&y<=height;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y =  event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchRect=getSelectRect(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                final RectF rect = getSelectRect(x, y);
                if(isInWindowRect(x,y)&&!touchRect.equals(rect)){
                    //start new rect animator
                    touchRect = rect;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                //click item
                RectF newRect = getSelectRect(x, y);

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }


}
