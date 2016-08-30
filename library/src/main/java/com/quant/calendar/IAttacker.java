package com.quant.calendar;

import android.graphics.Canvas;
import android.text.TextPaint;

/**
 * Created by cz on 8/30/16.
 */
public interface IAttacker {
    void onDraw(Canvas canvas,TextPaint textPaint,float startX,float startY,int year,int month,int day,String info);

    void onItemClick(int position);
}
