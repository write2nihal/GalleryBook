package com.gallerybook.compression.data

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by Nihal Srivastava on 31/10/21.
 */

fun fourCcToInt(byteArray: ByteArray): Int {
    // The bytes of a byteArray value are ordered from most significant to least significant.
    return ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).int
}

// Unused space available in file.
val FREE_ATOM =
    fourCcToInt(
        byteArrayOf(
            'f'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            'e'.toByte()
        )
    )

val JUNK_ATOM =
    fourCcToInt(
        byteArrayOf(
            'j'.toByte(),
            'u'.toByte(),
            'n'.toByte(),
            'k'.toByte()
        )
    )

// Movie sample data— media samples such as video frames and groups of audio samples. Usually this
// data can be interpreted only by using the movie resource.
val MDAT_ATOM =
    fourCcToInt(
        byteArrayOf(
            'm'.toByte(),
            'd'.toByte(),
            'a'.toByte(),
            't'.toByte()
        )
    )

// Movie resource metadata about the movie (number and type of tracks, location of sample data,
// and so on). Describes where the movie data can be found and how to interpret it.
val MOOV_ATOM =
    fourCcToInt(
        byteArrayOf(
            'm'.toByte(),
            'o'.toByte(),
            'o'.toByte(),
            'v'.toByte()
        )
    )

// Reference to movie preview data.
val PNOT_ATOM =
    fourCcToInt(
        byteArrayOf(
            'p'.toByte(),
            'n'.toByte(),
            'o'.toByte(),
            't'.toByte()
        )
    )

// Unused space available in file.
val SKIP_ATOM =
    fourCcToInt(
        byteArrayOf(
            's'.toByte(),
            'k'.toByte(),
            'i'.toByte(),
            'p'.toByte()
        )
    )

// Reserved space—can be overwritten by an extended size field if the following atom exceeds 2^32
// bytes, without displacing the contents of the following atom.
val WIDE_ATOM =
    fourCcToInt(
        byteArrayOf(
            'w'.toByte(),
            'i'.toByte(),
            'd'.toByte(),
            'e'.toByte()
        )
    )

val PICT_ATOM =
    fourCcToInt(
        byteArrayOf(
            'P'.toByte(),
            'I'.toByte(),
            'C'.toByte(),
            'T'.toByte()
        )
    )

// File type compatibility— identifies the file type and differentiates it from similar file
// types, such as MPEG-4 files and JPEG-2000 files.
val FTYP_ATOM =
    fourCcToInt(
        byteArrayOf(
            'f'.toByte(),
            't'.toByte(),
            'y'.toByte(),
            'p'.toByte()
        )
    )

val UUID_ATOM =
    fourCcToInt(
        byteArrayOf(
            'u'.toByte(),
            'u'.toByte(),
            'i'.toByte(),
            'd'.toByte()
        )
    )

val CMOV_ATOM =
    fourCcToInt(
        byteArrayOf(
            'c'.toByte(),
            'm'.toByte(),
            'o'.toByte(),
            'v'.toByte()
        )
    )

val STCO_ATOM =
    fourCcToInt(
        byteArrayOf(
            's'.toByte(),
            't'.toByte(),
            'c'.toByte(),
            'o'.toByte()
        )
    )

val CO64_ATOM =
    fourCcToInt(
        byteArrayOf(
            'c'.toByte(),
            'o'.toByte(),
            '6'.toByte(),
            '4'.toByte()
        )
    )
