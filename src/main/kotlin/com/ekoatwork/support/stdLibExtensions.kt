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

package com.ekoatwork.support

inline fun <reified T> withInstanceOf(any: Any, doWithA: T.() -> Unit): Boolean {
    return when (any) {
        is T -> {
            doWithA(any)
            true
        }
        else -> false
    }
}

val Boolean?.yesNoOrNull: String get() = when (this) {
    null -> "null"
    true -> "YES"
    else -> "NO"
}

/**
 * Removes the return type. Allows to convert any call to [Unit]
 *
 * Example:
 *
 *      val r:Runnable = { 3 + 6 }.unit
 */
inline val (()->Any?).unit:()->Unit get() = {this()}

inline fun <T> Collection<T>.withEach(applyThis: T.() -> Unit) = forEach(applyThis)

inline fun <T> Array<T>.withEach(applyThis: T.() -> Unit) = forEach(applyThis)