package com.rikucherry.quiz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_quiz_question.*

class QuizQuestionActivity : AppCompatActivity(), OnClickListener {
    val TAG = this.javaClass.simpleName

    private var mCurrentPosition: Int = 1
    private var mQuestionList: ArrayList<Question>? = null
    private var mSelectedOptionPosition: Int = 0
    private var mCorrectAnswer: Int = 0
    private var mQuestionCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        mQuestionList = Constants.getQuestions()
        mQuestionCount = mQuestionList!!.size
        setQuestion()

        tv_option1.setOnClickListener(this)
        tv_option2.setOnClickListener(this)
        tv_option3.setOnClickListener(this)
        tv_option4.setOnClickListener(this)
    }

    private fun setQuestion() {

        val currentQuestion  = mQuestionList!![mCurrentPosition - 1]
        defaultOptionsView()

        if(mCurrentPosition == mQuestionList!!.size) {
            btn_submit.text = "完成"
        } else {
            btn_submit.text = "提交"
        }

        progress_bar.progress = mCurrentPosition
        tv_progress.text = "$mCurrentPosition" + "/" + progress_bar.max
        tv_question.text = currentQuestion!!.question
        iv_flag_image.setImageResource(currentQuestion.image)
        tv_option1.text = currentQuestion.option1
        tv_option2.text = currentQuestion.option2
        tv_option3.text = currentQuestion.option3
        tv_option4.text = currentQuestion.option4
    }

    private fun defaultOptionsView() {
        val options = ArrayList<TextView>()
        options.add(0, tv_option1)
        options.add(1, tv_option2)
        options.add(2, tv_option3)
        options.add(3, tv_option4)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }
    }


    fun onClickSubmit(view: View) {
        if (mSelectedOptionPosition == 0) {
            //nothing selected
            mCurrentPosition ++ //go to the next question

            when{
                //回答进度小于等于100%
                mCurrentPosition <= mQuestionList!!.size -> {
                    setQuestion()
                } else -> {
                    Toast.makeText(this, "恭喜您完成问答！", Toast.LENGTH_SHORT).show()

                    var intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(Constants.INTENT_QUIZ_TOTAL, mQuestionCount)
                    intent.putExtra(Constants.INTENT_QUIZ_CORRECT, mCorrectAnswer)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            //something selected
            val question = mQuestionList?.get(mCurrentPosition - 1)
            if(question!!.correctAnswer != mSelectedOptionPosition) {
                answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
            } else {
                mCorrectAnswer++
            }
            answerView(question.correctAnswer, R.drawable.correct_option_border_bg)

            if(mCurrentPosition == mQuestionList!!.size) {
                btn_submit.text = "完成"
            } else {
                btn_submit.text = "下一题"
            }
            mSelectedOptionPosition = 0
        }
    }

    //onClick options
    override fun onClick(v: View?) {
        //change frame color
        when (v?.id) {
            R.id.tv_option1 -> selectOptionView(tv_option1, 1)
            R.id.tv_option2 -> selectOptionView(tv_option2, 2)
            R.id.tv_option3 -> selectOptionView(tv_option3, 3)
            R.id.tv_option4 -> selectOptionView(tv_option4, 4)
        }

    }

    private fun selectOptionView(tv: TextView, selectedOptionId: Int) {
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionId
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_option_border_bg
        )
    }

    private fun answerView(answer: Int, drawableView: Int){
        when (answer) {
            1 -> tv_option1.background = ContextCompat.getDrawable(this, drawableView)
            2 -> tv_option2.background = ContextCompat.getDrawable(this, drawableView)
            3 -> tv_option3.background = ContextCompat.getDrawable(this, drawableView)
            4 -> tv_option4.background = ContextCompat.getDrawable(this, drawableView)
        }
    }
}