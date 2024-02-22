package com.example.yoga.View

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.VideoView
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.yoga.Model.GlobalVariable
import com.example.yoga.Model.fileNameGetter
import com.example.yoga.R

class VideoGuide : AppCompatActivity() {
    private var global=GlobalVariable.getInstance()
    var poseName=""

    // yogamap next
    private lateinit var python : Python
    private lateinit var heatmapNext : PyObject
    private var nextThread: Thread? = null
    private var fileGetter=fileNameGetter()

    fun nextpage(){
        val intent = Intent(this, YogaMain::class.java).apply {
            putExtra("poseName",poseName)
        }
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // 隐藏title bar
        setContentView(R.layout.activity_video_guide)

        poseName = intent.getStringExtra("poseName").toString()

        val title = findViewById<TextView>(R.id.videoTitle)
        title.text = poseName

        //video player init
        val videoPlayer = findViewById<VideoView>(R.id.videoPlayer)
        val videoPath = "android.resource://" + packageName + "/" +  fileGetter.getfile(this, poseName.toString() )
        videoPlayer.setVideoURI(Uri.parse(videoPath))
        videoPlayer.start()
        // 设置循环播放
        videoPlayer.setOnPreparedListener { mp -> // mp = mediaplayer
            mp.isLooping = true
        }

        val finish = findViewById<ImageButton>(R.id.finish)
        finish.setOnClickListener {
            nextpage()

            try {
                nextThread?.interrupt()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }

        //啟動python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        /*python = Python.getInstance()

        // yogamap return
        heatmapNext = python.getModule("heatmap")

        // yogamap return
        nextThread = Thread {
            try {
                Thread.sleep(1000)
                while (!heatmapNext.callAttr("checkReturn").toBoolean()) {
                    Thread.sleep(100)
                }
                runOnUiThread {
                    nextpage()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        nextThread?.start()*/
    }
    override fun onPause() {
        super.onPause()
        global.TTS.stop()
        global.backgroundMusic.pause()
    }

    override fun onResume() {
        super.onResume()
        global.backgroundMusic.play()
    }
}