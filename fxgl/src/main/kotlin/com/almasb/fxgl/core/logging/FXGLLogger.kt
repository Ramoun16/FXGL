/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.core.logging

import org.apache.logging.log4j.core.config.Configurator
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLLogger
private constructor() {

    companion object {

        private val MAX_LOGS = 10

        /**
         * Can only be configured once per application.
         *
         * @param fileName config file name
         */
        @JvmStatic fun configure(fileName: String) {
            cleanOldLogs()
            Configurator.initialize("FXGL", fileName)
            //logSystemInfo()
        }

        @JvmStatic fun get(name: String): Logger {
            return Log4j2(name)
        }

        @JvmStatic fun get(caller: Class<*>): Logger {
            return get(caller.simpleName)
        }

        /**
         * @return System.out logger
         */
        @JvmStatic fun getSystemLogger(): Logger {
            return SystemLogger
        }

        @JvmStatic fun errorTraceAsString(e: Throwable): String {
            val sb = StringBuilder()
            sb.append("\n\nException occurred: ")
                    .append(e.javaClass.canonicalName)
                    .append(" : ")
                    .append("${e.message}\n")

            val elements = e.stackTrace
            for (el in elements) {
                sb.append("E: ").append(el.toString()).append('\n')
            }

            return sb.toString()
        }

        private fun cleanOldLogs() {
            val logDir = Paths.get("logs/")
            if (!Files.exists(logDir)) {
                Files.createDirectory(logDir)

                val readmeFile = Paths.get("logs/Readme.txt")

                Files.write(readmeFile, "This directory contains FXGL log files.".lines())
            }

            val logs = Files.walk(logDir, 1)
                    .filter { Files.isRegularFile(it) }
                    .sorted { file1, file2 -> Files.getLastModifiedTime(file1).compareTo(Files.getLastModifiedTime(file2)) }
                    .collect(Collectors.toList<Path>())

            val logSize = logs.size
            if (logSize >= MAX_LOGS) {
                for (i in 0..logSize + 1 - MAX_LOGS - 1) {
                    Files.delete(logs[i])
                }
            }
        }

        private fun logSystemInfo() {
            val logger = get("FXGLLogger")

            logger.debug("Logging System Info")

            val rt = Runtime.getRuntime()

            val MB = (1024 * 1024).toDouble()

            logger.debug("CPU cores: " + rt.availableProcessors())
            logger.debug(String.format("Free Memory: %.0fMB", rt.freeMemory() / MB))
            logger.debug(String.format("Max Memory: %.0fMB", rt.maxMemory() / MB))
            logger.debug(String.format("Total Memory: %.0fMB", rt.totalMemory() / MB))

            logger.debug("System Properties:")
            System.getProperties().forEach { k, v -> logger.debug { "$k=$v" } }
        }
    }
}