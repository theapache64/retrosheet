package com.github.theapache64.retrosheet.sample

import java.lang.reflect.Method

fun main(args: Array<String>) {
    println("Result: '${Method::class.java.getDeclaredField("signature")}'")
}