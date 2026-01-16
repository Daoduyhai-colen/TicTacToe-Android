package com.example.appcuaban

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CaroView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val board = Array(15) { IntArray(15) { 0 } }
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    
    private var cellSize = 0f
    private val boardPadding = 8f 
    private var mode = "PVP"
    private var isPlayer1Turn = true
    private var isGameOver = false

    private val colorGrid = Color.parseColor("#B0BEC5")
    private val colorX = Color.parseColor("#FF4444")
    private val colorO = Color.parseColor("#448AFF")

    var onGameEndListener: ((winner: Int) -> Unit)? = null

    fun setMode(m: String?) {
        mode = m ?: "PVP"
        reset()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width < height) width else height
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0) return

        cellSize = (width.toFloat() - boardPadding * 2) / 15
        
        paint.color = colorGrid
        paint.strokeWidth = 2f
        for (i in 0..15) {
            val pos = i * cellSize + boardPadding
            canvas.drawLine(boardPadding, pos, width.toFloat() - boardPadding, pos, paint)
            canvas.drawLine(pos, boardPadding, pos, height.toFloat() - boardPadding, paint)
        }

        paint.strokeWidth = 12f
        for (r in 0..14) for (c in 0..14) {
            val pad = cellSize * 0.28f
            val left = c * cellSize + boardPadding
            val top = r * cellSize + boardPadding
            
            if (board[r][c] == 1) {
                paint.color = colorX
                canvas.drawLine(left + pad, top + pad, left + cellSize - pad, top + cellSize - pad, paint)
                canvas.drawLine(left + cellSize - pad, top + pad, left + pad, top + cellSize - pad, paint)
            } else if (board[r][c] == 2) {
                paint.color = colorO
                canvas.drawCircle(left + cellSize / 2, top + cellSize / 2, cellSize / 2 - pad, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameOver || event.action != MotionEvent.ACTION_DOWN) return true

        val c = ((event.x - boardPadding) / cellSize).toInt()
        val r = ((event.y - boardPadding) / cellSize).toInt()

        if (r in 0..14 && c in 0..14 && board[r][c] == 0) {
            val currentPlayer = if (mode == "PVP") (if (isPlayer1Turn) 1 else 2) else 1
            board[r][c] = currentPlayer
            invalidate()

            if (checkWin(r, c, currentPlayer)) {
                endGame(currentPlayer)
            } else {
                if (mode == "PVP") isPlayer1Turn = !isPlayer1Turn
                else postDelayed({ aiSmartMove() }, 400)
            }
        }
        return true
    }

    
    private fun aiSmartMove() {
        if (isGameOver) return
        var bestScore = Int.MIN_VALUE; var bestR = -1; var bestC = -1
        for (r in 0..14) for (c in 0..14) {
            if (board[r][c] == 0 && hasNeighbor(r, c)) {
                val score = evaluate(r, c)
                if (score > bestScore) { bestScore = score; bestR = r; bestC = c }
            }
        }
        if (bestR == -1) { if (board[7][7] == 0) { bestR = 7; bestC = 7 } else { bestR = 6; bestC = 6 } }
        board[bestR][bestC] = 2
        invalidate()
        if (checkWin(bestR, bestC, 2)) endGame(2)
    }

    private fun hasNeighbor(r: Int, c: Int): Boolean {
        for (dr in -1..1) for (dc in -1..1) {
            if (dr == 0 && dc == 0) continue
            val nr = r + dr; val nc = c + dc
            if (nr in 0..14 && nc in 0..14 && board[nr][nc] != 0) return true
        }
        return false
    }

    private fun evaluate(r: Int, c: Int): Int {
        var score = 0
        board[r][c] = 2; score += evaluateLines(r, c, 2) * 2
        board[r][c] = 1; score += (evaluateLines(r, c, 1) * 1.5).toInt()
        board[r][c] = 0
        return score
    }

    private fun evaluateLines(r: Int, c: Int, player: Int): Int {
        var totalScore = 0
        val dirs = arrayOf(Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(1, -1))
        for (d in dirs) {
            var count = 1; var openEnds = 0
            var nr = r + d.first; var nc = c + d.second
            while (nr in 0..14 && nc in 0..14 && board[nr][nc] == player) { count++; nr += d.first; nc += d.second }
            if (nr in 0..14 && nc in 0..14 && board[nr][nc] == 0) openEnds++
            nr = r - d.first; nc = c - d.second
            while (nr in 0..14 && nc in 0..14 && board[nr][nc] == player) { count++; nr -= d.first; nc -= d.second }
            if (nr in 0..14 && nc in 0..14 && board[nr][nc] == 0) openEnds++
            if (count >= 5) totalScore += 100000
            else if (count == 4) totalScore += if (openEnds == 2) 10000 else 1000
            else if (count == 3) totalScore += if (openEnds == 2) 1000 else 10
            else if (count == 2) totalScore += if (openEnds == 2) 100 else 10
        }
        return totalScore
    }

    private fun checkWin(r: Int, c: Int, player: Int): Boolean {
        val dirs = arrayOf(Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(1, -1))
        for (d in dirs) {
            var count = 1
            var nr = r + d.first; var nc = c + d.second
            while (nr in 0..14 && nc in 0..14 && board[nr][nc] == player) { count++; nr += d.first; nc += d.second }
            nr = r - d.first; nc = c - d.second
            while (nr in 0..14 && nc in 0..14 && board[nr][nc] == player) { count++; nr -= d.first; nc -= d.second }
            if (count >= 5) return true
        }
        return false
    }

    private fun endGame(winner: Int) { isGameOver = true; onGameEndListener?.invoke(winner) }

    fun reset() {
        for (i in 0..14) for (j in 0..14) board[i][j] = 0
        isPlayer1Turn = true; isGameOver = false; invalidate()
    }
}