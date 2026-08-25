package com.swirlfist.simplepixel.presentation.uielements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableResId: Int,
    @StringRes contentDescriptionResId: Int,
    size: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    FilledIconButton(
        modifier = modifier
            .size(size),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        enabled = isEnabled,
    ) {
        Icon(
            painter = painterResource(drawableResId),
            contentDescription = stringResource(contentDescriptionResId),
        )
    }
}