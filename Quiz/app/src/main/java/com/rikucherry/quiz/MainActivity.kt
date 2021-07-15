package com.rikucherry.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var sp = this.getSharedPreferences(getString(R.string.key_preference),Context.MODE_PRIVATE) ?: return
        val preUserName = sp.getString(Constants.SP_USER_NAME, "")
        et_name.setText(preUserName)

    }

    fun onClickStart(view: View) {
        if(et_name.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        } else {
            var sp = this.getSharedPreferences(getString(R.string.key_preference),Context.MODE_PRIVATE) ?: return
            with(sp.edit()) {
                putString(Constants.SP_USER_NAME, et_name.text.toString())
                apply()
            }

            val intent = Intent(this,QuizQuestionActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}