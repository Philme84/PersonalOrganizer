package com.pascal.personalorganizer.presentation.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pascal.personalorganizer.R

val openSansFamily = FontFamily(
    Font(R.font.open_sans_bold, FontWeight.Bold),
    Font(R.font.open_sans_medium, FontWeight.Medium),
    Font(R.font.open_sans_regular, FontWeight.Normal),
)

val Typography = Typography(
    h4 = TextStyle(
        fontFamily = openSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    h5 = TextStyle(
        fontFamily = openSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    h6 = TextStyle(
        fontFamily = openSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    caption = TextStyle(
        fontFamily = openSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 35.sp
    ),
    overline = TextStyle(
        fontFamily = openSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)