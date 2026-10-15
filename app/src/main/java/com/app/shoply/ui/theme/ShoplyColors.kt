package com.app.shoply.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.shoply.R

object ShoplyColors {
    val Background = Color(0xFFF4F3EF)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceMuted = Color(0xFFF1EFEA)
    val IconCircle = Color(0xFFECEAE5)
    val Border = Color(0xFFE4E1DA)
    val FieldBorder = Color(0xFFD3D0C8)
    val OutlineButtonBorder = Color(0xFFB9B5AC)
    val Divider = Color(0xFFECEAE5)

    val TextPrimary = Color(0xFF17181C)
    val TextSecondary = Color(0xFF5B5E66)
    val TextPlaceholder = Color(0xFF6B6E76)
    val Icon = Color(0xFF3D4048)
    val IconMuted = Color(0xFF8A867E)

    val Accent = Color(0xFF2546D8)
    val ClearButton = Color(0xFF8A8D94)
    val StarFilled = Color(0xFFD99000)
    val StarEmpty = Color(0xFFD6D3CC)
    val Skeleton = Color(0xFFE8E5DF)
    val SheetHandle = Color(0xFFC9C6BF)
    val Scrim = Color(0x6B17181C)

    val Positive = Color(0xFF1B6E3F)
    val PositiveContainer = Color(0xFFE2F1E7)
    val PositiveDot = Color(0xFF1E8A4C)
    val Warning = Color(0xFF8A4B00)
    val WarningContainer = Color(0xFFFCEBD3)
    val WarningDot = Color(0xFFC46A00)
    val Negative = Color(0xFF9B2228)
    val NegativeContainer = Color(0xFFFBE5E5)
    val NegativeDot = Color(0xFFC0353B)
}

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
)

object ShoplyType {
    val Title = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, lineHeight = 28.sp)
    val SheetTitle = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, lineHeight = 24.sp)
    val SectionTitle = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, lineHeight = 22.sp)
    val CardTitle = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp)
    val Body = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp)
    val BodyMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)
    val BodySmall = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp)
    val Label = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp)
    val Input = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
    val Button = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp)
    val PriceCard = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.2).sp)
    val PriceSheet = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.4).sp)
    val ReviewScore = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 36.sp, letterSpacing = (-1).sp)
}