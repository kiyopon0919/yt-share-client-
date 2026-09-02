package com.example.ytplayer

import android.app.PictureInPictureParams
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URLDecoder
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var linkInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linkInput = findViewById(R.id.linkInput)
        webView = findViewById(R.id.playerWebView)
        val playButton: Button = findViewById(R.id.playButton)

        setupWebView()

        playButton.setOnClickListener {
            loadFromInput(linkInput.text.toString())
        }

        // If the app was opened via "Share" from YouTube, grab the link automatically.
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                linkInput.setText(it)
                loadFromInput(it)
            }
        }
    }

    private fun setupWebView() {
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.domStorageEnabled = true
        webView.webChromeClient = WebChromeClient() // needed for fullscreen video controls
    }

    private fun loadFromInput(rawInput: String) {
        val videoId = extractVideoId(rawInput.trim())
        if (videoId == null) {
            Toast.makeText(this, "Couldn't find a video ID in that link", Toast.LENGTH_SHORT).show()
            return
        }
        // Uses YouTube's official embedded IFrame player.
        // Ads are served by YouTube itself here; this app does not remove them.
        val embedUrl = "https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1"
        webView.loadUrl(embedUrl)
    }

    private fun extractVideoId(url: String): String? {
        val patterns = listOf(
            "(?:v=|/)([0-9A-Za-z_-]{11}).*",   // watch?v=..., /shorts/..., embed/...
            "youtu\\.be/([0-9A-Za-z_-]{11})"
        )
        for (p in patterns) {
            val m = Pattern.compile(p).matcher(url)
            if (m.find()) return m.group(1)
        }
        // Fall back: maybe the user just pasted a raw 11-char ID
        return if (url.matches(Regex("^[0-9A-Za-z_-]{11}$"))) url else null
    }

    // --- Resizable window support ---
    // android:resizeableActivity="true" in the manifest already lets Android's
    // multi-window / split-screen / freeform window manager resize this activity
    // on phones, tablets, foldables, and Chrome OS/desktop-mode devices.
    // Picture-in-Picture below gives an extra floating, drag-resizable mini window.

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipIfPossible()
    }

    private fun enterPipIfPossible() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && webView.visibility == View.VISIBLE) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            try {
                enterPictureInPictureMode(params)
            } catch (_: Exception) {
                // Device/manufacturer doesn't support PiP; ignore.
            }
        }
    }
}
