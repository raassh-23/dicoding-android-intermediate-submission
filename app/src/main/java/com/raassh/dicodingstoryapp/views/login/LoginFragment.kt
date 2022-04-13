package com.raassh.dicodingstoryapp.views.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.customviews.EditTextWithValidation
import com.raassh.dicodingstoryapp.data.SessionPreferences
import com.raassh.dicodingstoryapp.databinding.LoginFragmentBinding
import com.raassh.dicodingstoryapp.views.MainActivityViewModel
import com.raassh.dicodingstoryapp.views.dataStore
import com.raassh.dicodingstoryapp.misc.hideSoftKeyboard
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.visibility

class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private val sharedViewModel by activityViewModels<MainActivityViewModel> {
        MainActivityViewModel.Factory(SessionPreferences.getInstance(context?.dataStore as DataStore))
    }

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(false)
        val email = LoginFragmentArgs.fromBundle(arguments as Bundle).email

        binding.apply {
            goToRegister.setOnClickListener {
                it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            emailInput.setText(email)

            emailInput.setValidationCallback(object : EditTextWithValidation.InputValidation {
                override val errorMessage: String
                    get() = getString(R.string.email_validation_message)

                override fun validate(input: String) = !TextUtils.isEmpty(input)
                        && Patterns.EMAIL_ADDRESS.matcher(input).matches()
            })

            passwordInput.setValidationCallback(object : EditTextWithValidation.InputValidation {
                override val errorMessage: String
                    get() = getString(R.string.password_validation_message)

                override fun validate(input: String) = input.length >= 6
            })

            login.setOnClickListener {
                tryLogin()
            }
        }

        viewModel.apply {
            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            token.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    loggedIn(it)
                }
            }

            error.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { message ->
                    showSnackbar(binding.root, message)
                }
            }
        }

        sharedViewModel.getToken().observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                goToStories(it)
            }
        }
    }

    private fun tryLogin() {
        hideSoftKeyboard(activity as FragmentActivity)

        with(binding) {
            val isEmailValid = emailInput.validateInput()
            val isPasswordValid = passwordInput.validateInput()

            if (!isEmailValid || !isPasswordValid) {
                showSnackbar(root, getString(R.string.validation_error))
                return
            }

            viewModel.login(emailInput.text.toString(), passwordInput.text.toString())
        }
    }

    private fun loggedIn(token: String) {
        sharedViewModel.saveToken(token)
        showSnackbar(binding.root, getString(R.string.login_success))
    }

    private fun goToStories(token: String) {
        val navigateAction = LoginFragmentDirections
            .actionLoginFragmentToStoriesFragment()
        navigateAction.token = token

        findNavController().navigate(navigateAction)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loginGroup.visibility = visibility(!isLoading)
            loginLoadingGroup.visibility = visibility(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}