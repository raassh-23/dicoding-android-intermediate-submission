package com.raassh.dicodingstoryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.databinding.StoryItemBinding
import com.raassh.dicodingstoryapp.misc.loadImage

class ListStoriesAdapter(private val listStories: ArrayList<ListStoryItem>)
    : RecyclerView.Adapter<ListStoriesAdapter.ViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(story: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story =listStories[position]

        holder.apply {
            binding.apply {
                storyImage.loadImage(story.photoUrl)
                storyImage.contentDescription = itemView.context.getString(
                    R.string.stories_content_description, story.name
                )
                storyUser.text = itemView.context.getString(R.string.stories_user, story.name)
            }

            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(story)
            }
        }
    }

    override fun getItemCount() = listStories.size
}