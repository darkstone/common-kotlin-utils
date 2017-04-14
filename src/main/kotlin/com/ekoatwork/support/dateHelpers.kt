package com.ekoatwork.support

import java.time.LocalDateTime
import java.util.*

fun Date.toLocalDateTime(atTimeZone: TimeZone = TimeZone.getDefault()): LocalDateTime {
    return toInstant().atZone(atTimeZone.toZoneId()).toLocalDateTime()
}
