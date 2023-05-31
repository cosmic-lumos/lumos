package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ContentView
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var mAC: Sensor? = null
    private var acText:TextView? = null
    private var btToggle:Button? = null
    private var timeState:TextView? = null
    private var canSense = false
    private var startTime:Long = 0
    private var endTime:Long = 0
    private var dataSize: Int = 0

    private val maxArraySize:Int = 17

    private var dataArray = Array(3){FloatArray(maxArraySize)}

    private fun onClickSensingButton(): Unit{
        startTime = SystemClock.elapsedRealtime()
        dataSize = 0
        canSense = true
        Handler(Looper.getMainLooper()).postDelayed({
            canSense = false
            TODO("측정한 센서 데이터 dataArray를 Http 통신하는 과정 필요")
        },1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        acText = findViewById(R.id.tv_gyro)
        btToggle = findViewById(R.id.bt_toggle)
        timeState = findViewById(R.id.tv_time)

        btToggle?.setOnClickListener {
            onClickSensingButton()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAC = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        Toast.makeText(this, if (mAC != null) "Good" else "Bad" , Toast.LENGTH_LONG).show()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if(canSense){
            for(i in 0..2)
                dataArray[i][dataSize] = event.values[i]
            acText?.text = event.values[0].toString() + ", " +
                    event.values[1].toString() + ", " +
                    event.values[2].toString()
            endTime = SystemClock.elapsedRealtime()
            timeState?.text = (endTime - startTime).toString()

            println(
                "${dataSize++}, ${timeState?.text} " +
                        "[x]: ${event.values[0].toString()}\t" +
                        "[y]: ${event.values[1].toString()}\t" +
                        "[z]: ${event.values[2].toString()}"
            )
        }
        else{
            acText?.text = "Love you"
            timeState?.text = "0"
        }
    }

    override fun onResume() {
        super.onResume()
        mAC?.also{
            ac -> sensorManager.registerListener(this, ac, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }

    private fun request() {
        try {
            val url = URL("http://naver.com")

            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

            if(conn.responseCode == HttpURLConnection.HTTP_OK){
                val streamReader = InputStreamReader(conn.inputStream)
                val buffered = BufferedReader(streamReader)

                val content = StringBuilder()
                while(true){
                    val line = buffered.readLine() ?: break
                    content.append(line)
                }

                buffered.close()
                conn.disconnect()
                runOnUiThread{
                    acText?.text = content.toString()
                }
            }
        }
        catch(e: Exception){
            e.printStackTrace()
        }
    }
}