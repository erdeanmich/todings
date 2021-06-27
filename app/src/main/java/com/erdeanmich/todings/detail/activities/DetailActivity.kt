package com.erdeanmich.todings.detail.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.erdeanmich.todings.R
import com.erdeanmich.todings.detail.fragments.DatePickerFragment
import com.erdeanmich.todings.detail.fragments.DateReceiver
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoPriority
import com.erdeanmich.todings.util.CheckboxDrawableProvider
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(), DateReceiver {
    private lateinit var toDoItem : ToDoItem
    private val title: EditText by lazy { findViewById(R.id.title_edit) }
    private val description: EditText by lazy { findViewById(R.id.description_edit) }
    private val spinner: Spinner by lazy { findViewById(R.id.priority_spinner) }
    private val deadline: EditText by lazy { findViewById(R.id.deadline) }
    private val toDoCheckbox: ImageView by lazy { findViewById(R.id.detail_checkbox ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val id = intent.extras?.getInt("id")
        if(id != null) {
            // read todoitem from database
        } else {
            toDoItem = ToDoItem(-1, "", "", false, Date(), ToDoPriority.NONE)
        }

        val prioritySpinnerAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,ToDoPriority.values().map { it.toString() })
        spinner.adapter = prioritySpinnerAdapter

        initListeners()
        updateViewWithItem(toDoItem)
    }

    private fun initListeners() {
        toDoCheckbox.setOnClickListener {
            toDoItem.isDone = !toDoItem.isDone
            setCheckbox(toDoItem.isDone)
        }

        title.doOnTextChanged { text, start, before, count ->
            toDoItem.name = text.toString()
        }

        description.doOnTextChanged { text, start, before, count ->
            toDoItem.description = text.toString()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                toDoItem.priority = ToDoPriority.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (toDoItem.priority != ToDoPriority.NONE) {
                    return
                }

                toDoItem.priority = ToDoPriority.NONE
            }
        }

        deadline.setOnClickListener {
            DatePickerFragment().show(supportFragmentManager, "datePicker")
        }
    }

    override fun receiveDate(date: Date) {
        toDoItem.deadline = date
        updateViewWithItem(toDoItem)
    }

    private fun setDeadline(date: Date) {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN)
        deadline.setText(sdf.format(date))
    }

    private fun setCheckbox(isDone: Boolean) {
        val drawable = CheckboxDrawableProvider().getCheckboxState(isDone, this)
        toDoCheckbox.setImageDrawable(drawable)
    }

    private fun updateViewWithItem(toDoItem: ToDoItem) {
        setTitle(toDoItem.name)
        setDescription(toDoItem.description)
        setDeadline(toDoItem.deadline)
        setSpinner(toDoItem.priority)
        setCheckbox(toDoItem.isDone)
    }

    private fun setTitle(titleText: String) {
        title.setText(titleText)
    }
    
    private fun setDescription(descriptionText: String) {
        description.setText(descriptionText)
    }
    
    private fun setSpinner(toDoPriority: ToDoPriority) {
        spinner.setSelection(ToDoPriority.values().indexOf(toDoPriority))
    }

    //TODO button
}