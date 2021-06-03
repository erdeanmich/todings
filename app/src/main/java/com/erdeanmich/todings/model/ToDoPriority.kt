package com.erdeanmich.todings.model

import com.erdeanmich.todings.R

enum class ToDoPriority(val icon: Int) {
    NONE(R.drawable.ic_prio_none),
    LOW(R.drawable.ic_prio_low),
    MEDIUM(R.drawable.ic_prio_medium),
    HIGH(R.drawable.ic_prio_high),
    CRITICAL(R.drawable.ic_prio_critical)
}