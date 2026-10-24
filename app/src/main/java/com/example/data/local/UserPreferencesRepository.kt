package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "calculator_preferences")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AccentPalette(val displayName: String, val hex: Long) {
    TEAL("Teal & Slate", 0xFF0D9488),
    INDIGO("Royal Indigo", 0xFF4F46E5),
    EMERALD("Emerald Green", 0xFF059669),
    AMBER("Sunset Amber", 0xFFD97706),
    PURPLE("Vibrant Violet", 0xFF7C3AED),
    AMOLED("AMOLED Dark", 0xFF64748B)
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentPalette: AccentPalette = AccentPalette.TEAL,
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_PALETTE = stringPreferencesKey("accent_palette")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeModeStr)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }

            val paletteStr = preferences[PreferencesKeys.ACCENT_PALETTE] ?: AccentPalette.TEAL.name
            val accentPalette = try {
                AccentPalette.valueOf(paletteStr)
            } catch (e: Exception) {
                AccentPalette.TEAL
            }

            val haptic = preferences[PreferencesKeys.HAPTIC_ENABLED] ?: true
            val sound = preferences[PreferencesKeys.SOUND_ENABLED] ?: false

            UserPreferences(
                themeMode = themeMode,
                accentPalette = accentPalette,
                hapticEnabled = haptic,
                soundEnabled = sound
            )
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setAccentPalette(palette: AccentPalette) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCENT_PALETTE] = palette.name
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }
}
