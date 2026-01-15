package com.example.appcuaban

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    
    
    private var score1 = 0
    private var score2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val mode = intent.getStringExtra("MODE") ?: "PVP"
        
        val caroView = findViewById<CaroView>(R.id.caroView)
        val tvTitle = findViewById<TextView>(R.id.tvTitle) 
        val tvScore1 = findViewById<TextView>(R.id.tvScore1)
        val tvScore2 = findViewById<TextView>(R.id.tvScore2)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val imgPlayer2 = findViewById<ImageView>(R.id.imgPlayer2)

        caroView.setMode(mode)

        
        if (mode == "PVP") {
            tvTitle.text = "PVP"
            imgPlayer2.setImageResource(R.drawable.ic_user) 
        } else {
            tvTitle.text = "AI"
            imgPlayer2.setImageResource(R.drawable.ic_robot)
        }

        
        caroView.onGameEndListener = { winner ->
            if (winner == 1) {
                score1++
                tvScore1.text = score1.toString()
                showToast("Người chơi 1 Thắng!")
            } else {
                score2++
                tvScore2.text = score2.toString()
                val msg = if (mode == "AI") "Máy Thắng!" else "Người chơi 2 Thắng!"
                showToast(msg)
            }
        }

        btnReset.setOnClickListener {
            caroView.reset()
        }

        btnBack.setOnClickListener {
            finish() 
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}