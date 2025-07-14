package io.github.theapache64.retrosheet.core

import io.github.theapache64.retrosheet.annotations.Update
import io.github.theapache64.retrosheet.annotations.Write
import io.ktor.http.ParametersBuilder
import io.ktor.util.AttributeKey

const val TAG = "Retrosheet"

internal fun isGoogleFormSubmit(
    annotations: List<Any>,
    method: String
): Boolean {
    val isForm = annotations.find { it is Write || it is Update } != null
    val requestMethod = method
    if (isForm && requestMethod != "POST") {
        throw IllegalArgumentException("@Write should be always @POST, found @$requestMethod")
    }
    return isForm
}

private const val URL_START = "https://docs.google.com/spreadsheets/d"
internal fun isRetrosheetUrl(url: String): Boolean {
    return url.startsWith(URL_START)
}


internal val sheetNameKey = AttributeKey<String>("sheetName")
internal val requestJsonKey = AttributeKey<String>("requestJson")
internal val formNameKey = AttributeKey<String>("formName")
internal val paramBuilderKey = AttributeKey<ParametersBuilder>("submitMap")


internal fun String.proxify(shouldProxy: Boolean): String {
    return if(shouldProxy){
        "https://api.a64.in/retrosheet?url=$this"
    }else{
        this
    }
}