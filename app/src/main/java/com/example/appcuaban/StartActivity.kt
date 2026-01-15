package com.example.appcuaban
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        findViewById<Button>(R.id.btnVsAI).setOnClickListener { startToGame("AI") }
        findViewById<Button>(R.id.btnTwoPlayers).setOnClickListener { startToGame("PVP") }
    }
    private fun startToGame(mode: String) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("MODE", mode)
        startActivity(intent)
    }
}