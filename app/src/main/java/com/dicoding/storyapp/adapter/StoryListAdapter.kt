package com.dicoding.storyapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.entity.Story
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.ui.DetailStoryActivity
import com.dicoding.storyapp.ui.DetailStoryActivity.Companion.EXTRA_DETAIL
import com.dicoding.storyapp.utils.setLocalDateFormat


class StoryListAdapter :  PagingDataAdapter<Story, StoryListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, story: Story ) {
            binding.apply {
                Glide
                    .with(context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.image_loading_placeholder)
                    .error(R.drawable.image_load_error)
                    .into(ivStoryImage)
                tvStoryUsername.text = story.name
                tvStoryDate.setLocalDateFormat(story.createdAt)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivStoryImage, "story_image"),
                            Pair(tvStoryUsername, "username"),
                            Pair(tvStoryDate, "date"),
                        )

                    Intent(context, DetailStoryActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, story)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    companion object {
         val DiffCallback: DiffUtil.ItemCallback<Story> =
            object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(
                    oldItem: Story,
                    newItem: Story
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Story,
                    newItem: Story
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
