package kr.ds.helper.web

import timber.log.Timber

open class DefaultWebBridge : WebBridge() {

    private val bridgeMap = HashMap<String, Any>()

    override fun onWebMessage(webMessage: WebMessage) {
        val bridge: Any? = bridgeMap[webMessage.group]
        if (bridge != null) {
            try {
                if (webMessage.args != null) {
                    val method =
                        bridge.javaClass.getMethod(
                            webMessage.function,
                            Map::class.java
                        )
                    method.invoke(bridge, webMessage.args)
                } else {
                    val method =
                        bridge.javaClass.getMethod(
                            webMessage.function
                        )
                    method.invoke(bridge)
                }
            } catch (e: NoSuchMethodException) {
                Timber.e(e, "not found function: ${webMessage.group}.${webMessage.function}")
            } catch (e: Exception) {
                Timber.e(e, "exception on function: ${webMessage.group}.${webMessage.function}")
            }
        } else {
            Timber.e("not found group: ${webMessage.group}")
        }
    }

    fun addInterface(group: String, handler: Any) {
        bridgeMap[group] = handler
    }
}