package com.github.theapache64.retrosheet.bugs

internal data class NozzleStubImpl(
    override val name: String,
    override val type: String,
    override val color: String,
    override val debitAt1Bar: Float,
    override val debitAt2Bar: Float,
    override val debitAt3Bar: Float,
    override val debitAt4Bar: Float,
    override val debitAt5Bar: Float,
    override val debitAt6Bar: Float,
    override val debitAt7Bar: Float,
    override val debitAt8Bar: Float
) : NozzleStub