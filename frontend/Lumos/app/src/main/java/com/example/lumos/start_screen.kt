package com.example.lumos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class start_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_screen)

        //Intent 를 활용해서 액티비티 이동하기
        val intent = Intent(this, wifi_selection::class.java)

        //버튼 지정
        val buttonView = findViewById<Button>(R.id.btn_start)
        //클릭리스너
        buttonView.setOnClickListener {
            //액티비티 이동
            startActivity(intent)
        }
    }
}