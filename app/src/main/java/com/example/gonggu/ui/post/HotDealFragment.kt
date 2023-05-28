package com.example.gonggu.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.gonggu.MainActivity
import com.example.gonggu.R

class HotDealFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hotdeal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = view.findViewById(R.id.webView)
        val mainActivity = activity as MainActivity

        webView.webViewClient = WebViewClient()

        // 연결할 URL 설정
        val externalLink = "http://www.dealbada.com/bbs/board.php?bo_table=deal_domestic"

        webView.loadUrl(externalLink)

        mainActivity.addNavigation()
    }
}