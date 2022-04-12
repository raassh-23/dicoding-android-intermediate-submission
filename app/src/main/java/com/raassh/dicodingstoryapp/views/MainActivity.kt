package com.raassh.dicodingstoryapp.views

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.SessionPreferences
import com.raassh.dicodingstoryapp.databinding.ActivityMainBinding

internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModel.Factory(SessionPreferences.getInstance(dataStore))
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(binding.container.id)

        when(item.itemId) {
            R.id.logout -> {
                viewModel.saveToken("")

                // ref: https://github.com/android/architecture-components-samples/issues/767
                val navHostFragment = supportFragmentManager.findFragmentById(binding.container.id) as NavHostFragment
                val inflater = navHostFragment.navController.navInflater
                val graph = inflater.inflate(R.navigation.main_navigation)
                graph.setStartDestination(R.id.loginFragment)
                navController.graph = graph
            }
            android.R.id.home -> {
                navController.navigateUp()
            }
        }

        return true
    }
}