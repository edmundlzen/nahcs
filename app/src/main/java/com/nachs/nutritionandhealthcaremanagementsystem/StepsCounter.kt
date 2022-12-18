package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class StepsCounter : AppCompatActivity() {
    private var sensorManager: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps_counter)

        val lastCurrentSteps =
            applicationContext?.getSharedPreferences("data", MODE_PRIVATE)?.getInt(
                "currentSteps",
                0
            )
        findViewById<TextView>(R.id.tvSteps).text = lastCurrentSteps.toString()
        if (lastCurrentSteps != null) {
            findViewById<TextView>(R.id.tvCaloriesBurned).text =
                (roundOffDecimal(lastCurrentSteps * 0.04)).toString()
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                1
            )
        } else {
            val sm = getSystemService(SENSOR_SERVICE) as SensorManager
            val stepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            val sc = StepCount()
            sc.setTvSteps(findViewById(R.id.tvSteps))
            sc.setTvCaloriesBurned(findViewById(R.id.tvCaloriesBurned))
            sm.registerListener(sc, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val sm = getSystemService(SENSOR_SERVICE) as SensorManager
                val stepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                sm.registerListener(StepCount(), stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    class StepCount : android.hardware.SensorEventListener {
        private var tvSteps: TextView? = null
        private var tvCaloriesBurned: TextView? = null

        fun setTvSteps(view: TextView) {
            this.tvSteps = view
        }

        fun setTvCaloriesBurned(view: TextView) {
            this.tvCaloriesBurned = view
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: android.hardware.SensorEvent?) {
            if (event != null) {
                val lastDailyStepsRecord =
                    tvSteps?.context?.getSharedPreferences("data", MODE_PRIVATE)?.getInt(
                        "dailyStepsRecord",
                        0
                    )
                val lastDailyStepsRecordUpdateDate =
                    tvSteps?.context?.getSharedPreferences("data", MODE_PRIVATE)?.getLong(
                        "dailyStepsRecordUpdateDate",
                        Date().time
                    )

                if (lastDailyStepsRecord == 0 || (Date().time - Date(lastDailyStepsRecordUpdateDate!!).time > 1000 * 60 * 60 * 24)) {
                    tvSteps?.context?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putInt(
                        "dailyStepsRecord",
                        event.values[0].toInt()
                    )?.apply()
                    tvSteps?.context?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putLong(
                        "dailyStepsRecordUpdateDate",
                        Date().time
                    )?.apply()

                    tvSteps?.text = "0"
                    Log.d("StepsCounter", "Daily steps record reset")
                } else {
                    tvSteps?.text = (event.values[0].toInt() - lastDailyStepsRecord!!).toString()
                    Log.d("StepsCounter", "Using daily steps record")
                }
                tvSteps?.context?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putInt(
                    "currentSteps",
                    event.values[0].toInt() - lastDailyStepsRecord!!
                )?.apply()
                val caloriesBurned = (event.values[0].toInt() - lastDailyStepsRecord!!) * 0.04
                tvCaloriesBurned?.text = roundOffDecimal(caloriesBurned).toString()
            }
        }

        fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
}