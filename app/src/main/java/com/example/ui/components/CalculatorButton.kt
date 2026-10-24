package com.example.ui.components

import android.view.View
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    NUMBER,
    OPERATOR,
    ACTION,
    EQUALS,
    SECONDARY
}

@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null,
    testTag: String,
    buttonType: ButtonType = ButtonType.NUMBER,
    onClick: (View) -> Unit
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_scale"
    )

    val (containerColor, contentColor) = when (buttonType) {
        ButtonType.NUMBER -> Pair(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        ButtonType.OPERATOR -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        ButtonType.ACTION -> Pair(
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.onErrorContainer
        )
        ButtonType.EQUALS -> Pair(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        ButtonType.SECONDARY -> Pair(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Surface(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick(view) }
            ),
        shape = RoundedCornerShape(26.dp),
        color = containerColor,
        tonalElevation = if (isPressed) 2.dp else 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = contentColor
                )
            } else {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = when (buttonType) {
                        ButtonType.EQUALS, ButtonType.OPERATOR -> 28.sp
                        ButtonType.NUMBER -> 26.sp
                        ButtonType.ACTION -> 22.sp
                        ButtonType.SECONDARY -> 20.sp
                    },
                    fontWeight = when (buttonType) {
                        ButtonType.EQUALS, ButtonType.OPERATOR -> FontWeight.SemiBold
                        ButtonType.NUMBER -> FontWeight.Medium
                        ButtonType.ACTION, ButtonType.SECONDARY -> FontWeight.Normal
                    }
                )
            }
        }
    }
}
