package com.bbi.customalarm.Object;

import android.content.Intent;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bbi.customalarm.System.Type;
import com.bbi.customalarm.System.UIManager;

import java.util.GregorianCalendar;

/**
 * 알람 객체
 */
@Entity
public class AlarmItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "data")
    private String date;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "time")
    private String time;
    @ColumnInfo(name = "IsActive")
    private boolean isActive;

    public AlarmItem() {
        type = Type.DayOfTheWeek;
        date = "";
        name = "";
        time = "0:0";
        isActive = false;
    }

    // Getter and Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    // 시간 변환
    public String getStringTime(int[] time) {
        return String.format("%02d", time[0]) + ":" + String.format("%02d", time[1]);
    }
    public int[] getIntegerTime(String time) {
        String[] array = time.split(":");
        int[] resultArray = new int[2];

        for (int i = 0; i < array.length; i++) {
            resultArray[i] = Integer.parseInt(array[i]);
        }

        return resultArray;
    }

    public void setStringToDate(int year, int month, int day) {
        this.date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }

    public String getDateToSet() {
        String[] dayArray = new String[]{};

        dayArray = date.split("-");

        GregorianCalendar GregorianCalendar = new GregorianCalendar
                (Integer.parseInt(dayArray[0])
                , Integer.parseInt(dayArray[1]) -1
                , Integer.parseInt(dayArray[2])-1);

        return date + " (" + UIManager.getDayOfWeek(GregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK)) + ")";
    }

}
