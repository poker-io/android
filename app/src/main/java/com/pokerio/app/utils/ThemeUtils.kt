package com.pokerio.app.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

class ThemeUtils {
    companion object {
        val lightColorScheme = ColorScheme(
            primary = Color(0.49019608f, 0.34117648f, 0.05490196f),
            onPrimary = Color(1.0f, 1.0f, 1.0f),
            primaryContainer = Color(1.0f, 0.87058824f, 0.68235296f),
            onPrimaryContainer = Color(0.15686275f, 0.09803922f, 0.0f),
            inversePrimary = Color(0.94509804f, 0.74509805f, 0.42745098f),
            secondary = Color(0.43137255f, 0.35686275f, 0.2509804f),
            onSecondary = Color(1.0f, 1.0f, 1.0f),
            secondaryContainer = Color(0.9764706f, 0.8745098f, 0.73333335f),
            onSecondaryContainer = Color(0.14901961f, 0.09803922f, 0.015686275f),
            tertiary = Color(0.30980393f, 0.39215687f, 0.25882354f),
            onTertiary = Color(1.0f, 1.0f, 1.0f),
            tertiaryContainer = Color(0.81960785f, 0.91764706f, 0.74509805f),
            onTertiaryContainer = Color(0.05490196f, 0.1254902f, 0.019607844f),
            background = Color(1.0f, 0.9843137f, 1.0f),
            onBackground = Color(0.12156863f, 0.105882354f, 0.08627451f),
            surface = Color(1.0f, 0.9843137f, 1.0f),
            onSurface = Color(0.12156863f, 0.105882354f, 0.08627451f),
            surfaceVariant = Color(0.9372549f, 0.8784314f, 0.8117647f),
            onSurfaceVariant = Color(0.30980393f, 0.27058825f, 0.22352941f),
            surfaceTint = Color(0.49019608f, 0.34117648f, 0.05490196f),
            inverseSurface = Color(0.20392157f, 0.1882353f, 0.16470589f),
            inverseOnSurface = Color(0.972549f, 0.9372549f, 0.90588236f),
            error = Color(0.7019608f, 0.14901961f, 0.11764706f),
            onError = Color(1.0f, 1.0f, 1.0f),
            errorContainer = Color(0.9764706f, 0.87058824f, 0.8627451f),
            onErrorContainer = Color(0.25490198f, 0.05490196f, 0.043137256f),
            outline = Color(0.49803922f, 0.45490196f, 0.4f),
            outlineVariant = Color(0.7921569f, 0.76862746f, 0.8156863f),
            scrim = Color(0.0f, 0.0f, 0.0f)
        )
    }
}
