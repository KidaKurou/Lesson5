package com.mirea.lutchenkoam.audiorecord;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private Button recordButton;
    private Button playButton;

    private MediaRecorder recorder;
    private MediaPlayer player;

    private boolean isRecording = false;
    private boolean isPlaying = false;

    private boolean permissionToRecordAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);

        recordButton.setOnClickListener(v -> {
            onRecord(!isRecording); // Переключаем состояние записи
        });

        playButton.setOnClickListener(v -> {
            onPlay(!isPlaying); // Переключаем состояние воспроизведения
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            permissionToRecordAccepted = true;
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void updateButtonState() {
        recordButton.setText(isRecording ? "Stop recording" : "Start recording");
        playButton.setText(isPlaying ? "Stop playing" : "Start playing");
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission not granted for recording audio");
            return;
        }

        File audioDir = getExternalFilesDir(null);
        if (audioDir != null) {
            String recordFilePath = audioDir.getAbsolutePath() + "/audio_record.3gp";
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(recordFilePath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
                recorder.start();
                isRecording = true;
                updateButtonState();
            } catch (IOException e) {
                Log.e(TAG, "Failed to start recording", e);
            }
        } else {
            Log.e(TAG, "Failed to get external files directory");
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;
            updateButtonState();
        }
    }

    private void startPlaying() {
        File audioDir = getExternalFilesDir(null);
        if (audioDir != null) {
            String recordFilePath = audioDir.getAbsolutePath() + "/audio_record.3gp";
            player = new MediaPlayer();
            try {
                player.setDataSource(recordFilePath);
                player.prepare();
                player.start();
                isPlaying = true;
                updateButtonState();
            } catch (IOException e) {
                Log.e(TAG, "Failed to start playing", e);
            }
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
            isPlaying = false;
            updateButtonState();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
        stopPlaying();
    }
}