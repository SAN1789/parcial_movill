package com.unilibre.tallerescompose.taller05

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.delay

/**
 * Entry point for Taller 05 with its own internal navigation.
 */
@Composable
fun Taller05Main() {
    val navController = rememberNavController()
    val viewModel: Taller05ViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { InicioScreen { navController.navigate("camara") } }
        composable("camara") { CameraScreen(viewModel) { navController.navigate("ingredientes") } }
        composable("ingredientes") { IngredientsScreen(viewModel, { navController.navigate("recetasIA") }, { navController.popBackStack() }) }
        composable("recetasIA") { AiRecipesScreen(viewModel) { recipe -> navController.navigate("detalle/${recipe.id}") } }
        composable("detalle/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val recipe = uiState.generatedRecipes.find { it.id == recipeId }
            recipe?.let { RecipeDetailScreen(it) { navController.popBackStack() } }
        }
    }
}

@Composable
fun InicioScreen(onStart: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Chef IA", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
            Text("Escanea ingredientes y crea recetas mágicas", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = onStart) {
                Text("Empezar a Escanear")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(viewModel: Taller05ViewModel, onFinished: () -> Unit) {
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) { permissionState.launchPermissionRequest() }

    if (permissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(onIngredientsDetected = { viewModel.onIngredientsDetected(it) })
            
            Button(
                onClick = onFinished,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
            ) {
                Text("Ver Ingredientes")
            }
        }
    } else {
        Text("Permiso de cámara denegado")
    }
}

@Composable
fun CameraPreview(onIngredientsDetected: (List<DetectedIngredient>) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    
    val labeler = remember { ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS) }

    LaunchedEffect(Unit) {
        val cameraProvider = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context).get()
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    processImageProxy(labeler, imageProxy, onIngredientsDetected)
                }
            }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
fun processImageProxy(labeler: com.google.mlkit.vision.label.ImageLabeler, imageProxy: ImageProxy, onResult: (List<DetectedIngredient>) -> Unit) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val detected = labels
                    .filter { it.confidence > 0.7f } // Confidence threshold
                    .map { DetectedIngredient(it.text, it.confidence) }
                onResult(detected)
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(viewModel: Taller05ViewModel, onGenerate: () -> Unit, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredientes Detectados") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onGenerate,
                icon = { Icon(Icons.Default.AutoAwesome, null) },
                text = { Text("Generar Recetas IA") }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(state.detectedIngredients) { ingredient ->
                ListItem(
                    headlineContent = { Text(ingredient.name) },
                    supportingContent = { Text("Confianza: ${(ingredient.confidence * 100).toInt()}%") },
                    trailingContent = {
                        IconButton(onClick = { viewModel.removeIngredient(ingredient.name) }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRecipesScreen(viewModel: Taller05ViewModel, onSelect: (Recipe) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { if (state.generatedRecipes.isEmpty()) viewModel.generateRecipes() }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Recetas Sugeridas") }) }) { padding ->
        if (state.isGenerating) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(16.dp)) {
                items(state.generatedRecipes) { recipe ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onSelect(recipe) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(recipe.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(recipe.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(recipe: Recipe, onBack: () -> Unit) {
    var displayedInstructions by remember { mutableStateOf("") }
    
    // Typewriter effect
    LaunchedEffect(recipe.instructions) {
        recipe.instructions.forEachIndexed { index, _ ->
            displayedInstructions = recipe.instructions.substring(0, index + 1)
            delay(30)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Receta") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text(recipe.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text("Ingredientes:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            recipe.ingredients.forEach { Text("• $it") }
            Spacer(Modifier.height(24.dp))
            Text("Instrucciones (Generadas por IA):", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(displayedInstructions)
        }
    }
}
