package com.erdeanmich.todings.overview.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.erdeanmich.todings.R
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoPriority
import com.erdeanmich.todings.util.CheckboxDrawableProvider
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ToDoOverviewAdapter(private val toDoItems: List<ToDoItem>): RecyclerView.Adapter<ToDoOverviewAdapter.ToDoItemViewHolder>() {

    inner class ToDoItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val title: TextView by lazy { view.findViewById(R.id.to_do_item_title)}
        private val description: TextView by lazy { view.findViewById(R.id.to_do_item_description) }
        private val deadline: TextView by lazy { view.findViewById(R.id.to_do_item_deadline) }
        private val priorityImage: ImageView by lazy { view.findViewById(R.id.to_do_item_priority_image) }
        private val checkBoxButton: ImageView by lazy { view.findViewById(R.id.to_do_item_checkbox) }

        fun setTitle(title: String): ToDoItemViewHolder {
            this.title.text = title
            return this
        }

        fun setDescription(description: String): ToDoItemViewHolder {
            this.description.text = description
            return this
        }

        @SuppressLint("SimpleDateFormat")
        fun setDeadline(deadline: Date): ToDoItemViewHolder {
            val formatter = SimpleDateFormat("dd-MM-yyyy")
            this.deadline.text = formatter.format(deadline)
            return this
        }

        fun setPriority(priority: ToDoPriority): ToDoItemViewHolder {
            //TODO map priority to image
            return this
        }

        fun setDoneStatus(isDone: Boolean): ToDoItemViewHolder {
            val drawable = CheckboxDrawableProvider().getCheckboxState(isDone, view.context)
            checkBoxButton.setImageDrawable(drawable)
            return this
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_to_do_item, parent, false)
        return ToDoItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ToDoItemViewHolder, position: Int) {
        val toDoItem = toDoItems[position]

        viewHolder
                .setTitle(toDoItem.name)
                .setDescription(toDoItem.description)
                .setPriority(toDoItem.priority)
                .setDoneStatus(toDoItem.isDone)
                .setDeadline(toDoItem.deadline)
    }

    override fun getItemCount() = toDoItems.size
}