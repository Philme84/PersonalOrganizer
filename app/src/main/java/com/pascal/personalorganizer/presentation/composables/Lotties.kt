package com.pascal.personalorganizer.presentation.composables

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pascal.personalorganizer.R

@Composable
fun EmptyLottie(){
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.empty_lottie))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    )
}