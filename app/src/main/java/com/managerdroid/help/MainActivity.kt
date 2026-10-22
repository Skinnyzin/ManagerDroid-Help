package com.managerdroid.help

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.status)
        findViewById<Button>(R.id.grant).setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:$packageName"))
            )
        }
    }

    override fun onResume() {
        super.onResume()
        status.text = if (Environment.isExternalStorageManager())
            "Acesso a arquivos: permitido\nO ManagerDroid pode exportar nomes e ícones."
        else
            "Acesso a arquivos: NÃO permitido.\nToque no botão abaixo e autorize."
    }
}
