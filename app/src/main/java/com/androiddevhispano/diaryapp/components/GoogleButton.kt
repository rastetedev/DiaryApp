package com.androiddevhispano.diaryapp.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.ui.theme.Size.doubleExtraLarge
import com.androiddevhispano.diaryapp.ui.theme.Size.extraLarge
import com.androiddevhispano.diaryapp.ui.theme.Size.extraSmall
import com.androiddevhispano.diaryapp.ui.theme.Size.large
import com.androiddevhispano.diaryapp.ui.theme.Size.tiny

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    loadingState: Boolean = false,
    primaryText: String = stringResource(id = R.string.sign_in_google),
    secondaryText: String = stringResource(id = R.string.please_wait),
    icon: Int = R.drawable.google_logo,
    shape: Shape = Shapes().extraSmall,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    borderStrokeWidth: Dp = tiny,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier
            .clickable(enabled = !loadingState) {
                onClick()
            },
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(width = borderStrokeWidth, color = borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(extraLarge)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                tint = Color.Unspecified,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(large))
            Text(
                text = if (loadingState) secondaryText else primaryText,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
            )
            if (loadingState) {
                Spacer(modifier = Modifier.width(doubleExtraLarge))
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(doubleExtraLarge),
                    strokeWidth = extraSmall,
                    color = progressIndicatorColor
                )
            }
        }
    }
}

@Composable
@Preview
fun GoogleButtonPreview() {
    GoogleButton {}
}

@Composable
@Preview
fun GoogleButtonPreview2() {
    GoogleButton(loadingState = true) {}
}