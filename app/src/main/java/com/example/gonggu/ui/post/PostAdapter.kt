package com.example.gonggu.ui.post

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gonggu.R
import com.example.gonggu.databinding.ItemPostListBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PostAdapter(private val context: Context, private val postList: ArrayList<Any?>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostListBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val postData = postList[position]
        postData?.let { holder.bind(it) }

        if (postData is PostData){
            holder.itemView.setOnClickListener{
                PostViewerActivity.currentPost = postData
                context?.startActivity(Intent(context,PostViewerActivity::class.java))
            }
        }
        else if (postData is DeliveryData) {
            holder.itemView.setOnClickListener{
                DeliveryViewerActivity.currentDelivery = postData
                context?.startActivity(Intent(context,DeliveryViewerActivity::class.java))
            }
        }
    }

    override fun getItemCount() = postList.size

    inner class PostViewHolder(private val binding: ItemPostListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val now = Calendar.getInstance()
        private val postDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)

        fun bind(postData: Any) {
            if (postData is PostData){
                binding.itemPostTitle.text = postData.title
                // ImageView에 이미지 로드
                if( postData.imageUrl.isNotEmpty()) {
                    Glide.with(binding.root)
                        .load(postData.imageUrl)
                        .into(binding.itemPostImgList) // item_post_list.xml의 ImageView ID
                } else {
                    Glide.with(binding.root)
                        .load(R.drawable.no_image)
                        .into(binding.itemPostImgList)
                }

                // 게시글 작성 시간 데이터 파싱
                val postTime = Calendar.getInstance().apply {
                    time = postDataFormat.parse(postData.time)!!
                }

                // 날짜, 시간 변환
                val diff = now.timeInMillis - postTime.timeInMillis
                val timeString = when {
                    diff < 60 * 1000 -> "방금 전"
                    diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}분 전"
                    postTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
                        when (postTime.get(Calendar.DAY_OF_YEAR)) {
                            now.get(Calendar.DAY_OF_YEAR) -> postDataFormat.format(postTime.time).substring(11)
                            else -> SimpleDateFormat("MM/dd", Locale.KOREA).format(postTime.time)
                        }
                    }
                    else -> SimpleDateFormat("yy/MM/dd", Locale.KOREA).format(postTime.time)
                }
                binding.itemPostListTime.text = timeString

                // 참여 인원 수 확인
                binding.itemPostListNumOfParticipants.text = "참여 인원: ${postData.joiner.size}/${postData.numOfPeople}명"
            }
            else if (postData is DeliveryData){
                binding.itemPostTitle.text = postData.title
                // ImageView에 이미지 로드
                if( postData.imageUrl.isNotEmpty()) {
                    Glide.with(binding.root)
                        .load(postData.imageUrl)
                        .into(binding.itemPostImgList) // item_post_list.xml의 ImageView ID
                } else {
                    Glide.with(binding.root)
                        .load(R.drawable.no_image)
                        .into(binding.itemPostImgList)
                }

                // 게시글 작성 시간 데이터 파싱
                val postTime = Calendar.getInstance().apply {
                    time = postDataFormat.parse(postData.time)!!
                }

                // 날짜, 시간 변환
                val diff = now.timeInMillis - postTime.timeInMillis
                val timeString = when {
                    diff < 60 * 1000 -> "방금 전"
                    diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}분 전"
                    postTime.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
                        when (postTime.get(Calendar.DAY_OF_YEAR)) {
                            now.get(Calendar.DAY_OF_YEAR) -> postDataFormat.format(postTime.time).substring(11)
                            else -> SimpleDateFormat("MM/dd", Locale.KOREA).format(postTime.time)
                        }
                    }
                    else -> SimpleDateFormat("yy/MM/dd", Locale.KOREA).format(postTime.time)
                }
                binding.itemPostListTime.text = timeString

                // 참여 인원 수 확인
                binding.itemPostListNumOfParticipants.text = "참여 인원: ${postData.joiner.size}/${postData.numOfPeople}명"
            }
        }
    }
}