package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val backgroundColor: Color,
    val surfaceColor: Color
) {
    SAPPHIRE(
        "sapphire",
        "Sapphire",
        "Deep Sapphire Blue & Royal Blue",
        SapphirePrimary,
        SapphireSecondary,
        Color(0xFFF8FAFC),
        Color(0xFFFFFFFF)
    ),
    ARCTIC_BREEZE(
        "arctic_breeze",
        "Arctic Breeze",
        "Bleu glacier, blanc doux, gris argenté",
        ArcticPrimary,
        ArcticSecondary,
        ArcticBackground,
        Color(0xFFFFFFFF)
    ),
    PAPER_INK(
        "paper_ink",
        "Paper & Ink",
        "Blanc cassé, anthracite, ardoise subtile",
        PaperInkPrimary,
        PaperInkSecondary,
        PaperInkBackground,
        Color(0xFFFFFFFF)
    ),
    ALABASTER(
        "alabaster",
        "Alabaster",
        "Blanc pur, gris cendre, accent bleu neutre",
        AlabasterPrimary,
        AlabasterSecondary,
        AlabasterBackground,
        Color(0xFFF8FAFC)
    ),
    NORDIC_FROST(
        "nordic_frost",
        "Nordic Frost",
        "Gris pâle, turquoise atténué, blanc éclatant",
        NordicPrimary,
        NordicSecondary,
        NordicBackground,
        Color(0xFFFFFFFF)
    ),
    SAGE_SERENITY(
        "sage_serenity",
        "Sage Serenity",
        "Vert sauge, crème chaleureuse, beige doux",
        SagePrimary,
        SageSecondary,
        SageBackground,
        Color(0xFFFFFFFF)
    ),
    LAVENDER_MIST(
        "lavender_mist",
        "Lavender Mist",
        "Pervenche, lilas pâle, blanc doux",
        LavenderPrimary,
        LavenderSecondary,
        LavenderBackground,
        Color(0xFFFFFFFF)
    ),
    EARTHY_MOSS(
        "earthy_moss",
        "Earthy Moss",
        "Vert olive, taupe chaud, sable",
        MossPrimary,
        MossSecondary,
        MossBackground,
        Color(0xFFFFFFFF)
    )
}

fun getThemeColorScheme(appTheme: AppTheme): ColorScheme {
    return when (appTheme) {
        AppTheme.SAPPHIRE -> lightColorScheme(
            primary = SapphirePrimary,
            onPrimary = Color.White,
            primaryContainer = SapphireContainer,
            onPrimaryContainer = SapphirePrimary,
            secondary = SapphireSecondary,
            onSecondary = Color.White,
            background = Color(0xFFF8FAFC),
            surface = Color.White,
            onBackground = Slate900,
            onSurface = Slate800,
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = Color(0xFF475569),
            tertiary = GoldAmber
        )
        AppTheme.ARCTIC_BREEZE -> lightColorScheme(
            primary = ArcticPrimary,
            onPrimary = Color.White,
            primaryContainer = ArcticContainer,
            onPrimaryContainer = ArcticPrimary,
            secondary = ArcticSecondary,
            onSecondary = Color.White,
            background = ArcticBackground,
            surface = Color.White,
            onBackground = Color(0xFF0F172A),
            onSurface = Color(0xFF1E293B),
            surfaceVariant = Color(0xFFE0F2FE),
            onSurfaceVariant = Color(0xFF0369A1),
            tertiary = GoldAmber
        )
        AppTheme.PAPER_INK -> lightColorScheme(
            primary = PaperInkPrimary,
            onPrimary = Color.White,
            primaryContainer = PaperInkContainer,
            onPrimaryContainer = PaperInkPrimary,
            secondary = PaperInkSecondary,
            onSecondary = Color.White,
            background = PaperInkBackground,
            surface = Color.White,
            onBackground = Color(0xFF09090B),
            onSurface = Color(0xFF18181B),
            surfaceVariant = Color(0xFFF4F4F5),
            onSurfaceVariant = Color(0xFF52525B),
            tertiary = GoldAmber
        )
        AppTheme.ALABASTER -> lightColorScheme(
            primary = AlabasterPrimary,
            onPrimary = Color.White,
            primaryContainer = AlabasterContainer,
            onPrimaryContainer = AlabasterPrimary,
            secondary = AlabasterSecondary,
            onSecondary = Color.White,
            background = AlabasterBackground,
            surface = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),
            onSurface = Color(0xFF1E293B),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = Color(0xFF475569),
            tertiary = GoldAmber
        )
        AppTheme.NORDIC_FROST -> lightColorScheme(
            primary = NordicPrimary,
            onPrimary = Color.White,
            primaryContainer = NordicContainer,
            onPrimaryContainer = NordicPrimary,
            secondary = NordicSecondary,
            onSecondary = Color.White,
            background = NordicBackground,
            surface = Color.White,
            onBackground = Color(0xFF0F172A),
            onSurface = Color(0xFF1E293B),
            surfaceVariant = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF0F766E),
            tertiary = GoldAmber
        )
        AppTheme.SAGE_SERENITY -> lightColorScheme(
            primary = SagePrimary,
            onPrimary = Color.White,
            primaryContainer = SageContainer,
            onPrimaryContainer = SagePrimary,
            secondary = SageSecondary,
            onSecondary = Color.White,
            background = SageBackground,
            surface = Color.White,
            onBackground = Color(0xFF2C3E2D),
            onSurface = Color(0xFF384E3A),
            surfaceVariant = Color(0xFFEFE9DC),
            onSurfaceVariant = Color(0xFF3E543F),
            tertiary = GoldAmber
        )
        AppTheme.LAVENDER_MIST -> lightColorScheme(
            primary = LavenderPrimary,
            onPrimary = Color.White,
            primaryContainer = LavenderContainer,
            onPrimaryContainer = LavenderPrimary,
            secondary = LavenderSecondary,
            onSecondary = Color.White,
            background = LavenderBackground,
            surface = Color.White,
            onBackground = Color(0xFF3B0764),
            onSurface = Color(0xFF581C87),
            surfaceVariant = Color(0xFFF3E8FF),
            onSurfaceVariant = Color(0xFF6B21A8),
            tertiary = GoldAmber
        )
        AppTheme.EARTHY_MOSS -> lightColorScheme(
            primary = MossPrimary,
            onPrimary = Color.White,
            primaryContainer = MossContainer,
            onPrimaryContainer = MossPrimary,
            secondary = MossSecondary,
            onSecondary = Color.White,
            background = MossBackground,
            surface = Color.White,
            onBackground = Color(0xFF283618),
            onSurface = Color(0xFF3A401C),
            surfaceVariant = Color(0xFFE9E5D9),
            onSurfaceVariant = Color(0xFF4A522D),
            tertiary = GoldAmber
        )
    }
}

@Composable
fun AIWorksheetTutorTheme(
    appTheme: AppTheme = AppTheme.SAPPHIRE,
    content: @Composable () -> Unit
) {
    val colorScheme = getThemeColorScheme(appTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
