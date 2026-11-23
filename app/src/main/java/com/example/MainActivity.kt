package com.example

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.Coil
import com.example.data.firebase.BattleRepository
import com.example.data.firebase.UserProgressRepository
import com.example.data.firebase.configureAppCheck
import com.example.data.local.LanguageStorage
import com.example.data.local.ThemePreferences
import com.example.data.local.TriviaDatabase
import com.example.data.model.QuizDifficulty
import com.example.data.model.QuizMode
import com.example.data.model.ThemeMode
import com.example.data.model.TriviaCategory
import com.example.data.model.WikipediaArticle
import com.example.data.remote.NetworkMonitor
import com.example.data.remote.TriviaApiService
import com.example.data.remote.WikipediaApiService
import com.example.data.remote.createAtlasImageLoader
import com.example.data.repository.TriviaRepository
import com.example.data.repository.WikipediaRepository
import com.example.ui.auth.attemptAutoSignIn
import com.example.ui.screens.ArticleReaderScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BattleHubScreen
import com.example.ui.screens.BattleRoomScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.theme.TriviaAtlasTheme
import com.example.ui.viewmodel.ArticleReaderViewModel
import com.example.ui.viewmodel.BattleHubViewModel
import com.example.ui.viewmodel.BattleRoomViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.LeaderboardViewModel
import com.example.ui.viewmodel.ProfileViewModel
import com.example.ui.viewmodel.QuizViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

private object Routes {
    const val EXPLORE = "explore"
    const val BATTLE = "battle"
    const val BATTLE_ROOM = "battle_room/{roomCode}"
    const val LEADERBOARD = "leaderboard"
    const val PROFILE = "profile"
    const val QUIZ = "quiz/{category}/{difficulty}/{mode}/{count}"
    const val WIKI_ARTICLE = "wiki_article/{pageId}/{wikiLang}/{title}"
    const val WIKI_QUIZ = "wiki_quiz/{pageId}/{wikiLang}/{title}/{difficulty}/{mode}/{count}/{source}"

    fun battleRoom(roomCode: String): String = "battle_room/${Uri.encode(roomCode.trim().uppercase())}"

    fun quiz(
        category: TriviaCategory,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        count: Int
    ): String = "quiz/${Uri.encode(category.apiKey)}/${difficulty.apiKey}/${mode.key}/$count"

    fun wikiArticle(article: WikipediaArticle): String =
        "wiki_article/${article.pageId}/${Uri.encode(article.wikiLang)}/${Uri.encode(article.title)}"

    fun wikiQuiz(
        article: WikipediaArticle,
        difficulty: QuizDifficulty,
        mode: QuizMode,
        count: Int,
        source: String
    ): String =
        "wiki_quiz/${article.pageId}/${Uri.encode(article.wikiLang)}/${Uri.encode(article.title)}/${difficulty.apiKey}/${mode.key}/$count/${Uri.encode(source)}"
}

private data class NavTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TopTabs = listOf(
    NavTab(Routes.EXPLORE, R.string.nav_explore, Icons.Filled.Explore, Icons.Outlined.Explore, "nav_explore"),
    NavTab(Routes.BATTLE, R.string.nav_battle, Icons.Filled.Groups, Icons.Outlined.Groups, "nav_battle"),
    NavTab(Routes.LEADERBOARD, R.string.nav_leaderboard, Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard, "nav_leaderboard"),
    NavTab(Routes.PROFILE, R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
)

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageStorage.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        Coil.setImageLoader(createAtlasImageLoader(applicationContext))
        enableEdgeToEdge()
        val themePrefs = ThemePreferences(applicationContext)

        setContent {
            val themeMode by themePrefs.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val isDarkMode = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            val scope = rememberCoroutineScope()

            TriviaAtlasTheme(darkTheme = isDarkMode) {
                AppContent(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = {
                        scope.launch {
                            themePrefs.setThemeMode(if (isDarkMode) ThemeMode.LIGHT else ThemeMode.DARK)
                        }
                    },
                    onLanguageApplied = {
                        this@MainActivity.recreate()
                    }
                )
            }
        }
    }
}

@Composable
private fun AppContent(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onLanguageApplied: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var showSignInScreen by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                showSignInScreen = false
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose { Firebase.auth.removeAuthStateListener(listener) }
    }

    // Quietly auto-sign-in returning users without blocking Guest Mode startup
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = { currentUser = Firebase.auth.currentUser },
                onUnauthenticated = { /* Stay seamlessly in Guest Mode */ },
                scope = scope
            )
        }
    }

    if (showSignInScreen && currentUser == null) {
        AuthScreen(
            isDarkMode = isDarkMode,
            onToggleDarkMode = onToggleDarkMode,
            onContinueAsGuest = { showSignInScreen = false },
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
                showSignInScreen = false
            }
        )
    } else {
        val sessionKey = currentUser?.uid ?: "guest"
        key(sessionKey) {
            MainAtlasApp(
                currentUserId = currentUser?.uid,
                googleDisplayName = currentUser?.displayName,
                isDarkMode = isDarkMode,
                onLanguageApplied = onLanguageApplied,
                onOpenSignIn = { showSignInScreen = true },
                onAuthSuccess = {
                    currentUser = Firebase.auth.currentUser
                    showSignInScreen = false
                },
                onSignedOut = { currentUser = Firebase.auth.currentUser }
            )
        }
    }
}

@Composable
private fun MainAtlasApp(
    currentUserId: String?,
    googleDisplayName: String?,
    isDarkMode: Boolean,
    onLanguageApplied: () -> Unit,
    onOpenSignIn: () -> Unit,
    onAuthSuccess: () -> Unit,
    onSignedOut: () -> Unit
) {
    val appContext = LocalContext.current.applicationContext
    val activityContext = LocalContext.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isInQuiz = currentDestination?.route?.startsWith("quiz/") == true ||
        currentDestination?.route?.startsWith("wiki_quiz/") == true ||
        currentDestination?.route?.startsWith("wiki_article/") == true ||
        currentDestination?.route?.startsWith("battle_room/") == true
    val activeLangCode = LanguageStorage.effectiveLanguageCode(activityContext)

    val apiService = remember { TriviaApiService.create() }
    val wikiApiService = remember { WikipediaApiService.create() }
    val networkMonitor = remember(appContext) { NetworkMonitor(appContext) }
    val themePreferences = remember(appContext) { ThemePreferences(appContext) }
    val triviaRepo = remember(appContext) {
        TriviaRepository(
            apiService = apiService,
            dao = TriviaDatabase.getInstance(appContext).triviaDao(),
            networkMonitor = networkMonitor,
            themePreferences = themePreferences
        )
    }
    val wikiRepo = remember(appContext) {
        WikipediaRepository(
            wikiApi = wikiApiService,
            networkMonitor = networkMonitor,
            themePreferences = themePreferences
        )
    }
    val firestoreDb = remember(appContext) {
        FirebaseFirestore.getInstance(appContext.getString(R.string.firestore_database_id))
    }
    val progressRepo = remember(firestoreDb) {
        UserProgressRepository(firestoreDb)
    }
    val battleRepo = remember(firestoreDb, progressRepo, triviaRepo, wikiRepo) {
        BattleRepository(
            db = firestoreDb,
            progressRepository = progressRepo,
            triviaRepository = triviaRepo,
            wikipediaRepository = wikiRepo
        )
    }

    val vmKeySuffix = currentUserId ?: "guest"

    val homeViewModel: HomeViewModel = viewModel(
        key = "home_${vmKeySuffix}_$activeLangCode",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val db = FirebaseFirestore.getInstance(app.getString(R.string.firestore_database_id))
                HomeViewModel(
                    currentUserId = currentUserId,
                    googleDisplayName = googleDisplayName,
                    activeLangCode = activeLangCode,
                    triviaRepository = triviaRepo,
                    wikipediaRepository = wikiRepo,
                    progressRepository = UserProgressRepository(db),
                    networkMonitor = networkMonitor,
                    themePreferences = themePreferences
                )
            }
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, homeViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                homeViewModel.onAppOpened()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val leaderboardViewModel: LeaderboardViewModel = viewModel(
        key = "leaderboard_$vmKeySuffix",
        factory = viewModelFactory {
            initializer {
                LeaderboardViewModel(
                    currentUserId = currentUserId,
                    progressRepository = progressRepo
                )
            }
        }
    )

    val profileViewModel: ProfileViewModel = viewModel(
        key = "profile_$vmKeySuffix",
        factory = viewModelFactory {
            initializer {
                ProfileViewModel(
                    currentUserId = currentUserId,
                    progressRepository = progressRepo,
                    triviaRepository = triviaRepo,
                    wikipediaRepository = wikiRepo,
                    networkMonitor = networkMonitor,
                    themePreferences = themePreferences
                )
            }
        }
    )

    val battleHubViewModel: BattleHubViewModel = viewModel(
        key = "battle_hub_${vmKeySuffix}_$activeLangCode",
        factory = viewModelFactory {
            initializer {
                BattleHubViewModel(
                    currentUserId = currentUserId,
                    googleDisplayName = googleDisplayName,
                    activeLangCode = activeLangCode,
                    battleRepository = battleRepo,
                    progressRepository = progressRepo,
                    wikipediaRepository = wikiRepo,
                    themePreferences = themePreferences
                )
            }
        }
    )

    val selectedDifficulty by homeViewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val selectedPlayMode by homeViewModel.selectedPlayMode.collectAsStateWithLifecycle()
    val selectedCount by homeViewModel.selectedQuestionCount.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 680.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!useNavRail && !isInQuiz) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        TopTabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = stringResource(tab.labelRes)
                                    )
                                },
                                label = { Text(stringResource(tab.labelRes)) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail && !isInQuiz) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        TopTabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = stringResource(tab.labelRes)
                                    )
                                },
                                label = { Text(stringResource(tab.labelRes)) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.EXPLORE
                    ) {
                        composable(Routes.EXPLORE) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onStartQuiz = { cat, diff, mode, count ->
                                    navController.navigate(Routes.quiz(cat, diff, mode, count))
                                },
                                onOpenWikipediaArticle = { article ->
                                    navController.navigate(Routes.wikiArticle(article))
                                },
                                onOpenProfile = {
                                    navController.navigate(Routes.PROFILE) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onOpenSignIn = onOpenSignIn
                            )
                        }

                        composable(Routes.BATTLE) {
                            BattleHubScreen(
                                viewModel = battleHubViewModel,
                                onOpenRoom = { roomCode ->
                                    navController.navigate(Routes.battleRoom(roomCode))
                                },
                                onAuthSuccess = onAuthSuccess
                            )
                        }

                        composable(
                            route = Routes.BATTLE_ROOM,
                            arguments = listOf(
                                navArgument("roomCode") { type = NavType.StringType }
                            )
                        ) { entry ->
                            val roomCode = Uri.decode(entry.arguments?.getString("roomCode").orEmpty())
                            val roomViewModel: BattleRoomViewModel = viewModel(
                                key = "battle_room_${vmKeySuffix}_${roomCode}_${entry.id}",
                                factory = viewModelFactory {
                                    initializer {
                                        BattleRoomViewModel(
                                            roomCode = roomCode,
                                            currentUserId = currentUserId,
                                            googleDisplayName = googleDisplayName,
                                            battleRepository = battleRepo,
                                            progressRepository = progressRepo,
                                            themePreferences = themePreferences
                                        )
                                    }
                                }
                            )

                            BattleRoomScreen(
                                viewModel = roomViewModel,
                                onExit = { navController.popBackStack() }
                            )
                        }

                        composable(Routes.LEADERBOARD) {
                            LeaderboardScreen(
                                viewModel = leaderboardViewModel,
                                currentUserId = currentUserId,
                                onAuthSuccess = onAuthSuccess
                            )
                        }

                        composable(Routes.PROFILE) {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onLanguageApplied = onLanguageApplied,
                                onAuthSuccess = onAuthSuccess,
                                onSignedOut = onSignedOut
                            )
                        }

                        composable(
                            route = Routes.QUIZ,
                            arguments = listOf(
                                navArgument("category") { type = NavType.StringType },
                                navArgument("difficulty") { type = NavType.StringType },
                                navArgument("mode") { type = NavType.StringType },
                                navArgument("count") { type = NavType.IntType }
                            )
                        ) { entry ->
                            val category = TriviaCategory.fromApiKey(entry.arguments?.getString("category"))
                            val difficulty = QuizDifficulty.fromKey(entry.arguments?.getString("difficulty"))
                            val mode = QuizMode.fromKey(entry.arguments?.getString("mode"))
                            val count = entry.arguments?.getInt("count") ?: 10

                            val quizViewModel: QuizViewModel = viewModel(
                                key = "quiz_${vmKeySuffix}_${activeLangCode}_${entry.id}",
                                factory = viewModelFactory {
                                    initializer {
                                        QuizViewModel(
                                            currentUserId = currentUserId,
                                            googleDisplayName = googleDisplayName,
                                            category = category,
                                            difficulty = difficulty,
                                            mode = mode,
                                            questionCount = count,
                                            languageCode = activeLangCode,
                                            triviaRepository = triviaRepo,
                                            progressRepository = progressRepo,
                                            networkMonitor = networkMonitor,
                                            themePreferences = themePreferences
                                        )
                                    }
                                }
                            )

                            QuizScreen(
                                viewModel = quizViewModel,
                                onExit = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Routes.WIKI_ARTICLE,
                            arguments = listOf(
                                navArgument("pageId") { type = NavType.LongType },
                                navArgument("wikiLang") { type = NavType.StringType },
                                navArgument("title") { type = NavType.StringType }
                            )
                        ) { entry ->
                            val pageId = entry.arguments?.getLong("pageId") ?: 0L
                            val wikiLang = entry.arguments?.getString("wikiLang") ?: "en"
                            val articleTitle = entry.arguments?.getString("title")?.let { Uri.decode(it) }

                            val articleViewModel: ArticleReaderViewModel = viewModel(
                                key = "wiki_article_${vmKeySuffix}_${wikiLang}_${pageId}_${entry.id}",
                                factory = viewModelFactory {
                                    initializer {
                                        ArticleReaderViewModel(
                                            currentUserId = currentUserId,
                                            pageId = pageId,
                                            wikiLang = wikiLang,
                                            fallbackTitle = articleTitle,
                                            activeLangCode = activeLangCode,
                                            wikipediaRepository = wikiRepo,
                                            themePreferences = themePreferences
                                        )
                                    }
                                }
                            )

                            ArticleReaderScreen(
                                viewModel = articleViewModel,
                                difficulty = selectedDifficulty,
                                playMode = selectedPlayMode,
                                questionCount = selectedCount,
                                onBack = { navController.popBackStack() },
                                onStartQuiz = { article, source ->
                                    navController.navigate(
                                        Routes.wikiQuiz(
                                            article = article,
                                            difficulty = selectedDifficulty,
                                            mode = selectedPlayMode,
                                            count = selectedCount,
                                            source = source
                                        )
                                    )
                                }
                            )
                        }

                        composable(
                            route = Routes.WIKI_QUIZ,
                            arguments = listOf(
                                navArgument("pageId") { type = NavType.LongType },
                                navArgument("wikiLang") { type = NavType.StringType },
                                navArgument("title") { type = NavType.StringType },
                                navArgument("difficulty") { type = NavType.StringType },
                                navArgument("mode") { type = NavType.StringType },
                                navArgument("count") { type = NavType.IntType },
                                navArgument("source") { type = NavType.StringType }
                            )
                        ) { entry ->
                            val pageId = entry.arguments?.getLong("pageId") ?: 0L
                            val wikiLang = entry.arguments?.getString("wikiLang") ?: "en"
                            val articleTitle = entry.arguments?.getString("title")?.let { Uri.decode(it) }
                            val difficulty = QuizDifficulty.fromKey(entry.arguments?.getString("difficulty"))
                            val mode = QuizMode.fromKey(entry.arguments?.getString("mode"))
                            val count = entry.arguments?.getInt("count") ?: 10
                            val source = entry.arguments?.getString("source")?.let { Uri.decode(it) }
                                ?: WikipediaRepository.SOURCE_AUTO

                            val wikiQuizViewModel: QuizViewModel = viewModel(
                                key = "wiki_quiz_${vmKeySuffix}_${activeLangCode}_${pageId}_${source}_${entry.id}",
                                factory = viewModelFactory {
                                    initializer {
                                        QuizViewModel(
                                            currentUserId = currentUserId,
                                            googleDisplayName = googleDisplayName,
                                            category = TriviaCategory.WIKIPEDIA,
                                            difficulty = difficulty,
                                            mode = mode,
                                            questionCount = count,
                                            languageCode = activeLangCode,
                                            triviaRepository = triviaRepo,
                                            progressRepository = progressRepo,
                                            networkMonitor = networkMonitor,
                                            themePreferences = themePreferences,
                                            wikiPageId = pageId,
                                            wikiLang = wikiLang,
                                            wikiArticleTitle = articleTitle,
                                            wikiSourceMode = source,
                                            wikipediaRepository = wikiRepo
                                        )
                                    }
                                }
                            )

                            QuizScreen(
                                viewModel = wikiQuizViewModel,
                                onExit = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
