package com.raassh.dicodingstoryapp.views.stories

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.SessionPreferences
import com.raassh.dicodingstoryapp.databinding.LoginFragmentBinding
import com.raassh.dicodingstoryapp.databinding.StoriesFragmentBinding
import com.raassh.dicodingstoryapp.views.SharedViewModel
import com.raassh.dicodingstoryapp.views.dataStore
import com.raassh.dicodingstoryapp.views.login.LoginViewModel

class StoriesFragment : Fragment() {
    private val viewModel by viewModels<StoriesViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel> {
        SharedViewModel.Factory(SessionPreferences.getInstance(context?.dataStore as DataStore))
    }

    private var _binding: StoriesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StoriesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}