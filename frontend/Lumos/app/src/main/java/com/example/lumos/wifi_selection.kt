package com.example.lumos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import com.example.lumos.databinding.ActivityMainBinding
import com.example.lumos.databinding.ActivityWifiSelectionBinding


class wifi_selection : AppCompatActivity() {

    private val binding: ActivityWifiSelectionBinding by lazy { ActivityWifiSelectionBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        //Intent 를 활용해서 액티비티 이동하기
        val intent = Intent(this, MainActivity::class.java)

        //버튼 지정
        val buttonView = binding.backbtn

        //클릭리스너
        buttonView.setOnClickListener {
            //액티비티 이동
            startActivity(intent)
        }

    }
}