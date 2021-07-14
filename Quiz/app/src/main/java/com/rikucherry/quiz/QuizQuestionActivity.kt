package com.rikucherry.quiz

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_quiz_question.*

class QuizQuestionActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        val questionList = Constants.getQuestions()

        val currentPosition = 1
        val currentQuestion: Question? = questionList[currentPosition - 1]
        progress_bar.progress = currentPosition
        tv_progress.text = "$currentPosition" + "/" + progress_bar.max
        tv_question.text = currentQuestion!!.question
        iv_flag_image.setImageResource(currentQuestion.image)
        tv_option1.text = currentQuestion.option1
        tv_option2.text = currentQuestion.option2
        tv_option3.text = currentQuestion.option3
        tv_option4.text = currentQuestion.option4

    }

    fun onClickSubmit(view: View) {}
}