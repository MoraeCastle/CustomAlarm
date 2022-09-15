package com.bbi.customalarm.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.bbi.customalarm.Object.AlarmItem;
import java.util.List;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM AlarmItem")
    LiveData<List<AlarmItem>> getAll();
    //LiveData => Todo테이블에 있는 모든 객체를 계속 관찰하고있다가 변경이 일어나면 그것을 자동으로 업데이트하도록한다.
    //getAll() 은 관찰 가능한 객체가 된다.(즉 디비변경시 반응하는)

    @Insert
    void insert(AlarmItem alarmItem);

    @Update
    void update(AlarmItem alarmItem);

    @Delete
    void delete(AlarmItem alarmItem);

    @Query("DELETE FROM AlarmItem")
    void deleteAll();
}
