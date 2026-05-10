package com.fajar.kmp.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object PosPalette {
    val Ocean = Color(0xFF0069FF)
    val OceanDark = Color(0xFF003E9C)
    val Ink = Color(0xFF071527)
    val Slate = Color(0xFF64748B)
    val Muted = Color(0xFF8A9AAF)
    val Canvas = Color(0xFFF6F9FE)
    val Surface = Color.White
    val Line = Color(0xFFDDE7F3)
    val Wash = Color(0xFFEAF2FF)
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Danger = Color(0xFFEF4444)
}

object PosRadius {
    val small = 12.dp
    val medium = 18.dp
    val large = 26.dp
}

@Composable
fun PosCard(
    modifier: Modifier = Modifier,
    containerColor: Color = PosPalette.Surface,
    borderColor: Color = PosPalette.Line,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(PosRadius.large),
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(padding), content = content)
    }
}

@Composable
fun PosButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: PosButtonVariant = PosButtonVariant.Primary,
) {
    when (variant) {
        PosButtonVariant.Primary -> Button(
            onClick = onClick,
            modifier = modifier.height(52.dp),
            enabled = enabled,
            shape = RoundedCornerShape(PosRadius.medium),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PosPalette.Ocean,
                contentColor = Color.White,
                disabledContainerColor = PosPalette.Line,
                disabledContentColor = PosPalette.Muted,
            ),
        ) {
            Text(text = text, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        PosButtonVariant.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(52.dp),
            enabled = enabled,
            shape = RoundedCornerShape(PosRadius.medium),
            border = BorderStroke(1.dp, PosPalette.Line),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PosPalette.Ink),
        ) {
            Text(text = text, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        PosButtonVariant.Ghost -> TextButton(
            onClick = onClick,
            modifier = modifier.height(44.dp),
            enabled = enabled,
            shape = RoundedCornerShape(PosRadius.medium),
        ) {
            Text(text = text, color = PosPalette.Ocean, fontWeight = FontWeight.Bold)
        }
    }
}

enum class PosButtonVariant { Primary, Secondary, Ghost }

@Composable
fun PosTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = PosPalette.Muted) },
        singleLine = singleLine,
        shape = RoundedCornerShape(PosRadius.medium),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PosPalette.Ocean,
            unfocusedBorderColor = PosPalette.Line,
            focusedContainerColor = PosPalette.Surface,
            unfocusedContainerColor = PosPalette.Surface,
            cursorColor = PosPalette.Ocean,
            focusedLabelColor = PosPalette.Ocean,
            unfocusedLabelColor = PosPalette.Slate,
        ),
    )
}

@Composable
fun PosDropdown(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        PosFieldShell(label = label, value = value, trailing = "⌄", onClick = { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = PosPalette.Ink) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun PosCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PosRadius.medium))
            .clickable { onCheckedChange(!checked) }
            .border(1.dp, if (checked) PosPalette.Ocean else PosPalette.Line, RoundedCornerShape(PosRadius.medium))
            .background(if (checked) PosPalette.Wash else PosPalette.Surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = PosPalette.Ocean),
        )
        Column(Modifier.weight(1f)) {
            Text(label, color = PosPalette.Ink, fontWeight = FontWeight.Bold)
            supportingText?.let { Text(it, color = PosPalette.Slate, fontSize = 12.sp) }
        }
    }
}

@Composable
fun PosRadioGroup(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = PosPalette.Slate, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            options.forEach { option ->
                val isSelected = option == selected
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(PosRadius.medium))
                        .clickable { onSelected(option) }
                        .border(1.dp, if (isSelected) PosPalette.Ocean else PosPalette.Line, RoundedCornerShape(PosRadius.medium))
                        .background(if (isSelected) PosPalette.Wash else PosPalette.Surface)
                        .padding(vertical = 9.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelected(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = PosPalette.Ocean),
                    )
                    Text(option, color = PosPalette.Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PosSegmentedControl(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(PosRadius.medium))
            .background(PosPalette.Wash)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { option ->
            val active = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelected(option) }
                    .background(if (active) PosPalette.Surface else Color.Transparent)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(option, color = if (active) PosPalette.Ocean else PosPalette.Slate, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PosStatusPill(text: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(color))
        Text(text, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
    }
}

@Composable
private fun PosFieldShell(label: String, value: String, trailing: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = PosPalette.Slate, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(PosRadius.medium))
                .clickable(onClick = onClick)
                .border(1.dp, PosPalette.Line, RoundedCornerShape(PosRadius.medium))
                .background(PosPalette.Surface)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(value, color = PosPalette.Ink, fontWeight = FontWeight.Bold)
            Text(trailing, color = PosPalette.Ocean, style = MaterialTheme.typography.titleMedium)
        }
    }
}
