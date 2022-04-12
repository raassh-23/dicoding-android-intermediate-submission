package com.raassh.dicodingstoryapp.views.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
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

class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel> {
        SharedViewModel.Factory(SessionPreferences.getInstance(context?.dataStore as DataStore))
    }

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            goToRegister.setOnClickListener {
                it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

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
                val isEmailValid = emailInput.validateInput()
                val isPasswordValid = passwordInput.validateInput()

                if (!isEmailValid || !isPasswordValid) {
                    Toast.makeText(context, getString(R.string.validation_error),Toast.LENGTH_SHORT)
                        .show()
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
                        binding.progress.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progress.visibility = View.GONE
                        sharedViewModel.saveToken(result.data)
                        Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        binding.progress.visibility = View.GONE
                        Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        sharedViewModel.getToken().observe(viewLifecycleOwner) {
            token = it
            Log.d("asdas", "onViewCreated: $token")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}