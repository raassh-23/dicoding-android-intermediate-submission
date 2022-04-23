package com.raassh.dicodingstoryapp.views.stories

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
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
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.ApiService
import com.raassh.dicodingstoryapp.data.paging.ListStoriesAdapter
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.data.database.StoryDatabase
import com.raassh.dicodingstoryapp.data.paging.StoryRepository
import com.raassh.dicodingstoryapp.databinding.StoriesFragmentBinding
import com.raassh.dicodingstoryapp.databinding.StoryItemBinding
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.visibility
import com.raassh.dicodingstoryapp.views.newstory.NewStoryFragment

class StoriesFragment : Fragment() {
    private var token = ""

    private val viewModel by viewModels<StoriesViewModel> {
        StoriesViewModel.Factory(StoryRepository(
            StoryDatabase.getDatabase(context as Context),
            ApiConfig.getApiService(),
            getString(R.string.auth, token)
        ))
    }

    private var binding: StoriesFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

                adapter = ListStoriesAdapter().apply {
                    setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
                        override fun onItemClicked(
                            story: ListStoryItem,
                            storyBinding: StoryItemBinding
                        ) {
                            val extras = FragmentNavigatorExtras(
                                storyBinding.storyImage to getString(
                                    R.string.story_image,
                                    story.id
                                ),
                                storyBinding.storyUser to getString(R.string.story_user, story.id)
                            )

                            view.findNavController().navigate(
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

            addNew.setOnClickListener {
                goToNewStory()
            }
        }

        showLoading(false)

        viewModel.apply {
            stories.observe(viewLifecycleOwner) {
                postponeEnterTransition()

                binding?.apply {
                    (listStory.adapter as ListStoriesAdapter).submitData(lifecycle, it)

                    (view.parent as? ViewGroup)?.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
            }
        }
    }

    private fun storyAdded() {
//        viewModel.getAllStories()

        binding?.root?.let {
            showSnackbar(it, getString(R.string.upload_success))
        }
    }

    private fun goToNewStory() {
        val navigateAction = StoriesFragmentDirections
            .actionStoriesFragmentToNewStoryFragment()
        navigateAction.token = token

        findNavController().navigate(navigateAction)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            storiesGroup.visibility = visibility(!isLoading)
            storiesLoadingGroup.visibility = visibility(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = true
    }
}