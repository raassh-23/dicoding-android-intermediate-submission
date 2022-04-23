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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.data.database.StoryDatabase
import com.raassh.dicodingstoryapp.data.paging.ListStoriesAdapter
import com.raassh.dicodingstoryapp.data.paging.LoadingStateAdapter
import com.raassh.dicodingstoryapp.data.paging.StoryRepository
import com.raassh.dicodingstoryapp.databinding.StoriesFragmentBinding
import com.raassh.dicodingstoryapp.databinding.StoryItemBinding
import com.raassh.dicodingstoryapp.misc.isTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


class StoriesFragment : Fragment() {
    private var token = ""
    private var newStoryAdded = false

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

        val args = StoriesFragmentArgs.fromBundle(arguments as Bundle)
        token = args.token
        newStoryAdded = args.newStoryAdded
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

        val layoutManager = if (activity?.applicationContext
                ?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            LinearLayoutManager(context)
        } else {
            GridLayoutManager(context, 2)
        }

        val listStoriesAdapter = ListStoriesAdapter().apply {
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

            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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

                adapter = listStoriesAdapter.withLoadStateFooter(
                    LoadingStateAdapter {
                        listStoriesAdapter.retry()
                    }
                )
            }

            addNew.setOnClickListener {
                goToNewStory()
            }
        }

        viewModel.stories.observe(viewLifecycleOwner) {
            postponeEnterTransition()

            binding?.apply {
                listStoriesAdapter.submitData(viewLifecycleOwner.lifecycle, it)

                (view.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }

                // really hacky, but i can't find another working method
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(500)
                    if (newStoryAdded) {
                        newStoryAdded = false
                        binding?.listStory?.scrollToPosition(0)
                    }
                }
            }
        }
    }

    private fun goToNewStory() {
        val navigateAction = StoriesFragmentDirections
            .actionStoriesFragmentToNewStoryFragment()
        navigateAction.token = token

        findNavController().navigate(navigateAction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = true
    }
}