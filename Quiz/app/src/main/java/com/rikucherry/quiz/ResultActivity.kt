package com.rikucherry.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        var quizTotal = intent.getIntExtra(Constants.INTENT_QUIZ_TOTAL, 0)
        var quizCorrect = intent.getIntExtra(Constants.INTENT_QUIZ_CORRECT, 0)

        var sp = this.getSharedPreferences(getString(R.string.key_preference),Context.MODE_PRIVATE) ?: return
        val userName = sp.getString(Constants.SP_USER_NAME, "")
        tv_congratulations.text = getString(R.string.text_congratulations, userName)

        tv_score.text = getString(R.string.text_score, quizTotal, quizCorrect)
    }

    fun onClickRestart(view: View) {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun onClickFinish(view: View) {
        finish()
    }
}