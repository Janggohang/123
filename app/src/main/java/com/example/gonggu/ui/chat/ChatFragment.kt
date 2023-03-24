package com.example.gonggu.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonggu.Data.ChatDataList
import com.example.gonggu.R


class ChatFragment : Fragment() {
    companion object {
    fun newInstance() : ChatFragment {
        return ChatFragment()
    }
}

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_chatlist)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ChatListAdapter(ChatDataList)
        val spaceDecoration = RecyclerDecoration(40)
        recyclerView.addItemDecoration(spaceDecoration)

        // Inflate the layout for this fragment
        return view//inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}