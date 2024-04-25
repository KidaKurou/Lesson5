package com.mirea.lutchenkoam.mireaproject;

import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestSensorFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor; // You can choose any available sensor
    private TextView resultTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_sensor, container, false);

        resultTextView = rootView.findViewById(R.id.resultTextView);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0]; // Acceleration along the X-axis
            float y = event.values[1]; // Acceleration along the Y-axis
            float z = event.values[2]; // Acceleration along the Z-axis

            // Your logic problem-solving code here
            // Example: Check if the device is tilted left or right
            if (x > 5) {
                resultTextView.setText("Device tilted right");
            } else if (x < -5) {
                resultTextView.setText("Device tilted left");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed
        }
    };
}