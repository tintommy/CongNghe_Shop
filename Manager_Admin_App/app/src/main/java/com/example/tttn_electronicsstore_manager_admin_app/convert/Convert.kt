package com.example.tttn_electronicsstore_manager_admin_app.convert

import android.os.Build
import androidx.annotation.RequiresApi
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.util.Locale

class Convert {
    companion object {

        fun formatNumberWithDotSeparator(number: Int): String {
            val symbols = DecimalFormatSymbols(Locale.US).apply {
                groupingSeparator = '.'
            }
            val decimalFormat = DecimalFormat("#,###", symbols)
            return decimalFormat.format(number)
        }

        fun formatLongNumberWithDotSeparator(number: Long): String {
            val symbols = DecimalFormatSymbols(Locale.US).apply {
                groupingSeparator = '.'
            }
            val decimalFormat = DecimalFormat("#,###", symbols)
            return decimalFormat.format(number)
        }

        fun formatBigIntegerWithThousandSeparators(number: BigInteger): String {
            val numberFormat: NumberFormat = NumberFormat.getInstance(Locale("vi", "VN"))
            return numberFormat.format(number)
        }


         fun dinhDangNgay(ngay: Int, thang: Int, nam: Int): String {
            var temp = ""
            temp += if (ngay < 10) "0$ngay" else ngay.toString()
            temp += "/"
            temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
            temp += "/"
            temp += nam
            return temp
        }
        fun dinhDangNgayChat(ngay: Int, thang: Int, nam: Int): String {
            var temp = ""
            temp += nam
            temp += "/"
            temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
            temp += "/"
            temp += if (ngay < 10) "0$ngay" else ngay.toString()
            return temp
        }


         fun dinhDangNgayAPI(ngay: Int, thang: Int, nam: Int): String {
            var temp = ""
            temp += nam
            temp += "-"
            temp += if (thang + 1 < 10) "0" + (thang + 1).toString() else (thang + 1).toString()
            temp += "-"
            temp += if (ngay < 10) "0$ngay" else ngay.toString()
            return temp
        }


         fun formatNumber(input: String): String {
            val cleanString = input.replace(".", "")
            val formattedString = cleanString.toLongOrNull()?.let {
                String.format("%,d", it).replace(",", ".")
            }
            return formattedString ?: input
        }

        fun formatPriceToInt(input: String): Int? {
            val cleanString = input.replace(".", "")
            return cleanString.toIntOrNull()
        }

        fun formatDate(input:String):String{
            val str= input.split("-")
            return str[2]+"/"+str[1]+"/"+str[0]
        }
    }
}