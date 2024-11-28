package com.example.bvgrecruitmenttask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bvgrecruitmenttask.ui.ServerSentEventsScreen
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
                ServerSentEventsScreen(events = events,
                    networkConnectionAvailable = { viewModel.collectEvents() },
                    onSearchQuery = { viewModel.onSearchQuery(it) })
            }
        }
    }
}
