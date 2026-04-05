package com.example.sensorreader;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, light, proximity;

    private TextView tvAccelerometer, tvLight, tvProximity;
    private Button btnStart, btnStop;

    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI
        tvAccelerometer = findViewById(R.id.tvAccelerometer);
        tvLight = findViewById(R.id.tvLight);
        tvProximity = findViewById(R.id.tvProximity);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        // Sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        // Start Button
        btnStart.setOnClickListener(v -> startSensors());

        // Stop Button
        btnStop.setOnClickListener(v -> stopSensors());
    }

    private void startSensors() {
        isRunning = true;

        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (light != null)
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);

        if (proximity != null)
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensors() {
        isRunning = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (!isRunning) return;

        // Accelerometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            tvAccelerometer.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
        }

        // Light
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            tvLight.setText(lux + " lx");
        }

        // Proximity
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            tvProximity.setText(distance + " cm");

            // Extra feature: change background color
            if (distance < proximity.getMaximumRange()) {
                tvProximity.setBackgroundColor(Color.RED);
            } else {
                tvProximity.setBackgroundColor(Color.GREEN);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSensors();
    }
}