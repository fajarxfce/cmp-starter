package com.fajar.kmp

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fajar.kmp.core.designsystem.CmpTheme
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
import kotlinx.coroutines.delay

private enum class AppStage { Splash, Onboarding, Auth, StoreSetup, Home }
private enum class AuthMode { Login, Register }
private enum class HomeSection(val label: String) { Dashboard("Dashboard"), Catalog("Catalog"), Checkout("Checkout"), Sync("Sync"), Admin("Admin") }

@Composable
@Preview
fun App() {
    CmpTheme {
        var stage by rememberSaveable { mutableStateOf(AppStage.Splash) }
        var authMode by rememberSaveable { mutableStateOf(AuthMode.Login) }

        LaunchedEffect(Unit) {
            delay(900)
            if (stage == AppStage.Splash) stage = AppStage.Onboarding
        }

        AnimatedContent(
            targetState = stage,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "pos-gg-flow",
        ) { screen ->
            when (screen) {
                AppStage.Splash -> SplashScreen()
                AppStage.Onboarding -> OnboardingScreen(
                    onLogin = {
                        authMode = AuthMode.Login
                        stage = AppStage.Auth
                    },
                    onRegister = {
                        authMode = AuthMode.Register
                        stage = AppStage.Auth
                    },
                )
                AppStage.Auth -> AuthScreen(
                    mode = authMode,
                    onModeChange = { authMode = it },
                    onSuccess = { stage = AppStage.StoreSetup },
                )
                AppStage.StoreSetup -> StoreSetupScreen(onContinue = { stage = AppStage.Home })
                AppStage.Home -> HomeScreen(onLogout = { stage = AppStage.Auth })
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PosPalette.Ocean, Color(0xFF043B88), Color(0xFF06182D)))),
        contentAlignment = Alignment.Center,
    ) {
        GridBackdrop(tint = Color.White.copy(alpha = 0.12f))
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            LogoMark(72.dp, light = true)
            Text("POS-GG", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
            Text("Multi-tenant point of sale", color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Text("Auth • Store • Catalog • Transaction • Sync", color = Color.White.copy(alpha = 0.58f), fontSize = 12.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 34.dp))
    }
}

@Composable
private fun OnboardingScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    PosPage {
        BrandHeader("DigitalOcean-inspired POS console")
        PosCard(containerColor = PosPalette.Ink, borderColor = Color.Transparent, padding = PaddingValues(22.dp)) {
            GridBackdrop(tint = Color.White.copy(alpha = 0.07f))
            PosStatusPill("API mapped from Postman collection", PosPalette.Success)
            Spacer(Modifier.height(26.dp))
            Text("Satu workspace untuk toko, katalog, transaksi, sync offline, dan admin.", color = Color.White, fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(12.dp))
            Text("UI dibuat ulang flat, rapi, low-shadow, dan siap dipakai sebagai starter development POS-GG.", color = Color.White.copy(alpha = 0.70f), fontSize = 14.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                DarkMetric("13", "Endpoints", Modifier.weight(1f))
                DarkMetric("6", "Modules", Modifier.weight(1f))
                DarkMetric("5", "Tabs", Modifier.weight(1f))
            }
        }
        PosButton("Buat akun POS", onRegister, Modifier.fillMaxWidth())
        PosButton("Masuk", onLogin, Modifier.fillMaxWidth(), variant = PosButtonVariant.Secondary)
    }
}

@Composable
private fun AuthScreen(mode: AuthMode, onModeChange: (AuthMode) -> Unit, onSuccess: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("Admin POS") }
    var email by rememberSaveable { mutableStateOf("admin@posgg.dev") }
    var password by rememberSaveable { mutableStateOf("adminpass123") }
    var phone by rememberSaveable { mutableStateOf("") }
    var tenant by rememberSaveable { mutableStateOf("") }

    PosPage {
        BrandHeader(if (mode == AuthMode.Login) "Login /api/v1/auth/login" else "Register /api/v1/auth/register")
        PosCard(padding = PaddingValues(18.dp)) {
            Text(if (mode == AuthMode.Login) "Masuk ke tenant" else "Daftarkan user baru", color = PosPalette.Ink, fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("Payload disesuaikan dari collection POS-GG.", color = PosPalette.Slate, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            PosSegmentedControl(listOf("Login", "Register"), if (mode == AuthMode.Login) "Login" else "Register", { onModeChange(if (it == "Login") AuthMode.Login else AuthMode.Register) }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(14.dp))
            if (mode == AuthMode.Register) {
                PosTextField(fullName, { fullName = it }, "Full name", placeholder = "Admin POS")
                Spacer(Modifier.height(10.dp))
            }
            PosTextField(email, { email = it }, "Email", placeholder = "admin@posgg.dev", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(10.dp))
            PosTextField(password, { password = it }, "Password", placeholder = "adminpass123", isPassword = true)
            if (mode == AuthMode.Register) {
                Spacer(Modifier.height(10.dp))
                PosTextField(phone, { phone = it }, "Phone (optional)", placeholder = "081234567890", keyboardType = KeyboardType.Phone)
                Spacer(Modifier.height(10.dp))
                PosTextField(tenant, { tenant = it }, "Tenant ID (optional)", placeholder = "auto assign")
            }
            Spacer(Modifier.height(18.dp))
            PosButton(if (mode == AuthMode.Login) "Login dan lanjut setup" else "Register dan lanjut setup", onSuccess, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun StoreSetupScreen(onContinue: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("Toko Mas Bro") }
    var slug by rememberSaveable { mutableStateOf("toko-mas-bro") }
    var description by rememberSaveable { mutableStateOf("Toko serba ada") }
    var timezone by rememberSaveable { mutableStateOf("Asia/Jakarta") }
    var country by rememberSaveable { mutableStateOf("ID") }
    var email by rememberSaveable { mutableStateOf("toko@masbro.com") }
    var phone by rememberSaveable { mutableStateOf("081234567890") }

    PosPage {
        BrandHeader("Store Management /api/v1/stores/register")
        PosCard(padding = PaddingValues(18.dp)) {
            Text("Setup toko", color = PosPalette.Ink, fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text("Store menjadi scope untuk catalog, transaction, dan sync.", color = PosPalette.Slate, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            PosTextField(name, { name = it }, "Store name")
            Spacer(Modifier.height(10.dp))
            PosTextField(slug, { slug = it }, "Slug")
            Spacer(Modifier.height(10.dp))
            PosTextField(description, { description = it }, "Description")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PosDropdown("Timezone", timezone, listOf("Asia/Jakarta", "Asia/Makassar", "Asia/Jayapura"), { timezone = it }, Modifier.weight(1f))
                PosDropdown("Country", country, listOf("ID", "SG", "MY"), { country = it }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            PosTextField(phone, { phone = it }, "Phone", keyboardType = KeyboardType.Phone)
            Spacer(Modifier.height(10.dp))
            PosTextField(email, { email = it }, "Store email", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(18.dp))
            PosButton("Buka dashboard toko", onContinue, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun HomeScreen(onLogout: () -> Unit) {
    var section by rememberSaveable { mutableStateOf(HomeSection.Dashboard) }
    Box(Modifier.fillMaxSize().background(PosPalette.Canvas).windowInsetsPadding(WindowInsets.safeDrawing)) {
        GridBackdrop(tint = PosPalette.Ocean.copy(alpha = 0.055f))
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HomeTopBar(onLogout)
                AnimatedContent(section, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "section") { current ->
                    when (current) {
                        HomeSection.Dashboard -> DashboardSection()
                        HomeSection.Catalog -> CatalogSection()
                        HomeSection.Checkout -> CheckoutSection()
                        HomeSection.Sync -> SyncSection()
                        HomeSection.Admin -> AdminSection()
                    }
                }
            }
            BottomBar(section) { section = it }
        }
    }
}

@Composable
private fun DashboardSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard("Revenue", "Rp 4.850.000", "/transactions", PosPalette.Ocean, Modifier.weight(1f))
            MetricCard("Products", "128", "/products", PosPalette.Success, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard("Sync", "2 min ago", "/sync", PosPalette.Warning, Modifier.weight(1f))
            MetricCard("Stores", "24", "/admin/stores", PosPalette.OceanDark, Modifier.weight(1f))
        }
        SectionCard("Endpoint coverage", "Auth, Store Management, Catalog, Transaction, Offline Sync, dan Super Admin sudah direpresentasikan di flow UI.")
    }
}

@Composable
private fun CatalogSection() {
    var view by rememberSaveable { mutableStateOf("Products") }
    var trackStock by rememberSaveable { mutableStateOf(true) }
    var category by rememberSaveable { mutableStateOf("Makanan & Minuman") }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        PosSegmentedControl(listOf("Products", "Categories"), view, { view = it }, Modifier.fillMaxWidth())
        PosCard(padding = PaddingValues(18.dp)) {
            Text(if (view == "Products") "Create product" else "Create category", color = PosPalette.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(if (view == "Products") "POST /stores/{storeId}/products" else "POST /stores/{storeId}/categories", color = PosPalette.Slate, fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            if (view == "Products") {
                PosDropdown("Category", category, listOf("Makanan & Minuman", "Retail", "Service"), { category = it })
                Spacer(Modifier.height(10.dp))
                PosTextField("Kopi Susu", {}, "Product name")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PosTextField("KPS-001", {}, "SKU", Modifier.weight(1f))
                    PosTextField("cup", {}, "Unit", Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PosTextField("15000", {}, "Selling price", Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    PosTextField("100", {}, "Initial stock", Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
                Spacer(Modifier.height(10.dp))
                PosCheckbox(trackStock, { trackStock = it }, "Track stock", supportingText = "Maps to trackStock boolean")
            } else {
                PosTextField("Makanan & Minuman", {}, "Category name")
                Spacer(Modifier.height(10.dp))
                PosTextField("Produk F&B", {}, "Description")
                Spacer(Modifier.height(10.dp))
                PosDropdown("Icon", "food", listOf("food", "drink", "retail", "service"), {})
            }
        }
        DataRow("GET categories", "/api/v1/stores/{storeId}/categories")
        DataRow("GET products", "/api/v1/stores/{storeId}/products")
    }
}

@Composable
private fun CheckoutSection() {
    var payment by rememberSaveable { mutableStateOf("CASH") }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        PosCard(padding = PaddingValues(18.dp)) {
            Text("Create transaction", color = PosPalette.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("POST /stores/{storeId}/transactions", color = PosPalette.Slate, fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            DataRow("Kopi Susu", "2 × Rp 15.000")
            DataRow("Discount", "Rp 0")
            DataRow("Tax", "0%")
            Spacer(Modifier.height(10.dp))
            PosRadioGroup("Payment method", listOf("CASH", "QRIS", "CARD"), payment, { payment = it })
            Spacer(Modifier.height(10.dp))
            PosTextField("Pak Budi", {}, "Customer name")
            Spacer(Modifier.height(10.dp))
            PosTextField("50000", {}, "Paid amount", keyboardType = KeyboardType.Number)
            Spacer(Modifier.height(16.dp))
            PosButton("Charge Rp 30.000", {}, Modifier.fillMaxWidth())
        }
        DataRow("GET transactions", "/api/v1/stores/{storeId}/transactions")
    }
}

@Composable
private fun SyncSection() {
    var autoSync by rememberSaveable { mutableStateOf(true) }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        PosCard(padding = PaddingValues(18.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Offline-first sync", color = PosPalette.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text("POST /stores/{storeId}/sync", color = PosPalette.Slate, fontSize = 12.sp)
                }
                PosStatusPill("Healthy", PosPalette.Success)
            }
            Spacer(Modifier.height(14.dp))
            PosTextField("2024-01-01T00:00:00Z", {}, "lastSyncTimestamp")
            Spacer(Modifier.height(10.dp))
            PosCheckbox(autoSync, { autoSync = it }, "Auto sync when online", supportingText = "Push clientChanges then pull server updates")
            Spacer(Modifier.height(16.dp))
            PosButton("Sync now", {}, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun AdminSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionCard("Super Admin Dashboard", "Collection exposes read-only admin stats, store list, and user list endpoints.")
        DataRow("GET stats", "/api/v1/admin/stats")
        DataRow("GET stores", "/api/v1/admin/stores")
        DataRow("GET users", "/api/v1/admin/users")
    }
}

@Composable
private fun PosPage(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(PosPalette.Canvas).windowInsetsPadding(WindowInsets.safeDrawing)) {
        GridBackdrop(tint = PosPalette.Ocean.copy(alpha = 0.055f))
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}

@Composable
private fun BrandHeader(label: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LogoMark(44.dp)
            Column {
                Text("POS-GG", color = PosPalette.Ink, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Text(label, color = PosPalette.Slate, fontSize = 12.sp)
            }
        }
        PosStatusPill("v1 API", PosPalette.Ocean)
    }
}

@Composable
private fun HomeTopBar(onLogout: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text("Toko Mas Bro", color = PosPalette.Ink, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text("Multi-tenant dashboard", color = PosPalette.Slate, fontSize = 13.sp)
        }
        PosButton("Logout", onLogout, variant = PosButtonVariant.Ghost)
    }
}

@Composable
private fun BottomBar(selected: HomeSection, onSelected: (HomeSection) -> Unit) {
    Surface(color = PosPalette.Surface, shadowElevation = 0.dp, tonalElevation = 0.dp, border = androidx.compose.foundation.BorderStroke(1.dp, PosPalette.Line)) {
        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HomeSection.entries.forEach { item ->
                val active = item == selected
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).clickable { onSelected(item) }.background(if (active) PosPalette.Wash else Color.Transparent).padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(item.label, color = if (active) PosPalette.Ocean else PosPalette.Slate, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, endpoint: String, accent: Color, modifier: Modifier = Modifier) {
    PosCard(modifier = modifier, padding = PaddingValues(14.dp)) {
        PosStatusPill(endpoint, accent)
        Spacer(Modifier.height(12.dp))
        Text(label, color = PosPalette.Slate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(value, color = PosPalette.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
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
            drawCircle(Color.White, radius = this.size.minDimension * 0.28f, center = center.copy(y = center.y - this.size.minDimension * 0.03f))
            drawCircle(PosPalette.Ocean, radius = this.size.minDimension * 0.16f, center = center.copy(x = center.x + this.size.minDimension * 0.10f, y = center.y - this.size.minDimension * 0.08f))
        }
    }
}

@Composable
private fun GridBackdrop(tint: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val step = 34.dp.toPx()
        var x = -size.height
        while (x < size.width + size.height) {
            drawLine(tint, Offset(x, 0f), Offset(x + size.height, size.height), strokeWidth = 1f)
            x += step
        }
    }
}
