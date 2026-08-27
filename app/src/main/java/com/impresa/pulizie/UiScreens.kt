package com.impresa.pulizie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(database: AppDatabase) {
    val scope = rememberCoroutineScope()
    var interventi by remember { mutableStateOf(emptyList<Intervento>()) }
    var cliente by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        interventi = database.interventoDao().getAll()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestione Pulizie") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (cliente.isNotBlank()) {
                    scope.launch {
                        val nuovo = Intervento(cliente = cliente, note = note)
                        database.interventoDao().insert(nuovo)
                        interventi = database.interventoDao().getAll()
                        cliente = ""
                        note = ""
                    }
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = cliente,
                onValueChange = { cliente = it },
                label = { Text("Nome Cliente / Cantiere") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note Intervento") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Interventi Registrati",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(interventi) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.cliente,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = item.note,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    database.interventoDao().delete(item)
                                    interventi = database.interventoDao().getAll()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Elimina")
                            }
                        }
                    }
                }
            }
        }
    }
}
