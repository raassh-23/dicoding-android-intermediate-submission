package com.raassh.dicodingstoryapp.views.stories

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.adapter.ListStoriesAdapter
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.databinding.StoriesFragmentBinding
import com.raassh.dicodingstoryapp.databinding.StoryItemBinding
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.visibility
import com.raassh.dicodingstoryapp.views.newstory.NewStoryFragment

class StoriesFragment : Fragment() {
    private var token = ""

    private val viewModel by viewModels<StoriesViewModel> {
        StoriesViewModel.Factory(getString(R.string.auth, token))
    }

    private var binding: StoriesFragmentBinding? = null

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StoriesFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = StoriesFragmentArgs.fromBundle(arguments as Bundle).token
        postponeEnterTransition()

        setFragmentResultListener(NewStoryFragment.ADD_RESULT) { _, bundle ->
            if (bundle.getBoolean(NewStoryFragment.IS_SUCCESS)) {
                storyAdded()
            }
        }

        val layoutManager = if (activity?.applicationContext
                ?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            LinearLayoutManager(context)
        } else {
            GridLayoutManager(context, 2)
        }

        binding?.apply {
            listStory.apply {
                setHasFixedSize(true)
                this.layoutManager = layoutManager
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        layoutManager.orientation
                    )
                )
            }

            addNew.setOnClickListener {
                goToNewStory()
            }
        }

        viewModel.apply {
            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            stories.observe(viewLifecycleOwner) {
                setStoriesAdapter(ArrayList(it))
                binding?.noDataText?.visibility = visibility(it.isEmpty())
                (view.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }
            }

            error.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { message ->
                    val root = binding?.root ?: return@observe

                    if (message.isEmpty()) {
                        showSnackbar(
                            root,
                            getString(R.string.generic_error),
                            getString(R.string.retry)
                        ) {
                            viewModel.getAllStories()
                        }
                    } else {
                        showSnackbar(root, message, getString(R.string.retry)) {
                            viewModel.getAllStories()
                        }
                    }
                }
            }
        }
    }

    private fun storyAdded() {
        viewModel.getAllStories()

        val root = binding?.root ?: return
        showSnackbar(root, getString(R.string.upload_success))
    }

    private fun goToNewStory() {
        val navigateAction = StoriesFragmentDirections
            .actionStoriesFragmentToNewStoryFragment()
        navigateAction.token = token

        findNavController().navigate(navigateAction)
    }

    private fun setStoriesAdapter(stories: ArrayList<ListStoryItem>) {
        binding?.listStory?.adapter = ListStoriesAdapter(stories).apply {
            setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
                override fun onItemClicked(story: ListStoryItem, storyBinding: StoryItemBinding) {
                    val extras = FragmentNavigatorExtras(
                        storyBinding.storyImage to getString(R.string.story_image, story.id),
                        storyBinding.storyUser to getString(R.string.story_user, story.id)
                    )

                    view?.findNavController()?.navigate(
                        R.id.action_storiesFragment_to_storyDetailFragment,
                        bundleOf(
                            "story" to story
                        ),
                        null,
                        extras
                    )
                }
            })
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            listStory.visibility = visibility(!isLoading)
            storiesLoadingGroup.visibility = visibility(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}