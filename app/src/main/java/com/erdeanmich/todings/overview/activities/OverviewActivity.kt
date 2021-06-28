package com.erdeanmich.todings.overview.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdeanmich.todings.R
import com.erdeanmich.todings.detail.activities.DetailActivity
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoOverviewSortMode
import com.erdeanmich.todings.model.ToDoPriority
import com.erdeanmich.todings.overview.view.OverviewViewModel
import com.erdeanmich.todings.overview.view.ToDoOverviewAdapter
import com.erdeanmich.todings.util.observeOnce


class OverviewActivity : AppCompatActivity(), ToDoOverviewAdapter.OnItemClickListener,
    ToDoOverviewAdapter.OnPrioritySelectListener, ToDoOverviewAdapter.OnCheckBoxClickListener {

    private val viewModel: OverviewViewModel by viewModels()
    private val toDoItems = ArrayList<ToDoItem>()
    private val adapter: ToDoOverviewAdapter by lazy {
        ToDoOverviewAdapter(
            toDoItems,
            this,
            this,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_overview)
        setSupportActionBar(findViewById(R.id.toolbar))

        //setup recyclerview
        val overviewRecyclerView = findViewById<RecyclerView>(R.id.overview_recycler_view)
        overviewRecyclerView.adapter = adapter
        overviewRecyclerView.layoutManager = LinearLayoutManager(this)
        overviewRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.getToDoItems().observe(this, { items ->
            toDoItems.clear()
            toDoItems.addAll(items)
            adapter.notifyDataSetChanged()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_overview, menu)
        val menuItem = menu?.findItem(R.id.action_sort)
        menuInflater.inflate(R.menu.menu_sort_sub, menuItem?.subMenu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                true
            }

            R.id.action_delete_all -> {
                viewModel.deleteToDoItems()
                true
            }

            R.id.action_delete_all_online -> {
                true
            }

            R.id.action_sync_all_to_server -> {
                true
            }

            R.id.action_sort_by_date_and_priority -> {
                viewModel.setSortMode(ToDoOverviewSortMode.DATE_PRIORITY)
                true
            }

            R.id.action_sort_by_priority_and_date -> {
                viewModel.setSortMode(ToDoOverviewSortMode.PRIORITY_DATE)
                true
            }

            R.id.action_add -> {
                val intent = Intent(this, DetailActivity::class.java)
                startActivity(intent)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onItemClick(id: Long) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("id", id)
        }
        startActivity(intent)
    }

    override fun onPrioritySelect(id: Long, toDoPriority: ToDoPriority) {
        val item = toDoItems.first { it.id == id }.apply { priority = toDoPriority }
        viewModel.update(item)
    }

    override fun onCheckBoxClick(id: Long) {
        val item = toDoItems.first { it.id == id }.apply { isDone = !isDone }
        viewModel.update(item)
    }
}