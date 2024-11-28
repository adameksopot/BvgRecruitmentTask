package com.example.bvgrecruitmenttask.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(onValueChange: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .layout { measurable, constraints ->
                            val paddingCompensation = 16.dp.toPx().roundToInt()
                            val adjustedConstraints =
                                constraints.copy(
                                    // not a good idea inside horizontal scroll view,
                                    // but I guess we can assume that's not the case here
                                    maxWidth = constraints.maxWidth + paddingCompensation,
                                )
                            val placeable = measurable.measure(adjustedConstraints)
                            layout(placeable.width, placeable.height) {
                                placeable.place(-paddingCompensation / 2, 0)
                            }
                        }.background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onValueChange(it)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    // .padding(horizontal = 8.dp),
                    placeholder = { Text("Search events by username") },
                    singleLine = true,
                )
            }
        },
    )
}

@Preview
@Composable
fun PreviewSearchTopBar() {
    SearchTopBar(onValueChange = {})
}
