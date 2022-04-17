package com.bbi.customalarm.Object;

import com.bbi.customalarm.System.Type;

/**
 * 알람 객체
 */
public class AlarmItem {
    private Type.AlarmType type;
    private String date;
    private int[] time;
    private boolean isActive;

    public AlarmItem() {
        type = Type.AlarmType.DayOfTheWeek;
        date = "";
        time = new int[2];
        isActive = false;
    }

    // Getter and Setter
    public Type.AlarmType getType() {
        return type;
    }
    public void setType(Type.AlarmType type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public int[] getTime() {
        return time;
    }
    public void setTime(int[] time) {
        this.time = time;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStringTime() {
        return String.format("%02d", time[0]) + ":" + String.format("%02d", time[1]);
    }
}
