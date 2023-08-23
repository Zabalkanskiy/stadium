package com.example.stadium

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.scaffold.R

class StadiumInfoActuvity : AppCompatActivity() {
    lateinit var infoTextView: TextView
    lateinit var intentButton: Button
    lateinit var imageView: ImageView
    lateinit var titleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info)
         val getIntent = intent
         val position = getIntent.getIntExtra("POSITION", 0)
         val question: Question = mQuestionBank[position]
        titleTextView = findViewById(R.id.title_textView)
        infoTextView = findViewById(R.id.info_textview)
        intentButton =findViewById(R.id.info_back_button)
        imageView = findViewById(R.id.stadium_info_image)

        imageView.setImageResource(question.descriptionImage)

        infoTextView.text = question.descriptionStadium

        titleTextView.text = question.nameStadium

        intentButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
}