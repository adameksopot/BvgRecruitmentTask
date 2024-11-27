package com.example.bvgrecruitmenttask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bvgrecruitmenttask.ui.ActivityScreen
import com.example.bvgrecruitmenttask.ui.theme.BvgRecruitmentTaskTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ServerSentEventsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BvgRecruitmentTaskTheme {
                val events by viewModel.state.collectAsState()
                ActivityScreen(events = events)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewModel.getSSEEvents()
    }
}
