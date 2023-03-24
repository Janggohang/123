package com.example.gonggu.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gonggu.R

class ChatListData (val profile :Int, val name : String, val Lastchat : String, val chatTime : String)

class CustomViewHolder(v: View): RecyclerView.ViewHolder(v) {

    val profile: ImageView = v.findViewById(R.id.item_image_chat_list)
    val name: TextView = v.findViewById(R.id.item_name_chat_list)
    val Lastchat: TextView = v.findViewById(R.id.item_last_chat)
    val chatTime: TextView = v.findViewById(R.id.item_chat_list_time)
}

class ChatListAdapter(val ChatDataList:ArrayList<ChatListData>) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent,false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.profile.setImageResource(ChatDataList[position].profile)
        holder.name.text = ChatDataList[position].name
        holder.Lastchat.text = ChatDataList[position].Lastchat
        holder.chatTime.text = ChatDataList[position].chatTime
    }

    override fun getItemCount() = ChatDataList.size
}