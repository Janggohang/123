package com.example.gonggu.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.gonggu.R
import com.example.gonggu.ui.post.PostViewerActivity

class ImageDialogFragment(private val imageUri: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.image_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(true)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.image_dialog, container, false)

        val imageView: ImageView = view.findViewById(R.id.dialogImageView)

        if( imageUri.isNotEmpty()) {
            Glide.with(this)
                .load(PostViewerActivity.currentPost.imageUrl)
                .into(imageView) // item_post_list.xml의 ImageView ID

        }

        view.setOnClickListener {
            dismiss()
        }

        return view
    }
}