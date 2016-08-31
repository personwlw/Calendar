package com.quant.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by cz on 16/8/29.
 * 日历对象
 */
public class CalendarDay implements Cloneable, Parcelable {
    public int year;
    public int month;
    public int day;

    public CalendarDay() {
    }

    public CalendarDay(long timeMillis) {
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(timeMillis);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
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
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
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
        return year * 366 + month * 31 + day;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
    }

    protected CalendarDay(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
    }

    public static final Parcelable.Creator<CalendarDay> CREATOR = new Parcelable.Creator<CalendarDay>() {
        public CalendarDay createFromParcel(Parcel source) {
            return new CalendarDay(source);
        }

        public CalendarDay[] newArray(int size) {
            return new CalendarDay[size];
        }
    };
}