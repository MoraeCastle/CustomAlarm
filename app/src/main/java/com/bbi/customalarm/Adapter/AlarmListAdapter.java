package com.bbi.customalarm.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bbi.customalarm.Object.AlarmItem;
import com.bbi.customalarm.R;
import java.util.ArrayList;

/**
 * 알람 리스트 아이템 아답터
 */
public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder> {
    private final String TAG = "Testing... >>";
    private ArrayList<AlarmItem> alarmItems;
    private Context mContext;
    private OnItemLongClickListener longClickListener;

    public AlarmListAdapter(Context context, ArrayList<AlarmItem> alarmItems) {
        mContext = context;
        this.alarmItems = alarmItems;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(convertView, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(alarmItems.get(position).getDate());
        holder.time.setText(alarmItems.get(position).getTime());
        holder.activeBtn.setChecked(alarmItems.get(position).isActive());

        /*holder.clickLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "롱롱");

                return false;
            }
        });

        holder.clickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "롱111롱");
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return alarmItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout clickLayout;
        public TextView date, time;
        public Switch activeBtn;

        public ViewHolder (View itemView, final OnItemLongClickListener listener) {
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

            clickLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(view, getAdapterPosition());
                    return true;
                }
            });
        }
    }
}