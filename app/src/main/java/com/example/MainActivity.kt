package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.JejakKuApp
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

  private val mainViewModel: MainViewModel by viewModels {
    MainViewModel.Factory(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { JejakKuApp(viewModel = mainViewModel) }
  }
}
