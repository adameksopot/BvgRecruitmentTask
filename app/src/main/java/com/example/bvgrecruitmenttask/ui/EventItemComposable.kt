package com.example.bvgrecruitmenttask.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bvgrecruitmenttask.domain.model.Event


@Composable
fun EventItem(event: Event) {
    Column(modifier = Modifier.padding(8.dp)) {
        event.id?.let { Text(text = "ID:${event.id}") }
        event.createdAt?.let {
            Text(text = "Created At: $it")
        }
        Text(text = "Type: ${event.eventType.name}")
        Text(text = "Username: ${event.account?.username ?: "Unknown User"}")
    }
}