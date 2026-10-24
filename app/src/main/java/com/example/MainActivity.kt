package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CalculatorDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.CalculatorRepository
import com.example.ui.CalculatorScreen
import com.example.ui.CalculatorViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels {
        val database = CalculatorDatabase.getInstance(applicationContext)
        val repo = CalculatorRepository(database.calculationDao())
        val prefRepo = UserPreferencesRepository(applicationContext)
        CalculatorViewModel.Factory(application, repo, prefRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by viewModel.preferences.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = preferences.themeMode,
                palette = preferences.accentPalette
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorScreen(viewModel = viewModel)
                }
            }
        }
    }
}