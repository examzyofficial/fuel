package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.ads.AdMobBanner
import com.example.ads.InterstitialAdController
import com.example.ads.findActivity
import com.example.ui.screens.AstmTablesScreen
import com.example.ui.screens.ConverterScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CanvasAccentGlow
import com.example.ui.theme.CanvasGradientBottom
import com.example.ui.theme.CanvasGradientMid
import com.example.ui.theme.CanvasGradientTop
import com.example.ui.theme.CardPerimeter
import com.example.ui.theme.DarkAccentGlow
import com.example.ui.theme.DarkGradientBottom
import com.example.ui.theme.DarkGradientMid
import com.example.ui.theme.DarkGradientTop
import com.example.ui.theme.FuelDensityTheme
import com.example.ui.theme.NavyPrimary

object AppRoutes {
    const val ROUTE_CONVERTER = "converter"
    const val ROUTE_ASTM_TABLES = "astm_tables"
}

private data class NavDestinationItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TopDestinations = listOf(
    NavDestinationItem(
        route = AppRoutes.ROUTE_CONVERTER,
        labelRes = R.string.nav_converter,
        selectedIcon = Icons.Filled.Calculate,
        unselectedIcon = Icons.Outlined.Calculate,
        testTag = "nav_tab_converter"
    ),
    NavDestinationItem(
        route = AppRoutes.ROUTE_ASTM_TABLES,
        labelRes = R.string.nav_astm_tables,
        selectedIcon = Icons.Filled.TableChart,
        unselectedIcon = Icons.Outlined.TableChart,
        testTag = "nav_tab_astm_tables"
    )
)

@Composable
fun FuelDensityApp(
    isAdsInitialized: Boolean = false,
    viewModel: FuelDensityViewModel = viewModel()
) {
    val calibrationSettings by viewModel.calibrationSettings.collectAsStateWithLifecycle()

    FuelDensityTheme(darkTheme = calibrationSettings.isDarkMode) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route ?: AppRoutes.ROUTE_CONVERTER

        val converterState by viewModel.converterState.collectAsStateWithLifecycle()
        val astmTablesState by viewModel.astmTablesState.collectAsStateWithLifecycle()
        val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val interstitialController = remember(context) {
            InterstitialAdController(context)
        }
        LaunchedEffect(isAdsInitialized) {
            if (isAdsInitialized) {
                interstitialController.loadAd()
            }
        }
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(snackbarMessage) {
            snackbarMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                viewModel.consumeMessage()
            }
        }

        fun navigateTo(route: String) {
            if (route == currentRoute) return
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        val isDark = calibrationSettings.isDarkMode
        val bgGradient = if (isDark) {
            listOf(DarkGradientTop, DarkGradientMid, DarkGradientBottom)
        } else {
            listOf(CanvasGradientTop, CanvasGradientMid, CanvasGradientBottom)
        }
        val glowColor = if (isDark) DarkAccentGlow else CanvasAccentGlow

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = bgGradient))
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor, Color.Transparent),
                            center = Offset(size.width * 0.85f, size.height * 0.12f),
                            radius = size.maxDimension * 0.55f
                        ),
                        radius = size.maxDimension * 0.55f,
                        center = Offset(size.width * 0.85f, size.height * 0.12f)
                    )
                }
        ) {
            val useNavigationRail = maxWidth >= 840.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TopDestinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTo(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(dest.labelRes),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = AmberAccent.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        InstrumentTopBar(
                            isDarkMode = calibrationSettings.isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode() }
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = AppRoutes.ROUTE_CONVERTER,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .widthIn(max = 1200.dp)
                            ) {
                                composable(AppRoutes.ROUTE_CONVERTER) {
                                    ConverterScreen(
                                        state = converterState,
                                        onSelectFuelGrade = { viewModel.selectFuelGrade(it) },
                                        onTempChange = { viewModel.updateTempInput(it) },
                                        onStepTemp = { viewModel.stepTemp(it) },
                                        onDensityChange = { viewModel.updateDensityInput(it) },
                                        onStepDensity = { viewModel.stepDensity(it) },
                                        onCalculateAndCache = {
                                            viewModel.calculateAndCache()
                                            interstitialController.onCalculateTapped(context.findActivity())
                                        }
                                    )
                                }

                                composable(AppRoutes.ROUTE_ASTM_TABLES) {
                                    AstmTablesScreen(
                                        state = astmTablesState,
                                        onSelectGradeFilter = { viewModel.setTableGradeFilter(it) },
                                        onSelectTempFilter = { viewModel.setTableTempFilter(it) },
                                        onLoadRowIntoConverter = { row ->
                                            viewModel.loadAstmRowIntoConverter(row)
                                            navigateTo(AppRoutes.ROUTE_CONVERTER)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(
                                bottom = if (useNavigationRail) 84.dp else 160.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdMobBanner(isAdsInitialized = isAdsInitialized)

                        if (!useNavigationRail) {
                            FloatingPillBottomDock(
                                currentRoute = currentRoute,
                                onSelectRoute = { navigateTo(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstrumentTopBar(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_header_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Top-right Dark Mode Toggle Button
        Surface(
            modifier = Modifier
                .size(48.dp)
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .clickable(onClick = onToggleDarkMode)
                .testTag("dark_mode_toggle_button"),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, CardPerimeter)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = if (isDarkMode) AmberAccent else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun FloatingPillBottomDock(
    currentRoute: String,
    onSelectRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(999.dp),
                ambientColor = NavyPrimary.copy(alpha = 0.14f),
                spotColor = NavyPrimary.copy(alpha = 0.14f)
            ),
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopDestinations.forEach { dest ->
                val selected = currentRoute == dest.route
                val label = stringResource(dest.labelRes)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(999.dp))
                        .clickable { onSelectRoute(dest.route) }
                        .padding(vertical = 6.dp)
                        .testTag(dest.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = label,
                        tint = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    AnimatedVisibility(
                        visible = selected,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(AmberAccent)
                        )
                    }
                    if (!selected) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }
    }
}
