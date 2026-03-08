package com.orbit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.orbit.app.data.repository.AuthRepository
import com.orbit.app.data.repository.UserRepository
import com.orbit.app.presentation.navigation.OrbitNavGraph
import com.orbit.app.presentation.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRepo: AuthRepository
    @Inject lateinit var userRepo: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            OrbitTheme {
                OrbitNavGraph(
                    authRepo = authRepo,
                    userRepo = userRepo
                )
            }
        }
    }
}
