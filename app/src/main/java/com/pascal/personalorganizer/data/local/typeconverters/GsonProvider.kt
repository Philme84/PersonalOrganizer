package com.pascal.personalorganizer.data.local.typeconverters

import com.google.gson.Gson

/**
 * Using a provider to only have one instance of Gson
 */
object GsonProvider {

    val gson = Gson()

}