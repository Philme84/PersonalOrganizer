package com.pascal.personalorganizer.domain.models

import java.util.*

data class CalendarModel(var data: Date, var isCurrentDay: Boolean, var isSelected: Boolean)  //this class is use to construct our schedule calendar