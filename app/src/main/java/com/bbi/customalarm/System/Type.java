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

    /**
     * 알람종료 타입 - 비활성
     */
    public static String FinishAlarm = "alarmFinished";

    /**
     * 알람종료 타입 - 다시 울리기
     */
    public static String ReCallAlarm = "reCallAlarm";
    public static String RefreshTime = "refreshTime";
    public static String CheckAlarm = "checkAlarm";

    // Service
    public static final String AlarmService = "alarmService";
}
