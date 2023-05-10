package com.pascal.personalorganizer.data.repositoryImpl

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import com.pascal.personalorganizer.domain.repository.RecorderRepository
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class RecorderRepositoryImpl @Inject constructor(
    private val context: Context
): RecorderRepository {

    /**
     * This is used to record audio for the notes.
     */

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    private var audioFile: File? = null

    override fun startRecord(fileName: String) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) //Phone microphone as source
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //audio format
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) //encoder
            setOutputFile(FileOutputStream(
                File(context.cacheDir, "$fileName.mp3").also {
                    audioFile = it
                }
            ).fd)

            prepare()
            start()

            recorder = this
        }
    }

    /**
     * when we finish the record whe send the uri as a callback
     * uri is need to play the audio
     */
    override fun stopRecord(uri: (Uri) -> Unit) {
        audioFile?.toUri()?.let { uri(it) }
        recorder?.stop()
        recorder?.reset()
        recorder = null
        audioFile = null
    }

    override fun playFile(uri: Uri) {
        MediaPlayer.create(context, uri).apply {
            player = this
            start()
        }
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}