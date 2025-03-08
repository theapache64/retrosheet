package com.github.theapache64.retrosheet.utils

import com.github.theapache64.expekt.should
import org.junit.Test

/**
 * Created by theapache64 : Aug 01 Sat,2020 @ 10:15
 */
internal class SheetUtilsTest {

    @Test
    fun getLetterAt() {
        SheetUtils.getLetterAt(1).should.equal("A")
        SheetUtils.getLetterAt(2).should.equal("B")
        SheetUtils.getLetterAt(26).should.equal("Z")
        SheetUtils.getLetterAt(27).should.equal("AA")
        SheetUtils.getLetterAt(52).should.equal("AZ")
    }

    @Test
    fun toLetterMap() {
        val output = SheetUtils.toLetterMap("id", "name", "age")
        output.keys.toString().should.equal("[id, name, age]")
        output.values.toString().should.equal("[A, B, C]")
    }
}