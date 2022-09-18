package com.bbi.customalarm.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.R;
import com.bbi.customalarm.System.SystemManager;
import com.bbi.customalarm.System.VibrationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 알람 리스트 아이템 아답터
 */
public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder> {
    private final String TAG = "Testing... >>";
    private ArrayList<AlarmItem> alarmItems;
    private Context mContext;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private OnCheckedChangeListener switchListener;
    public Map<Integer, SwitchCompat> switchCompatMap;

    public AlarmListAdapter(Context context, ArrayList<AlarmItem> alarmItems) {
        mContext = context;
        this.alarmItems = alarmItems;
        switchCompatMap = new HashMap<>();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemLongClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListener = listener;
    }

    public interface OnCheckedChangeListener{
        void onCheckedChange(int position, boolean isActive);
    }
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        switchListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(convertView, clickListener, longClickListener, switchListener);
    }

    // 데이터 적용.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(alarmItems.get(position).getType().equals("Date")) {
            String[] dataArray = alarmItems.get(position).getDate().split("-");
            if(dataArray != null) {
                holder.date.setText(dataArray[0] + "\n" + dataArray[1] + "-" + dataArray[2]);
            }
        } else {
            holder.date.setText(alarmItems.get(position).getDayOfWeek().toString().replaceAll("\\[|\\]", ""));
        }

        if(alarmItems.get(position).getRepeat() != 0) {
            holder.reCallImg.setAlpha(1f);
        } else {
            holder.reCallImg.setAlpha(0.3f);
        }

        if(!alarmItems.get(position).getVibrationType().equals("")) {
            holder.vibrationImg.setAlpha(1f);
        } else {
            holder.vibrationImg.setAlpha(0.3f);
        }

        holder.time.setText(alarmItems.get(position).getTime());
        holder.activeBtn.setChecked(alarmItems.get(position).isActive());

        switchCompatMap.put(position, holder.activeBtn);
    }

    @Override
    public int getItemCount() {
        return alarmItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout clickLayout;
        public TextView date, time;
        public SwitchCompat activeBtn;
        private ImageView vibrationImg, reCallImg;

        public ViewHolder (View itemView, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener, final OnCheckedChangeListener checkListener) {
            super(itemView);
            clickLayout = itemView.findViewById(R.id.item_alarm);
            date = itemView.findViewById(R.id.alarmItem_day);
            time = itemView.findViewById(R.id.alarmItem_time);
            activeBtn = itemView.findViewById(R.id.alarmItem_switch);
            vibrationImg = itemView.findViewById(R.id.alarmItem_alarmVibration);
            reCallImg = itemView.findViewById(R.id.alarmItem_alarmReCall);

            // 롱클릭 연결.
            clickLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });

            // 롱클릭 연결.
            clickLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClickListener.onItemLongClick(view, getAdapterPosition());
                    return true;
                }
            });

            // 스위치 체크 감지.
            activeBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        checkListener.onCheckedChange(getAdapterPosition(), activeBtn.isChecked());
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }
}