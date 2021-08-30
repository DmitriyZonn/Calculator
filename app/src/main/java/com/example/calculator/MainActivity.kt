package com.example.calculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

enum class ButtonTypes{NONE, DIGIT, OPERATION, DOT, EQUALS, ERROR}

class MainActivity : AppCompatActivity() {
    var lastButton: ButtonTypes = ButtonTypes.NONE
    var dotInNumber: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onDigitButtonClick(view: View) {
        val textView = findViewById<TextView>(R.id.text_view)
        val button = view as Button

        if(lastButton == ButtonTypes.EQUALS || lastButton == ButtonTypes.ERROR)
            textView.text = button.text
        else
            textView.append(button.text)
        lastButton = ButtonTypes.DIGIT
    }

    fun onOperationButtonClick(view: View) {
        val textView = findViewById<TextView>(R.id.text_view)
        val button = view as Button
        if(lastButton == ButtonTypes.DIGIT || lastButton == ButtonTypes.EQUALS) {
            textView.append(button.text)
            lastButton = ButtonTypes.OPERATION
            dotInNumber = false
        }
        if(lastButton == ButtonTypes.OPERATION){
            val text = textView.text
            textView.text = text.substring(0..text.length-2) + button.text
            lastButton = ButtonTypes.OPERATION
            dotInNumber = false
        }
    }

    fun onDotButtonClick(view: View) {
        val textView = findViewById<TextView>(R.id.text_view)
        val button = view as Button
        if(lastButton == ButtonTypes.DIGIT && !dotInNumber) {
            textView.append(button.text)
            lastButton = ButtonTypes.DOT
            dotInNumber = true
        }
    }

    fun onEqualsButtonClick(view: View) {
        val textView = findViewById<TextView>(R.id.text_view)
        try {
            val result = evaluate(textView.text.toString()).toString()
            textView.text = result
            lastButton = ButtonTypes.EQUALS
        } catch (e: Exception){
            textView.text = e.message
            lastButton = ButtonTypes.ERROR
        }
    }

    fun onSettingsButtonClick(view: View){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}

val operations = listOf<Char>('+','-','*','/')
fun evaluate(string: String): Double{
    for(operation in operations) {
        val operationPos = string.lastIndexOf(operation)
        if(operationPos>=0) {
            val leftString = string.substring(0..operationPos - 1)
            val rightString = string.substring(operationPos + 1..string.length - 1)
            val leftNumber = evaluate(leftString)
            val rightNumber = evaluate(rightString)
            when (operation) {
                '+' -> return leftNumber + rightNumber
                '-' -> return leftNumber - rightNumber
                '*' -> return leftNumber * rightNumber
                '/' -> return if (rightNumber != 0.0) leftNumber / rightNumber else throw Exception("Division by zero")
            }
        }
    }
    return string.toDouble()
}