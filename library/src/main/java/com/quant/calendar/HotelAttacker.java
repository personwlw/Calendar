package com.quant.calendar;

import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * Created by cz on 8/30/16.
 */
public class HotelAttacker implements IAttacker {
    private final CalendarView calendarView;

    public HotelAttacker(CalendarView calendarView) {
        this.calendarView = calendarView;
    }

    @Override
    public void onDraw(Canvas canvas, TextPaint textPaint,float startX,float startY, int year, int month, int day, String remark) {
        float itemWidth = calendarView.getItemWidth();
        float itemHeight = calendarView.getItemHeight();
        float itemPadding = calendarView.getItemPadding();

        String text=String.valueOf(day);
        float textWidth =textPaint.measureText(text, 0, text.length());

        if(TextUtils.isEmpty(remark)){
            canvas.drawText(text,startX+(itemWidth-textWidth)/2,
                    startY+(itemHeight - (textPaint.descent() + textPaint.ascent())) / 2,textPaint);
        } else {
            float textInfoWidth=textPaint.measureText(remark, 0, remark.length());

            canvas.drawText(text,startX+(itemWidth-textWidth)/2,
                    startY-(textPaint.descent() + textPaint.ascent())+itemPadding,textPaint);
            canvas.drawText(remark,startX+(itemWidth-textInfoWidth)/2,
                    startY-(textPaint.descent() + textPaint.ascent())+(itemHeight+(textPaint.descent() + textPaint.ascent())-itemPadding),textPaint);
        }
    }

    @Override
    public void onItemClick(int position) {

    }
}
