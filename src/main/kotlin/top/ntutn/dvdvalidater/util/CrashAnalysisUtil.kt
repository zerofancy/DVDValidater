package top.ntutn.dvdvalidater.util

import com.vaadin.open.Open
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess


object CrashAnalysisUtil: Thread.UncaughtExceptionHandler {
    fun plant() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread?, tr: Throwable?) {
        val outputFile = File.createTempFile("crash", ".html")
        val stringWriter = StringWriter()
        val writer = PrintWriter(stringWriter)
        tr?.printStackTrace(writer)

        val content = """
            <html lang="zh-Hans">
            <head>
            <title>糟糕，应用发生了崩溃</title>
            </head>
            <body>
            <h1>应用发生了崩溃</h1>
            <p>以下信息有助于我们排查问题：</p>
            <p>线程名：${thread?.name}</p>
            <pre>
            <code>
            $stringWriter
            </code>
            </pre>
            </body>
            </html>
        """.trimIndent()

        outputFile.writeText(content)
        println(content)

        println("Report is saved to $outputFile")

        Open.open(outputFile.toURI().toString())

        exitProcess(1)
    }
}