package com.bbi.customalarm.Room;

import android.net.Uri;
import androidx.room.TypeConverter;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public String fromArray(ArrayList<String> strings) {
        String string = "";
        for(String s : strings) string += (s + ",");

        return string;
    }

    @TypeConverter
    public ArrayList<String> toArray(String data) {
        ArrayList<String> myStrings = new ArrayList<>();

        for(String s : data.split(",")) {
            myStrings.add(s);
        }

        return myStrings;
    }

    @TypeConverter
    public String fromUri(Uri data) {
        String stringUri;
        stringUri = data.toString();

        return stringUri;
    }

    @TypeConverter
    public Uri toUri(String data) {
        Uri uri;
        uri = Uri.parse(data);

        return uri;
    }
}