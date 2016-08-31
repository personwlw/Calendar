package com.quant.calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by cz on 8/29/16.
 */
public class CalendarView extends View {
    private static final String TAG = "CalendarView";

    private static final int ANIM_TAB_PRESS_START=1;
    private static final int ANIM_TAB_PRESS_END=2;
    private static final int ANIM_RANGE=3;
    private static final int ANIM_TOUCH=4;

    private static final int WEEK_DAY_COUNT=7;
    private static final int MAX_SELECT_DAY =30;
    private static final int MAX_RANGE_DAY =20;
    private static final int AUTO_ITEM_HEIGHT=0;
    public static final int NONE=0x01;
    public static final int LEFT=0x02;
    public static final int TOP=0x04;
    public static final int RIGHT=0x08;
    public static final int BOTTOM=0x10;
    public static final int DIVIDE=0x20;


    @IntDef(value={NONE,LEFT,TOP,RIGHT,BOTTOM,DIVIDE})
    public @interface Gravity{
    }

    private final int PREVIOUS_MONTH=0;
    private final int CURRENT_MONTH=1;
    private final int NEXT_MONTH=2;
    private final int NEXT_TOUCH=3;
    private float[] fractions;

    private final int[] WEEK_DAY=new int[]{7,1,2,3,4,5,6};
    private final HashMap<CalendarDay,String> calendarInfos;
    private OnCalendarItemClickListener listener;
    private final Calendar calendar;
    private CalendarDay selectCalendar;
    private int itemSelectColor;
    private final Paint dividePaint;
    private final Paint selectPaint;
    private final TextPaint labelPaint;
    private final TextPaint textPaint;
    private float itemSelectRoundRadii;
    private CalendarDay calendarDay;
    private CalendarDay calendarToday;
    private float itemPadding;
    private int divideGravity;
    private int itemSelectTabColor;
    private int itemSelectRangeColor;
    private int itemSelectRangeTextColor;
    private int selectTextColor;
    private int textDisableColor;
    private int textColor;
    private float divideSize;
    private int itemHeight;
    private int maxSelectDay;
    private final String todayValue;
    private int touchPosition;
    private final List<CalendarDay> selectCalendarItems;

    public CalendarView(Context context) {
        this(context, null, 0);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchPosition=-1;
        fractions=new float[5];
        selectCalendarItems =new ArrayList<>();
        calendarInfos=new HashMap<>();
        dividePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        calendarDay = new CalendarDay(System.currentTimeMillis());
        calendarToday = new CalendarDay(System.currentTimeMillis());
        calendar= Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setFirstDayOfWeek(calendar.MONDAY);
        calendar.set(calendarDay.year, calendarDay.month, 1);
        maxSelectDay= MAX_SELECT_DAY;
        todayValue="今天";


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        setItemSelectColor(a.getColor(R.styleable.CalendarView_cv_itemSelectColor, Color.BLUE));
        setItemHeight(a.getLayoutDimension(R.styleable.CalendarView_cv_itemHeight, "cv_itemHeight"));
        setItemTextColor(a.getColor(R.styleable.CalendarView_cv_itemTextColor, Color.BLACK));
        setItemTextSize(a.getDimensionPixelSize(R.styleable.CalendarView_cv_itemTextSize, 0));
        setItemSelectTabColor(a.getColor(R.styleable.CalendarView_cv_itemSelectTabColor,Color.BLUE));
        setItemSelectRangeColor(a.getColor(R.styleable.CalendarView_cv_itemSelectRangeColor, Color.DKGRAY));
        setItemSelectRangeTextColor(a.getColor(R.styleable.CalendarView_cv_itemSelectRangeTextColor, Color.BLUE));
        setItemLabelColor(a.getColor(R.styleable.CalendarView_cv_itemLabelTextColor, Color.WHITE));
        setItemLabelTextSize(a.getDimensionPixelSize(R.styleable.CalendarView_cv_itemLabelTextSize, 0));
        setItemPadding(a.getDimension(R.styleable.CalendarView_cv_itemPadding, 0));
        setItemSelectTextColor(a.getColor(R.styleable.CalendarView_cv_itemSelectTextColor, Color.WHITE));
        setItemDisableTextColor(a.getColor(R.styleable.CalendarView_cv_itemDisableTextColor, Color.GRAY));
        setItemDivideColor(a.getColor(R.styleable.CalendarView_cv_itemDivideColor, Color.DKGRAY));
        setItemDivideSize(a.getDimension(R.styleable.CalendarView_cv_itemDivideSize, 0));
        setDivideGravityInner(a.getInt(R.styleable.CalendarView_cv_divideGravity, NONE));
        setItemSelectRoundRadii(a.getDimension(R.styleable.CalendarView_cv_itemSelectRoundRadii, 0));
        a.recycle();
    }

    public void setItemSelectTabColor(int color) {
        this.itemSelectTabColor=color;
        invalidate();
    }

    public void setItemSelectRangeTextColor(int color) {
        this.itemSelectRangeTextColor=color;
        invalidate();
    }

    public void setItemSelectRoundRadii(float radius) {
        this.itemSelectRoundRadii=radius;
        invalidate();
    }

    public void setItemLabelTextSize(int textSize) {
        this.labelPaint.setTextSize(textSize);
        invalidate();
    }

    public void setItemLabelColor(int color) {
        this.labelPaint.setColor(color);
        invalidate();
    }


    public void setItemDivideSize(float size) {
        this.dividePaint.setStrokeWidth(size);
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

    public void setItemSelectColor(int color) {
        this.itemSelectColor=color;
        invalidate();
    }

    public void setItemHeight(int height) {
        this.itemHeight=height;
        requestLayout();
    }

    public void setItemPadding(float padding) {
        this.itemPadding=padding;
        invalidate();
    }

    public void setItemSelectTextColor(int color) {
        this.selectTextColor =color;
        invalidate();
    }

    public void setItemTextColor(int color) {
        this.textColor=color;
        invalidate();
    }

    public void setItemDisableTextColor(int color) {
        this.textDisableColor=color;
        invalidate();
    }

    public void setItemTextSize(int textSize) {
        this.textPaint.setTextSize(textSize);
        invalidate();
    }

    public void setItemSelectRangeColor(int color) {
        this.itemSelectRangeColor=color;
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
        addCalendarInfo(day.year, day.month, day.day, text);
    }

    public void addCalendarInfo(int year,int month,int day,String text){
        calendarInfos.put(new CalendarDay(year, month, day), text);
        invalidate();
    }

    public void setSelectCalendarDay(CalendarDay newSelectDay){
        this.selectCalendar=newSelectDay;
//        calendar.set(calendarDay.year,calendarDay.month,1);
        if(0>newSelectDay.hashCode()-calendarDay.hashCode()){
            int day = newSelectDay.compare(calendarDay);
            maxSelectDay=MAX_RANGE_DAY+day+1;
        }
        this.selectCalendarItems.add(newSelectDay);
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

    public void clearSelectCalendar() {
        this.selectCalendar=null;
        this.selectCalendarItems.clear();
        invalidate();
    }

    public void clearCalendarInfo() {
        calendarInfos.clear();
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
        int startDay = WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1]-1;
        int monthDays=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH,-startDay);
        float itemWidth=getWidth()*1.0f/WEEK_DAY_COUNT;

        int total=WEEK_DAY_COUNT*totalColumn;
        CalendarDay newDay;
        for(int i=1;i<=total;i++,calendar.add(Calendar.DAY_OF_MONTH,1)){
            newDay=new CalendarDay(calendar.getTimeInMillis());
            int column=(i-1)/WEEK_DAY_COUNT;
            int row=(i-1)%WEEK_DAY_COUNT;
            float startX=row*itemWidth;
            float startY=column*itemHeight;
            String text;
            int state;
            int newDayCode = newDay.hashCode();
            int calendarCode=calendarDay.hashCode();
            if(newDayCode<calendarCode){
                //previous month
                state=PREVIOUS_MONTH;
                textPaint.setColor(textDisableColor);
                text=String.valueOf(newDay.day);
            } else if(newDayCode>=calendarCode&&newDayCode<calendarCode+monthDays){
                //current days
                state=CURRENT_MONTH;
                if(calendarToday.day==i-startDay){
                    text=todayValue;
                } else {
                    text=String.valueOf(newDay.day);
                }
                if(calendarToday.hashCode()>newDayCode){
                    textPaint.setColor(textDisableColor);
                } else if(calendarToday.hashCode()==newDayCode){
                    textPaint.setColor(itemSelectRangeTextColor);
                } else if(newDayCode-calendarToday.hashCode()< MAX_SELECT_DAY){
                    textPaint.setColor(textColor);
                } else {
                    textPaint.setColor(textDisableColor);
                }
            } else {
                //next days
                state=NEXT_MONTH;
                textPaint.setColor(textDisableColor);
                text=String.valueOf(newDay.day);
            }

            if(-1!=touchPosition&&touchPosition==column*WEEK_DAY_COUNT+row){
//                canvas.drawCircle(startX+itemWidth/2,startY+itemHeight/2,Math.min(itemWidth,itemHeight)/2*(0.4f+0.6f*fractions[ANIM_TOUCH]),dividePaint);
                canvas.drawCircle(startX+itemWidth/2,startY+itemHeight/2,Math.min(itemWidth,itemHeight)/2*(fractions[ANIM_TOUCH]),dividePaint);
            }

            if(CURRENT_MONTH==state){
                //draw touch rect
                if(!selectCalendarItems.isEmpty()){
                    int index = selectCalendarItems.indexOf(newDay);
                    if(0==index){
                        selectPaint.setColor(itemSelectTabColor);
                        selectPaint.setAlpha((int) (0xFF*fractions[ANIM_TAB_PRESS_START]));
                        canvas.drawPath(getRoundRectPath(new RectF(itemWidth*row,itemHeight*column,itemWidth*(row+1),itemHeight*(column+1)), itemSelectRoundRadii, 0, itemSelectRoundRadii, 0), selectPaint);
                    } else if(1==index){
                        selectPaint.setColor(itemSelectTabColor);
                        selectPaint.setAlpha((int) (0xFF*fractions[ANIM_TAB_PRESS_END]));
                        canvas.drawPath(getRoundRectPath(new RectF(itemWidth*row,itemHeight*column,itemWidth*(row+1),itemHeight*(column+1)), 0, itemSelectRoundRadii,0,itemSelectRoundRadii), selectPaint);
                    }
                    int size = selectCalendarItems.size();
                    if(1==size){
                        CalendarDay selectStartDay = selectCalendarItems.get(0);
                        int startCode = calendarDay.hashCode();
                        int selectCode = selectStartDay.hashCode();
                        int endCode = startCode+MAX_SELECT_DAY-1;
                        if(endCode-selectCode>MAX_RANGE_DAY){
                            endCode=selectCode+MAX_RANGE_DAY;
                        }
                        if(newDayCode<selectCode){
                            textPaint.setColor(textDisableColor);
                        } else if(newDayCode<endCode){
                            textPaint.setColor(textColor);
                        } else {
                            textPaint.setColor(textDisableColor);
                        }
                    } else if(2==size&&CURRENT_MONTH==state){
                        CalendarDay selectStartDay = selectCalendarItems.get(0);
                        CalendarDay selectEndDay = selectCalendarItems.get(1);
                        if(newDayCode>selectStartDay.hashCode()&&newDayCode<selectEndDay.hashCode()){
                            textPaint.setColor(itemSelectRangeTextColor);
                            selectPaint.setColor(itemSelectRangeColor);
                            selectPaint.setAlpha((int) (0xFF*fractions[ANIM_RANGE]));
                            canvas.drawRect(new RectF(itemWidth*row,itemHeight*column,itemWidth*(row+1),itemHeight*(column+1)),selectPaint);
                        }
                    }
                }

                String calendarInfo =null;
                if(calendarInfos.containsKey(newDay)){
                    calendarInfo = calendarInfos.get(newDay);
                }

                float textWidth =textPaint.measureText(text, 0, text.length());
                if(TextUtils.isEmpty(calendarInfo)){
                    canvas.drawText(text,startX+(itemWidth-textWidth)/2,
                            startY+(itemHeight - (textPaint.descent() + textPaint.ascent())) / 2,textPaint);
                } else {
                    textPaint.setColor(itemSelectRangeTextColor);
                    canvas.drawText(text,startX+(itemWidth-textWidth)/2,startY+itemHeight/2-itemPadding,textPaint);

                    float textInfoWidth=labelPaint.measureText(calendarInfo, 0, calendarInfo.length());
                    float textHeight=-(labelPaint.descent() + labelPaint.ascent());
                    canvas.drawText(calendarInfo,startX+(itemWidth-textInfoWidth)/2,startY+itemHeight/2+textHeight+itemPadding,labelPaint);
                }
            }
        }
    }

    private void drawDivide(Canvas canvas,int column) {
        int width = getWidth();
        int height = getHeight();
        float itemWidth=width*1.0f/WEEK_DAY_COUNT;

        int strokeWidth = (int) dividePaint.getStrokeWidth();
        int start= Math.round(strokeWidth * 1.0f / 2);
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
        if(divideGravity==(divideGravity|DIVIDE)){
            //draw horizontal divide
            for(int i=1;i<column;i++){
                canvas.drawLine(0,itemHeight*i,width,itemHeight*i,dividePaint);
            }
            //draw vertical divide
            for(int i=1;i<WEEK_DAY_COUNT;i++){
                canvas.drawLine(itemWidth*i,0,itemWidth*i,height,dividePaint);
            }
        }

    }

    public Path getRoundRectPath(RectF rect, float tl, float tr,float bl,float br){
        Path path=new Path();
        path.moveTo(rect.left + tl ,rect.top);
        path.lineTo(rect.right - tr,rect.top);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + tr);
        path.lineTo(rect.right ,rect.bottom - br);
        path.quadTo(rect.right ,rect.bottom, rect.right - br, rect.bottom);
        path.lineTo(rect.left + bl,rect.bottom);
        path.quadTo(rect.left,rect.bottom,rect.left, rect.bottom - bl);
        path.lineTo(rect.left,rect.top + tl);
        path.quadTo(rect.left,rect.top, rect.left + tl, rect.top);
        path.close();
        return path;
    }

    private RectF getSelectRect(float x,float y){
        float itemWidth=getWidth()*1.0f/WEEK_DAY_COUNT;
        int row= (int) (y/itemHeight);
        int column= (int) (x/itemWidth);
        return new RectF(column*itemWidth,row*itemHeight,(column+1)*itemWidth,(row+1)*itemHeight);
    }

    private int getSelectPosition(float x,float y){
        float itemWidth=getWidth()*1.0f/WEEK_DAY_COUNT;
        int row= (int) (y/itemHeight);
        int column= (int) (x/itemWidth);
        return row*WEEK_DAY_COUNT+column;
    }

    private boolean isInWindowRect(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        return 0<=x&&x<=width&&0<=y&&y<=height;
    }

    public CalendarDay getCalendarByPosition(int position){
        calendar.set(calendarDay.year, calendarDay.month, 1);
        int startDay = WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1]-1;
        if(position<startDay){
            //previous month
            calendar.add(Calendar.DAY_OF_MONTH, -(startDay-position));
        } else {
            //next days
            calendar.add(Calendar.DAY_OF_MONTH, position-startDay);
        }
        return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y =  event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchPosition=getSelectPosition(x,y);
                startAnimator(ANIM_TOUCH,false,null);
//                RectF touchRect=getSelectRect(x,y);
//                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //click item
//                final RectF rect = getSelectRect(x, y);
//                if(isInWindowRect(x,y)&&!rect.equals(touchRect)){
//                    //start new rect animator
//                    touchRect = rect;
//                    invalidate();
//                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int position = getSelectPosition(x, y);
                calendar.set(calendarDay.year, calendarDay.month, 1);
                int startDay = WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1]-1;
                int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                startAnimator(ANIM_TOUCH,true,null);
                if((calendarDay.year==calendarToday.year&&calendarDay.month==calendarToday.month&&position>=calendarToday.day+startDay-1&&position<startDay+days)  ||
                        (calendarDay.year==calendarToday.year&& calendarToday.month+1==calendarDay.month&& maxSelectDay>=position-1&&position>=startDay)){
                    //同年/同月,日期小于当天,并小于下月初  or 同年/下一月
                    final CalendarDay calendarDay = getCalendarByPosition(position);
                    if(2== selectCalendarItems.size()){
                        startAnimator(ANIM_TAB_PRESS_START,true,null);
                        startAnimator(ANIM_RANGE,true,null);
                        startAnimator(ANIM_TAB_PRESS_END, true, new Runnable() {
                            @Override
                            public void run() {
                                selectCalendar=null;
                                selectCalendarItems.clear();
                                maxSelectDay=MAX_SELECT_DAY;
                                if(null!=listener){
                                    listener.onItemClick(null,null,true);
                                }
                            }
                        });
                    } else if(null==selectCalendar){
                        startAnimator(ANIM_TAB_PRESS_START,false,null);
                        int endCode = calendarToday.hashCode()+ MAX_SELECT_DAY;
                        int selectCode=calendarDay.hashCode();
                        if(MAX_RANGE_DAY>endCode-selectCode){
                            maxSelectDay=MAX_SELECT_DAY;
                        } else {
                            maxSelectDay=MAX_RANGE_DAY+(position-startDay)+1;
                        }
                        selectCalendarItems.clear();
                        selectCalendar=calendarDay;
                        selectCalendarItems.add(calendarDay);
                        if(null!=listener){
                            listener.onItemClick(calendarDay,null,false);
                        }
                    } else if(calendarDay.equals(selectCalendar)){
                        startAnimator(ANIM_TAB_PRESS_START, true, new Runnable() {
                            @Override
                            public void run() {
                                selectCalendarItems.remove(selectCalendar);
                                selectCalendar=null;
                                if(null!=listener){
                                    listener.onItemClick(null,null,true);
                                }
                            }
                        });
                    } else if(0>calendarDay.hashCode()-selectCalendar.hashCode()){
                        Toast.makeText(getContext(), "选择日期异常!", Toast.LENGTH_SHORT).show();
                    } else {
                        selectCalendarItems.add(calendarDay);
                        if(null!=listener){
                            listener.onItemClick(selectCalendar,calendarDay,false);
                        }
                        startAnimator(ANIM_TAB_PRESS_END,false,null);
                        startAnimator(ANIM_RANGE, false,null);
                    }
                }
                invalidate();
                break;
        }
        return true;
    }

    private void startAnimator(final int index, final boolean reverse, final Runnable action){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                fractions[index]=reverse?(1f-fraction):fraction;
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(null!=action){
                    action.run();
                }
            }
        });
        valueAnimator.start();
    }

    public void setOnCalendarItemClickListener(OnCalendarItemClickListener listener){
        this.listener=listener;
    }

    public interface OnCalendarItemClickListener{
        void onItemClick(CalendarDay calendarDay1, CalendarDay calendarDay2, boolean cancle);

    }


}
