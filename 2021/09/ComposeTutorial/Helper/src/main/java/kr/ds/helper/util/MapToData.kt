package kr.ds.helper.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapToData {
    companion object {
        val gson = Gson()
    }
}

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = MapToData.gson.toJson(this)
    return MapToData.gson.fromJson(json, object : TypeToken<O>() {}.type)
}