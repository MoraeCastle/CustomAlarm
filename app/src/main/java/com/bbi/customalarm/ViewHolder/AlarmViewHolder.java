package com.bbi.customalarm.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bbi.customalarm.R;

/**
 * 알람 리스트 뷰홀더
 */
public class AlarmViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout clickLayout;
    public TextView date, time;
    public Switch activeBtn;

    public AlarmViewHolder (Context context, View itemView) {
        super(itemView);
        clickLayout = itemView.findViewById(R.id.item_alarm);
        date = itemView.findViewById(R.id.alarmItem_day);
        time = itemView.findViewById(R.id.alarmItem_time);
        activeBtn = itemView.findViewById(R.id.alarmItem_switch);

        clickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 알람 정보로 이동

            }
        });
    }
}
