package com.ogs.taskeep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class CreateTask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        val createTaskButton: Button = findViewById(R.id.create_task)
        val fh: TextView = findViewById(R.id.fh)
        val fm: TextView = findViewById(R.id.fm)
        val th: TextView = findViewById(R.id.th)
        val tm: TextView = findViewById(R.id.tm)
        val lh: TextView = findViewById(R.id.lh)
        val lm: TextView = findViewById(R.id.lm)
        val tn: TextView = findViewById(R.id.tn)
        val tc: TextView = findViewById(R.id.tc)
        createTaskButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("fromHour", fh.text.toString())
                putExtra("fromMinute", fm.text.toString())
                putExtra("toHour", th.text.toString())
                putExtra("toMinute", tm.text.toString())
                putExtra("lengthHour", lh.text.toString())
                putExtra("lengthMinute", lm.text.toString())
                putExtra("taskName", tn.text.toString())
                putExtra("taskComment", tc.text.toString())
            }
            startActivity(intent)
        }
    }
}