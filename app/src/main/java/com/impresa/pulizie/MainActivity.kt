package com.impresa.pulizie

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setContent {
            var schermataSelezionata by remember { mutableStateOf(0) }

            val clienti by db.clienteDao().getTuttiClienti().collectAsState(initial = emptyList())

            val inizioGiornata = remember {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            val interventiOggi by db.interventoDao().getInterventiOggi(inizioGiornata).collectAsState(initial = emptyList())

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = schermataSelezionata == 0,
                            onClick = { schermataSelezionata = 0 },
                            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Timer") },
                            label = { Text("Timer") }
                        )
                        NavigationBarItem(
                            selected = schermataSelezionata == 1,
                            onClick = { schermataSelezionata = 1 },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Clienti") },
                            label = { Text("Clienti") }
                        )
                        NavigationBarItem(
                            selected = schermataSelezionata == 2,
                            onClick = { schermataSelezionata = 2 },
                            icon = { Icon(Icons.Default.DateRange, contentDescription = "Report") },
                            label = { Text("Report") }
                        )
                        NavigationBarItem(
                            selected = schermataSelezionata == 3,
                            onClick = { schermataSelezionata = 3 },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Profilo") },
                            label = { Text("Profilo") }
                        )
                    }
                }
            ) { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                val nomeOp = prefs.getString("nome_operatore", "Operatore") ?: "Operatore"
                val waDest = prefs.getString("wa_destinatario", "") ?: ""
                val mailDest = prefs.getString("email_destinatario", "") ?: ""

                Box(modifier = modifier) {
                    when (schermataSelezionata) {
                        0 -> TimerInterventoScreen(
                            clienti = clienti,
                            nomeOperatorePredefinito = nomeOp,
                            onSalvaIntervento = { nuovo ->
                                lifecycleScope.launch { db.interventoDao().inserisciIntervento(nuovo) }
                            }
                        )
                        1 -> GestioneClientiScreen(
                            clienti = clienti,
                            onSalvaCliente = { nuovo ->
                                lifecycleScope.launch { db.clienteDao().inserisciCliente(nuovo) }
                            }
                        )
                        2 -> ReportGiornalieroScreen(
                            interventiOggi = interventiOggi,
                            nomeOperatore = nomeOp,
                            telefonoDestinatario = waDest,
                            emailDestinatario = mailDest
                        )
                        3 -> ProfiloScreen()
                    }
                }
            }
        }
    }
}
