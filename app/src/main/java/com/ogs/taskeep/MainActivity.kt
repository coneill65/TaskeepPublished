package com.ogs.taskeep

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val gson = Gson()

        data class TaskData(
            val intVariable1: Int,
            val intVariable2: Int,
            val intVariable3: Int,
            val intVariable4: Int,
            val intVariable5: Int,
            val intVariable6: Int,
            val stringVariable1: String,
            val stringVariable2: String
        )

        val fromHourString = intent.getStringExtra("fromHour") ?: "1"
        var intVariable1 = 1
        if (fromHourString.isNotEmpty()) {
            intVariable1 = fromHourString.toInt()
        }

        val fromMinuteString = intent.getStringExtra("fromMinute") ?: ""
        var intVariable2 = 0

        if (fromMinuteString.isNotEmpty()) {
            intVariable2 = fromMinuteString.toInt()
        }

        val toHourString = intent.getStringExtra("toHour") ?: "24"
        var intVariable3 = 24
        if (toHourString.isNotEmpty()) {
            intVariable3 = toHourString.toInt()
        }

        val toMinuteString = intent.getStringExtra("toMinute") ?: "59"
        var intVariable4 = 59
        if (toMinuteString.isNotEmpty()) {
            intVariable4 = toMinuteString.toInt()
        }

        val lengthHourString = intent.getStringExtra("lengthHour") ?: "1"
        var intVariable5 = 1
        if (lengthHourString.isNotEmpty()) {
            intVariable5 = lengthHourString.toInt()
        }

        val lengthMinuteString = intent.getStringExtra("lengthMinute") ?: "0"
        var intVariable6 = 0
        if (lengthMinuteString.isNotEmpty()) {
            intVariable6 = lengthMinuteString.toInt()
        }

        val stringVariable1 = intent.getStringExtra("taskName") ?: ""
        val stringVariable2 = intent.getStringExtra("taskComment") ?: stringVariable1

        if (stringVariable1 != ""){
            if (intVariable1 > 24){intVariable1 = 24}
            if (intVariable3 > 24){intVariable3 = 24}
            if (intVariable5 > 24){intVariable5 = 24}
            if (intVariable2 > 59){intVariable2 = 59}
            if (intVariable4 > 59){intVariable4 = 59}
            if (intVariable6 > 59){intVariable6 = 59}
            if (intVariable1 < 1){intVariable1 = 1}
            if (intVariable3 < 1){intVariable3 = 1}
            if (intVariable5 < 1){intVariable5 = 1}
            if (intVariable2 < 0){intVariable2 = 0}
            if (intVariable4 < 0){intVariable4 = 0}
            if (intVariable6 < 0){intVariable6 = 0}

            val newTask = TaskData(intVariable1, intVariable2, intVariable3, intVariable4, intVariable5, intVariable6, stringVariable1, stringVariable2)

            val taskListJson = sharedPref.getString("task_list", null)
            val taskListType = object : TypeToken<MutableList<TaskData>>() {}.type
            val taskList = gson.fromJson<MutableList<TaskData>>(taskListJson, taskListType) ?: mutableListOf()

            taskList.add(newTask)

            val updatedTaskListJson = gson.toJson(taskList)
            sharedPref.edit().putString("task_list", updatedTaskListJson).apply()

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val notificationIntent = Intent(this, YourNotificationReceiver::class.java)
            notificationIntent.putExtra(stringVariable1, stringVariable1)
            notificationIntent.putExtra(stringVariable2, stringVariable2)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                // Set the desired time for the notification
                set(Calendar.HOUR_OF_DAY, intVariable1)
                set(Calendar.MINUTE, intVariable2)
                set(Calendar.SECOND, 0)
            }

            // Set the alarm to trigger at the specified time
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

        }


        // Inside your activity or fragment
        val recyclerView: RecyclerView = findViewById(R.id.TaskView)

        val taskListJson = sharedPref.getString("task_list", null)
        val taskListType = object : TypeToken<List<TaskData>>() {}.type
        val taskList = gson.fromJson<List<TaskData>>(taskListJson, taskListType) ?: mutableListOf()

        // Assuming you have a list of items
        val itemList: MutableList<YourItem> = mutableListOf()
        for (task in taskList) {
            val iv1 = task.intVariable1
            val iv2 = task.intVariable2
            val iv3 = task.intVariable3
            val iv4 = task.intVariable4
            val iv5 = task.intVariable5
            val iv6 = task.intVariable6
            val sv1 = task.stringVariable1
            var sv2 = task.stringVariable2
            if (sv1 != null && sv1 != "") {
                if (sv2 == ""){
                    sv2 = sv1
                }
                val formattedMinutes = String.format("%02d", iv2)
                val item = YourItem(sv1, sv2, "$iv1:$formattedMinutes") // Create your item here based on the task's variables
                itemList.add(item)
            }
        }
        itemList.sortWith(compareBy { (it.time.split(":")[0].toInt() * 100) + it.time.split(":")[1].toInt() })
        // Create and set the adapter
        val adapter = YourAdapter(sharedPref, itemList)
        recyclerView.adapter = adapter

        // Set the layout manager
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val intent = Intent(this, CreateTask::class.java)
        val newTaskButton: Button = findViewById(R.id.createTaskBtn)
        newTaskButton.setOnClickListener {
            startActivity(intent)
        }
    }
}

data class YourItem(val title: String, val description: String, val time: String)

class YourAdapter(private val t: SharedPreferences, private val data: MutableList<YourItem>) : RecyclerView.Adapter<YourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return YourViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: YourViewHolder, position: Int) {
        val item = data[position]

        holder.button.setOnClickListener {
            onDeleteButtonClick(position)
        }

        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
        holder.timeTextView.text = item.time
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun onDeleteButtonClick(position: Int) {
        if (position in 0 until data.size) {
            data.removeAt(position)
            notifyItemRemoved(position)

            // Update local storage after deleting the item
            updateLocalStorage(data)
        }
    }

    private fun updateLocalStorage(data: List<YourItem>) {
        val gson = Gson()
        val jsonString = gson.toJson(data)

        val sharedPrefs = t
        sharedPrefs.edit().putString("task_list", jsonString).apply()
    }
}


class YourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.textView)
    val descriptionTextView: TextView = itemView.findViewById(R.id.textView2)
    val timeTextView: TextView = itemView.findViewById(R.id.textView3)
    val button: Button = itemView.findViewById(R.id.button)
}

class YourNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationTitle = intent.getStringExtra("notification_title")
        val notificationMessage = intent.getStringExtra("notification_message")

        // Create and show the notification
        if (notificationMessage != null) {
            if (notificationTitle != null) {
                createNotification(context, notificationTitle, notificationMessage)
            }
        }
    }

    private fun createNotification(context: Context, title: String, message: String) {
        // Build the notification content
        val notificationBuilder = NotificationCompat.Builder(context, title)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(com.google.android.material.R.drawable.ic_mtrl_checked_circle)
            .setAutoCancel(true)

        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(UUID.randomUUID().hashCode(), notificationBuilder.build())
    }
}