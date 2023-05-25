package com.example.gonggu.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonggu.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(private val context: Context , private val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val receive = 1 // 받는 타입
    private val send = 2 // 보내는 타입

    override fun getItemViewType(position: Int): Int {

        //메시지값
        val currentMessage = messageList[position]

        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)) {
            send
        } else {
            receive
        }
    }

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.send_message_text)
        val sendTime: TextView = itemView.findViewById(R.id.send_time)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.receive_message_text)
        val receiveTime: TextView = itemView.findViewById(R.id.receive_time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) { // 받는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            ReceiveViewHolder(view)
        } else { // 보내는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
            SendViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        // 보내는 데이터
        if (holder is SendViewHolder) {
            val sendViewHolder = holder
            sendViewHolder.sendMessage.text = currentMessage.message
            sendViewHolder.sendTime.text = formatTime(currentMessage.timestamp)
        }
        // 받는 데이터
        else if (holder is ReceiveViewHolder) {
            val receiveViewHolder = holder
            receiveViewHolder.receiveMessage.text = currentMessage.message
            receiveViewHolder.receiveTime.text = formatTime(currentMessage.timestamp)
        }

//        // 날짜 변경 확인
//        if (position > 0) {
//            val previousMessage = messageList[position - 1]
//            if (!isSameDate(currentMessage.time, previousMessage.time)) {
//                // 날짜가 변경됐을 때 날짜를 표시하는 줄로 구분
//                holder.itemView.findViewById<View>(R.id.dateSeparator).visibility = View.VISIBLE
//                holder.itemView.findViewById<TextView>(R.id.dateText).text = formatDate(currentMessage.time)
//            } else {
//                holder.itemView.findViewById<View>(R.id.dateSeparator).visibility = View.GONE
//            }
//        } else {
//            holder.itemView.findViewById<View>(R.id.dateSeparator).visibility = View.VISIBLE
//            holder.itemView.findViewById<TextView>(R.id.dateText).text = formatDate(currentMessage.time)
//        }
    }

    private fun formatTime(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun formatDate(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun isSameDate(time1: Long, time2: Long): Boolean {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar1.timeInMillis = time1
        calendar2.timeInMillis = time2
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }
}