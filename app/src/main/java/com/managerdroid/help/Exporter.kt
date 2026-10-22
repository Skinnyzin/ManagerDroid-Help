package com.managerdroid.help

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Environment
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exporta nome e ícone para /sdcard/ManagerDroid.
 * - request.txt (opcional): um nome de pacote por linha. Se existir, exporta SOMENTE esses apps.
 *   Sem o arquivo, exporta todos os apps (primeira sincronização).
 * - apps.json: lista {"pkg","label"} dos apps exportados (gravado por último, de forma atômica).
 * - icons.zip: ícones dos apps exportados.
 * - error.txt: criado somente se algo falhar.
 */
object Exporter {
    private const val ICON_SIZE = 96
    private const val THREADS = 4

    fun run(context: Context) {
        val root = File(Environment.getExternalStorageDirectory(), "ManagerDroid")
        val iconDir = File(root, "icons")
        val errorFile = File(root, "error.txt")
        try {
            root.mkdirs(); iconDir.mkdirs()
            errorFile.delete()
            if (!Environment.isExternalStorageManager()) {
                throw SecurityException("Permissão 'Acesso a todos os arquivos' não concedida.")
            }
            val pm = context.packageManager
            val all = pm.getInstalledApplications(0)
            val requested: Set<String>? = File(root, "request.txt").takeIf { it.isFile }
                ?.readLines()?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()
            val apps = if (requested == null) all else all.filter { it.packageName in requested }

            val pool = Executors.newFixedThreadPool(THREADS)
            val futures = apps.map { info ->
                pool.submit<JSONObject> {
                    val pkg = info.packageName
                    val label = pm.getApplicationLabel(info).toString()
                    val iconFile = File(iconDir, "$pkg.png")
                    val installed = runCatching { pm.getPackageInfo(pkg, 0).lastUpdateTime }.getOrDefault(0L)
                    val cacheValid = iconFile.exists() && iconFile.length() > 0 && iconFile.lastModified() >= installed
                    if (!cacheValid) {
                        runCatching { saveIcon(pm.getApplicationIcon(info), iconFile) }
                            .onFailure { iconFile.delete() }
                    }
                    JSONObject().put("pkg", pkg).put("label", label)
                }
            }
            val array = JSONArray()
            futures.forEach { array.put(it.get()) }
            pool.shutdown()

            // Sem request: exportação completa, então remove ícones de apps desinstalados.
            if (requested == null) {
                val installedNames = all.map { "${it.packageName}.png" }.toHashSet()
                iconDir.listFiles()?.forEach { if (it.name !in installedNames) it.delete() }
            }

            // Ícones dos apps exportados em um único icons.zip (um só adb pull no PC).
            val zipTmp = File(root, "icons.zip.tmp")
            ZipOutputStream(FileOutputStream(zipTmp)).use { zos ->
                for (info in apps) {
                    val f = File(iconDir, "${info.packageName}.png")
                    if (!f.isFile || f.length() == 0L) continue
                    zos.putNextEntry(ZipEntry("${info.packageName}.png"))
                    f.inputStream().use { it.copyTo(zos) }
                    zos.closeEntry()
                }
            }
            if (!zipTmp.renameTo(File(root, "icons.zip"))) throw IllegalStateException("Falha ao gravar icons.zip")

            // apps.json por último: sinal para o PC de que a exportação terminou.
            val tmp = File(root, "apps.json.tmp")
            tmp.writeText(array.toString(), Charsets.UTF_8)
            if (!tmp.renameTo(File(root, "apps.json"))) throw IllegalStateException("Falha ao gravar apps.json")
        } catch (e: Throwable) {
            errorFile.writeText("${e.javaClass.simpleName}: ${e.message}", Charsets.UTF_8)
        }
    }

    private fun saveIcon(drawable: Drawable, dest: File) {
        val bitmap = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, ICON_SIZE, ICON_SIZE)
        drawable.draw(canvas)
        FileOutputStream(dest).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
