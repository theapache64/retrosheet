package com.theapache64.retrosheet.core

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 03:08
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention
annotation class SheetParams(
    val rawQuery: String = "",
    val range: String = "",
    val headers: Int = -1
)