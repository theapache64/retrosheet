package com.github.theapache64.retrosheet.core

/**
 * To tell Retrosheet that this method will be returning a List
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention
annotation class ReadAsList