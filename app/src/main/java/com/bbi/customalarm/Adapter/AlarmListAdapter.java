package com.bbi.customalarm.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.R;
import com.bbi.customalarm.ViewHolder.AlarmViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 알람 리스트 아이템 아답터
 */
public class AlarmListAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private final String TAG = "Testing... >>";
    private ArrayList<AlarmItem> alarmItems;

    public AlarmListAdapter(ArrayList<AlarmItem> alarmItems) {
        this.alarmItems = alarmItems;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_alarm, parent, false);
        AlarmViewHolder viewHolder = new AlarmViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        holder.date.setText(alarmItems.get(position).getDate());
        holder.time.setText(alarmItems.get(position).getStringTime());
        holder.activeBtn.setChecked(alarmItems.get(position).isActive());
    }

    @Override
    public int getItemCount() {
        return alarmItems.size();
    }
}