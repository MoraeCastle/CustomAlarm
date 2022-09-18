package com.bbi.customalarm.System;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MediaManager {
    private final String TAG = "MediaManager";
    private MediaPlayer mMediaPlayer;
    private Context context;

    public MediaManager(Context context) {
        this.context = context;
    }
    /**
     * 알람음 울리기.
     */
    public void startRingtone(Uri uriRingtone, boolean isLooping) {
        this.releaseRingtone();

        try {
            mMediaPlayer = MediaPlayer.create(context, uriRingtone );
            if( mMediaPlayer == null ) {
                throw new Exception( "Can't create player" ); }
            // STREAM_VOICE_CALL, STREAM_SYSTEM, STREAM_RING, STREAM_MUSIC, STREAM_ALARM
            // STREAM_NOTIFICATION, STREAM_DTMF
            // mMediaPlayer.setAudioStreamType( AudioManager.STREAM_ALARM );
            mMediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
            //mMediaPlayer.setAudioAttributes();
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.start();
        } catch( Exception e ) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT ).show();
            Log.e(TAG, e.getMessage() );
            e.printStackTrace();
        }
    }

    /**
     * 알람음 끄기.
     */
    public void releaseRingtone() {
        if( mMediaPlayer != null ) {
            if( mMediaPlayer.isPlaying() ) {
                mMediaPlayer.stop();
            }

            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
