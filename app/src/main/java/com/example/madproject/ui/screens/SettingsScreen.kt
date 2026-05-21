package com.example.madproject.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madproject.ui.components.GlassCard
import com.example.madproject.ui.viewmodel.AppViewModelFactory
import com.example.madproject.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(factory: AppViewModelFactory, snackbarHostState: SnackbarHostState) {
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var pendingExport by remember { mutableStateOf(ExportType.Csv) }
    var budgetInput by remember(uiState.monthlyBudget) {
        mutableStateOf(if (uiState.monthlyBudget == 0.0) "" else uiState.monthlyBudget.toString())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                when (pendingExport) {
                    ExportType.Csv -> viewModel.exportCsv()
                    ExportType.Pdf -> viewModel.exportPdf()
                }
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Storage permission denied")
                }
            }
        }
    )

    LaunchedEffect(uiState.exportMessage) {
        uiState.exportMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Dark mode", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Follow your preferred look",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = uiState.isDarkTheme, onCheckedChange = viewModel::setDarkTheme)
                }
            }

            GlassCard {
                Column {
                    Text(text = "Monthly budget", style = MaterialTheme.typography.titleMedium)
                    TextField(
                        value = budgetInput,
                        onValueChange = { value ->
                            budgetInput = value
                            val amount = value.toDoubleOrNull()
                            if (amount != null) {
                                viewModel.setMonthlyBudget(amount)
                            } else if (value.isBlank()) {
                                viewModel.setMonthlyBudget(0.0)
                            }
                        },
                        placeholder = { Text("0.00") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        Spacer(modifier = Modifier.height(8.dp))

            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Export", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                val granted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (!granted) {
                                    pendingExport = ExportType.Csv
                                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                } else {
                                    viewModel.exportCsv()
                                }
                            } else {
                                viewModel.exportCsv()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Export CSV")
                    }
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                val granted = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (!granted) {
                                    pendingExport = ExportType.Pdf
                                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                } else {
                                    viewModel.exportPdf()
                                }
                            } else {
                                viewModel.exportPdf()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Export PDF")
                    }
                }
            }

            TextButton(onClick = { viewModel.setMonthlyBudget(0.0) }) {
                Text("Reset budget")
            }
        }
    }
}

private enum class ExportType {
    Csv,
    Pdf
}
