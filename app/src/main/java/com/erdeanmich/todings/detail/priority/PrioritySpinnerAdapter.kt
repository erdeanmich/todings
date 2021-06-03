package com.erdeanmich.todings.detail.priority

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.erdeanmich.todings.R
import com.erdeanmich.todings.model.ToDoPriority

class PrioritySpinnerAdapter(context: Context, resource: Int) : ArrayAdapter<ToDoPriority>(context, resource, ToDoPriority.values()) {

    override fun getCount(): Int {
        return ToDoPriority.values().size
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup) : View {
        val inflater = LayoutInflater.from(parent.context)
        val view = convertView ?: inflater.inflate(R.layout.priority_spinner_item, parent, false)
        setPriorityForView(view, ToDoPriority.values()[position])
        return view
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun setPriorityForView(view: View, priority: ToDoPriority) {
        val image = view.findViewById<ImageView>(R.id.priority_spinner_image)
        image.setImageDrawable(ContextCompat.getDrawable(context, priority.icon))
    }

}