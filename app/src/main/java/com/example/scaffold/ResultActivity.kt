package com.example.scaffold

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    lateinit var resultTextView: TextView
    lateinit var score: TextView
    lateinit var scoreText: TextView
    lateinit var intentButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        resultTextView = findViewById(R.id.result_textview)
        score = findViewById(R.id.result_score)
        scoreText = findViewById(R.id.result_motivation)
        intentButton = findViewById(R.id.again)

        val intent: Intent = getIntent()
        val winGame: Int = intent.getIntExtra("WINGAME", 0)
        val loseGame: Int = intent.getIntExtra("LOSEGAME", 0)
        val totalGame = winGame + loseGame
        resultTextView.text = "Total game played ${totalGame}"
        if (winGame>loseGame) {
            score.text ="Win game ${winGame} Lose game ${loseGame}"
            scoreText.text = "Your win game"
            score.setTextColor(Color.parseColor("#FFD700"))
            scoreText.setTextColor(Color.parseColor("#FFD700"))
        } else if(loseGame> winGame){
            score.text ="Win game ${winGame} Lose game ${loseGame}"
            scoreText.text = "Don't give up. Try again"
            score.setTextColor(Color.parseColor("#a19d94"))
            scoreText.setTextColor(Color.parseColor("#a19d94"))
        } else {
            score.text ="Win game ${winGame} Lose game ${loseGame}"
            scoreText.text = "Draw"
            score.setTextColor(Color.parseColor("#cd7f32"))
            scoreText.setTextColor(Color.parseColor("#cd7f32"))
        }
        // нужно вставить свою логику для показа результата

        intentButton.setOnClickListener{ view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}