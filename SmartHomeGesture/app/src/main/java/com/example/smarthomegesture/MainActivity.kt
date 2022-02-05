package com.example.smarthomegesture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView

import android.widget.ArrayAdapter
import android.widget.Spinner

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner: Spinner = findViewById(R.id.spinner_gesture_list)
                ArrayAdapter.createFromResource(
            this,
            R.array.gesture_list_names,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // Apply the adapter to the spinner
                    spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val value= parent?.selectedItem.toString() //getSelectedItem().toString()
                if(position !=0 ) {
                    val intent = Intent(this@MainActivity, SecondPage::class.java)
                    intent.setClassName(
                        applicationContext,
                        "com.example.smarthomegesture.SecondPage"
                    )
                    intent.putExtra("position", position.toString())
                    intent.putExtra("gesture", value)
                    startActivity(intent)
                }

            }

        }

    }

}

