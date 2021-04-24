package com.github.theapache64.retrosheet.core

/**
 * To tell Retrosheet that the method expects List/multiple items
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention
annotation class ReadAsList