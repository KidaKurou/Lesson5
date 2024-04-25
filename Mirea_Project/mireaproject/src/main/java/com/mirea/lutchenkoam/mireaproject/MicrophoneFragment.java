package com.mirea.lutchenkoam.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.media.MediaPlayer;

public class MicrophoneFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private MediaRecorder recorder = null;
    private static String fileName = null;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private boolean isRecording = false;
    private MediaPlayer player = null;
    private boolean isPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_microphone, container, false);

        fileName = getActivity().getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (isGranted.containsValue(false)) {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        });

        Button recordButton = view.findViewById(R.id.btn_record);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(permissions);
                } else {
                    if (isRecording) {
                        stopRecording();
                        recordButton.setText("Start recording");
                    } else {
                        startRecording();
                        recordButton.setText("Stop recording");
                    }
                    isRecording = !isRecording;
                }
            }
        });

        Button playButton = view.findViewById(R.id.btn_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(permissions);
                } else {
                    if (isPlaying) {
                        stopPlaying();
                        playButton.setText("Start playing");
                    } else {
                        startPlaying();
                        playButton.setText("Stop playing");
                    }
                    isPlaying = !isPlaying;
                }
            }
        });

        return view;
    }

    private void startRecording() {
        if (recorder != null) {
            recorder.release();
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "recorder.prepare() failed", Toast.LENGTH_LONG).show();
        }

        recorder.start();
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Button playButton = getView().findViewById(R.id.btn_play);
                    playButton.setText("Start playing");
                    isPlaying = false;
                }
            });
            Toast.makeText(getActivity(), "Record is playing!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "player.prepare() failed", Toast.LENGTH_LONG).show();
        }
    }


    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
