package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
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

fun getThemeColorScheme(appTheme: AppTheme, isDarkMode: Boolean): ColorScheme {
    if (isDarkMode) {
        return when (appTheme) {
            AppTheme.SAPPHIRE -> darkColorScheme(
                primary = Color(0xFF93C5FD), // Light Sapphire Blue
                onPrimary = Color(0xFF1E3A8A),
                primaryContainer = Color(0xFF1E40AF),
                onPrimaryContainer = Color(0xFFDBEAFE),
                secondary = Color(0xFF60A5FA),
                onSecondary = Color(0xFF1E3A8A),
                background = Color(0xFF0F172A), // Slate-900 (Deep rich navy-black)
                surface = Color(0xFF1E293B), // Slate-800
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFEFF6FF),
                surfaceVariant = Color(0xFF334155),
                onSurfaceVariant = Color(0xFF94A3B8),
                tertiary = GoldAmber
            )
            AppTheme.ARCTIC_BREEZE -> darkColorScheme(
                primary = Color(0xFF7DD3FC), // Light arctic blue
                onPrimary = Color(0xFF0369A1),
                primaryContainer = Color(0xFF075985),
                onPrimaryContainer = Color(0xFFE0F2FE),
                secondary = Color(0xFF38BDF8),
                onSecondary = Color(0xFF0C4A6E),
                background = Color(0xFF030712), // Deep near-black
                surface = Color(0xFF0F172A), // Ice deep dark
                onBackground = Color(0xFFF0F9FF),
                onSurface = Color(0xFFE0F2FE),
                surfaceVariant = Color(0xFF1E293B),
                onSurfaceVariant = Color(0xFF38BDF8),
                tertiary = GoldAmber
            )
            AppTheme.PAPER_INK -> darkColorScheme(
                primary = Color(0xFFFAFAFA), // Crisp white
                onPrimary = Color(0xFF18181B),
                primaryContainer = Color(0xFF27272A),
                onPrimaryContainer = Color(0xFFF4F4F5),
                secondary = Color(0xFFD4D4D8),
                onSecondary = Color(0xFF27272A),
                background = Color(0xFF09090B), // Near absolute black
                surface = Color(0xFF18181B), // Zinc-900
                onBackground = Color(0xFFFAFAFA),
                onSurface = Color(0xFFF4F4F5),
                surfaceVariant = Color(0xFF27272A),
                onSurfaceVariant = Color(0xFFA1A1AA),
                tertiary = GoldAmber
            )
            AppTheme.ALABASTER -> darkColorScheme(
                primary = Color(0xFF60A5FA),
                onPrimary = Color(0xFF0F172A),
                primaryContainer = Color(0xFF1E3A8A),
                onPrimaryContainer = Color(0xFFEFF6FF),
                secondary = Color(0xFF94A3B8),
                onSecondary = Color(0xFF1E293B),
                background = Color(0xFF0B0F19),
                surface = Color(0xFF1E293B),
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF1F5F9),
                surfaceVariant = Color(0xFF334155),
                onSurfaceVariant = Color(0xFF94A3B8),
                tertiary = GoldAmber
            )
            AppTheme.NORDIC_FROST -> darkColorScheme(
                primary = Color(0xFF2DD4BF), // Light Teal
                onPrimary = Color(0xFF042F2E),
                primaryContainer = Color(0xFF115E59),
                onPrimaryContainer = Color(0xFFCCFBF1),
                secondary = Color(0xFF5EEAD4),
                onSecondary = Color(0xFF134E5A),
                background = Color(0xFF020617),
                surface = Color(0xFF0F172A),
                onBackground = Color(0xFFF1F5F9),
                onSurface = Color(0xFFF1F5F9),
                surfaceVariant = Color(0xFF1E293B),
                onSurfaceVariant = Color(0xFF14B8A6),
                tertiary = GoldAmber
            )
            AppTheme.SAGE_SERENITY -> darkColorScheme(
                primary = Color(0xFFA9DFBF), // Calm green
                onPrimary = Color(0xFF145A32),
                primaryContainer = Color(0xFF1E8449),
                onPrimaryContainer = Color(0xFFD4EFDF),
                secondary = Color(0xFF789461),
                onSecondary = Color(0xFF1A301D),
                background = Color(0xFF151D16), // Pine dark
                surface = Color(0xFF232D24),
                onBackground = Color(0xFFFDFBF7),
                onSurface = Color(0xFFF5F0E6),
                surfaceVariant = Color(0xFF2E3E30),
                onSurfaceVariant = Color(0xFF789461),
                tertiary = GoldAmber
            )
            AppTheme.LAVENDER_MIST -> darkColorScheme(
                primary = Color(0xFFC084FC), // Lavender glow
                onPrimary = Color(0xFF4A044E),
                primaryContainer = Color(0xFF6B21A8),
                onPrimaryContainer = Color(0xFFF3E8FF),
                secondary = Color(0xFFA78BFA),
                onSecondary = Color(0xFF2E1065),
                background = Color(0xFF120B1E), // Velvet night
                surface = Color(0xFF1D1233),
                onBackground = Color(0xFFFAF5FF),
                onSurface = Color(0xFFF3E8FF),
                surfaceVariant = Color(0xFF2E1854),
                onSurfaceVariant = Color(0xFFA78BFA),
                tertiary = GoldAmber
            )
            AppTheme.EARTHY_MOSS -> darkColorScheme(
                primary = Color(0xFFC2D994),
                onPrimary = Color(0xFF2A3D0B),
                primaryContainer = Color(0xFF4A522D),
                onPrimaryContainer = Color(0xFFE9E5D9),
                secondary = Color(0xFFA3B18A),
                onSecondary = Color(0xFF252B14),
                background = Color(0xFF11160C),
                surface = Color(0xFF1E2517),
                onBackground = Color(0xFFF8F6F0),
                onSurface = Color(0xFFE9E5D9),
                surfaceVariant = Color(0xFF2B3322),
                onSurfaceVariant = Color(0xFFA3B18A),
                tertiary = GoldAmber
            )
        }
    }

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
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getThemeColorScheme(appTheme, isDarkMode)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
