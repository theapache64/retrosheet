package com.github.theapache64.retrosheetsample.boil

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by theapache64 : Aug 09 Sun,2020 @ 11:14
 */
@Serializable
data class Group(
    @SerialName("instructions")
    val instructions: String,
    @SerialName("files")
    val classes: String, // SingleLiveEvent.kt
    @SerialName("group_name")
    val groupName: String, // Group Name
    @SerialName("id")
    val id: Int // 1
) {
    @Transient
    val classList = classes.split("\n")
}
