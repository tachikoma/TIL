package com.example.composetutorial.webbridge

import kr.ds.helper.web.WebMessageArgs

interface ImageHandler {
    fun save(args: WebMessageArgs?)
    fun share(args: WebMessageArgs?)
}

interface ShowHandler {
    fun camera(args: WebMessageArgs?)
}
