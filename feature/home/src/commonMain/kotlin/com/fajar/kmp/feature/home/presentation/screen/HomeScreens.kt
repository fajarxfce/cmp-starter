package com.fajar.kmp.feature.home.presentation.screen

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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.fajar.kmp.core.designsystem.PosSegmentedControl
import com.fajar.kmp.core.designsystem.PosTextField
import com.fajar.kmp.core.navigation.AppRoute
import com.fajar.kmp.core.navigation.displayTitle
import com.fajar.kmp.feature.pos.presentation.shell.PosShellState

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(PosPalette.Ocean, Color(0xFF043B88), Color(0xFF06182D)))),
        contentAlignment = Alignment.Center,
    ) {
        GridBackdrop(tint = Color.White.copy(alpha = 0.10f))
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            LogoMark(72.dp, light = true)
            Text("Kasir POS", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
            Text("Kelola penjualan toko dari satu tempat", color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Text("Cepat dibuka, mudah dipakai, siap jualan", color = Color.White.copy(alpha = 0.58f), fontSize = 12.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 34.dp))
    }
}

@Composable
fun OnboardingScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    PosPage {
        BrandHeader("Mulai kelola toko")
        PosCard(containerColor = PosPalette.Ink, borderColor = Color.Transparent, padding = PaddingValues(22.dp)) {
            GridBackdrop(tint = Color.White.copy(alpha = 0.07f))
            Text("Kasir yang rapi untuk operasional harian.", color = Color.White, fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(12.dp))
            Text("Pantau penjualan, susun katalog, dan layani pelanggan tanpa ribet.", color = Color.White.copy(alpha = 0.70f), fontSize = 14.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                DarkMetric("Cepat", "Checkout", Modifier.weight(1f))
                DarkMetric("Rapi", "Katalog", Modifier.weight(1f))
                DarkMetric("Aman", "Sinkron", Modifier.weight(1f))
            }
        }
        PosButton("Buat akun toko", onRegister, Modifier.fillMaxWidth())
        PosButton("Masuk ke akun saya", onLogin, Modifier.fillMaxWidth(), variant = PosButtonVariant.Secondary)
    }
}

@Composable
fun HomeScreen(
    route: AppRoute,
    state: PosShellState,
    onLoadCatalog: () -> Unit,
    onCheckout: () -> Unit,
    onSync: () -> Unit,
    onLoadAdmin: () -> Unit,
    onNavigate: (AppRoute) -> Unit,
    onLogout: () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(PosPalette.Canvas).windowInsetsPadding(WindowInsets.safeDrawing)) {
        GridBackdrop(tint = PosPalette.Ocean.copy(alpha = 0.04f))
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HomeTopBar(onLogout)
                AnimatedContent(route, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "section") { current ->
                    when (current) {
                        AppRoute.Dashboard -> DashboardSection(state)
                        AppRoute.Catalog -> CatalogSection(state, onLoadCatalog)
                        AppRoute.Checkout -> CheckoutSection(state, onCheckout)
                        AppRoute.Sync -> SyncSection(state, onSync)
                        AppRoute.Admin -> AdminSection(state, onLoadAdmin)
                        else -> DashboardSection(state)
                    }
                }
            }
            BottomBar(route, onNavigate)
        }
    }
}

@Composable
private fun DashboardSection(state: PosShellState) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard("Penjualan hari ini", "Rp 4.850.000", "Hari ini", PosPalette.Ocean, Modifier.weight(1f))
            MetricCard("Produk aktif", "128", "Katalog", PosPalette.Success, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard("Sinkronisasi", "Baru saja", "Online", PosPalette.Warning, Modifier.weight(1f))
            MetricCard("Toko", if (state.hasActiveStore) "Aktif" else "Belum aktif", "Profil", PosPalette.OceanDark, Modifier.weight(1f))
        }
        SectionCard("Ringkasan toko", "Semua fitur utama siap diakses dari tab bawah: katalog, kasir, sinkronisasi, dan pengaturan toko.")
        StatusRow("Akun", state.authStatus, state.isAuthLoading)
        StatusRow("Toko", state.storeStatus, state.isStoreLoading)
    }
}

@Composable
private fun CatalogSection(state: PosShellState, onLoadCatalog: () -> Unit) {
    var view by rememberSaveable { mutableStateOf("Produk") }
    var trackStock by rememberSaveable { mutableStateOf(true) }
    var category by rememberSaveable { mutableStateOf("Makanan & Minuman") }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        PosSegmentedControl(listOf("Produk", "Kategori"), view, { view = it }, Modifier.fillMaxWidth())
        PosCard(padding = PaddingValues(18.dp)) {
            Text(if (view == "Produk") "Tambah produk" else "Tambah kategori", color = PosPalette.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(if (view == "Produk") "Lengkapi produk agar kasir bisa menjual lebih cepat." else "Kelompokkan produk supaya katalog mudah dicari.", color = PosPalette.Slate, fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            if (view == "Produk") {
                PosDropdown("Kategori", category, listOf("Makanan & Minuman", "Retail", "Jasa"), { category = it })
                Spacer(Modifier.height(10.dp))
                PosTextField("Kopi Susu", {}, "Nama produk")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PosTextField("KPS-001", {}, "SKU", Modifier.weight(1f))
                    PosTextField("cup", {}, "Satuan", Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PosTextField("15000", {}, "Harga jual", Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    PosTextField("100", {}, "Stok awal", Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
                Spacer(Modifier.height(10.dp))
                PosCheckbox(trackStock, { trackStock = it }, "Pantau stok", supportingText = "Stok akan berkurang otomatis saat transaksi.")
            } else {
                PosTextField("Makanan & Minuman", {}, "Nama kategori")
                Spacer(Modifier.height(10.dp))
                PosTextField("Produk F&B", {}, "Deskripsi")
            }
        }
        StatusRow("Katalog", state.catalogStatus, state.isCatalogLoading)
        PosButton("Muat katalog", onLoadCatalog, Modifier.fillMaxWidth(), enabled = !state.isCatalogLoading)
    }
}

@Composable
private fun CheckoutSection(state: PosShellState, onCheckout: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionCard("Kasir cepat", "Buat transaksi sederhana dan simpan pembayaran pelanggan.")
        DataRow("Keranjang", "2x Kopi Susu")
        DataRow("Pembayaran tunai", "Dibayar Rp 50.000")
        StatusRow("Kasir", state.checkoutStatus, state.isCheckoutLoading)
        PosButton("Simpan transaksi", onCheckout, Modifier.fillMaxWidth(), enabled = !state.isCheckoutLoading)
    }
}

@Composable
private fun SyncSection(state: PosShellState, onSync: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionCard("Sinkronisasi toko", "Pastikan perubahan produk dan transaksi tersimpan aman.")
        DataRow("Menunggu sinkron", "3 perubahan")
        DataRow("Terakhir diperbarui", "Baru saja")
        StatusRow("Sinkron", state.syncStatus, state.isSyncLoading)
        PosButton("Sinkronkan sekarang", onSync, Modifier.fillMaxWidth(), enabled = !state.isSyncLoading)
    }
}

@Composable
private fun AdminSection(state: PosShellState, onLoadAdmin: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionCard("Pengaturan toko", "Kelola akses tim dan pantau kondisi toko dari satu tempat.")
        DataRow("Toko aktif", "1 toko")
        DataRow("Anggota tim", "3 orang")
        StatusRow("Pengaturan", state.adminStatus, state.isAdminLoading)
        PosButton("Muat pengaturan", onLoadAdmin, Modifier.fillMaxWidth(), enabled = !state.isAdminLoading)
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
private fun HomeTopBar(onLogout: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BrandHeader("Dashboard toko")
        Spacer(Modifier.width(10.dp))
        PosButton("Keluar", onLogout, variant = PosButtonVariant.Ghost)
    }
}

@Composable
private fun BottomBar(selected: AppRoute, onSelected: (AppRoute) -> Unit) {
    NavigationBar(containerColor = PosPalette.Surface, tonalElevation = 0.dp) {
        listOf(AppRoute.Dashboard, AppRoute.Catalog, AppRoute.Checkout, AppRoute.Sync, AppRoute.Admin).forEach { item ->
            val active = item == selected
            NavigationBarItem(
                selected = active,
                onClick = { onSelected(item) },
                icon = { Box(Modifier.size(if (active) 10.dp else 7.dp).clip(CircleShape).background(if (active) PosPalette.Ocean else PosPalette.Muted)) },
                label = { Text(item.displayTitle, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center) },
            )
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, detail: String, accent: Color, modifier: Modifier = Modifier) {
    PosCard(modifier = modifier, padding = PaddingValues(14.dp)) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(accent))
        Spacer(Modifier.height(12.dp))
        Text(label, color = PosPalette.Slate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(value, color = PosPalette.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(detail, color = PosPalette.Slate, fontSize = 11.sp)
    }
}

@Composable
private fun SectionCard(title: String, body: String) {
    PosCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
        Text(title, color = PosPalette.Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(6.dp))
        Text(body, color = PosPalette.Slate, fontSize = 13.sp, lineHeight = 20.sp)
    }
}

@Composable
private fun DataRow(title: String, detail: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(PosPalette.Surface).border(1.dp, PosPalette.Line, RoundedCornerShape(18.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(PosPalette.Ocean))
        Spacer(Modifier.width(10.dp))
        Text(title, color = PosPalette.Ink, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(detail, color = PosPalette.Slate, fontSize = 12.sp)
    }
}

@Composable
private fun StatusRow(title: String, status: String, loading: Boolean) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(PosPalette.Wash).border(1.dp, PosPalette.Line, RoundedCornerShape(18.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(if (loading) PosPalette.Warning else statusColor(status)))
        Spacer(Modifier.width(10.dp))
        Text(title, color = PosPalette.Ink, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(if (loading) "Memproses..." else status, color = PosPalette.Slate, fontSize = 12.sp, textAlign = TextAlign.End)
    }
}

private fun statusColor(status: String): Color = when {
    status.contains("gagal", ignoreCase = true) -> PosPalette.Danger
    status.contains("unauthorized", ignoreCase = true) -> PosPalette.Danger
    status.contains("offline", ignoreCase = true) -> PosPalette.Danger
    status.contains("belum", ignoreCase = true) -> PosPalette.Warning
    else -> PosPalette.Success
}

@Composable
private fun DarkMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = 0.10f)).border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(18.dp)).padding(12.dp)) {
        Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(label, color = Color.White.copy(alpha = 0.66f), fontSize = 11.sp)
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
