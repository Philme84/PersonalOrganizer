package com.pascal.personalorganizer.domain.repository

import android.net.Uri

interface RecorderRepository {

    fun startRecord(fileName: String)
    fun stopRecord(uri: (Uri) -> Unit)
    fun playFile(uri: Uri)
    fun stop()
}