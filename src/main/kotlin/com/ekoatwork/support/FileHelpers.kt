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

@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package com.ekoatwork.support

import java.io.File
import java.io.IOException


fun File.expand(vararg paths: String): File = paths.fold(this, ::File)

fun File.ensureLocationDir() {
    if (!parentFile.exists() and !parentFile.mkdirs()) throw IOException("Failed to create directory for file ($this)")
}


/**
 * Exception which gets thrown when renaming a file failed.
 *
 * @param reason The reason the exception was thrown.
 * @param src The source file
 * @param dest The destination file
 */
class FileRenameFailedException(val reason: Reason, val src: File, val dest: File)
    : IOException("Failed [$reason] to rename `$src` to `$dest`.") {
    enum class Reason {
        SOURCE_DOES_NOT_EXISTS,
        DEST_ALREADY_EXISTS,
        PLATFORM_FAILURE
    }
}

/**
 * Rename a file by supplying a name and extension
 *
 * @param nameOnly The name part, _without_ the extension
 * @param extension The extension, with or without the *dot*
 * @throws FileRenameFailedException to indicate why and what could not be renamed.
 * @return An existing file with a new name.
 */
fun File.renameTo(nameOnly: String, extension: String): File {
    return withNameAndExtension(nameOnly, extension).also { dest ->
        if (!exists()) throw FileRenameFailedException(FileRenameFailedException.Reason.SOURCE_DOES_NOT_EXISTS, this, dest)
        if (dest.exists()) throw FileRenameFailedException(FileRenameFailedException.Reason.DEST_ALREADY_EXISTS, this, dest)
        if (!renameTo(dest)) throw FileRenameFailedException(FileRenameFailedException.Reason.PLATFORM_FAILURE, this, dest)
    }
}

/**
 * Builds a new file with name and extension.
 *
 * @param name The name, _without_ the extension.
 * @param ext The extension, with or without the dot.
 * @return The new name with a new file name and extension, or the same file if nothing chanaged.
 */
fun File.withNameAndExtension(name: String, ext: String): File {
    return if (name == nameWithoutExtension && (ext == extension || ext == ".$extension"))
        this
    else File(parent, buildString {
        append(name)
        if (!ext.isEmpty()) {
            if (ext.first() != '.') append('.')
            append(ext)
        }
    })
}