package com.erdeanmich.todings.detail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Spinner
import com.erdeanmich.todings.R
import com.erdeanmich.todings.detail.priority.PrioritySpinnerAdapter

class DetailActivity : AppCompatActivity() {
    private val spinner: Spinner by lazy { findViewById(R.id.priority_spinner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val prioritySpinnerAdapter = PrioritySpinnerAdapter(this,R.layout.priority_spinner_item)
        spinner.adapter = prioritySpinnerAdapter
    }
}