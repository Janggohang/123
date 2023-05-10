package com.example.gonggu.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gonggu.Data.ChatDataList
import com.example.gonggu.Data.HomeDataList
import com.example.gonggu.MainActivity
import com.example.gonggu.R
import com.example.gonggu.databinding.FragmentHomeBinding
import com.example.gonggu.ui.chat.ChatFragment
import com.example.gonggu.ui.chat.ChatListAdapter
import com.example.gonggu.ui.chat.RecyclerDecoration
import com.example.gonggu.ui.post.PostFragment

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var mainContext : Context
    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
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
    ): View {
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_realtimelist)
        val wrBtn = view.findViewById<Button>(R.id.wrBtn) // 글쓰기 버튼
        val mActivity = activity as MainActivity
        wrBtn.setOnClickListener {
            mActivity.replaceFragment(PostFragment())
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ChatListAdapter(ChatDataList)
        val spaceDecoration = RecyclerDecoration(40)
        recyclerView.addItemDecoration(spaceDecoration)
        mainContext = container!!.context
        return view //inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
    }

}