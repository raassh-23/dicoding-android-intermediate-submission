package com.raassh.dicodingstoryapp.views.stories

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.adapter.ListStoriesAdapter
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.databinding.StoriesFragmentBinding
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.visibility
import com.raassh.dicodingstoryapp.views.newstory.NewStoryFragment

class StoriesFragment : Fragment() {
    private var token = ""

    private val viewModel by viewModels<StoriesViewModel> {
        StoriesViewModel.Factory(token)
    }

    private var _binding: StoriesFragmentBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        _binding = StoriesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = StoriesFragmentArgs.fromBundle(arguments as Bundle).token

        setFragmentResultListener(NewStoryFragment.ADD_RESULT) { _, bundle ->
            if (bundle.getBoolean("isSuccess")) {
                storyAdded()
            }
        }

        val layoutManager = if (activity?.applicationContext
                ?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager(context)
        } else {
            GridLayoutManager(context, 2)
        }

        binding.apply {
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
                binding.noDataText.visibility = visibility(it.isEmpty())
            }

            error.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    showSnackbar(binding.root, it, getString(R.string.retry)) {
                        viewModel.getAllStories()
                    }
                }
            }
        }
    }

    private fun storyAdded() {
        showSnackbar(binding.root, getString(R.string.upload_success))

        viewModel.getAllStories()
    }

    private fun goToNewStory() {
        val navigateAction = StoriesFragmentDirections
            .actionStoriesFragmentToNewStoryFragment()
        navigateAction.token = token

        findNavController().navigate(navigateAction)
    }

    private fun setStoriesAdapter(stories: ArrayList<ListStoryItem>) {
        binding.listStory.adapter = ListStoriesAdapter(stories).apply {
            setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
                override fun onItemClicked(story: ListStoryItem) {
                    view?.findNavController()?.navigate(
                        StoriesFragmentDirections
                            .actionStoriesFragmentToStoryDetailFragment(story)
                    )
                }
            })
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            listStory.visibility = visibility(!isLoading)
            storiesLoadingGroup.visibility = visibility(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}