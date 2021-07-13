package com.rikucherry.mycalculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rikucherry.mycalculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName

    private lateinit var binding: ActivityMainBinding
    var lastNumeric = false
    var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun onDigit(view: View) {
        binding.tvInput.append((view as Button).text)
        lastNumeric = true
    }

    fun onClear(view: View) {
        binding.tvInput.text = ""
        lastNumeric = false
        lastDot = false
    }

    //Only append a dot when last input char is a numeric but not also a dot(only allow one dot)
    fun onDecimalPoint(view: View) {
        if (lastNumeric && !lastDot) {
            binding.tvInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onOperator(view: View) {
        val text = when (resources.getResourceEntryName(view.id)) {
            "btn_divide" -> "/"
            "btn_multiply" -> "*"
            "btn_subtract" -> "-"
            "btn_add" -> "+"
            else -> ""
        }

        if ((binding.tvInput.text.isEmpty() && text == "-")
            || canAddOperator(binding.tvInput.text.toString())) {
            binding.tvInput.append(text)
            lastNumeric = false
            lastDot = false
        }
    }


    fun onCalculation(view: View) {
        if (lastNumeric) {
            var equation = binding.tvInput.text.toString()
            Log.d(TAG, "The equation to calculate is : $equation")
            try {
                val result = evalFromString(equation).toString()

                result.replaceFirst("\\.0*$|(\\.\\d*?)0+$", "$1")

                binding.tvInput.text = result
                lastNumeric = true
                lastDot = result.contains(".")

            } catch (e: ArithmeticException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun canAddOperator(input: String) : Boolean {
        return input.isNotEmpty() && (Character.isDigit(input.last()))
    }

    private fun evalFromString(equation: String) : Float {
        val split = equation.toCharArray()
        val calUnits = ArrayList<String>()

        //Combine chars into numbers or left it as an operator, then store them into an array-list
        //the result would be like {-, 2.03, * , 5.23}
        var unit: String = ""
        for (i in split.indices) {
            when (val char = split[i].toString()) {
                "-", "+", "*", "/" -> {
                    //if there is already something stored in the current calculation unit,
                    //we need to store the operator separately into the next one.
                    if (unit.isNotEmpty()) {
                        calUnits.add(unit)
                        unit = ""
                    }

                    unit += char
                    calUnits.add(unit)
                    unit = ""
                }

                else -> {
                    unit += char
                    // stop loop when reach the end, otherwise continue to check patterns as above
                    if (i == split.size - 1) {
                        calUnits.add(unit)
                    }
                }
            }
        }

        //eliminate leading zeros
        calUnits.forEach { it.replaceFirst("^0+(?!$)", "") }

        Log.d(TAG, "Result calUnits is : $calUnits")
        return calResult(calUnits)
    }


    //refer to https://www.programmersought.com/article/17237084603/
    private fun calResult(calUnits: ArrayList<String>): Float {
        var left: Float
        var right: Float
        var result = 0f
        var count = 0 //sum of multiplication and division operators
        var total = 0f
        var addOrSubtract = 0

        (0 until calUnits.size)
            .filter { calUnits[it] == "*" || calUnits[it] == "/" }
            .forEach { _ -> count += 1 }

        for (m in 0..count) {
            loop@ for (n in 0..calUnits.size - 2) {
                when (val unit = calUnits[n]) {
                    "*", "/" -> {
                        left = calUnits[n - 1].toFloat()
                        right = calUnits[n + 1].toFloat()
                        if ("*" == unit) {
                            result = (left * right)
                            Log.d(TAG, "Calculated $left * $right = $result")
                        } else if ("/" == unit) {
                            Log.d(TAG, "Calculated $left / $right = $result")
                            result = left / right
                        }

                        calUnits[n] = result.toString()

                        calUnits.removeAt(n + 1)
                        calUnits.removeAt(n - 1)
                        break@loop //TODO: learn more
                    }
                }
            }
        }

        Log.d(TAG, "After / and * were calculated, the calUnits became to: $calUnits")
        for (m in 0 until calUnits.size) {
            when(val unit = calUnits[m]){
                "+", "-" -> {
                    if ("+" == unit) {
                        addOrSubtract = 0
                    } else if ("-" == unit) {
                        addOrSubtract = 1
                    }
                }

                else -> {
                    if (addOrSubtract == 0) {
                        total += calUnits[m].toFloat()
                        Log.d(TAG, " + ${calUnits[m].toDouble()}, total is $total. ")
                    } else if (addOrSubtract == 1) {
                        total -= calUnits[m].toFloat()
                        Log.d(TAG, " - ${calUnits[m].toDouble()}, total is $total. ")
                    }
                }
            }
        }

        return total
    }

}