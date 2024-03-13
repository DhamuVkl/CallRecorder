package com.example.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.IOException;

public class CallReceiver extends BroadcastReceiver {

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            // Incoming call detected
            startRecording();
        } else if (state != null && state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // Call ended
            stopRecording();
        }
    }

    private void startRecording() {
        if (isRecording) {
            return;
        }

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            String filePath = getFilePath();
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
            isRecording = false;
            if (mediaRecorder != null) {
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }
    }

    private void stopRecording() {
        if (!isRecording || mediaRecorder == null) {
            return;
        }

        try {
            mediaRecorder.stop();
            mediaRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mediaRecorder = null;
            isRecording = false;
        }
    }

    private String getFilePath() {
        return context.getExternalCacheDir().getAbsolutePath() + "/call_recording.mp4";
    }
}
