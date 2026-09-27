package com.example.pesantiket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pesantiket.ui.theme.PesantiketTheme
import com.example.pesantiket.ui.theme.PesantiketTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PesantiketTheme()  {
                TicketBookingParentScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketBookingParentScreen() {
    var hargaTiket by rememberSaveable { mutableIntStateOf(50000) }
    var jumlahTiket by rememberSaveable { mutableIntStateOf(1) }
    var namaPembeli by rememberSaveable { mutableStateOf("") }

    var statusText by rememberSaveable { mutableStateOf("Silakan pesan tiket") }
    var isProcessing by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }
    var isSuccess by rememberSaveable { mutableStateOf(false) }
    var triggerProcess by remember { mutableStateOf(false) }

    LaunchedEffect(triggerProcess) {
        if (triggerProcess) {
            isProcessing = true
            isError = false
            isSuccess = false
            statusText = "Memproses pesanan..."

            delay(2000)

            isProcessing = false
            isSuccess = true
            statusText = "Tiket berhasil dipesan!"

            delay(5000)
            statusText = "Silakan pesan tiket"
            isSuccess = false
            triggerProcess = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pemesanan Tiket", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        }
    ) { innerPadding ->
        TicketBookingContent(
            modifier = Modifier.padding(innerPadding),
            namaPembeli = namaPembeli,
            onNamaChange = {
                namaPembeli = it
                if (isError && it.isNotBlank()) {
                    isError = false
                    statusText = "Silakan pesan tiket"
                }
            },
            jumlahTiket = jumlahTiket,
            onTambahTiket = { jumlahTiket++ },
            onKurangTiket = { if (jumlahTiket > 1) jumlahTiket-- },
            hargaTiket = hargaTiket,
            statusText = statusText,
            isProcessing = isProcessing,
            isError = isError,
            isSuccess = isSuccess,
            onPesanClick = {
                if (namaPembeli.isBlank()) {
                    isError = true
                    isSuccess = false
                    statusText = "Nama harus diisi"
                } else {
                    triggerProcess = true
                }
            }
        )
    }
}

@Composable
fun TicketBookingContent(
    modifier: Modifier = Modifier,
    namaPembeli: String,
    onNamaChange: (String) -> Unit,
    jumlahTiket: Int,
    onTambahTiket: () -> Unit,
    onKurangTiket: () -> Unit,
    hargaTiket: Int,
    statusText: String,
    isProcessing: Boolean,
    isError: Boolean,
    isSuccess: Boolean,
    onPesanClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = namaPembeli,
            onValueChange = onNamaChange,
            label = { Text("Nama") },
            placeholder = { Text("Masukkan nama Anda") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing,
            isError = isError
        )

        Text(text = "Jumlah Tiket", fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onKurangTiket,
                enabled = !isProcessing && jumlahTiket > 1,
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
            ) {
                Text("-", fontSize = 20.sp, color = Color.Gray)
            }

            Text(
                text = "$jumlahTiket",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onTambahTiket,
                enabled = !isProcessing,
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
            ) {
                Text("+", fontSize = 20.sp, color = Color.Gray)
            }
        }

        Text(
            text = "Total Harga: Rp ${hargaTiket * jumlahTiket}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onPesanClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F51B5),
                disabledContainerColor = Color(0xFF9FA8DA)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Pesan Tiket", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        val backgroundColor = when {
            isError -> Color(0xFFFFEBEE)
            isSuccess -> Color(0xFFE8F5E9)
            else -> Color(0xFFF5F5F5)
        }

        val contentColor = when {
            isError -> Color(0xFFD32F2F)
            isSuccess -> Color(0xFF2E7D32)
            else -> Color.Gray
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF3F51B5)
                    )
                } else {
                    val statusSymbol = when {
                        isError -> "!"
                        isSuccess -> "✓"
                        else -> "i"
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .background(contentColor, CircleShape)
                    ) {
                        Text(
                            text = statusSymbol,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Status: $statusText",
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}