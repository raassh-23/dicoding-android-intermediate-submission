package com.raassh.dicodingstoryapp.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.SessionPreferences
import com.raassh.dicodingstoryapp.databinding.StoriesWidgetConfigureBinding
import com.raassh.dicodingstoryapp.misc.showSnackbar
import com.raassh.dicodingstoryapp.misc.visibility
import com.raassh.dicodingstoryapp.views.SharedViewModel
import com.raassh.dicodingstoryapp.views.dataStore
import com.raassh.dicodingstoryapp.views.login.LoginViewModel

class StoriesWidgetConfigureActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var binding: StoriesWidgetConfigureBinding
    private val sharedViewModel by viewModels<SharedViewModel> {
        SharedViewModel.Factory(SessionPreferences.getInstance(dataStore))
    }
    private val loginViewModel by viewModels<LoginViewModel>()


    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = StoriesWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        sharedViewModel.getToken().observe(this) {
            if (it.isNotEmpty()) {
                showWidget()
            }
        }

        loginViewModel.apply {
            isLoading.observe(this@StoriesWidgetConfigureActivity) {
                showLoading(it)
            }

            token.observe(this@StoriesWidgetConfigureActivity) {
                it.getContentIfNotHandled()?.let { token ->
                    Toast.makeText(
                        this@StoriesWidgetConfigureActivity,
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    sharedViewModel.saveToken(token)
                }
            }

            error.observe(this@StoriesWidgetConfigureActivity) {
                it.getContentIfNotHandled()?.let { message ->
                    showSnackbar(binding.root, message)
                }
            }
        }

        binding.apply {
            login.setOnClickListener {
                val isEmailValid = emailInput.validateInput()
                val isPasswordValid = passwordInput.validateInput()

                if (!isEmailValid || !isPasswordValid) {
                    showSnackbar(root, getString(R.string.validation_error))
                    return@setOnClickListener
                }

                loginViewModel.login(emailInput.text.toString(), passwordInput.text.toString())
            }

        }

        // Find the widget id from the intent.
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loginGroup.visibility = visibility(!isLoading)
            loginLoadingGroup.visibility = visibility(isLoading)
        }
    }

    private fun showWidget() {
        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(this)
        StoriesWidget.updateAppWidget(this, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(RESULT_OK, resultValue)
        finish()
    }
}