package com.sepher.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

data class Song(val id: Long, val title: String, val artist: String, val uri: Uri)

class MainActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer
    private var songs by mutableStateOf<List<Song>>(emptyList())
    private var playingTitle by mutableStateOf("هنوز آهنگی انتخاب نشده")
    private var isPlaying by mutableStateOf(false)

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) loadSongs()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(this).build()

        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        } else {
            permissionLauncher.launch(permission)
        }

        setContent {
            val navy = Color(0xFF080D1D)
            val purple = Color(0xFF7655E8)

            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = purple,
                    background = navy,
                    surface = Color(0xFF151D34)
                )
            ) {
                Column(
                    Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(navy, Color(0xFF171331))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Text(
                        "SEPHER",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "MUSIC PLAYER",
                        color = Color(0xFFB9A9FF),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(18.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1B2440)
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(
                            Modifier.fillMaxWidth().padding(18.dp)
                        ) {
                            Text("در حال پخش", color = Color(0xFFB9A9FF))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                playingTitle,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (player.hasPreviousMediaItem()) {
                                            player.seekToPreviousMediaItem()
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.SkipPrevious,
                                        null,
                                        tint = Color.White
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (player.isPlaying) player.pause() else player.play()
                                        isPlaying = player.isPlaying
                                    }
                                ) {
                                    Icon(
                                        if (isPlaying) Icons.Default.PauseCircle
                                        else Icons.Default.PlayCircle,
                                        null,
                                        tint = Color(0xFFB9A9FF),
                                        modifier = Modifier.size(54.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (player.hasNextMediaItem()) {
                                            player.seekToNextMediaItem()
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.SkipNext,
                                        null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "کتابخانه موسیقی",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.weight(1f))
                        Text("${songs.size} آهنگ", color = Color.LightGray)
                    }
                    Spacer(Modifier.height(8.dp))

                    if (songs.isEmpty()) {
                        Text(
                            "اگر آهنگی در گوشی وجود دارد، مجوز دسترسی به فایل‌های صوتی را فعال کنید.",
                            color = Color.LightGray
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(songs) { song ->
                                Card(
                                    Modifier.fillMaxWidth().clickable { playSong(song) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF151D34)
                                    )
                                ) {
                                    Row(
                                        Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.MusicNote,
                                            null,
                                            tint = Color(0xFFB9A9FF),
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                song.title,
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                song.artist,
                                                color = Color.LightGray,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadSongs() {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )
        val found = mutableListOf<Song>()

        contentResolver.query(
            collection,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                found += Song(
                    id,
                    cursor.getString(titleCol) ?: "ناشناخته",
                    cursor.getString(artistCol) ?: "هنرمند ناشناخته",
                    Uri.withAppendedPath(collection, id.toString())
                )
            }
        }

        songs = found
        player.clearMediaItems()
        player.setMediaItems(found.map { MediaItem.fromUri(it.uri) })
        player.prepare()
    }

    private fun playSong(song: Song) {
        val index = songs.indexOf(song)
        if (index >= 0) {
            player.seekToDefaultPosition(index)
            player.play()
            playingTitle = song.title
            isPlaying = true
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}
