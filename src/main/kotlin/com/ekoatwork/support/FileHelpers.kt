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

import java.io.File
import java.io.IOException


fun File.expand(vararg paths: String): File = paths.fold(this, ::File)

fun File.ensureLocationDir() {
    if (!parentFile.exists() and !parentFile.mkdirs()) throw IOException("Failed to create directory for file ($this)")
}