package com.app.shoply

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.app.shoply.data.repo.ProductRepository
import com.app.shoply.ui.theme.ShoplyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ShoplyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val items = productRepository.searchProducts("Apple").collectAsLazyPagingItems()

                    LaunchedEffect(key1 = Unit) {
                        Log.d("TAG", "QuickTest: ${items.itemSnapshotList.items}")
                    }

                    Text(
                        text = "Shoply",
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
