package com.impresa.pulizie

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun DropdownMenuClienti(
    clienti: List<Cliente>,
    selezionato: Cliente?,
    onSeleziona: (Cliente) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { if (enabled) expanded = true },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selezionato?.nome ?: "Seleziona Cliente")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            clienti.forEach { cliente ->
                DropdownMenuItem(
                    text = { Text(cliente.nome) },
                    onClick = {
                        onSeleziona(cliente)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimerInterventoScreen(
    clienti: List<Cliente>,
    nomeOperatorePredefinito: String,
    onSalvaIntervento: (Intervento) -> Unit
) {
    var clienteSelezionato by remember { mutableStateOf<Cliente?>(null) }
    var numeroOperatori by remember { mutableStateOf("1") }
    
    var isRunning by remember { mutableStateOf(false) }
    var tempoInizioMs by remember { mutableStateOf(0L) }
    var tempoTrascorsoSec by remember { mutableStateOf(0L) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            tempoTrascorsoSec++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nuovo Intervento", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))

        DropdownMenuClienti(
            clienti = clienti,
            selezionato = clienteSelezionato,
            onSeleziona = { clienteSelezionato = it },
            enabled = !isRunning
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = numeroOperatori,
            onValueChange = { if (it.all { char -> char.isDigit() }) numeroOperatori = it },
            label = { Text("Numero Operatori") },
            enabled = !isRunning,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        val minuti = tempoTrascorsoSec / 60
        val secondi = tempoTrascorsoSec % 60
        Text(
            text = String.format("%02d:%02d", minuti, secondi),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isRunning) {
            Button(
                onClick = {
                    if (clienteSelezionato != null) {
                        tempoInizioMs = System.currentTimeMillis()
                        tempoTrascorsoSec = 0L
                        isRunning = true
                    }
                },
                enabled = clienteSelezionato != null,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("AVVIA INTERVENTO")
            }
        } else {
            Button(
                onClick = {
                    val tempoFineMs = System.currentTimeMillis()
                    val durataMinuti = (tempoTrascorsoSec / 60).coerceAtLeast(1)

                    val nuovoIntervento = Intervento(
                        clienteId = clienteSelezionato!!.id,
                        nomeCliente = clienteSelezionato!!.nome,
                        nomeOperatore = nomeOperatorePredefinito,
                        dataOraInizio = tempoInizioMs,
                        dataOraFine = tempoFineMs,
                        durataMinuti = durataMinuti,
                        numeroOperatori = numeroOperatori.toIntOrNull() ?: 1
                    )

                    onSalvaIntervento(nuovoIntervento)

                    isRunning = false
                    tempoTrascorsoSec = 0L
                    clienteSelezionato = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("TERMINA E SALVA")
            }
        }
    }
}

@Composable
fun GestioneClientiScreen(
    clienti: List<Cliente>,
    onSalvaCliente: (Cliente) -> Unit
) {
    var nomeCliente by remember { mutableStateOf("") }
    var indirizzo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Inserimento Nuovo Cliente", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nomeCliente,
            onValueChange = { nomeCliente = it },
            label = { Text("Nome / Ragione Sociale") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = indirizzo,
            onValueChange = { indirizzo = it },
            label = { Text("Indirizzo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Telefono Referente (Opzionale)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (nomeCliente.isNotBlank()) {
                    onSalvaCliente(Cliente(nome = nomeCliente, indirizzo = indirizzo, telefono = telefono))
                    nomeCliente = ""
                    indirizzo = ""
                    telefono = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva Cliente")
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("Elenco Clienti (" + clienti.size + ")", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(clienti) { cliente ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(cliente.nome, style = MaterialTheme.typography.titleMedium)
                        if (cliente.indirizzo.isNotBlank()) Text("📍 " + cliente.indirizzo)
                        if (cliente.telefono.isNotBlank()) Text("📞 " + cliente.telefono)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportGiornalieroScreen(
    interventiOggi: List<Intervento>,
    nomeOperatore: String,
    telefonoDestinatario: String,
    emailDestinatario: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Report di Oggi", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(interventiOggi) { i ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(i.nomeCliente, style = MaterialTheme.typography.titleMedium)
                        Text("Durata: ${i.durataMinuti} min | Operatori: ${i.numeroOperatori}")
                        Text("Ore uomo: ${i.durataMinuti * i.numeroOperatori} min")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val testo = ReportSender.generaTestoReport(nomeOperatore, interventiOggi)
                ReportSender.inviaViaWhatsApp(context, testo, telefonoDestinatario)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Invia Report via WhatsApp")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                val testo = ReportSender.generaTestoReport(nomeOperatore, interventiOggi)
                ReportSender.inviaViaEmail(context, emailDestinatario, testo)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Invia Report via Email")
        }
    }
}

@Composable
fun ProfiloScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    var operatore by remember { mutableStateOf(prefs.getString("nome_operatore", "Marco") ?: "") }
    var whatsappDest by remember { mutableStateOf(prefs.getString("wa_destinatario", "") ?: "") }
    var emailDest by remember { mutableStateOf(prefs.getString("email_destinatario", "") ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Configurazione Operatore e Destinatari", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = operatore,
            onValueChange = { operatore = it },
            label = { Text("Nome Operatore") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = whatsappDest,
            onValueChange = { whatsappDest = it },
            label = { Text("Numero WhatsApp Report (es. +393401234567)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = emailDest,
            onValueChange = { emailDest = it },
            label = { Text("Email Destinatario Report") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                prefs.edit()
                    .putString("nome_operatore", operatore)
                    .putString("wa_destinatario", whatsappDest)
                    .putString("email_destinatario", emailDest)
                    .apply()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva Impostazioni")
        }
    }
}
