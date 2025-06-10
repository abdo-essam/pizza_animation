package com.ae.pizza_animation

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

data class Topping(
    val id: String,
    val name: String,
    val resourceGetter: (Int) -> Int, // Function to get resource by index
    val price: Double = 2.0
)

data class Pizza(
    val id: String,
    val name: String,
    val baseImageRes: Int,
    val basePrice: Double,
    val toppings: List<Topping> = emptyList()
)

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

// Updated Sample Data
val availableToppings = listOf(
    Topping("basil", "Basil", ::getBasilResource),
    Topping("broccoli", "Broccoli", ::getBroccoliResource),
    Topping("onion", "Onion", ::getOnionResource),
    Topping("mushroom", "Mushroom", ::getMushroomResource, 2.5),
    Topping("sausage", "Sausage", ::getSausageResource, 3.0)
)

val availablePizzas = listOf(
    Pizza("margherita", "Margherita", getBreadResource(1), 17.0),
    Pizza("pepperoni", "Pepperoni", getBreadResource(2), 19.0),
    Pizza("vegetarian", "Vegetarian", getBreadResource(3), 18.0),
    Pizza("hawaiian", "Hawaiian", getBreadResource(4), 20.0)
)


@Composable
fun ToppingOption(
    topping: Topping,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFF5F5F5) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.Black else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(topping.resourceGetter(1)), // Use first image for display
            contentDescription = topping.name,
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Fit
        )
    }
}

enum class PizzaSize(val scale: Float, val priceMultiplier: Double) {
    SMALL(0.7f, 0.8),
    MEDIUM(0.85f, 1.0),
    LARGE(1.0f, 1.5)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaOrderingScreen() {
    var selectedPizzaIndex by remember { mutableIntStateOf(0) }
    val selectedPizza = availablePizzas[selectedPizzaIndex]
    var selectedSize by remember { mutableStateOf(PizzaSize.MEDIUM) }
    var selectedToppings by remember { mutableStateOf(setOf<Topping>()) }
    var animatingToppings by remember { mutableStateOf(listOf<ToppingAnimationState>()) }

    // Swipe animation states
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val totalPrice = remember(selectedPizza, selectedSize, selectedToppings) {
        val basePrice = selectedPizza.basePrice * selectedSize.priceMultiplier
        val toppingsPrice = selectedToppings.sumOf { it.price }
        basePrice + toppingsPrice
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pizza", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle favorite */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_heart),
                            contentDescription = "Favorite"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pizza Display with Swipe Gesture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .pointerInput(selectedPizzaIndex) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                            },
                            onDragEnd = {
                                isDragging = false
                                scope.launch {
                                    val threshold = with(density) { 20.dp.toPx() }

                                    when {
                                        dragOffset > threshold && selectedPizzaIndex > 0 -> {
                                            // Swipe right - previous pizza
                                            selectedPizzaIndex--
                                            selectedToppings = emptySet()
                                            animatingToppings = emptyList()
                                        }

                                        dragOffset < -threshold && selectedPizzaIndex < availablePizzas.size - 1 -> {
                                            // Swipe left - next pizza
                                            selectedPizzaIndex++
                                            selectedToppings = emptySet()
                                            animatingToppings = emptyList()
                                        }
                                    }

                                    // Animate back to center
                                    animate(
                                        initialValue = dragOffset,
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = 0.8f,
                                            stiffness = 300f
                                        )
                                    ) { value, _ ->
                                        dragOffset = value
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                dragOffset += dragAmount
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Pizza carousel
                PizzaCarousel(
                    pizzas = availablePizzas,
                    selectedIndex = selectedPizzaIndex,
                    size = selectedSize,
                    selectedToppings = selectedToppings,
                    animatingToppings = animatingToppings,
                    dragOffset = dragOffset,
                    isDragging = isDragging
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Display with simple animation
            val animatedPrice by animateFloatAsState(
                targetValue = totalPrice.toFloat(),
                animationSpec = tween(300),
                label = "price"
            )

            Text(
                text = "$${String.format("%.0f", animatedPrice)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Size Selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                PizzaSize.values().forEach { size ->
                    SizeOption(
                        size = size,
                        isSelected = selectedSize == size,
                        onClick = { selectedSize = size }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Customize Pizza Section
            Text(
                "CUSTOMIZE YOUR PIZZA",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )

            // Toppings Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(availableToppings) { topping ->
                    ToppingOption(
                        topping = topping,
                        isSelected = selectedToppings.contains(topping),
                        onClick = {
                            if (selectedToppings.contains(topping)) {
                                selectedToppings = selectedToppings - topping
                            } else {
                                selectedToppings = selectedToppings + topping
                                // Trigger falling animation
                                scope.launch {
                                    animatingToppings =
                                        animatingToppings + ToppingAnimationState(topping)
                                    delay(50)
                                    animatingToppings = animatingToppings.map {
                                        if (it.topping == topping) {
                                            it.copy(offsetY = 0f)
                                        } else it
                                    }
                                    delay(1000)
                                    animatingToppings =
                                        animatingToppings.filter { it.topping != topping }
                                }
                            }
                        }
                    )
                }
            }

            // Pizza Type Indicators
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availablePizzas.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(
                                width = if (index == selectedPizzaIndex) 24.dp else 8.dp,
                                height = 8.dp
                            )
                            .background(
                                color = if (index == selectedPizzaIndex) Color.Black else Color.LightGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            // Add to Cart Button
            Button(
                onClick = { /* Handle add to cart */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cart),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to cart", fontSize = 16.sp)
            }
        }
    }
}

// Updated PizzaDisplay with separate plate and pizza
@Composable
fun PizzaDisplay(
    pizza: Pizza,
    size: PizzaSize,
    selectedToppings: Set<Topping>,
    animatingToppings: List<ToppingAnimationState>,
    isAnimatingSwitch: Boolean,
    horizontalOffset: Float = 0f
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(250.dp)
    ) {
        // Shadow - fixed position
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(y = 10.dp)
                .background(
                    brush = radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 200f
                    )
                )
        )

        // Plate - fixed position, never moves
        Image(
            painter = painterResource(R.drawable.plate),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // Pizza Container - this moves with swipe
        Box(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .graphicsLayer {
                    translationX = horizontalOffset
                },
            contentAlignment = Alignment.Center
        ) {
            // Pizza Base
            val pizzaScale by animateFloatAsState(
                targetValue = size.scale,
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 300f
                ),
                label = "pizzaScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pizzaScale),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(pizza.baseImageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )

                // Static Toppings
                selectedToppings.forEach { topping ->
                    if (animatingToppings.none { it.topping == topping }) {
                        repeat(8) { index ->
                            ToppingPiece(
                                topping = topping,
                                index = index,
                                size = 1f
                            )
                        }
                    }
                }

                // Animating Toppings
                animatingToppings.forEach { animState ->
                    val offsetY by animateFloatAsState(
                        targetValue = animState.offsetY,
                        animationSpec = spring(
                            dampingRatio = 0.6f,
                            stiffness = 300f
                        ),
                        label = "toppingFall"
                    )

                    repeat(8) { index ->
                        ToppingPiece(
                            topping = animState.topping,
                            index = index,
                            size = 1f,
                            animationOffsetY = offsetY,
                            animationRotation = animState.rotation + index * 45f,
                            animationScale = animState.scale
                        )
                    }
                }
            }
        }
    }
}

// Updated PizzaCarousel to handle only pizza movement
@Composable
fun PizzaCarousel(
    pizzas: List<Pizza>,
    selectedIndex: Int,
    size: PizzaSize,
    selectedToppings: Set<Topping>,
    animatingToppings: List<ToppingAnimationState>,
    dragOffset: Float,
    isDragging: Boolean
) {
    val density = LocalDensity.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Single fixed plate for all pizzas
        Box(
            modifier = Modifier.size(250.dp),
            contentAlignment = Alignment.Center
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(y = 10.dp)
                    .background(
                        brush = radialGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            radius = 200f
                        )
                    )
            )

            // Plate - always visible and fixed
            Image(
                painter = painterResource(R.drawable.plate),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            // Pizza container that handles all pizzas
            Box(
                modifier = Modifier.fillMaxSize(0.8f),
                contentAlignment = Alignment.Center
            ) {
                pizzas.forEachIndexed { index, pizza ->
                    val offset =
                        (index - selectedIndex) * with(density) { 250.dp.toPx() } + dragOffset
                    val alpha =
                        1f - (abs(offset) / with(density) { 250.dp.toPx() }).coerceIn(0f, 1f)

                    // Only show pizzas that are visible or nearly visible
                    if (abs(offset) < with(density) { 300.dp.toPx() }) {
                        PizzaOnly(
                            pizza = pizza,
                            size = size,
                            selectedToppings = if (index == selectedIndex) selectedToppings else emptySet(),
                            animatingToppings = if (index == selectedIndex) animatingToppings else emptyList(),
                            horizontalOffset = offset,
                            alpha = alpha
                        )
                    }
                }
            }
        }
    }
}


// Updated ToppingPiece with stable positioning
@Composable
fun ToppingPiece(
    topping: Topping,
    index: Int,
    size: Float,
    animationOffsetY: Float = 0f,
    animationRotation: Float = 0f,
    animationScale: Float = 1f
) {
    // Remember random values to prevent shaking
    val randomAngle = remember { Random.nextFloat() * 360f }
    val randomRadius = remember { Random.nextFloat() * 60f + 20f } // Random radius between 20-80
    val randomRotation = remember { Random.nextFloat() * 360f }
    val resourceIndex = remember { (Random.nextInt(10) + 1) }

    // Calculate position based on random angle and radius
    val offsetX = remember(randomAngle, randomRadius) {
        randomRadius * kotlin.math.cos(Math.toRadians(randomAngle.toDouble())).toFloat()
    }
    val offsetY = remember(randomAngle, randomRadius) {
        randomRadius * kotlin.math.sin(Math.toRadians(randomAngle.toDouble())).toFloat()
    }

    Image(
        painter = painterResource(topping.resourceGetter(resourceIndex)),
        contentDescription = null,
        modifier = Modifier
            .size(35.dp)
            .graphicsLayer {
                translationX = offsetX
                translationY = offsetY + animationOffsetY
                rotationZ = if (animationOffsetY != 0f) animationRotation else randomRotation
                scaleX = animationScale
                scaleY = animationScale
            }
    )
}

// Enhanced topping distribution system
@Composable
fun PizzaOnly(
    pizza: Pizza,
    size: PizzaSize,
    selectedToppings: Set<Topping>,
    animatingToppings: List<ToppingAnimationState>,
    horizontalOffset: Float,
    alpha: Float
) {
    val pizzaScale by animateFloatAsState(
        targetValue = size.scale,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "pizzaScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = horizontalOffset
                this.alpha = alpha
            }
            .scale(pizzaScale),
        contentAlignment = Alignment.Center
    ) {
        // Pizza Base
        Image(
            painter = painterResource(pizza.baseImageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )

        // Static Toppings with better distribution
        selectedToppings.forEach { topping ->
            if (animatingToppings.none { it.topping == topping }) {
                // Generate different number of pieces based on topping type
                val pieceCount = remember(topping) {
                    when (topping.id) {
                        "basil" -> Random.nextInt(6, 10)
                        "mushroom" -> Random.nextInt(5, 8)
                        "onion" -> Random.nextInt(8, 12)
                        "sausage" -> Random.nextInt(6, 9)
                        "broccoli" -> Random.nextInt(5, 8)
                        else -> 8
                    }
                }

                repeat(pieceCount) { index ->
                    key(topping.id, index) { // Key to maintain stable identity
                        ToppingPiece(
                            topping = topping,
                            index = index,
                            size = 1f
                        )
                    }
                }
            }
        }

        // Animating Toppings
        animatingToppings.forEach { animState ->
            val offsetY by animateFloatAsState(
                targetValue = animState.offsetY,
                animationSpec = spring(
                    dampingRatio = 0.6f,
                    stiffness = 300f
                ),
                label = "toppingFall"
            )

            val pieceCount = remember(animState.topping) {
                when (animState.topping.id) {
                    "basil" -> Random.nextInt(6, 10)
                    "mushroom" -> Random.nextInt(5, 8)
                    "onion" -> Random.nextInt(8, 12)
                    "sausage" -> Random.nextInt(6, 9)
                    "broccoli" -> Random.nextInt(5, 8)
                    else -> 8
                }
            }

            repeat(pieceCount) { index ->
                key(animState.topping.id, index) {
                    ToppingPiece(
                        topping = animState.topping,
                        index = index,
                        size = 1f,
                        animationOffsetY = offsetY,
                        animationRotation = animState.rotation,
                        animationScale = animState.scale
                    )
                }
            }
        }
    }
}

// Alternative: Create a more sophisticated topping distribution
@Composable
fun DistributedToppingPiece(
    topping: Topping,
    toppingIndex: Int,
    pieceIndex: Int,
    size: Float,
    animationOffsetY: Float = 0f,
    animationScale: Float = 1f
) {
    // Create zones for better distribution
    val zone = remember(toppingIndex, pieceIndex) {
        when (pieceIndex % 3) {
            0 -> 0.3f to 0.5f  // Inner zone
            1 -> 0.5f to 0.7f  // Middle zone
            else -> 0.7f to 0.9f // Outer zone
        }
    }

    val position = remember(toppingIndex, pieceIndex) {
        val angle = Random.nextFloat() * 360f
        val radiusPercent = Random.nextFloat() * (zone.second - zone.first) + zone.first
        val radius = radiusPercent * 80f // Max radius of 80

        val x = radius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = radius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()

        Offset(x, y)
    }

    val rotation = remember { Random.nextFloat() * 360f }
    val resourceIndex = remember { Random.nextInt(10) + 1 }
    val sizeVariation = remember { Random.nextFloat() * 0.3f + 0.85f } // 0.85 to 1.15

    Image(
        painter = painterResource(topping.resourceGetter(resourceIndex)),
        contentDescription = null,
        modifier = Modifier
            .size(35.dp * sizeVariation)
            .graphicsLayer {
                translationX = position.x
                translationY = position.y + animationOffsetY
                rotationZ = rotation
                scaleX = animationScale
                scaleY = animationScale
            }
    )
}

// Updated Topping Animation State with stable values
data class ToppingAnimationState(
    val topping: Topping,
    val offsetY: Float = -500f,
    val rotation: Float = 0f, // Remove random rotation from here
    val offsetX: Float = 0f,
    val scale: Float = 1f
)

@Composable
fun SizeOption(
    size: PizzaSize,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Visual size indicator
            Box(
                modifier = Modifier
                    .size(
                        when (size) {
                            PizzaSize.SMALL -> 20.dp
                            PizzaSize.MEDIUM -> 24.dp
                            PizzaSize.LARGE -> 28.dp
                        }
                    )
                    .background(
                        color = if (isSelected) Color.Black.copy(alpha = 0.1f) else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Black else Color.Gray,
                        shape = CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (size) {
                PizzaSize.SMALL -> "S"
                PizzaSize.MEDIUM -> "M"
                PizzaSize.LARGE -> "L"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.Black else Color.Gray
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(Color.Black, CircleShape)
            )
        }
    }
}

// Keep all other composables as they are...

@Composable
fun PizzaTypeOption(
    pizza: Pizza,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFF5F5F5) else Color.White)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = Color.Black,
                    shape = CircleShape
                )
        ) {
            Image(
                painter = painterResource(pizza.baseImageRes),
                contentDescription = pizza.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = pizza.name,
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}
