package com.erdeanmich.todings.overview.view


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.erdeanmich.todings.R
import com.erdeanmich.todings.model.ToDoItem
import com.erdeanmich.todings.model.ToDoPriority
import com.erdeanmich.todings.util.CheckboxDrawableProvider
import java.text.SimpleDateFormat
import java.util.*

class ToDoOverviewAdapter(
    private val toDoItems: List<ToDoItem>,
    private val onItemClickListener: OnItemClickListener,
    private val onCheckBoxClickListener: OnCheckBoxClickListener,
    private val onPrioritySelectListener: OnPrioritySelectListener
) : RecyclerView.Adapter<ToDoOverviewAdapter.ToDoItemViewHolder>() {

    inner class ToDoItemViewHolder(
        private val view: View, context: Context,
        onCheckBoxClickListener: OnCheckBoxClickListener,
        onPrioritySelectListener: OnPrioritySelectListener
    ) :
        RecyclerView.ViewHolder(view) {
        private val title: TextView by lazy { view.findViewById(R.id.to_do_item_title) }
        private val description: TextView by lazy { view.findViewById(R.id.to_do_item_description) }
        private val deadline: TextView by lazy { view.findViewById(R.id.to_do_item_deadline) }
        private val spinner: Spinner by lazy { view.findViewById(R.id.to_do_item_priority_spinner) }
        private val checkBoxButton: ImageView by lazy { view.findViewById(R.id.to_do_item_checkbox) }
        private var toDoItem: ToDoItem? = null

        init {
            val prioritySpinnerAdapter = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                ToDoPriority.values().map { it.toString() })

            spinner.adapter = prioritySpinnerAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    onPrioritySelectListener.onPrioritySelect(
                        toDoItem!!.id,
                        ToDoPriority.values()[position]
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            checkBoxButton.setOnClickListener {
                onCheckBoxClickListener.onCheckBoxClick(toDoItem!!.id)
            }


        }

        fun setToDoItem(toDoItem: ToDoItem) {
            this.toDoItem = toDoItem
            title.text = toDoItem.name
            description.text = toDoItem.description

            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMAN)
            deadline.text = formatter.format(toDoItem.deadline)

            spinner.setSelection(ToDoPriority.values().indexOf(toDoItem.priority))
            val drawable = CheckboxDrawableProvider().getCheckboxState(toDoItem.isDone, view.context)
            checkBoxButton.setImageDrawable(drawable)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_to_do_item, parent, false)
        return ToDoItemViewHolder(view, parent.context, onCheckBoxClickListener, onPrioritySelectListener)
    }

    override fun onBindViewHolder(viewHolder: ToDoItemViewHolder, position: Int) {
        val toDoItem = toDoItems[position]

        viewHolder.setToDoItem(toDoItem)

        viewHolder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(toDoItem.id)
        }
    }

    override fun getItemCount() = toDoItems.size

    interface OnItemClickListener {
        fun onItemClick(id: Long)
    }

    interface OnCheckBoxClickListener {
        fun onCheckBoxClick(id: Long)
    }

    interface OnPrioritySelectListener {
        fun onPrioritySelect(id: Long, toDoPriority: ToDoPriority)
    }
}

