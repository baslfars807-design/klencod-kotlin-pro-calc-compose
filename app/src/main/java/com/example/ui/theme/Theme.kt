package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.local.AccentPalette
import com.example.data.local.ThemeMode

fun getCustomColorScheme(palette: AccentPalette, darkTheme: Boolean): ColorScheme {
    return when (palette) {
        AccentPalette.TEAL -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF2DD4BF),
                onPrimary = Color(0xFF003731),
                primaryContainer = Color(0xFF134E4A),
                onPrimaryContainer = Color(0xFF99F6E4),
                secondary = Color(0xFF38BDF8),
                onSecondary = Color(0xFF00354E),
                secondaryContainer = Color(0xFF075985),
                onSecondaryContainer = Color(0xFFBAE6FD),
                background = Color(0xFF0B1320),
                onBackground = Color(0xFFE2E8F0),
                surface = Color(0xFF0F172A),
                onSurface = Color(0xFFF1F5F9),
                surfaceVariant = Color(0xFF1E293B),
                onSurfaceVariant = Color(0xFF94A3B8)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF0D9488),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFCCFBF1),
                onPrimaryContainer = Color(0xFF115E59),
                secondary = Color(0xFF0284C7),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE0F2FE),
                onSecondaryContainer = Color(0xFF0369A1),
                background = Color(0xFFF8FAFC),
                onBackground = Color(0xFF0F172A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFE2E8F0),
                onSurfaceVariant = Color(0xFF475569)
            )
        }

        AccentPalette.INDIGO -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF818CF8),
                onPrimary = Color(0xFF1E1B4B),
                primaryContainer = Color(0xFF312E81),
                onPrimaryContainer = Color(0xFFC7D2FE),
                secondary = Color(0xFFA5B4FC),
                background = Color(0xFF090D16),
                surface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFF1E293B)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF4F46E5),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE0E7FF),
                onPrimaryContainer = Color(0xFF3730A3),
                secondary = Color(0xFF6366F1),
                background = Color(0xFFF8FAFC),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE2E8F0)
            )
        }

        AccentPalette.EMERALD -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF34D399),
                onPrimary = Color(0xFF022C22),
                primaryContainer = Color(0xFF064E3B),
                onPrimaryContainer = Color(0xFFA7F3D0),
                secondary = Color(0xFF6EE7B7),
                background = Color(0xFF061510),
                surface = Color(0xFF0A1F17),
                surfaceVariant = Color(0xFF133227)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF059669),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFD1FAE5),
                onPrimaryContainer = Color(0xFF065F46),
                secondary = Color(0xFF10B981),
                background = Color(0xFFF0FDF4),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFDCFCE7)
            )
        }

        AccentPalette.AMBER -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFFBBF24),
                onPrimary = Color(0xFF451A03),
                primaryContainer = Color(0xFF78350F),
                onPrimaryContainer = Color(0xFFFDE68A),
                secondary = Color(0xFFF59E0B),
                background = Color(0xFF18181B),
                surface = Color(0xFF27272A),
                surfaceVariant = Color(0xFF3F3F46)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFD97706),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFEF3C7),
                onPrimaryContainer = Color(0xFF92400E),
                secondary = Color(0xFFF59E0B),
                background = Color(0xFFFFFBEB),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFDE68A)
            )
        }

        AccentPalette.PURPLE -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFA78BFA),
                onPrimary = Color(0xFF2E1065),
                primaryContainer = Color(0xFF4C1D95),
                onPrimaryContainer = Color(0xFFDDD6FE),
                secondary = Color(0xFFC084FC),
                background = Color(0xFF0D0B18),
                surface = Color(0xFF15102A),
                surfaceVariant = Color(0xFF231B42)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF7C3AED),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFEDE9FE),
                onPrimaryContainer = Color(0xFF5B21B6),
                secondary = Color(0xFF8B5CF6),
                background = Color(0xFFFAF5FF),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFF3E8FF)
            )
        }

        AccentPalette.AMOLED -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF38BDF8),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF1E293B),
                onPrimaryContainer = Color(0xFFE2E8F0),
                secondary = Color(0xFF94A3B8),
                background = Color(0xFF000000),
                onBackground = Color(0xFFFFFFFF),
                surface = Color(0xFF050505),
                onSurface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFF121212),
                onSurfaceVariant = Color(0xFFCBD5E1)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF0284C7),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE0F2FE),
                background = Color(0xFFF8FAFC),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: AccentPalette = AccentPalette.TEAL,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = getCustomColorScheme(palette, darkTheme)

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
