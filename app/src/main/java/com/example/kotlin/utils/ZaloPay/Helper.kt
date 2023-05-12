package com.example.kotlin.utils.ZaloPay

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Base64
import java.util.Date
import java.util.LinkedList
import java.util.Objects
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class Helpers {
    companion object {
        private var transIdDefault = 1

        @get:SuppressLint("DefaultLocale")
        val appTransId: String
            get() {
                if (transIdDefault >= 100000) {
                    transIdDefault = 1
                }
                transIdDefault += 1
                @SuppressLint("SimpleDateFormat") val formatDateTime =
                    SimpleDateFormat("yyMMdd_hhmmss")
                val timeString: String = formatDateTime.format(Date())
                return String.format("%s%06d", timeString, transIdDefault)
            }

        @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
        fun getMac(key: String, data: String): String {
            return Objects.requireNonNull(
                HMacUtil.HMacHexStringEncode(
                    HMacUtil.HMACSHA256,
                    key,
                    data
                )
            )!!
        }
    }
}
class HexStringUtil {
    companion object {
        private val HEX_CHAR_TABLE = byteArrayOf(
            '0'.code.toByte(),
            '1'.code.toByte(),
            '2'.code.toByte(),
            '3'.code.toByte(),
            '4'.code.toByte(),
            '5'.code.toByte(),
            '6'.code.toByte(),
            '7'.code.toByte(),
            '8'.code.toByte(),
            '9'.code.toByte(),
            'a'.code.toByte(),
            'b'.code.toByte(),
            'c'.code.toByte(),
            'd'.code.toByte(),
            'e'.code.toByte(),
            'f'.code.toByte()
        )
        // @formatter:on
        /**
         * Convert a byte array to a hexadecimal string
         *
         * @param raw
         * A raw byte array
         *
         * @return Hexadecimal string
         */
        fun byteArrayToHexString(raw: ByteArray): String {
            val hex = ByteArray(2 * raw.size)
            var index = 0
            for (b in raw) {
                val v = b.toInt() and 0xFF
                hex[index++] = HEX_CHAR_TABLE[v ushr 4]
                hex[index++] = HEX_CHAR_TABLE[v and 0xF]
            }
            return String(hex)
        }

        /**
         * Convert a hexadecimal string to a byte array
         *
         * @param hex
         * A hexadecimal string
         *
         * @return The byte array
         */
        fun hexStringToByteArray(hex: String): ByteArray {
            val hexstandard = hex.lowercase()
            val sz = hexstandard.length / 2
            val bytesResult = ByteArray(sz)
            var idx = 0
            for (i in 0 until sz) {
                bytesResult[i] = hexstandard[idx].code.toByte()
                ++idx
                var tmp = hexstandard[idx].code.toByte()
                ++idx
                if (bytesResult[i] > HEX_CHAR_TABLE[9]) {
                    bytesResult[i] = (bytesResult[i] - (('a'.code.toByte() - 10).toByte())).toByte()
                } else {
                    bytesResult[i] = (bytesResult[i] - '0'.code.toByte()).toByte()
                }
                if (tmp > HEX_CHAR_TABLE[9]) {
                    tmp = (tmp - ('a'.code.toByte() - 10).toByte()).toByte()
                } else {
                    tmp = (tmp - ('0'.code.toByte())).toByte()
                }
                bytesResult[i] = (bytesResult[i] * 16 + tmp).toByte()
            }
            return bytesResult
        }
    }
}
class HMacUtil {
    companion object {
        val HMACMD5 = "HmacMD5"
        val HMACSHA1 = "HmacSHA1"
        val HMACSHA256 = "HmacSHA256"
        val HMACSHA512 = "HmacSHA512"
        val UTF8CHARSET = StandardCharsets.UTF_8
        val HMACS = LinkedList(
            Arrays.asList(
                "UnSupport",
                "HmacSHA256",
                "HmacMD5",
                "HmacSHA384",
                "HMacSHA1",
                "HmacSHA512"
            )
        )

        // @formatter:on
        private fun HMacEncode(algorithm: String, key: String, data: String): ByteArray? {
            var macGenerator: Mac? = null
            try {
                macGenerator = Mac.getInstance(algorithm)
                val signingKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), algorithm)
                macGenerator.init(signingKey)
            } catch (ex: Exception) {
            }
            if (macGenerator == null) {
                return null
            }
            var dataByte: ByteArray? = null
            try {
                dataByte = data.toByteArray(charset("UTF-8"))
            } catch (e: UnsupportedEncodingException) {
            }
            return macGenerator.doFinal(dataByte)
        }

        /**
         * Calculating a message authentication code (MAC) involving a cryptographic
         * hash function in combination with a secret cryptographic key.
         *
         * The result will be represented base64-encoded string.
         *
         * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
         *
         * @param key A secret cryptographic key
         *
         * @param data The message to be authenticated
         *
         * @return Base64-encoded HMAC String
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        fun HMacBase64Encode(algorithm: String, key: String, data: String): String? {
            val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
            return Base64.getEncoder().encodeToString(hmacEncodeBytes)
        }

        /**
         * Calculating a message authentication code (MAC) involving a cryptographic
         * hash function in combination with a secret cryptographic key.
         *
         * The result will be represented hex string.
         *
         * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
         *
         * @param key A secret cryptographic key
         *
         * @param data The message to be authenticated
         *
         * @return Hex HMAC String
         */
        fun HMacHexStringEncode(algorithm: String, key: String, data: String): String? {
            val hmacEncodeBytes = HMacEncode(algorithm, key, data) ?: return null
            return HexStringUtil.byteArrayToHexString(hmacEncodeBytes)
        }
    }
}