package com.impresa.pulizie

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportSender {

    fun generaTestoReport(
        nomeOperatore: String,
        interventi: List<Intervento>
    ): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.ITALIAN)
        val dataOggi = dateFormat.format(Date())

        val sb = StringBuilder()
        sb.append("📋 *REPORT GIORNALIERO PULIZIE*\n")
        sb.append("📅 Data: $dataOggi\n")
        sb.append("👤 Operatore: $nomeOperatore\n")
        sb.append("----------------------------------------\n\n")

        var totaleMinutiComplessivi = 0L

        interventi.forEach { i ->
            val oraInizio = timeFormat.format(Date(i.dataOraInizio))
            val oraFine = timeFormat.format(Date(i.dataOraFine))
            val minutiTotaliOperatore = i.durataMinuti * i.numeroOperatori
            totaleMinutiComplessivi += minutiTotaliOperatore

            val oreIntervento = i.durataMinuti / 60
            val minIntervento = i.durataMinuti % 60

            sb.append("📍 *Cliente:* ${i.nomeCliente}\n")
            sb.append("⏱ *Ora:* $oraInizio - $oraFine (${i.durataMinuti} min)\n")
            sb.append("👥 *Operatori:* ${i.numeroOperatori}\n")
            sb.append("📊 *Ore Uomo:* ${oreIntervento}h ${minIntervento}m\n\n")
        }

        val oreTotali = totaleMinutiComplessivi / 60
        val minTotali = totaleMinutiComplessivi % 60

        sb.append("----------------------------------------\n")
        sb.append("Totale Interventi: ${interventi.size}\n")
        sb.append("⏱ *Totale Ore Uomo Giornata:* ${oreTotali}h ${minTotali}m")

        return sb.toString()
    }

    fun inviaViaWhatsApp(context: Context, messaggio: String, numeroTelefono: String = "") {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = if (numeroTelefono.isNotBlank()) {
            "https://api.whatsapp.com/send?phone=$numeroTelefono&text=" + URLEncoder.encode(messaggio, "UTF-8")
        } else {
            "https://api.whatsapp.com/send?text=" + URLEncoder.encode(messaggio, "UTF-8")
        }
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun inviaViaEmail(context: Context, emailDestinatario: String, messaggio: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailDestinatario))
            putExtra(Intent.EXTRA_SUBJECT, "Report Pulizie Giornaliero - ${SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(Date())}")
            putExtra(Intent.EXTRA_TEXT, messaggio.replace("*", ""))
        }
        context.startActivity(Intent.createChooser(intent, "Invia Email con..."))
    }
}
