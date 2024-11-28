package com.example.bvgrecruitmenttask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun NetworkStatusBroadcastReceiver(
    onNetworkRestored: () -> Unit
) {
    val context = LocalContext.current
    val currentOnNetworkRestored by rememberUpdatedState(onNetworkRestored)

    DisposableEffect(context) {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                if (isNetworkAvailable(context)) {
                    currentOnNetworkRestored()
                }
            }
        }

        context.registerReceiver(broadcastReceiver, intentFilter)

        onDispose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
