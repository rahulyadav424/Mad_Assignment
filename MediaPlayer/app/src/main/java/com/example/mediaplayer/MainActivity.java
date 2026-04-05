package com.example.mediaplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.MediaController;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    EditText urlInput;
    SeekBar seekBar;
    TextView statusText, timeText;
    ProgressBar loading;

    Button openFileBtn, openUrlBtn, playBtn, pauseBtn, stopBtn, restartBtn;

    Uri mediaUri;
    Handler handler = new Handler();

    private static final int PICK_FILE = 1;
    private static final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        videoView = findViewById(R.id.videoView);
        urlInput = findViewById(R.id.urlInput);
        seekBar = findViewById(R.id.seekBar);
        statusText = findViewById(R.id.statusText);
        timeText = findViewById(R.id.timeText);
        loading = findViewById(R.id.loading);

        openFileBtn = findViewById(R.id.openFileBtn);
        openUrlBtn = findViewById(R.id.openUrlBtn);
        playBtn = findViewById(R.id.playBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        stopBtn = findViewById(R.id.stopBtn);
        restartBtn = findViewById(R.id.restartBtn);

        // Media Controller (built-in controls)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Runtime Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_CODE);
            }
        }

        // Open File
        openFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_FILE);
        });

        // Open URL
        openUrlBtn.setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();

            if (!url.isEmpty()) {
                try {
                    loading.setVisibility(ProgressBar.VISIBLE);
                    mediaUri = Uri.parse(url);
                    videoView.setVideoURI(mediaUri);

                    setupVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter URL first", Toast.LENGTH_SHORT).show();
            }
        });

        // PLAY (FIXED)
        playBtn.setOnClickListener(v -> {
            if (mediaUri != null) {
                if (!videoView.isPlaying()) {
                    videoView.start();
                    statusText.setText("▶ Playing");
                    updateSeekBar();
                }
            } else {
                Toast.makeText(this, "Select file or URL first", Toast.LENGTH_SHORT).show();
            }
        });

        // PAUSE (FIXED)
        pauseBtn.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                statusText.setText("⏸ Paused");
            }
        });

        // STOP
        stopBtn.setOnClickListener(v -> {
            if (mediaUri != null) {
                videoView.pause();
                videoView.seekTo(0);
                statusText.setText("⏹ Stopped");
            }
        });

        // RESTART
        restartBtn.setOnClickListener(v -> {
            if (mediaUri != null) {
                videoView.setVideoURI(mediaUri);
                videoView.start();
                statusText.setText("🔁 Restarted");
                updateSeekBar();
            } else {
                Toast.makeText(this, "Select file or URL first", Toast.LENGTH_SHORT).show();
            }
        });

        // SeekBar Control
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaUri != null) {
                    videoView.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Setup Video (FIT SCREEN FIX)
    private void setupVideo() {
        videoView.setOnPreparedListener(mp -> {

            loading.setVisibility(ProgressBar.GONE);

            // Maintain Aspect Ratio
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            int viewWidth = videoView.getWidth();
            int newHeight = (int) ((float) viewWidth * videoHeight / videoWidth);

            videoView.getLayoutParams().height = newHeight;
            videoView.requestLayout();

            statusText.setText("🎬 Ready to Play");
            seekBar.setMax(videoView.getDuration());
            updateSeekBar();
        });
    }

    // SeekBar + Timer
    private void updateSeekBar() {
        handler.postDelayed(() -> {
            if (videoView != null && videoView.isPlaying()) {
                int current = videoView.getCurrentPosition();
                int total = videoView.getDuration();

                seekBar.setProgress(current);
                timeText.setText(formatTime(current) + " / " + formatTime(total));

                updateSeekBar();
            }
        }, 500);
    }

    // Format Time
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    // File Picker Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            mediaUri = data.getData();
            videoView.setVideoURI(mediaUri);
            setupVideo();
        }
    }

    // Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (!(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                Toast.makeText(this,
                        "Permission required to access files",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}