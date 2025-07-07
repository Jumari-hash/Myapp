package com.shortsbox.player

import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private var videoPaths = listOf<String>()
    private var currentIndex = 0

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val shortsDir = File("/sdcard/ShortsBox")
        if (shortsDir.exists()) {
            videoPaths = shortsDir.listFiles { file -> file.extension == "mp4" }
                ?.map { it.absolutePath } ?: emptyList()
        }

        if (videoPaths.isEmpty()) {
            Toast.makeText(this, "No videos found in /sdcard/ShortsBox", Toast.LENGTH_LONG).show()
            return
        }

        val prefs = getSharedPreferences("shortsbox_prefs", MODE_PRIVATE)
        currentIndex = prefs.getInt("last_index", 0)
        if (currentIndex >= videoPaths.size) currentIndex = 0

        loadVideo(currentIndex)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val deltaY = e2.y - e1.y
                if (deltaY < -100) nextVideo()
                else if (deltaY > 100) previousVideo()
                return true
            }
        })

        playerView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }

    private fun loadVideo(index: Int) {
        if (index in videoPaths.indices) {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(videoPaths[index])))
            player.prepare()
            player.play()
        }
    }

    private fun nextVideo() {
        if (currentIndex < videoPaths.lastIndex) {
            currentIndex++
            loadVideo(currentIndex)
        }
    }

    private fun previousVideo() {
        if (currentIndex > 0) {
            currentIndex--
            loadVideo(currentIndex)
        }
    }

    override fun onPause() {
        super.onPause()
        getSharedPreferences("shortsbox_prefs", MODE_PRIVATE).edit()
            .putInt("last_index", currentIndex).apply()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}