package com.bbi.customalarm.System;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bbi.customalarm.R;

/**
 * UI 관련 출력
 */
public class UIManager {
    private Context context;
    private boolean toastActive;
    private View toastView;
    private String toastMessage;

    public UIManager() {
        toastActive = false;
    }

    public void setContext(Context context) {
        this.context = context;
        toastMessage = context.getString(R.string.toastMessage);
    }

    // 토스트
    public void setToastView(View toastView) {
        this.toastView = toastView;
    }
    public void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
    }
    public boolean isToastActive() {
        return toastActive;
    }

    public void printToast(String toastMessage) {
        if(toastView == null) {
            Log.d("TESTING", "1");
            Toast.makeText(context, "토스트 출력 오류", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d("TESTING", "2");
        if(toastActive) {
            return;
        }
        Log.d("TESTING", "3");
        toastActive = true;
        ConstraintLayout toastLayout = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        toastLayout = (ConstraintLayout) inflater.inflate(R.layout.layout_notice, (ConstraintLayout)toastView, false);

        ((ConstraintLayout) toastView).addView(toastLayout);

        TextView text = toastLayout.findViewById(R.id.noticeLayout_txt);
        if(toastMessage != null) {
            text.setText(toastMessage);//스낵바메시지 변경
        } else {
            text.setText(this.toastMessage);
        }

        ConstraintLayout resultLayout = toastLayout;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resultLayout.setVisibility(View.GONE);//1초 후 스낵바 배경 사라짐
                toastActive = false;
            }
        }, 1500);
    }

    /**
     * 다이얼로그 출력.
     */
    public void showDialog(
            Context context,
            String title,
            String message,
            DialogInterface.OnClickListener yesListener,
            DialogInterface.OnClickListener noListener, String leftTxt, String rightTxt, boolean cancel) {
        AlertDialog.Builder msg = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(leftTxt, yesListener)
                .setNegativeButton(rightTxt, noListener);
        AlertDialog msgDlg = msg.create();
        msgDlg.setCancelable(cancel);
        msgDlg.show();
    }

    /**
     * 공지사항용 취소가 불가능한 안내.
     */
    public void showDialogToNotice(
            Context context,
            String message,
            DialogInterface.OnClickListener listener) {
        AlertDialog.Builder msg = new AlertDialog.Builder(context)
                .setTitle("안내")
                .setMessage(message)
                .setPositiveButton("확인", listener);
        AlertDialog msgDlg = msg.create();
        msgDlg.setCancelable(false);
        msgDlg.show();
    }
    
    public static String getDayOfWeek(int num) {
        switch (num) {
            case 1: return "월";
            case 2: return "화";
            case 3: return "수";
            case 4: return "목";
            case 5: return "금";
            case 6: return "토";
            default: return "일";
        }
    }
}
