package kr.ds.helper.web

import androidx.annotation.Keep

typealias WebMessageArgs = Map<String, Any>

@Keep
data class WebMessage(
    val group: String,
    val function: String,
    val args: WebMessageArgs?
)