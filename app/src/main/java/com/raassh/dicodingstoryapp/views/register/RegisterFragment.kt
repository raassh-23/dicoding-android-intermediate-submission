package com.raassh.dicodingstoryapp.views.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.customviews.EditTextWithValidation
import com.raassh.dicodingstoryapp.databinding.RegisterFragmentBinding
import com.raassh.dicodingstoryapp.misc.hideSoftKeyboard
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.visibility

class RegisterFragment : Fragment() {
    private val viewModel by viewModels<RegisterViewModel>()

    private var _binding: RegisterFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(false)

        binding.apply {
            goToLogin.setOnClickListener {
                it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            nameInput.setValidationCallback(object : EditTextWithValidation.InputValidation {
                override val errorMessage: String
                    get() = getString(R.string.name_validation_message)

                override fun validate(input: String) = !TextUtils.isEmpty(input)
            })

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

            register.setOnClickListener {
                tryRegister()
            }
        }

        viewModel.apply {
            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }

            isSuccess.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { success ->
                    if (success) {
                        registered()
                    }
                }
            }

            error.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { message ->
                    showSnackbar(binding.root, message)
                }
            }
        }
    }

    private fun tryRegister() {
        hideSoftKeyboard(activity as FragmentActivity)

        with(binding) {
            // note to self:
            // doing it like this will validate all input
            // instead of stopping after the first invalid
            val isNameValid = nameInput.validateInput()
            val isEmailValid = emailInput.validateInput()
            val isPasswordValid = passwordInput.validateInput()

            if (!isNameValid || !isEmailValid || !isPasswordValid) {
                showSnackbar(root, getString(R.string.validation_error))
                return
            }

            viewModel.register(
                nameInput.text.toString(),
                emailInput.text.toString(),
                passwordInput.text.toString()
            )
        }
    }

    private fun registered() {
        showSnackbar(binding.root, getString(R.string.register_success))

        val navigateAction = RegisterFragmentDirections
            .actionRegisterFragmentToLoginFragment()
        navigateAction.email = binding.emailInput.text.toString()

        findNavController().navigate(navigateAction)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            registerGroup.visibility = visibility(!isLoading)
            registerLoadingGroup.visibility = visibility(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}