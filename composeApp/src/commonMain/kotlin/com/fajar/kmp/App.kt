package com.fajar.kmp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fajar.kmp.core.designsystem.CmpColors
import com.fajar.kmp.core.designsystem.CmpTheme
import kotlinx.coroutines.delay

private val OceanBlue = Color(0xFF0069FF)
private val DeepOcean = Color(0xFF061B3A)
private val Ink = Color(0xFF08111F)
private val Slate = Color(0xFF5E6B7E)
private val Mist = Color(0xFFF5F8FF)
private val Cloud = Color(0xFFFFFFFF)
private val Line = Color(0xFFE1E9F5)
private val SeaFoam = Color(0xFF00D7B5)
private val Amber = Color(0xFFFFB84D)
private val SoftBlue = Color(0xFFEAF2FF)
private val Rose = Color(0xFFFF6B7A)

private enum class AppStage { Splash, Onboarding, Auth, Home }
private enum class AuthMode { Login, Register, ForgotPassword }
private enum class HomeTab(val label: String, val icon: ImageVector) {
    Dashboard("Home", PosIcons.Home),
    Sales("Sales", PosIcons.Receipt),
    Products("Stock", PosIcons.Box),
    Reports("Report", PosIcons.Chart),
    Profile("Profile", PosIcons.User),
}

@Composable
@Preview
fun App() {
    CmpTheme {
        var stage by rememberSaveable { mutableStateOf(AppStage.Splash) }
        var authMode by rememberSaveable { mutableStateOf(AuthMode.Login) }

        LaunchedEffect(Unit) {
            delay(1_100)
            if (stage == AppStage.Splash) stage = AppStage.Onboarding
        }

        Surface(color = Mist, modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = stage,
                transitionSpec = {
                    fadeIn(tween(360)) + slideInVertically(tween(360)) { it / 18 } togetherWith
                        fadeOut(tween(220)) + slideOutVertically(tween(220)) { -it / 24 }
                },
                label = "pos-flow",
            ) { current ->
                when (current) {
                    AppStage.Splash -> SplashScreen()
                    AppStage.Onboarding -> OnboardingScreen(
                        onSignIn = {
                            authMode = AuthMode.Login
                            stage = AppStage.Auth
                        },
                        onCreateAccount = {
                            authMode = AuthMode.Register
                            stage = AppStage.Auth
                        },
                    )
                    AppStage.Auth -> AuthScreen(
                        mode = authMode,
                        onModeChange = { authMode = it },
                        onAuthenticated = { stage = AppStage.Home },
                    )
                    AppStage.Home -> PosHomeScreen(onLogout = {
                        authMode = AuthMode.Login
                        stage = AppStage.Auth
                    })
                }
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "splash-alpha",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF178BFF), OceanBlue, DeepOcean),
                    center = Offset(180f, 120f),
                    radius = 1_200f,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        OceanGrid(Modifier.fillMaxSize().alpha(0.18f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.alpha(alpha),
        ) {
            BrandMark(size = 86.dp, elevated = true)
            Text(
                text = "Droplet POS",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.8).sp,
            )
            Text(
                text = "Cloud-simple point of sale",
                color = Color.White.copy(alpha = 0.76f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Text(
            text = "DigitalOcean inspired • POS ready",
            color = Color.White.copy(alpha = 0.62f),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 34.dp),
        )
    }
}

@Composable
private fun OnboardingScreen(onSignIn: () -> Unit, onCreateAccount: () -> Unit) {
    AppChrome {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            TopBrandRow(trailing = "POS OS")
            HeroPanel()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Kasir yang tenang saat toko lagi rame.",
                    color = Ink,
                    fontSize = 36.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.3).sp,
                )
                Text(
                    text = "Kelola transaksi, stok, pelanggan, dan laporan harian dalam UI yang bersih—terinspirasi clarity DigitalOcean.",
                    color = Slate,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricPill("2.4s", "checkout", SeaFoam, Modifier.weight(1f))
                MetricPill("99+", "produk", OceanBlue, Modifier.weight(1f))
                MetricPill("Live", "laporan", Amber, Modifier.weight(1f))
            }
            PrimaryActionButton("Mulai sekarang", onClick = onCreateAccount, modifier = Modifier.fillMaxWidth())
            SecondaryActionButton("Saya sudah punya akun", onClick = onSignIn, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun AuthScreen(mode: AuthMode, onModeChange: (AuthMode) -> Unit, onAuthenticated: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("owner@warunglaut.id") }
    var password by rememberSaveable { mutableStateOf("password") }

    AppChrome {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            TopBrandRow(trailing = when (mode) {
                AuthMode.Login -> "Welcome back"
                AuthMode.Register -> "Start selling"
                AuthMode.ForgotPassword -> "Recovery"
            })
            ElegantCard(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(22.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AuthHeader(mode)
                    AuthModeSwitch(mode, onModeChange)
                    AnimatedVisibility(mode == AuthMode.Register) {
                        PosTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nama toko",
                            placeholder = "Warung Laut Biru",
                        )
                    }
                    PosTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = if (mode == AuthMode.ForgotPassword) "Email pemulihan" else "Email",
                        placeholder = "owner@toko.id",
                        keyboardType = KeyboardType.Email,
                    )
                    AnimatedVisibility(mode != AuthMode.ForgotPassword) {
                        PosTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            placeholder = "••••••••",
                            isPassword = true,
                        )
                    }
                    PrimaryActionButton(
                        text = when (mode) {
                            AuthMode.Login -> "Masuk ke dashboard"
                            AuthMode.Register -> "Buat akun POS"
                            AuthMode.ForgotPassword -> "Kirim link pemulihan"
                        },
                        onClick = {
                            if (mode == AuthMode.ForgotPassword) onModeChange(AuthMode.Login) else onAuthenticated()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextButton(onClick = { onModeChange(AuthMode.ForgotPassword) }) {
                            Text("Lupa password?", color = OceanBlue, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = {
                            onModeChange(if (mode == AuthMode.Register) AuthMode.Login else AuthMode.Register)
                        }) {
                            Text(
                                text = if (mode == AuthMode.Register) "Masuk" else "Daftar",
                                color = Ink,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
            SecurityStrip()
        }
    }
}

@Composable
private fun PosHomeScreen(onLogout: () -> Unit) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.Dashboard) }

    AppChrome {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HomeHeader(onLogout)
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "home-tab",
                ) { tab ->
                    when (tab) {
                        HomeTab.Dashboard -> DashboardTab()
                        HomeTab.Sales -> SalesTab()
                        HomeTab.Products -> ProductsTab()
                        HomeTab.Reports -> ReportsTab()
                        HomeTab.Profile -> ProfileTab()
                    }
                }
            }
            BottomNavigationBar(selectedTab = selectedTab, onSelect = { selectedTab = it })
        }
    }
}

@Composable
private fun AppChrome(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF7FAFF), Color(0xFFEFF5FF), Color.White),
                ),
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        OceanGrid(Modifier.fillMaxSize().alpha(0.12f), dark = false)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 42.dp)
                .size(180.dp)
                .clip(CircleShape)
                .background(OceanBlue.copy(alpha = 0.09f)),
        )
        content()
    }
}

@Composable
private fun TopBrandRow(trailing: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BrandMark(size = 42.dp, elevated = false)
            Column {
                Text("Droplet POS", color = Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("Retail cloud console", color = Slate, fontSize = 12.sp)
            }
        }
        Text(
            text = trailing,
            color = OceanBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(SoftBlue)
                .padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun HeroPanel() {
    ElegantCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.18f),
        color = DeepOcean,
        border = Color.White.copy(alpha = 0.12f),
        padding = PaddingValues(20.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            OceanGrid(Modifier.fillMaxSize().alpha(0.22f))
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    MiniChip("Today", SeaFoam)
                    MiniChip("Cloud synced", Color.White)
                }
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Rp 8.420.000", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
                    Text("Gross sales", color = Color.White.copy(alpha = 0.62f), fontSize = 13.sp)
                    SalesBars()
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    DarkStat("142", "Orders", Modifier.weight(1f))
                    DarkStat("18", "Low stock", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DashboardTab() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryCard("Revenue", "Rp 12.8jt", "+18%", OceanBlue, Modifier.weight(1f))
            SummaryCard("Orders", "286", "+31", SeaFoam, Modifier.weight(1f))
        }
        ElegantCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Text("Checkout cepat", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text("Scan, pilih produk, bayar. Flow kasir tanpa distraksi.", color = Slate, fontSize = 13.sp)
                }
                IconBadge(PosIcons.Scan, OceanBlue)
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                QuickAction("New sale", PosIcons.Receipt, OceanBlue, Modifier.weight(1f))
                QuickAction("Add item", PosIcons.Box, SeaFoam, Modifier.weight(1f))
            }
        }
        SectionTitle("Aktivitas terbaru")
        ActivityRow("INV-1028", "Kopi Susu x2 • QRIS", "Rp 48.000", SeaFoam)
        ActivityRow("INV-1027", "Roti Bakar • Cash", "Rp 22.000", OceanBlue)
        ActivityRow("Stock alert", "Susu Oat tersisa 4 pcs", "Restock", Amber)
    }
}

@Composable
private fun SalesTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("Sales queue")
        CheckoutPreview()
        ActivityRow("Meja 03", "Americano, Croissant", "Rp 67.000", OceanBlue)
        ActivityRow("Takeaway", "Matcha Latte", "Rp 31.000", SeaFoam)
    }
}

@Composable
private fun ProductsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("Inventory pulse")
        ProductStock("Kopi Susu", 82, OceanBlue)
        ProductStock("Susu Oat", 18, Amber)
        ProductStock("Croissant", 36, SeaFoam)
        ProductStock("Cup 12oz", 12, Rose)
    }
}

@Composable
private fun ReportsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("Laporan")
        ElegantCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
            Text("7-day sales", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(16.dp))
            SalesBars(isLight = true)
            Spacer(Modifier.height(14.dp))
            Text("Puncak transaksi jam 18:00–20:00. Siapkan shift ekstra untuk weekend.", color = Slate, fontSize = 13.sp)
        }
        SummaryCard("Net margin", "42.5%", "+4.2% vs last week", SeaFoam, Modifier.fillMaxWidth())
    }
}

@Composable
private fun ProfileTab() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionTitle("Store profile")
        ElegantCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                IconBadge(PosIcons.User, OceanBlue, size = 52.dp)
                Column {
                    Text("Warung Laut Biru", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("Owner • owner@warunglaut.id", color = Slate, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("Plan: Starter Cloud", color = OceanBlue, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun HomeHeader(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("Selamat sore, Fajar", color = Slate, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("Toko hari ini", color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.9).sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconBadge(PosIcons.Bell, OceanBlue)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onLogout)
                    .background(Cloud)
                    .border(1.dp, Line, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text("Keluar", color = Slate, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(selectedTab: HomeTab, onSelect: (HomeTab) -> Unit) {
    Surface(
        color = Cloud.copy(alpha = 0.96f),
        shadowElevation = 18.dp,
        border = BorderStroke(1.dp, Line),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            HomeTab.entries.forEach { tab ->
                val selected = tab == selectedTab
                val alpha by animateFloatAsState(if (selected) 1f else 0.62f, label = "tab-alpha")
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelect(tab) }
                        .background(if (selected) SoftBlue else Color.Transparent)
                        .padding(vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(tab.icon, contentDescription = tab.label, tint = if (selected) OceanBlue else Slate, modifier = Modifier.size(20.dp).alpha(alpha))
                    Text(tab.label, color = if (selected) OceanBlue else Slate, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun AuthHeader(mode: AuthMode) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = when (mode) {
                AuthMode.Login -> "Masuk ke console"
                AuthMode.Register -> "Buat toko digital"
                AuthMode.ForgotPassword -> "Reset akses"
            },
            color = Ink,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.8).sp,
        )
        Text(
            text = when (mode) {
                AuthMode.Login -> "Pantau penjualan, stok, dan shift dari satu dashboard."
                AuthMode.Register -> "Setup ringan untuk toko kecil sampai multi-cabang."
                AuthMode.ForgotPassword -> "Masukkan email, kami kirim instruksi pemulihan."
            },
            color = Slate,
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun AuthModeSwitch(mode: AuthMode, onModeChange: (AuthMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F5FF))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AuthMode.entries.filterNot { it == AuthMode.ForgotPassword }.forEach { item ->
            val selected = mode == item
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(13.dp))
                    .clickable { onModeChange(item) }
                    .background(if (selected) Cloud else Color.Transparent)
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (item == AuthMode.Login) "Login" else "Register",
                    color = if (selected) OceanBlue else Slate,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun PosTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, color = Slate) },
        placeholder = { Text(placeholder, color = Slate.copy(alpha = 0.55f)) },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Cloud,
            unfocusedContainerColor = Color(0xFFFAFCFF),
            focusedIndicatorColor = OceanBlue,
            unfocusedIndicatorColor = Line,
            cursorColor = OceanBlue,
        ),
    )
}

@Composable
private fun ElegantCard(
    modifier: Modifier = Modifier,
    color: Color = Cloud.copy(alpha = 0.92f),
    border: Color = Line,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = color,
        tonalElevation = 0.dp,
        shadowElevation = 9.dp,
        border = BorderStroke(1.dp, border),
    ) {
        Box(Modifier.padding(padding)) { content() }
    }
}

@Composable
private fun PrimaryActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue, contentColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Text(text, fontWeight = FontWeight.Black, fontSize = 15.sp)
    }
}

@Composable
private fun SecondaryActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(54.dp).clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick),
        color = Cloud,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Line),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = Ink, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
    }
}

@Composable
private fun BrandMark(size: androidx.compose.ui.unit.Dp, elevated: Boolean) {
    Surface(
        modifier = Modifier.size(size),
        shape = RoundedCornerShape(size / 3),
        color = Color.White.copy(alpha = if (elevated) 0.96f else 1f),
        shadowElevation = if (elevated) 18.dp else 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(size * 0.56f)) {
                drawCircle(OceanBlue, radius = this.size.minDimension / 2)
                drawCircle(Color.White, radius = this.size.minDimension / 3.4f, center = center.copy(y = center.y - this.size.minDimension * 0.04f))
                drawCircle(OceanBlue, radius = this.size.minDimension / 5.8f, center = center.copy(x = center.x + this.size.minDimension * 0.12f, y = center.y - this.size.minDimension * 0.1f))
            }
        }
    }
}

@Composable
private fun MetricPill(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    ElegantCard(modifier = modifier, padding = PaddingValues(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(label, color = Slate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MiniChip(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun DarkStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(14.dp),
    ) {
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Text(label, color = Color.White.copy(alpha = 0.62f), fontSize = 12.sp)
    }
}

@Composable
private fun SalesBars(isLight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().height(84.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        listOf(0.34f, 0.62f, 0.45f, 0.88f, 0.72f, 0.95f, 0.58f).forEachIndexed { index, height ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(height)
                    .clip(RoundedCornerShape(topStart = 999.dp, topEnd = 999.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(
                        if (isLight && index == 5) OceanBlue else if (isLight) SoftBlue else Color.White.copy(alpha = if (index == 5) 0.95f else 0.28f),
                    ),
            )
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, subtitle: String, accent: Color, modifier: Modifier = Modifier) {
    ElegantCard(modifier = modifier, padding = PaddingValues(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            IconBadge(PosIcons.Chart, accent, size = 38.dp)
            Text(title, color = Slate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Ink, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.4).sp)
            Text(subtitle, color = accent, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun IconBadge(icon: ImageVector, color: Color, size: androidx.compose.ui.unit.Dp = 44.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 2.9f))
            .background(color.copy(alpha = 0.13f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(size * 0.48f))
    }
}

@Composable
private fun QuickAction(title: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(1.dp, accent.copy(alpha = 0.13f), RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
        Text(title, color = Ink, fontWeight = FontWeight.Black, fontSize = 13.sp)
    }
}

@Composable
private fun ActivityRow(code: String, detail: String, value: String, accent: Color) {
    ElegantCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(accent))
            Column(Modifier.weight(1f)) {
                Text(code, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(detail, color = Slate, fontSize = 12.sp)
            }
            Text(value, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.3).sp)
}

@Composable
private fun CheckoutPreview() {
    ElegantCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Current cart", color = Slate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Rp 128.000", color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Black)
            }
            IconBadge(PosIcons.Scan, OceanBlue, size = 52.dp)
        }
        Spacer(Modifier.height(18.dp))
        PrimaryActionButton("Charge customer", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ProductStock(name: String, percent: Int, accent: Color) {
    ElegantCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(15.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, color = Ink, fontWeight = FontWeight.Black)
                Text("$percent%", color = accent, fontWeight = FontWeight.Black)
            }
            Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)).background(SoftBlue)) {
                Box(Modifier.fillMaxWidth(percent / 100f).height(8.dp).clip(RoundedCornerShape(999.dp)).background(accent))
            }
        }
    }
}

@Composable
private fun SecurityStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DeepOcean)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBadge(PosIcons.Shield, SeaFoam)
        Column {
            Text("Secure by default", color = Color.White, fontWeight = FontWeight.Black)
            Text("Role akses, cloud backup, dan audit trail siap diaktifkan.", color = Color.White.copy(alpha = 0.66f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun OceanGrid(modifier: Modifier = Modifier, dark: Boolean = true) {
    Canvas(modifier) {
        val strokeColor = if (dark) Color.White.copy(alpha = 0.20f) else OceanBlue.copy(alpha = 0.12f)
        val step = 38.dp.toPx()
        var x = -size.height
        while (x < size.width + size.height) {
            drawLine(strokeColor, Offset(x, 0f), Offset(x + size.height, size.height), strokeWidth = 1f)
            x += step
        }
        drawCircle(
            color = if (dark) Color.White.copy(alpha = 0.08f) else OceanBlue.copy(alpha = 0.07f),
            radius = size.minDimension * 0.38f,
            center = Offset(size.width * 0.86f, size.height * 0.12f),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

private object PosIcons {
    val Home = icon("Home") {
        path(fill = SolidColorCompat) {
            moveTo(12f, 3f); lineTo(3f, 10.5f); lineTo(4.6f, 12.4f); lineTo(6f, 11.2f); lineTo(6f, 20f); lineTo(10f, 20f); lineTo(10f, 15f); lineTo(14f, 15f); lineTo(14f, 20f); lineTo(18f, 20f); lineTo(18f, 11.2f); lineTo(19.4f, 12.4f); lineTo(21f, 10.5f); close()
        }
    }
    val Receipt = icon("Receipt") {
        path(fill = SolidColorCompat) {
            moveTo(6f, 3f); lineTo(18f, 3f); lineTo(18f, 21f); lineTo(15.8f, 19.8f); lineTo(13.6f, 21f); lineTo(11.4f, 19.8f); lineTo(9.2f, 21f); lineTo(7f, 19.8f); lineTo(6f, 20.4f); close(); moveTo(8.5f, 8f); lineTo(15.5f, 8f); lineTo(15.5f, 9.8f); lineTo(8.5f, 9.8f); close(); moveTo(8.5f, 12f); lineTo(14f, 12f); lineTo(14f, 13.8f); lineTo(8.5f, 13.8f); close()
        }
    }
    val Box = icon("Box") {
        path(fill = SolidColorCompat) {
            moveTo(4f, 7f); lineTo(12f, 3f); lineTo(20f, 7f); lineTo(20f, 17f); lineTo(12f, 21f); lineTo(4f, 17f); close(); moveTo(6.4f, 8.2f); lineTo(12f, 11f); lineTo(17.6f, 8.2f); lineTo(12f, 5.5f); close(); moveTo(13f, 12.7f); lineTo(13f, 18.3f); lineTo(18f, 15.8f); lineTo(18f, 10.2f); close(); moveTo(6f, 10.2f); lineTo(6f, 15.8f); lineTo(11f, 18.3f); lineTo(11f, 12.7f); close()
        }
    }
    val Chart = icon("Chart") {
        path(fill = SolidColorCompat) {
            moveTo(5f, 19f); lineTo(19f, 19f); lineTo(19f, 21f); lineTo(3f, 21f); lineTo(3f, 4f); lineTo(5f, 4f); close(); moveTo(7f, 16f); lineTo(10f, 16f); lineTo(10f, 10f); lineTo(7f, 10f); close(); moveTo(12f, 16f); lineTo(15f, 16f); lineTo(15f, 6f); lineTo(12f, 6f); close(); moveTo(17f, 16f); lineTo(20f, 16f); lineTo(20f, 12f); lineTo(17f, 12f); close()
        }
    }
    val User = icon("User") {
        path(fill = SolidColorCompat) {
            moveTo(12f, 12f); curveTo(14.2f, 12f, 16f, 10.2f, 16f, 8f); curveTo(16f, 5.8f, 14.2f, 4f, 12f, 4f); curveTo(9.8f, 4f, 8f, 5.8f, 8f, 8f); curveTo(8f, 10.2f, 9.8f, 12f, 12f, 12f); close(); moveTo(4.5f, 20f); curveTo(5.3f, 16.7f, 8.2f, 14.5f, 12f, 14.5f); curveTo(15.8f, 14.5f, 18.7f, 16.7f, 19.5f, 20f); close()
        }
    }
    val Bell = icon("Bell") {
        path(fill = SolidColorCompat) {
            moveTo(12f, 22f); curveTo(13.1f, 22f, 14f, 21.1f, 14f, 20f); lineTo(10f, 20f); curveTo(10f, 21.1f, 10.9f, 22f, 12f, 22f); close(); moveTo(5f, 18f); lineTo(19f, 18f); lineTo(17f, 15.5f); lineTo(17f, 10f); curveTo(17f, 7.2f, 15.1f, 5f, 13f, 4.3f); lineTo(13f, 3f); lineTo(11f, 3f); lineTo(11f, 4.3f); curveTo(8.9f, 5f, 7f, 7.2f, 7f, 10f); lineTo(7f, 15.5f); close()
        }
    }
    val Scan = icon("Scan") {
        path(fill = SolidColorCompat) {
            moveTo(4f, 4f); lineTo(10f, 4f); lineTo(10f, 6f); lineTo(6f, 6f); lineTo(6f, 10f); lineTo(4f, 10f); close(); moveTo(14f, 4f); lineTo(20f, 4f); lineTo(20f, 10f); lineTo(18f, 10f); lineTo(18f, 6f); lineTo(14f, 6f); close(); moveTo(4f, 14f); lineTo(6f, 14f); lineTo(6f, 18f); lineTo(10f, 18f); lineTo(10f, 20f); lineTo(4f, 20f); close(); moveTo(18f, 14f); lineTo(20f, 14f); lineTo(20f, 20f); lineTo(14f, 20f); lineTo(14f, 18f); lineTo(18f, 18f); close(); moveTo(8f, 11f); lineTo(16f, 11f); lineTo(16f, 13f); lineTo(8f, 13f); close()
        }
    }
    val Shield = icon("Shield") {
        path(fill = SolidColorCompat) {
            moveTo(12f, 2.5f); lineTo(20f, 6f); lineTo(20f, 11.5f); curveTo(20f, 16.2f, 16.8f, 20.1f, 12f, 22f); curveTo(7.2f, 20.1f, 4f, 16.2f, 4f, 11.5f); lineTo(4f, 6f); close(); moveTo(10.8f, 14.6f); lineTo(16.5f, 8.9f); lineTo(15.1f, 7.5f); lineTo(10.8f, 11.8f); lineTo(8.9f, 9.9f); lineTo(7.5f, 11.3f); close()
        }
    }
}

private val SolidColorCompat = androidx.compose.ui.graphics.SolidColor(Color.Black)

private fun icon(name: String, builder: ImageVector.Builder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply(builder).build()
