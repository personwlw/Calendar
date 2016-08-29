package com.quant.calendar;

import java.util.Calendar;

/**
 * Created by cz on 16/8/29.
 * 日历对象
 */
public class CalendarDay implements Cloneable {
    public int year;
    public int month;
    public int day;

    public CalendarDay() {
    }

    public CalendarDay(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)-1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public CalendarDay(CalendarDay calendarDay) {
        year = calendarDay.year;
        month = calendarDay.month;
        day = calendarDay.day;
    }

    public CalendarDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void set(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void set(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public int hashCode() {
        return year * 365 + month * 30 + day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDay calendarDay = (CalendarDay) o;
        return year == calendarDay.year && month == calendarDay.month && day == calendarDay.day;
    }

    @Override
    public String toString() {
        return year + " 年 " + (month + 1) + " 月 " + day + " 日";
    }

    @Override
    public CalendarDay clone() {
        return new CalendarDay(this);
    }
}