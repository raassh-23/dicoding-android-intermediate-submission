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
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.customviews.EditTextWithValidation
import com.raassh.dicodingstoryapp.data.SessionPreferences
import com.raassh.dicodingstoryapp.databinding.LoginFragmentBinding
import com.raassh.dicodingstoryapp.views.SharedViewModel
import com.raassh.dicodingstoryapp.views.dataStore
import com.raassh.dicodingstoryapp.misc.Result
import com.raassh.dicodingstoryapp.misc.hideSoftKeyboard
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.visibility

class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel> {
        SharedViewModel.Factory(SessionPreferences.getInstance(context?.dataStore as DataStore))
    }

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                hideSoftKeyboard(activity as FragmentActivity)

                val isEmailValid = emailInput.validateInput()
                val isPasswordValid = passwordInput.validateInput()

                if (!isEmailValid || !isPasswordValid) {
                    showSnackbar(binding.root, getString(R.string.validation_error))
                    return@setOnClickListener
                }

                viewModel.login(emailInput.text.toString(), passwordInput.text.toString())
            }
        }

        viewModel.result.observe(viewLifecycleOwner) {
            val result = it.getContentIfNotHandled()
            if (result != null) {
                when(result) {
                    is Result.Loading ->
                        showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        sharedViewModel.saveToken(result.data)
                        showSnackbar(binding.root, getString(R.string.login_success))
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showSnackbar(binding.root, result.error)
                    }
                }
            }
        }

        sharedViewModel.getToken().observe(viewLifecycleOwner) {
            if (!TextUtils.isEmpty(it)) {
                view.findNavController().navigate(R.id.action_loginFragment_to_storiesFragment)
            }
        }
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