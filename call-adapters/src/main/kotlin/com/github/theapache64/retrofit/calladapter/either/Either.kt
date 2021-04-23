package com.github.theapache64.retrofit.calladapter.either

sealed class Either<out L, out R> {

    data class Left<out L>(val left: L) : Either<L, Nothing>()
    data class Right<out R>(val right: R) : Either<Nothing, R>()

    fun <T> fold(leftOp: (L) -> T, rightOp: (R) -> T): T = when (this) {
        is Left -> leftOp(this.left)
        is Right -> rightOp(this.right)
    }

    companion object {
        fun <R> right(value: R): Either<Nothing, R> =
            Right(value)

        fun <L> left(value: L): Either<L, Nothing> =
            Left(value)
    }
}

