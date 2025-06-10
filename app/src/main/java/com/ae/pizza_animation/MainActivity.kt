package com.ae.pizza_animation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ae.pizza_animation.ui.theme.PizzaAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PizzaAppTheme {
                PizzaOrderingScreen()
            }
        }
    }
}

data class Ingredient(
    val name: String,
    val frames: List<Int>,
    val isSelected: Boolean = false
)

@Composable
fun PizzaOrderingScreen() {
    var selectedIngredients by remember { mutableStateOf(setOf<String>()) }
    var isAnimating by remember { mutableStateOf(false) }

    val ingredients = listOf(
        Ingredient("Basil", (1..10).map { getBasilResource(it) }),
        Ingredient("Bread", (1..5).map { getBreadResource(it) }),
        Ingredient("Broccoli", (1..10).map { getBroccoliResource(it) }),
        Ingredient("Mushroom", (1..10).map { getMushroomResource(it) }),
        Ingredient("Onion", (1..10).map { getOnionResource(it) }),
        Ingredient("Sausage", (1..10).map { getSausageResource(it) })
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Build Your Pizza",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Pizza Display Area
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            PizzaDisplay(
                selectedIngredients = selectedIngredients,
                ingredients = ingredients,
                isAnimating = isAnimating
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Ingredients Selection
        Text(
            text = "Choose Your Toppings",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(ingredients) { ingredient ->
                IngredientCard(
                    ingredient = ingredient,
                    isSelected = selectedIngredients.contains(ingredient.name),
                    onToggle = {
                        selectedIngredients = if (selectedIngredients.contains(ingredient.name)) {
                            selectedIngredients - ingredient.name
                        } else {
                            selectedIngredients + ingredient.name
                        }
                        isAnimating = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Order Button
        AnimatedOrderButton(
            selectedCount = selectedIngredients.size,
            onClick = {
                // Handle order
            }
        )
    }

    // Reset animation state
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(1000)
            isAnimating = false
        }
    }
}

@Composable
fun PizzaDisplay(
    selectedIngredients: Set<String>,
    ingredients: List<Ingredient>,
    isAnimating: Boolean
) {
    val rotationAnimation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Pizza Base (Plate)
        Image(
            painter = painterResource(id = R.drawable.plate),
            contentDescription = "Pizza Base",
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAnimation),
            contentScale = ContentScale.Fit
        )

        // Animated Ingredients
        selectedIngredients.forEach { ingredientName ->
            val ingredient = ingredients.find { it.name == ingredientName }
            ingredient?.let {
                AnimatedIngredient(
                    ingredient = it,
                    isAnimating = isAnimating,
                    baseRotation = rotationAnimation
                )
            }
        }
    }
}

@Composable
fun AnimatedIngredient(
    ingredient: Ingredient,
    isAnimating: Boolean,
    baseRotation: Float
) {
    var currentFrame by remember { mutableIntStateOf(0) }
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Frame animation
    LaunchedEffect(isAnimating) {
        if (isAnimating && ingredient.frames.isNotEmpty()) {
            for (frame in ingredient.frames.indices) {
                currentFrame = frame
                delay(100)
            }
        }
    }

    if (ingredient.frames.isNotEmpty()) {
        Image(
            painter = painterResource(id = ingredient.frames[currentFrame]),
            contentDescription = ingredient.name,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .rotate(baseRotation)
                .graphicsLayer {
                    alpha = if (isAnimating) 0.8f else 1f
                },
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun IngredientCard(
    ingredient: Ingredient,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color(0xFFFF6B35) else Color(0xFF2A2A2A)
            )
            .clickable { onToggle() }
            .padding(12.dp)
            .scale(scale)
    ) {
        if (ingredient.frames.isNotEmpty()) {
            Image(
                painter = painterResource(id = ingredient.frames[0]),
                contentDescription = ingredient.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = ingredient.name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AnimatedOrderButton(
    selectedCount: Int,
    onClick: () -> Unit
) {
    val buttonColor by animateColorAsState(
        targetValue = if (selectedCount > 0) Color(0xFFFF6B35) else Color(0xFF666666),
        animationSpec = tween(300)
    )

    val buttonText = if (selectedCount > 0) {
        "Order Pizza with $selectedCount toppings"
    } else {
        "Choose toppings to order"
    }

    Button(
        onClick = onClick,
        enabled = selectedCount > 0,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = buttonText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// Resource helper functions
private fun getBasilResource(index: Int): Int = when (index) {
    1 -> R.drawable.basil_1
    2 -> R.drawable.basil_2
    3 -> R.drawable.basil_3
    4 -> R.drawable.basil_4
    5 -> R.drawable.basil_5
    6 -> R.drawable.basil_6
    7 -> R.drawable.basil_7
    8 -> R.drawable.basil_8
    9 -> R.drawable.basil_9
    10 -> R.drawable.basil_10
    else -> R.drawable.basil_1
}

private fun getBreadResource(index: Int): Int = when (index) {
    1 -> R.drawable.bread_1
    2 -> R.drawable.bread_2
    3 -> R.drawable.bread_3
    4 -> R.drawable.bread_4
    5 -> R.drawable.bread_5
    else -> R.drawable.bread_1
}

private fun getBroccoliResource(index: Int): Int = when (index) {
    1 -> R.drawable.broccoli_1
    2 -> R.drawable.broccoli_2
    3 -> R.drawable.broccoli_3
    4 -> R.drawable.broccoli_4
    5 -> R.drawable.broccoli_5
    6 -> R.drawable.broccoli_6
    7 -> R.drawable.broccoli_7
    8 -> R.drawable.broccoli_8
    9 -> R.drawable.broccoli_9
    10 -> R.drawable.broccoli_10
    else -> R.drawable.broccoli_1
}

private fun getMushroomResource(index: Int): Int = when (index) {
    1 -> R.drawable.mushroom_1
    2 -> R.drawable.mushroom_2
    3 -> R.drawable.mushroom_3
    4 -> R.drawable.mushroom_4
    5 -> R.drawable.mushroom_5
    6 -> R.drawable.mushroom_6
    7 -> R.drawable.mushroom_7
    8 -> R.drawable.mushroom_8
    9 -> R.drawable.mushroom_9
    10 -> R.drawable.mushroom_10
    else -> R.drawable.mushroom_1
}

private fun getOnionResource(index: Int): Int = when (index) {
    1 -> R.drawable.onion_1
    2 -> R.drawable.onion_2
    3 -> R.drawable.onion_3
    4 -> R.drawable.onion_4
    5 -> R.drawable.onion_5
    6 -> R.drawable.onion_6
    7 -> R.drawable.onion_7
    8 -> R.drawable.onion_8
    9 -> R.drawable.onion_9
    10 -> R.drawable.onion_10
    else -> R.drawable.onion_1
}

private fun getSausageResource(index: Int): Int = when (index) {
    1 -> R.drawable.sausage_1
    2 -> R.drawable.sausage_2
    3 -> R.drawable.sausage_3
    4 -> R.drawable.sausage_4
    5 -> R.drawable.sausage_5
    6 -> R.drawable.sausage_6
    7 -> R.drawable.sausage_7
    8 -> R.drawable.sausage_8
    9 -> R.drawable.sausage_9
    10 -> R.drawable.sausage_10
    else -> R.drawable.sausage_1
}

@Preview(showBackground = true)
@Composable
fun PizzaOrderingPreview() {
    PizzaAppTheme {
        PizzaOrderingScreen()
    }
}