

@file:Suppress("ktlint:standard:filename")

package com.example.bvgrecruitmenttask.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bvgrecruitmenttask.NetworkStatusBroadcastReceiver
import com.example.bvgrecruitmenttask.domain.model.Event

@Composable
fun ServerSentEventsScreen(
    events: List<Event>,
    onSearchQuery: (String) -> Unit,
    networkConnectionAvailable: () -> Unit,
) {
    NetworkStatusBroadcastReceiver(onNetworkRestored = networkConnectionAvailable)
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            SearchTopBar(onValueChange = onSearchQuery)
        },
    ) { paddingValues ->

        if (events.isEmpty()) {
            Loader()
        }

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
        ) {
            items(events) { event ->
                EventItem(event)
            }
        }
    }
}
