package com.erdeanmich.todings.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.erdeanmich.todings.R

class CheckboxDrawableProvider {
    fun getCheckboxState(isDone: Boolean, context: Context): Drawable? {
        val resourceID = when(isDone) {
            true -> R.drawable.ic_checked
            false -> R.drawable.ic_unchecked
        }

        return ContextCompat.getDrawable(context, resourceID)
    }
}