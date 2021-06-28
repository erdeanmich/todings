package com.erdeanmich.todings.detail.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.erdeanmich.todings.R
import com.erdeanmich.todings.detail.fragments.DatePickerFragment
import com.erdeanmich.todings.detail.fragments.DateReceiver
import com.erdeanmich.todings.detail.fragments.TimePickerFragment
import com.erdeanmich.todings.detail.fragments.TimeReceiver
import com.erdeanmich.todings.detail.view.DetailViewModel
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoPriority
import com.erdeanmich.todings.overview.activities.OverviewActivity
import com.erdeanmich.todings.util.CheckboxDrawableProvider
import com.erdeanmich.todings.util.observeOnce
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(), DateReceiver, TimeReceiver {
    private val viewModel: DetailViewModel by viewModels()
    private val title: EditText by lazy { findViewById(R.id.title_edit) }
    private val description: EditText by lazy { findViewById(R.id.description_edit) }
    private val spinner: Spinner by lazy { findViewById(R.id.priority_spinner) }
    private val deadline: EditText by lazy { findViewById(R.id.deadline) }
    private val toDoCheckbox: ImageView by lazy { findViewById(R.id.detail_checkbox ) }
    private val saveButton: Button by lazy { findViewById(R.id.save_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        val id = getIdFromIntent()
        viewModel.getToDoItemById(id).observeOnce(this, { toDoItem ->
            updateViewWithItem(toDoItem)
            initListeners(toDoItem)
        })

        val prioritySpinnerAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,ToDoPriority.values().map { it.toString() })
        spinner.adapter = prioritySpinnerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete -> {
                // delete from database

                backToOverview()
            }
            R.id.action_back_from_detail -> {
                backToOverview()
            }
        }
       return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_delete)?.isEnabled = viewModel.existingInDatabase()
        return true
    }

    private fun getIdFromIntent(): Long? {
        return intent.extras?.getLong("id")
    }

    private fun initListeners(toDoItem: ToDoItem) {
        toDoCheckbox.setOnClickListener {
            val isDone = viewModel.invertIsDone()
            setCheckbox(isDone)
        }

        title.doOnTextChanged { text, start, before, count ->
            viewModel.setName(text.toString())
        }

        description.doOnTextChanged { text, start, before, count ->
           viewModel.setDescription(text.toString())
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setPriority(ToDoPriority.values()[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.setPriority(ToDoPriority.NONE)
            }
        }


        deadline.setOnClickListener {
            val c = Calendar.getInstance()
            c.time = toDoItem.deadline
            DatePickerFragment(c).show(supportFragmentManager, "datePicker")
        }

        saveButton.setOnClickListener {
            viewModel.save()
        }
    }

    private fun backToOverview() {
        val intent = Intent(this, OverviewActivity::class.java)
        startActivity(intent)
    }

    override fun receiveDate(date: Date) {
        viewModel.setDeadline(date)
        setDeadline(date)
        val c = Calendar.getInstance()
        c.time = date
        TimePickerFragment(c).show(supportFragmentManager, "timePicker")
    }

    override fun receiveTime(date: Date) {
        viewModel.setDeadline(date)
        setDeadline(date)
    }

    private fun setDeadline(date: Date) {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMAN)
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


}