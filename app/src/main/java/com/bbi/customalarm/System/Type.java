package com.bbi.customalarm.System;

public class Type {
    /**
     * 검색 자료형
     */
    public enum SearchType {
        Int,
        String,
        Boolean,
        Float,
        ETC
    }

    /**
     * 알람 유형
     */
    public enum AlarmType {
        DayOfTheWeek,
        Date
    }
    public static String DayOfTheWeek = "DayOfTheWeek";
    public static String Date = "Date";


    // Intent
    public static String AlarmId = "AlarmId";
    public static String AlarmData = "alarmDataList";
    public static String FinishAlarm = "alarmFinished";
    public static String ReCallAlarm = "reCallAlarm";
    public static String RefreshTime = "refreshTime";
}
