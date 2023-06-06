package com.example.lumos

import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.activity_main.*
//import com.example.filwallet.databinding.ActivityMainBinding
import com.example.lumos.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), SensorEventListener {


    //private lateinit var binding: ActivityMainBinding
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //private var mBinding: ActivityMainBinding? = null
    //private val binding get() = mBinding!!
    private lateinit var sensorManager: SensorManager
    private var mAC: Sensor? = null

    private var canSense = false
    private var dataSize: Int = 0
    private var gestureType: String = ""

    private val maxArraySize:Int = 1000

    private var dataArray = Array(3){FloatArray(maxArraySize)}


    private fun onClickSensingButton(): Unit{

        dataSize = 0
        canSense = true

        Handler(Looper.getMainLooper()).postDelayed({
            canSense = false
            val seriesData = JSONArray()
            for(i in 0 until 90){
                val stepData = JSONObject()
                stepData.put("xValue", dataArray[0][i])
                stepData.put("yValue", dataArray[1][i])
                stepData.put("zValue", dataArray[2][i])
                seriesData.put(stepData)
            }
            val jsonPostData = JSONObject()
            jsonPostData.put("sensorData", seriesData)
            jsonPostData.put("gestureType", true)

            thread(start = true){
                val client = OkHttpClient()

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonPostData.toString().toRequestBody(mediaType)

                val request = Request.Builder()
//                    .url("http://192.168.0.18/"+if(isPredict) "gesture/predict/" else "api/sensor/")
                    .url("http://192.168.0.18:8000/gesture/predict/")
                    .post(requestBody)
                    .build()
                try{
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful){
                        val responseBody = response.body?.string()
                        println(responseBody)

                    } else{
                        println("HTTP Error: "+response.code)
                    }
                }
                catch(e: Exception){
                    println(e.toString())
                }
            }
        },1000)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAC = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)



        //setContentView(R.layout.activity_main)
        //val progress: ProgressBar = findViewById<View>(R.id.progress_status) as ProgressBar
        // progress.setProgress(30)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //val view = binding.root
        setContentView(binding.root)

        //Intent 를 활용해서 액티비티 이동하기
        val intent = Intent(this, wifi_selection::class.java)

        //버튼 지정
        val buttonView = binding.settingBtn

        //클릭리스너
        buttonView.setOnClickListener {
            //액티비티 이동
            startActivity(intent)
        }

        binding.controlBtn.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                applicationContext,
                "핸드폰을 휘두르세요!",
                Toast.LENGTH_SHORT
            ).show()
            onClickSensingButton()
        })

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if(canSense){
            for(i in 0..2)
                dataArray[i][dataSize] = event.values[i]
//            acText?.text = event.values[0].toString() + ", " +
//                    event.values[1].toString() + ", " +
//                    event.values[2].toString()

//            println(
//                "${dataSize}, ${timeState?.text} " +
//                        "[x]: ${event.values[0].toString()}\t" +
//                        "[y]: ${event.values[1].toString()}\t" +
//                        "[z]: ${event.values[2].toString()}"
//            )
            dataSize++
        }
    }

    override fun onResume() {
        super.onResume()
        mAC?.also{
                ac -> sensorManager.registerListener(this, ac, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }

}