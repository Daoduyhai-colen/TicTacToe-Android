package com.example.appcuaban

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // 1. Khai báo các biến dùng chung cho cả game
    // Mảng 2 chiều chứa 9 nút bấm (3 hàng x 3 cột)
    private val buttons = Array(3) { arrayOfNulls<Button>(3) }
    
    // Biến xác định lượt đi: true là X đi, false là O đi
    private var player1Turn = true
    
    // Biến đếm số lượt đã đi (để biết khi nào hòa)
    private var roundCount = 0
    
    // Biến tham chiếu đến dòng chữ "Lượt chơi: X"
    private var textViewPlayer: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Kết nối Code với Giao diện (Ánh xạ ID)
        textViewPlayer = findViewById(R.id.player_turn_text)

        // Dùng vòng lặp để lấy ID của 9 nút (button00, button01...)
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                // Tạo tên ID động: "button00", "button01", v.v.
                val buttonID = "button$i$j"
                val resID = resources.getIdentifier(buttonID, "id", packageName)
                
                // Gán nút vào mảng và đặt chế độ lắng nghe sự kiện bấm
                buttons[i][j] = findViewById(resID)
                buttons[i][j]?.setOnClickListener(this)
            }
        }

        // Cài đặt nút Reset
        val buttonReset = findViewById<Button>(R.id.reset_button)
        buttonReset.setOnClickListener {
            resetGame()
        }
    }

    // 3. Hàm xử lý khi người dùng bấm vào một ô cờ
    override fun onClick(v: View?) {
        // Ép kiểu View thành Button để xử lý
        val button = v as? Button ?: return

        // Nếu ô này đã có chữ (đã đánh rồi) thì không cho đánh lại
        if (button.text.toString() != "") {
            return
        }

        // Logic đánh cờ:
        if (player1Turn) {
            button.text = "X"
            button.setTextColor(Color.RED) // X màu Đỏ
            textViewPlayer?.text = "Lượt chơi: O"
        } else {
            button.text = "O"
            button.setTextColor(Color.BLUE) // O màu Xanh
            textViewPlayer?.text = "Lượt chơi: X"
        }

        // Tăng số lượt đã đánh lên 1
        roundCount++

        // Kiểm tra xem ai thắng chưa
        if (checkForWin()) {
            if (player1Turn) {
                playerWins("X thắng!")
            } else {
                playerWins("O thắng!")
            }
        } else if (roundCount == 9) {
            // Nếu đánh hết 9 ô mà chưa ai thắng -> Hòa
            playerWins("Hòa rồi!")
        } else {
            // Đổi lượt chơi cho người kia
            player1Turn = !player1Turn
        }
    }

    // 4. Thuật toán kiểm tra chiến thắng (Trọng tài)
    private fun checkForWin(): Boolean {
        val field = Array(3) { arrayOfNulls<String>(3) }

        // Lấy toàn bộ chữ (X hoặc O) trên bàn cờ ra để so sánh
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                field[i][j] = buttons[i][j]?.text.toString()
            }
        }

        // Kiểm tra 3 hàng ngang
        for (i in 0 until 3) {
            if (field[i][0] == field[i][1] && field[i][0] == field[i][2] && field[i][0] != "") {
                return true
            }
        }

        // Kiểm tra 3 hàng dọc
        for (i in 0 until 3) {
            if (field[0][i] == field[1][i] && field[0][i] == field[2][i] && field[0][i] != "") {
                return true
            }
        }

        // Kiểm tra 2 đường chéo
        if (field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0] != "") {
            return true
        }

        if (field[0][2] == field[1][1] && field[0][2] == field[2][0] && field[0][2] != "") {
            return true
        }

        return false
    }

    // 5. Hàm xử lý khi có người thắng
    private fun playerWins(message: String) {
        textViewPlayer?.text = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        // Khóa bàn cờ lại không cho đánh tiếp? Thôi cứ để vậy cho đơn giản,
        // hoặc gọi resetGame() ngay nếu muốn. Ở đây mình giữ nguyên hiện trường.
    }

    // 6. Hàm Reset game (Xóa bàn cờ làm lại)
    private fun resetGame() {
        roundCount = 0
        player1Turn = true
        textViewPlayer?.text = "Lượt chơi: X"

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                buttons[i][j]?.text = ""
            }
        }
    }
}