package com.example.smarthomegesture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SecondPage : AppCompatActivity() {

    var videoView: VideoView? = null
    var gesture:String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val replay = findViewById<Button>(R.id.replay_button)
        val practice = findViewById<Button>(R.id.practice_button)

        val position = intent.getStringExtra("position").toString()
        gesture = intent.getStringExtra("gesture").toString()
        videoView = findViewById<VideoView>(R.id.videoView_Play)


        val uriPath = getVideoPath(position)
        print(uriPath)
            //"android.resource://" + packageName + "/" + raw/aha_hands_only_cpr_english
        videoView!!.setVideoURI(Uri.parse(uriPath))
        videoView!!.requestFocus()
        videoView!!.start()
        var noofplay = 1

        videoView!!.setOnCompletionListener {
            if(noofplay <3){
                videoView!!.start()
                noofplay++
            }
        }

//        videoView!!.setOnErrorListener{ videoView, i, i2 ->
//            Toast.makeText(applicationContext,"Error while playing Video",Toast.LENGTH_LONG).show()
//            false
//        }

        replay.setOnClickListener {
            videoView = findViewById<VideoView>(R.id.videoView_Play)

            videoView!!.setVideoURI(Uri.parse(getVideoPath(position)))
            videoView!!.requestFocus()
            videoView!!.start()
        }

        practice.setOnClickListener{
            val practicepage = Intent(this@SecondPage, PracticePage::class.java)
            practicepage.putExtra("gesture",gesture)
            startActivity(practicepage)
        }

    }
    private fun getVideoPath(pos: String): String {
        val result =  when (pos) {
            "1" -> "hlighton"
            "2" -> "hlightoff"
            "3" -> "hfanon"
            "4" -> "hfanoff"
            "5" -> "hincreasefanspeed"
            "6" -> "hdecreasefanspeed"
            "7" -> "hsetthermo"
            "8" -> "h0"
            "9" -> "h1"
            "10" -> "h2"
            "11" -> "h3"
            "12" -> "h4"
            "13" -> "h5"
            "14" -> "h6"
            "15" -> "h7"
            "16" -> "h8"
            "17" -> "h9"
            else -> ""
        }
        //"android.resource://" + getPackageName() + "/" + R.raw.taipei
        return "android.resource://"+packageName+"/raw/"+result
    }

}

