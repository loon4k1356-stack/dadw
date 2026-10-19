package com.example.stealthapphider

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.stealthapphider.ui.theme.StealthAppHiderTheme

private const val PREFS_NAME = "stealth_preferences"
private const val PIN_KEY = "pin"
private const val HIDDEN_APPS_KEY = "hidden_apps"
private const val DEFAULT_PIN = "1234"

private enum class AppScreen(val label: String) {
    Apps("Приложения"),
    Settings("Настройки"),
}

private data class DemoApp(
    val id: String,
    val name: String,
    val description: String,
    val color: Color,
)

private val demoApps = listOf(
    DemoApp("photos", "Private Photos", "Личные фотографии", Color(0xFFE477A3)),
    DemoApp("notes", "Secret Notes", "Заметки и идеи", Color(0xFF8C9EFF)),
    DemoApp("messenger", "Quiet Messenger", "Приватные сообщения", Color(0xFF65C7B5)),
    DemoApp("finance", "My Finances", "Финансовый контроль", Color(0xFFFFB86C)),
    DemoApp("vault", "Secure Vault", "Защищенные файлы", Color(0xFFB39DDB)),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setContent {
            StealthAppHiderTheme {
                StealthApp(
                    preferences = preferences,
                    onExit = { finish() },
                )
            }
        }
    }
}

@Composable
private fun StealthApp(
    preferences: android.content.SharedPreferences,
    onExit: () -> Unit,
) {
    var isAuthenticated by rememberSaveable { mutableStateOf(false) }
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Apps) }
    var pin by rememberSaveable { mutableStateOf("") }
    var pinError by rememberSaveable { mutableStateOf(false) }

    if (!isAuthenticated) {
        LoginScreen(
            pin = pin,
            pinError = pinError,
            onPinChange = {
                pin = it.filter(Char::isDigit).take(8)
                pinError = false
            },
            onLogin = {
                if (pin == preferences.getString(PIN_KEY, DEFAULT_PIN)) {
                    isAuthenticated = true
                    pin = ""
                    pinError = false
                } else {
                    pinError = true
                }
            },
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(
                title = currentScreen.label,
                onExit = {
                    isAuthenticated = false
                    onExit()
                },
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.Apps,
                    onClick = { currentScreen = AppScreen.Apps },
                    icon = { Icon(Icons.Outlined.Apps, contentDescription = null) },
                    label = { Text("Приложения") },
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.Settings,
                    onClick = { currentScreen = AppScreen.Settings },
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    label = { Text("Настройки") },
                )
            }
        },
    ) { paddingValues ->
        when (currentScreen) {
            AppScreen.Apps -> AppsScreen(
                preferences = preferences,
                contentPadding = paddingValues,
            )
            AppScreen.Settings -> SettingsScreen(
                preferences = preferences,
                contentPadding = paddingValues,
            )
        }
    }
}

@Composable
private fun LoginScreen(
    pin: String,
    pinError: Boolean,
    onPinChange: (String) -> Unit,
    onLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Stealth App Hider",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Введите PIN-код для доступа к защищенной зоне",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = onPinChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("PIN-код") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            singleLine = true,
            isError = pinError,
            supportingText = {
                if (pinError) Text("Неверный PIN-код") else Text("По умолчанию: 1234")
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onLogin,
            enabled = pin.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text("Войти", fontSize = 16.sp)
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Безопасный прототип интерфейса",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(title: String, onExit: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(
                    "Защищенная зона",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onExit) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Выйти")
            }
        },
    )
}

@Composable
private fun AppsScreen(
    preferences: android.content.SharedPreferences,
    contentPadding: PaddingValues,
) {
    var hiddenApps by remember {
        mutableStateOf(
            preferences.getStringSet(HIDDEN_APPS_KEY, emptySet()).orEmpty(),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            shape = RoundedCornerShape(20.dp),
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp),
                )
                Spacer(Modifier.size(14.dp))
                Column {
                    Text("Все под контролем", fontWeight = FontWeight.Bold)
                    Text(
                        "Выберите приложения для защищенного режима",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Мои приложения",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Переключатель меняет состояние только внутри прототипа.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(demoApps, key = { it.id }) { app ->
                val isHidden = app.id in hiddenApps
                AppVisibilityCard(
                    app = app,
                    isHidden = isHidden,
                    onToggle = {
                        hiddenApps = if (isHidden) {
                            hiddenApps - app.id
                        } else {
                            hiddenApps + app.id
                        }
                        preferences.edit()
                            .putStringSet(HIDDEN_APPS_KEY, hiddenApps)
                            .apply()
                    },
                )
            }
        }
    }
}

@Composable
private fun AppVisibilityCard(
    app: DemoApp,
    isHidden: Boolean,
    onToggle: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(app.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isHidden) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = null,
                    tint = app.color,
                )
            }
            Spacer(Modifier.size(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.name, fontWeight = FontWeight.SemiBold)
                Text(
                    if (isHidden) "Скрыто в прототипе" else app.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = isHidden, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun SettingsScreen(
    preferences: android.content.SharedPreferences,
    contentPadding: PaddingValues,
) {
    var showPinDialog by remember { mutableStateOf(false) }
    var biometricEnabled by remember {
        mutableStateOf(preferences.getBoolean("biometric", false))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Безопасность", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column {
                SettingRow(
                    icon = Icons.Outlined.Lock,
                    title = "Изменить PIN-код",
                    subtitle = "Обновите код доступа",
                    onClick = { showPinDialog = true },
                )
                SettingRow(
                    icon = Icons.Outlined.Security,
                    title = "Биометрический вход",
                    subtitle = "Демонстрационная настройка",
                    trailing = {
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = {
                                biometricEnabled = it
                                preferences.edit().putBoolean("biometric", it).apply()
                            },
                        )
                    },
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("О приложении", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Stealth App Hider", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(5.dp))
                Text(
                    "Версия 1.0 • Compose prototype",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Это демонстрационное приложение. Оно не изменяет системный лаунчер и не скрывает реальные приложения.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    if (showPinDialog) {
        ChangePinDialog(
            onDismiss = { showPinDialog = false },
            onSave = { newPin ->
                preferences.edit().putString(PIN_KEY, newPin).apply()
                showPinDialog = false
            },
        )
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.padding(0.dp) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.size(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (trailing != null) trailing()
        else if (onClick != null) {
            TextButton(onClick = onClick) { Text("Изменить") }
        }
    }
}

@Composable
private fun ChangePinDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var newPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(24.dp)) {
                Text("Новый PIN-код", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Используйте от 4 до 8 цифр.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = newPin,
                    onValueChange = {
                        newPin = it.filter(Char::isDigit).take(8)
                        error = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("PIN-код") },
                    isError = error,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                )
                if (error) {
                    Text("Введите от 4 до 8 цифр", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    TextButton(onClick = {
                        if (newPin.length in 4..8) onSave(newPin) else error = true
                    }) { Text("Сохранить") }
                }
            }
        }
    }
}
