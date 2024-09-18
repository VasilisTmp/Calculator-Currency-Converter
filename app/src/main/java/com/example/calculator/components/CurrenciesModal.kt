import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.calculator.components.CloseB
import kotlinx.coroutines.delay

@Composable
fun CurrenciesModal(
    onDismissRequest: () -> Unit,
    items: Map<String, String>,
    onItemClick: (String) -> Unit,
    selected: String
) {

    Dialog(onDismissRequest = onDismissRequest) {
        CompositionLocalProvider(
            LocalTextInputService provides null
        ) {
            Box(
                modifier = Modifier
                    .heightIn(max = 450.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Select Currency",
                                color = MaterialTheme.colorScheme.inverseSurface,
                                fontWeight = FontWeight.Bold
                            )
                            CloseB(onClick = onDismissRequest)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                    ) {
                        items(items.toList()) { (symbol, name) ->
                            var isPressed by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .border(
                                        width = 0.1.dp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                isPressed = true
                                                delay(80)
                                                val released =
                                                    tryAwaitRelease()
                                                isPressed = false
                                                if (released) {
                                                    onItemClick(symbol)
                                                    onDismissRequest()
                                                }
                                            }
                                        )
                                    }
                                    .background(
                                        if (selected == symbol) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onBackground
                                    )
                                    .height(50.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (isPressed) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f),
                                            )
                                    )
                                }
                                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                                    if (name != "") {
                                        Text(
                                            text = name,
                                            color = MaterialTheme.colorScheme.inverseSurface,
                                            modifier = Modifier.padding(end = 10.dp)
                                        )
                                    }
                                    Text(
                                        text = symbol,
                                        color = MaterialTheme.colorScheme.tertiary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

