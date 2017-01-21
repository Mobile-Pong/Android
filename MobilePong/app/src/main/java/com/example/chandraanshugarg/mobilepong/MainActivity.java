    package com.example.chandraanshugarg.mobilepong;

    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.app.Activity;
    import android.content.Context;
    import android.hardware.Sensor;
    import android.hardware.SensorEvent;
    import android.hardware.SensorEventListener;
    import android.hardware.SensorManager;
    import android.os.Bundle;
    import android.os.Vibrator;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.text.DecimalFormat;

    public class MainActivity extends AppCompatActivity implements SensorEventListener {

        private float lastX, lastY, lastZ;
        private SensorManager sensorManager;
        private Sensor accelerometer;
        private Sensor gyroscope;
        private boolean mInitialized = false;

        private float deltaX = 0;
        private float deltaY = 0;
        private float deltaZ = 0;

        private float roll = 0;
        private float pitch = 0;
        private float yaw = 0;

        private float vibrateThreshold = 0;

        private TextView currentX, currentY, currentZ, currentG1, currentG2, currentG3;
        public Vibrator v;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            initializeViews();
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                vibrateThreshold = accelerometer.getMaximumRange() / 2;
            } else {
                Toast.makeText(MainActivity.this, "No Accelerometer on Device", Toast.LENGTH_LONG).show();
            }

            if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
                gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
                vibrateThreshold = gyroscope.getMaximumRange() / 2;
            } else {
                Toast.makeText(MainActivity.this, "No Gyroscope on Device", Toast.LENGTH_LONG).show();
            }
            v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        }

        public void initializeViews() {
            currentX = (TextView) findViewById(R.id.currentX);
            currentY = (TextView) findViewById(R.id.currentY);
            currentZ = (TextView) findViewById(R.id.currentZ);
            currentG1 = (TextView) findViewById(R.id.currentG1);
            currentG2 = (TextView) findViewById(R.id.currentG2);
            currentG3 = (TextView) findViewById(R.id.currentG3);
        }

        protected void onResume() {
            super.onResume();
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }

        protected void onPause() {
            super.onPause();
            sensorManager.unregisterListener(this);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                double x = event.values[0];
                double y = event.values[1];
                double z = event.values[2];

                final double alpha = 0.8;

                double[] gravity = {0, 0, 0};

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                x = event.values[0] - gravity[0];
                y = event.values[1] - gravity[1];
                z = event.values[2] - gravity[2];
//
//


                //displayMaxValues();
//            deltaX = Math.abs(lastX - event.values[0]);
//            deltaY = Math.abs(lastY - event.values[1]);
//            deltaZ = Math.abs(lastZ - event.values[2]);

                if (!mInitialized) {
                    // sensor is used for the first time, initialize the last read values
                    lastX = (float) x;
                    lastY = (float) y;
                    lastZ = (float) z;
                    mInitialized = true;
                } else {
                    // sensor is already initialized, and we have previously read values.
                    // take difference of past and current values and decide which
                    // axis acceleration was detected by comparing values

                    deltaX = (float) Math.abs(lastX - x);
                    deltaY = (float) Math.abs(lastY - y);
                    deltaZ = (float) Math.abs(lastZ - z);
                    lastX = (float) x;
                    lastY = (float) y;
                    lastZ = (float) z;
                    if (deltaX < 0.1)
                        deltaX = 0;
                    if (deltaY < 0.1)
                        deltaY = 0;
                    if (deltaZ < 0.1)
                        deltaZ = 0;
//                if ((deltaZ > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
//                    v.vibrate(50);
//                }
                }

                displayCleanValuesAcc();
                displayCurrentValues();
            } else {
                roll = event.values[2];
                pitch = event.values[1];
                yaw = event.values[0];
                displayCleanValuesGyr();
                displayCurrentValues();
                if (roll < 0.1)
                    deltaX = 0;
                if (deltaY < 0.1)
                    deltaY = 0;
                if (deltaZ < 0.1)
                    deltaZ = 0;
            }
        }
        public void displayCleanValuesAcc() {
            currentX.setText("0.0");
            currentY.setText("0.0");
            currentZ.setText("0.0");
        }
        public void displayCleanValuesGyr() {
            currentG1.setText("0.0");
            currentG2.setText("0.0");
            currentG3.setText("0.0");
        }

        public void displayCurrentValues() {
            currentX.setText(Float.toString(deltaX));
            currentY.setText(Float.toString(deltaY));
            currentZ.setText(Float.toString(deltaZ));
            currentG1.setText(Float.toString(roll));
            currentG2.setText(Float.toString(pitch));
            currentG3.setText(Float.toString(yaw));
        }




    }
