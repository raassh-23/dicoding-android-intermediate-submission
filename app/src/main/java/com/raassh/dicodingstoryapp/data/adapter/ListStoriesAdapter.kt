package com.raassh.dicodingstoryapp.data.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.databinding.StoryItemBinding
import com.raassh.dicodingstoryapp.misc.loadImage

class ListStoriesAdapter(private val listStories: ArrayList<ListStoryItem>) :
    RecyclerView.Adapter<ListStoriesAdapter.ViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun onItemClicked(story: ListStoryItem, storyBinding: StoryItemBinding)
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
        val story = listStories[position]

        holder.apply {
            binding.apply {
                storyImage.loadImage(story.photoUrl, object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                storyImage.contentDescription = itemView.context.getString(
                    R.string.stories_content_description, story.name
                )
                storyImage.transitionName =
                    itemView.context.getString(R.string.story_image, story.id)
                storyUser.text = itemView.context.getString(R.string.stories_user, story.name)
                storyUser.transitionName = itemView.context.getString(R.string.story_user, story.id)
            }

            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(story, binding)
            }
        }
    }

    override fun getItemCount() = listStories.size
}