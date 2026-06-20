package com.fajar.kmp.feature.pos.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fajar.kmp.core.designsystem.PosButton
import com.fajar.kmp.core.designsystem.PosButtonVariant
import com.fajar.kmp.core.designsystem.PosCard
import com.fajar.kmp.core.designsystem.PosCheckbox
import com.fajar.kmp.core.designsystem.PosDropdown
import com.fajar.kmp.core.designsystem.PosPalette
import com.fajar.kmp.core.designsystem.PosRadioGroup
import com.fajar.kmp.core.designsystem.PosSegmentedControl
import com.fajar.kmp.core.designsystem.PosStatusPill
import com.fajar.kmp.core.designsystem.PosTextField
import com.fajar.kmp.core.navigation.AppRoute
import com.fajar.kmp.core.navigation.displayTitle
import com.fajar.kmp.feature.pos.data.api.StoreRegisterRequest

@Composable
fun StoreSetupScreen(storeStatus: String,
    isStoreLoading: Boolean, onContinue: (StoreRegisterRequest) -> Unit) {
    var name by rememberSaveable { mutableStateOf("Toko Mas Bro") }
    var slug by rememberSaveable { mutableStateOf("toko-mas-bro") }
    var description by rememberSaveable { mutableStateOf("Toko serba ada") }
    var timezone by rememberSaveable { mutableStateOf("Asia/Jakarta") }
    var country by rememberSaveable { mutableStateOf("ID") }
    var email by rememberSaveable { mutableStateOf("toko@masbro.com") }
    var phone by rememberSaveable { mutableStateOf("081234567890") }

    PosPage {
        BrandHeader("Profil toko")
        PosCard(padding = PaddingValues(18.dp)) {
            Text("Lengkapi toko Anda", color = PosPalette.Ink, fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("Informasi ini dipakai di struk, katalog, dan laporan penjualan.", color = PosPalette.Slate, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            PosTextField(name, { name = it }, "Nama toko")
            Spacer(Modifier.height(10.dp))
            PosTextField(slug, { slug = it }, "Alamat singkat toko")
            Spacer(Modifier.height(10.dp))
            PosTextField(description, { description = it }, "Deskripsi toko")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PosDropdown("Zona waktu", timezone, listOf("Asia/Jakarta", "Asia/Makassar", "Asia/Jayapura"), { timezone = it }, Modifier.weight(1f))
                PosDropdown("Negara", country, listOf("ID", "SG", "MY"), { country = it }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            PosTextField(phone, { phone = it }, "Nomor telepon", keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(10.dp))
            PosTextField(email, { email = it }, "Email toko", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(18.dp))
            StatusRow("Toko", storeStatus, isStoreLoading)
            Spacer(Modifier.height(12.dp))
            PosButton(
                "Simpan dan buka toko",
                {
                    onContinue(
                        StoreRegisterRequest(
                            name = name,
                            slug = slug,
                            description = description,
                            timezone = timezone,
                            country = country,
                            phone = phone,
                            email = email,
                        ),
                    )
                },
                Modifier.fillMaxWidth(),
                enabled = !isStoreLoading,
            )
        }
    }
}


@Composable
private fun PosPage(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(PosPalette.Canvas).windowInsetsPadding(WindowInsets.safeDrawing)) {
        GridBackdrop(tint = PosPalette.Ocean.copy(alpha = 0.06f))
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}

@Composable
private fun BrandHeader(label: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        LogoMark(44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Kasir POS", color = PosPalette.Ink, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(label, color = PosPalette.Slate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StatusRow(title: String, status: String, loading: Boolean) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(PosPalette.Wash).border(1.dp, PosPalette.Line, RoundedCornerShape(18.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(if (loading) PosPalette.Warning else statusColor(status)))
        Spacer(Modifier.width(10.dp))
        Text(title, color = PosPalette.Ink, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(if (loading) "Loading..." else status, color = PosPalette.Slate, fontSize = 12.sp, textAlign = TextAlign.End)
    }
}

@Composable
private fun LogoMark(size: androidx.compose.ui.unit.Dp, light: Boolean = false) {
    Box(Modifier.size(size).clip(RoundedCornerShape(size / 3)).background(if (light) Color.White else PosPalette.Wash).border(1.dp, if (light) Color.White.copy(alpha = 0.40f) else PosPalette.Line, RoundedCornerShape(size / 3)), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size * 0.56f)) {
            drawCircle(PosPalette.Ocean)
            drawCircle(Color.White, radius = this.size.minDimension * 0.28f, center = center.copy(y = center.y - this.size.minDimension * 0.05f))
            drawCircle(PosPalette.Success, radius = this.size.minDimension * 0.16f, center = Offset(center.x + this.size.minDimension * 0.24f, center.y + this.size.minDimension * 0.20f))
        }
    }
}

@Composable
private fun GridBackdrop(tint: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val gap = 42.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(tint, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += gap
        }
        var y = 0f
        while (y < size.height) {
            drawLine(tint, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += gap
        }
    }
}

private fun statusColor(status: String): Color = when {
    status.contains("unauthorized", ignoreCase = true) -> PosPalette.Danger
    status.contains("failed", ignoreCase = true) -> PosPalette.Danger
    status.contains("offline", ignoreCase = true) -> PosPalette.Danger
    status.contains("empty", ignoreCase = true) -> PosPalette.Warning
    status.contains("not ", ignoreCase = true) -> PosPalette.Muted
    else -> PosPalette.Success
}
