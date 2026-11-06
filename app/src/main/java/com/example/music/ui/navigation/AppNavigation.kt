package com.example.music.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.music.ui.player.MiniPlayer
import com.example.music.ui.player.PlayerScreen
import com.example.music.ui.player.PlayerViewModel
import com.example.music.ui.screens.browse.BrowseScreen
import com.example.music.ui.screens.home.HomeScreen
import com.example.music.ui.screens.library.LibraryScreen
import com.example.music.ui.screens.profile.ProfileScreen
import com.example.music.ui.screens.radio.RadioScreen
import com.example.music.ui.screens.search.SearchScreen
import com.example.music.ui.theme.DarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAppNavigation() {
    val activity = LocalContext.current as ComponentActivity

    // One shared PlayerViewModel for playback state across the whole app
    val playerVM: PlayerViewModel = hiltViewModel(activity)
    val navController = rememberNavController()

    val nowPlaying by playerVM.nowPlaying.collectAsStateWithLifecycle()
    val isPlaying by playerVM.isPlaying.collectAsStateWithLifecycle()
    val position by playerVM.currentPosition.collectAsStateWithLifecycle()
    val duration by playerVM.duration.collectAsStateWithLifecycle()

    var showPlayer by rememberSaveable { mutableStateOf(false) }
    val playerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val openPlayer: () -> Unit = { showPlayer = true }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.ListenNow.route
            ) {
                composable(Screen.ListenNow.route) {
                    HomeScreen(
                        onPlaySong = { song ->
                            playerVM.playSong(song)
                            openPlayer()
                        }
                    )
                }
                composable(Screen.Browse.route) {
                    BrowseScreen(
                        onPlaySong = { song ->
                            playerVM.playSong(song)
                            openPlayer()
                        }
                    )
                }
                composable(Screen.Radio.route) {
                    RadioScreen(
                        onPlaySong = { song ->
                            playerVM.playSong(song)
                            openPlayer()
                        }
                    )
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onPlaySong = { song ->
                            playerVM.playSong(song)
                            openPlayer()
                        }
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        onPlayResults = { results, index ->
                            playerVM.playSongs(results, index)
                            openPlayer()
                        }
                    )
                }
            }

            // Mini player floats above the bottom navigation bar
            nowPlaying?.let { song ->
                MiniPlayer(
                    song = song,
                    isPlaying = isPlaying,
                    onPlayPauseClick = playerVM::playPause,
                    onSkipNextClick = playerVM::skipNext,
                    onClick = openPlayer,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
        }
    }

    // Full player as a modal bottom sheet (Apple Music-style)
    if (showPlayer) {
        ModalBottomSheet(
            onDismissRequest = { showPlayer = false },
            sheetState = playerSheetState,
            containerColor = DarkBackground,
            dragHandle = null
        ) {
            PlayerScreen(
                vm = playerVM,
                onClose = { showPlayer = false }
            )
        }
    }
}

enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    ListenNow("listen_now", "Listen Now", Icons.Default.Home),
    Browse("browse", "Browse", Icons.Default.Search),
    Radio("radio", "Radio", Icons.Default.Radio),
    Library("library", "Library", Icons.Default.LibraryMusic),
    Search("search", "Search", Icons.Default.Search)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = DarkBackground) {
        Screen.entries.forEach { screen ->
            val selected = navController.currentDestination?.route == screen.route
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}