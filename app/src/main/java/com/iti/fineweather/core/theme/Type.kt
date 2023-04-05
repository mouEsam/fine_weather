package com.iti.fineweather.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.iti.fineweather.R


object AppTypography {

    // TODO: fix
    private val Montserrat = FontFamily.Default ?: getGoogleFontFamily(
        name = "Montserrat",
        weights = listOf(
            FontWeight.Thin,
            FontWeight.ExtraLight,
            FontWeight.Light,
            FontWeight.Normal,
            FontWeight.Medium,
            FontWeight.SemiBold,
            FontWeight.Bold,
            FontWeight.ExtraBold,
            FontWeight.Black,
        )
    )

    val headerLarge = TextStyle(
        fontFamily = Montserrat,
        fontSize = 84.sp,
        fontWeight = FontWeight.W900
    )

    val header = TextStyle(
        fontFamily = Montserrat,
        fontSize = 42.sp,
        fontWeight = FontWeight.W900
    )

    val title = TextStyle(
        fontFamily = Montserrat,
        fontSize = 22.sp,
        fontWeight = FontWeight.W700
    )

    val subtitle = TextStyle(
        fontFamily = Montserrat,
        fontSize = 18.sp,
        fontWeight = FontWeight.W600
    )

    val action = TextStyle(
        fontFamily = Montserrat,
        fontSize = 16.sp,
        fontWeight = FontWeight.W600
    )

    val labelBold = TextStyle(
        fontFamily = Montserrat,
        fontSize = 16.sp,
        fontWeight = FontWeight.W600
    )

    val bodyBold = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp,
        fontWeight = FontWeight.W500
    )

    val body = TextStyle(
        fontFamily = Montserrat,
        fontSize = 14.sp,
        fontWeight = FontWeight.W400
    )

    val label = TextStyle(
        fontFamily = Montserrat,
        fontSize = 11.sp,
        fontWeight = FontWeight.W400
    )

    fun mapToMaterialTypography() = Typography(
        bodyLarge = bodyBold,
        bodyMedium = body,
        titleLarge = title,
        labelLarge = labelBold,
        labelSmall = label,
    )

    private fun getGoogleFontFamily(
        name: String,
        provider: GoogleFont.Provider = googleFontProvider,
        weights: List<FontWeight>
    ): FontFamily {
        val font = GoogleFont(name)
        return FontFamily(
            weights.map {
                Font(font, provider, it)
            }
        )
    }

    private val googleFontProvider: GoogleFont.Provider by lazy {
        GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = R.array.com_google_android_gms_fonts_certs
        )
    }
}
