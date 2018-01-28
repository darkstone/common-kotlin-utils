/*
 * Copyright 2016 Andries Spies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ekoatwork.support.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ExtLogger : Logger {

    fun debug(message: () -> String) {
        if (isDebugEnabled) {
            debug(message().trimMargin())
        }
    }

    fun info(message: () -> String) {
        if (isInfoEnabled) {
            info(message().trimMargin())
        }
    }

    fun error(e:Exception, message: () -> String) {
        error(message(), e)
    }


    companion object {

        inline operator fun <reified T> invoke(): ExtLogger {
            return object : Logger by LoggerFactory.getLogger(T::class.java), ExtLogger {}
        }

        inline operator fun <reified T> invoke(name: String): ExtLogger {
            val fqn = "${T::class.qualifiedName}.$name"
            return object : Logger by LoggerFactory.getLogger(fqn), ExtLogger {}
        }
    }
}