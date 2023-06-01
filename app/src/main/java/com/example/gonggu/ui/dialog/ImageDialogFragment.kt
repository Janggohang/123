package com.example.gonggu.ui.dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
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

        val imageView: ImageView = dialog.findViewById(R.id.dialogImageView)
        if( imageUri.isNotEmpty()) {
            Glide.with(dialog.context)
                .load(PostViewerActivity.currentPost.imageUrl)
                .into(imageView) // item_post_list.xml의 ImageView ID

        }
        return dialog
    }
}