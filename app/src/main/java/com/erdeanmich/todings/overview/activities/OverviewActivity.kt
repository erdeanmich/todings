package com.erdeanmich.todings.overview.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdeanmich.todings.R
import com.erdeanmich.todings.detail.activities.DetailActivity
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoPriority
import com.erdeanmich.todings.overview.view.ToDoOverviewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class OverviewActivity : AppCompatActivity() {
    private val toDoItems = listOf(
            ToDoItem(1, "Name1", "Description 1", false, Date(), ToDoPriority.CRITICAL),
            ToDoItem(2, "Name2", "Description 2", true, Date(), ToDoPriority.LOW),
            ToDoItem(3, "Name3", "Description 3", true, Date(), ToDoPriority.CRITICAL),
            ToDoItem(4, "Name4", "Description 4", false, Date(), ToDoPriority.MEDIUM),
            ToDoItem(5, "Name5", "Description 5", false, Date(), ToDoPriority.HIGH),
            ToDoItem(6, "Name6", "Description 6", true, Date(), ToDoPriority.CRITICAL),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_overview)
        setSupportActionBar(findViewById(R.id.toolbar))

        //setup recyclerview
        val overviewRecyclerView = findViewById<RecyclerView>(R.id.overview_recycler_view)
        val adapter = ToDoOverviewAdapter(toDoItems)
        overviewRecyclerView.adapter = adapter
        overviewRecyclerView.layoutManager = LinearLayoutManager(this)
        overviewRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        findViewById<FloatingActionButton>(R.id.create_button).setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java).apply {
                // put extras here
            }
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_overview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_sort -> {
                true
            }

            R.id.action_settings -> {
                true
            }

            else ->  {
                super.onOptionsItemSelected(item)
            }
        }
    }
}