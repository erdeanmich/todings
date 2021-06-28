package com.erdeanmich.todings.detail.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class  DatePickerFragment(private val calendar: Calendar) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val a = requireActivity()
        if (a is DateReceiver) {
            val c = Calendar.getInstance()
            c.set(year,month,dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            a.receiveDate(c.time)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireActivity(), this, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        return datePickerDialog
    }
}

interface DateReceiver {
    fun receiveDate(date: Date)
}