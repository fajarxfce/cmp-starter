package com.fajar.kmp.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object CmpColors {
    val Primary = Color(0xFF0069FF)
    val PrimaryPressed = Color(0xFF005FE6)
    val PrimaryContainer = Color(0xFFEAF2FF)
    val Secondary = Color(0xFF00AEEF)
    val BackgroundLight = Color(0xFFF7F9FC)
    val SurfaceLight = Color.White
    val BorderLight = Color(0xFFD9E2EC)
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val Success = Color(0xFF15CD72)
    val Warning = Color(0xFFFFB020)
    val Error = Color(0xFFE5484D)
    val DarkBackground = Color(0xFF0B1220)
    val DarkSurface = Color(0xFF111827)
    val DarkBorder = Color(0xFF253044)
}

object CmpSpacing {
    val xSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val xLarge = 32.dp
}

private val LightColors = lightColorScheme(
    primary = CmpColors.Primary,
    secondary = CmpColors.Secondary,
    background = CmpColors.BackgroundLight,
    surface = CmpColors.SurfaceLight,
    error = CmpColors.Error,
    onPrimary = Color.White,
    onBackground = CmpColors.TextPrimary,
    onSurface = CmpColors.TextPrimary,
)

private val DarkColors = darkColorScheme(
    primary = CmpColors.Primary,
    secondary = CmpColors.Secondary,
    background = CmpColors.DarkBackground,
    surface = CmpColors.DarkSurface,
    error = CmpColors.Error,
    onPrimary = Color.White,
)

@Composable
fun CmpTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}

@Composable
fun CmpAppScaffold(content: @Composable (PaddingValues) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        content(PaddingValues(CmpSpacing.medium))
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CmpColors.BorderLight),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(CmpSpacing.medium)) {
            content()
        }
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = CmpColors.Primary),
    ) {
        Text(text)
    }
}

@Composable
fun LoadingState(message: String = "Loading") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Text(message, modifier = Modifier.padding(top = CmpSpacing.small))
    }
}

@Composable
fun EmptyState(title: String, message: String) {
    DashboardCard {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(message, color = CmpColors.TextSecondary)
    }
}

@Composable
fun ErrorState(message: String) {
    DashboardCard {
        Text("Something went wrong", color = CmpColors.Error)
        Text(message, color = CmpColors.TextSecondary)
    }
}
