package com.theapache64.retrosheet.core

/**
 * Created by theapache64 : Jul 25 Sat,2020 @ 23:08
 */
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention
annotation class QueryParam(
    val value: String = ""
)