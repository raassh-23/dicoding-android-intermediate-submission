package com.raassh.dicodingstoryapp.views.storydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.databinding.StoryDetailFragmentBinding
import com.raassh.dicodingstoryapp.misc.loadImage
import com.raassh.dicodingstoryapp.misc.withDateFormat

class StoryDetailFragment : Fragment() {
    private var _binding: StoryDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StoryDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val story = StoryDetailFragmentArgs.fromBundle(arguments as Bundle).story

        binding.apply {
            storyImage.loadImage(story.photoUrl)
            storyImage.contentDescription = getString(
                R.string.stories_content_description, story.name
            )
            storyUser.text = getString(R.string.stories_user, story.name)
            storyUploaded.text =
                getString(R.string.stories_uploaded, story.createdAt.withDateFormat())
            storyDesc.text = getString(R.string.stories_desc, story.description)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}