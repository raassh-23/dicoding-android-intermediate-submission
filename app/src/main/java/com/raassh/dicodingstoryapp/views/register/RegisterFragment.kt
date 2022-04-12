package com.raassh.dicodingstoryapp.views.register

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.customviews.EditTextWithValidation
import com.raassh.dicodingstoryapp.data.SessionPreferences
import com.raassh.dicodingstoryapp.databinding.LoginFragmentBinding
import com.raassh.dicodingstoryapp.databinding.RegisterFragmentBinding
import com.raassh.dicodingstoryapp.views.SharedViewModel
import com.raassh.dicodingstoryapp.views.dataStore
import com.raassh.dicodingstoryapp.views.login.LoginViewModel

class RegisterFragment : Fragment() {
    private val viewModel by viewModels<RegisterViewModel>()

    private var _binding: RegisterFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                // note to self:
                // doing it like this will validate all input
                // instead of stopping after the first invalid
                val isNameValid = nameInput.validateInput()
                val isEmailValid = emailInput.validateInput()
                val isPasswordValid = passwordInput.validateInput()

                if (!isNameValid || !isEmailValid || !isPasswordValid) {
                    return@setOnClickListener
                }

                Toast.makeText(context, "daftar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}