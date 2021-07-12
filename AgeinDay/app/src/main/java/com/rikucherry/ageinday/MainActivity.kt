package com.rikucherry.ageinday

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG: String? = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_select_date.setOnClickListener {
            view -> onClickDatePicker(view)
        }
    }

    private fun onClickDatePicker(view: View) {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        //Use datePicker dialog
        /*Java: DatePickerDialog datePicker = new DatePickerDialog(this,(v, y, m, d) -> { }, year, month, day)
                FunctionalInterface: OnDateSetListener
        */
        val picker = DatePickerDialog(this,
            {v, y, m, d ->
                //When click OK
                val theDate = "$y/${(m + 1)}/$d"

                //format the date
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
                val selectedDate = sdf.parse(theDate)

                try {
                    Log.d(TAG, "The chosen date is ${sdf.format(selectedDate)}")
                    tv_selected_date.text = sdf.format(selectedDate)

                    val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))
                    Log.d(TAG, "Current date is ${sdf.format(System.currentTimeMillis())}")
                    val diffDate = (currentDate!!.time /60_000/60/24) - (selectedDate!!.time / 60_000/60/24)
                    tv_age_in_day.text = diffDate.toString()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            , year
            , month
            , day )

        picker.datePicker.maxDate = Date().time - 86_400_000 //Yesterday
        picker.show()
    }
}