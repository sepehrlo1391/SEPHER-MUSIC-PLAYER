package com.sepher.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private val songs = mutableListOf<Pair<String, Uri>>()
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()

        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val title = TextView(this).apply {
            text = "SEPHER MUSIC PLAYER"
            textSize = 25f
            setPadding(0, 20, 0, 30)
        }

        layout.addView(title)

        setContentView(layout)
        checkPermission()
    }

    private fun checkPermission() {
        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            loadSongs()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            loadSongs()
        }
    }

    private fun loadSongs() {
        songs.clear()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE
        )

        val cursor: Cursor? = contentResolver.query(
            collection,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(
                MediaStore.Audio.Media._ID
            )
            val titleColumn = it.getColumnIndexOrThrow(
                MediaStore.Audio.Media.TITLE
            )

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(titleColumn)
                val uri = Uri.withAppendedPath(collection, id.toString())
                songs.add(Pair(name, uri))
            }
        }

        showSongs()
    }

    private fun showSongs() {
        songs.forEach { song ->
            val button = Button(this).apply {
                text = song.first
                setOnClickListener {
                    playSong(song.second)
                }
            }
            layout.addView(button)
        }

        if (songs.isEmpty()) {
            Toast.makeText(
                this,
                "No music found",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun playSong(uri: Uri) {
        player?.apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            play()
        }
    }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
}
