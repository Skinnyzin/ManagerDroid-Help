package com.managerdroid.help

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.concurrent.thread

/** Recebe o broadcast do ManagerDroid e roda a exportação fora da thread principal. */
class ExportReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "com.managerdroid.help.EXPORT") return
        val pending = goAsync()
        thread(name = "dc-export") {
            try { Exporter.run(context.applicationContext) } finally { pending.finish() }
        }
    }
}
