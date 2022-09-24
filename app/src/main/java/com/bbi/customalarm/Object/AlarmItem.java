package com.bbi.customalarm.Object;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bbi.customalarm.System.Type;
import com.bbi.customalarm.System.UIManager;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 알람 객체
 */
@Entity
public class AlarmItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "dayOfWeek")
    private ArrayList<String> dayOfWeek;
    @ColumnInfo(name = "time")
    private String time;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "ringUri")
    private Uri ringUri;
    @ColumnInfo(name = "vibrationType")
    private String vibrationType;
    @ColumnInfo(name = "repeat")
    private int repeat;
    @ColumnInfo(name = "reCallDate")
    private String reCallDate;
    @ColumnInfo(name = "IsActive")
    private boolean isActive;

    public AlarmItem() {
        type = Type.Date;
        date = "";
        name = "";
        vibrationType = "";
        repeat = 0;
        reCallDate = "";
        time = "00:00";
        isActive = false;

        dayOfWeek = new ArrayList<>();
        //dayOfWeek.add("dd");
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

    public ArrayList<String> getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(ArrayList<String> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Uri getRingUri() {
        return ringUri;
    }
    public void setRingUri(Uri ringUri) {
        this.ringUri = ringUri;
    }

    public String getVibrationType() {
        return vibrationType;
    }
    public void setVibrationType(String vibrationType) {
        this.vibrationType = vibrationType;
    }

    public int getRepeat() {
        return repeat;
    }
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getReCallDate() {
        return reCallDate;
    }
    public void setReCallDate(String reCallDate) {
        this.reCallDate = reCallDate;
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

        Log.d("AlarmInfoActivity", "으악악 " + date);
        dayArray = date.split("-");

        GregorianCalendar GregorianCalendar = new GregorianCalendar
                (Integer.parseInt(dayArray[0])
                , Integer.parseInt(dayArray[1]) -1
                , Integer.parseInt(dayArray[2])-1);

        return date + " (" + UIManager.getDayOfWeek(GregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK)) + ")";
    }

    /**
     * 순서에 맞는 요일을 반환합니다.
     * 일요일 부터 시작합니다.
     */
    public static String convertDayOfWeek(int position) {
        switch (position) {
            case 0: return "일";
            case 1: return "월";
            case 2: return "화";
            case 3: return "수";
            case 4: return "목";
            case 5: return "금";
            case 6: return "토";
            default: return "";
        }
    }
    public static int getDayOfWeekPosition(String dayString) {
        switch (dayString) {
            case "일": return 0;
            case "월": return 1;
            case "화": return 2;
            case "수": return 3;
            case "목": return 4;
            case "금": return 5;
            case "토": return 6;
            default: return -1;
        }
    }

    /**
     * 요일을 오름차순으로 재정렬합니다.
     */
    public static ArrayList<String> resetDayOfWeek(ArrayList<String> data) {
        ArrayList<String> result = new ArrayList<>();

        for (int i=0; i<7; i++) {
            if(data.contains(convertDayOfWeek(i))) {
                result.add(convertDayOfWeek(i));
            }
        }

        return result;
    }
}
