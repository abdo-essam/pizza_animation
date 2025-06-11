package com.ae.pizza_animation

import androidx.compose.material3.ripple
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlin.random.Random


data class ToppingPieceAnimation(
    val id: Int,
    val targetX: Float,
    val targetY: Float,
    val rotation: Float,
    val resourceIndex: Int,
    val startDelay: Long,
    val size: Float = 1f
)

data class ToppingAnimationState(
    val topping: Topping,
    val pieces: List<ToppingPieceAnimation>,
    val isAnimating: Boolean = true
)

fun generateToppingPositions(topping: Topping): List<ToppingPieceAnimation> {
    val pieceCount = when (topping.id) {
        "basil" -> Random.nextInt(6, 10)
        "mushroom" -> Random.nextInt(5, 8)
        "onion" -> Random.nextInt(8, 12)
        "sausage" -> Random.nextInt(6, 9)
        "broccoli" -> Random.nextInt(5, 8)
        else -> 8
    }

    val positions = mutableListOf<Pair<Float, Float>>()
    val minDistance = 35f // Minimum distance between pieces

    return List(pieceCount) { index ->
        var position: Pair<Float, Float>
        var attempts = 0

        // Try to find a position that doesn't overlap too much
        do {
            val angle = Random.nextFloat() * 360f
            val radius = Random.nextFloat() * 70f + 10f // 10-80 radius
            val x = radius * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = radius * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
            position = x to y
            attempts++
        } while (
            attempts < 50 &&
            positions.any {
                val distance = kotlin.math.sqrt(
                    (it.first - position.first) * (it.first - position.first) +
                            (it.second - position.second) * (it.second - position.second)
                )
                distance < minDistance
            }
        )

        positions.add(position)

        ToppingPieceAnimation(
            id = index,
            targetX = position.first,
            targetY = position.second,
            rotation = Random.nextFloat() * 360f,
            resourceIndex = Random.nextInt(10) + 1,
            startDelay = index * 50L, // Cascade effect
            size = Random.nextFloat() * 0.3f + 0.85f
        )
    }
}

data class Topping(
    val id: String,
    val name: String,
    val image: Int, // Main display image
    val resourceGetter: (Int) -> Int,
    val price: Double = 2.0,
    val isSelected: Boolean = false,
    val ingredients: List<Int> = emptyList() // List of topping piece resources
)

data class Pizza(
    val id: String,
    val name: String,
    val image: Int, // Main pizza image
    val baseImageRes: Int,
    val basePrice: Double,
    val size: PizzaSize = PizzaSize.MEDIUM,
    val ingredients: List<Topping> = emptyList()
) {
    val totalPrice: Double
        get() = (basePrice * size.priceMultiplier) +
                ingredients.filter { it.isSelected }.sumOf { it.price }
}

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
    Topping(
        id = "basil",
        name = "Basil",
        image = R.drawable.basil_1,
        resourceGetter = ::getBasilResource,
        ingredients = (1..10).map { getBasilResource(it) }
    ),
    Topping(
        id = "broccoli",
        name = "Broccoli",
        image = R.drawable.broccoli_1,
        resourceGetter = ::getBroccoliResource,
        ingredients = (1..10).map { getBroccoliResource(it) }
    ),
    Topping(
        id = "onion",
        name = "Onion",
        image = R.drawable.onion_1,
        resourceGetter = ::getOnionResource,
        ingredients = (1..10).map { getOnionResource(it) }
    ),
    Topping(
        id = "mushroom",
        name = "Mushroom",
        image = R.drawable.mushroom_1,
        resourceGetter = ::getMushroomResource,
        price = 2.5,
        ingredients = (1..10).map { getMushroomResource(it) }
    ),
    Topping(
        id = "sausage",
        name = "Sausage",
        image = R.drawable.sausage_1,
        resourceGetter = ::getSausageResource,
        price = 3.0,
        ingredients = (1..10).map { getSausageResource(it) }
    )
)

val availablePizzas = listOf(
    Pizza(
        id = "margherita",
        name = "Margherita",
        image = getBreadResource(1),
        baseImageRes = getBreadResource(1),
        basePrice = 17.0,
        ingredients = availableToppings
    ),
    Pizza(
        id = "pepperoni",
        name = "Pepperoni",
        image = getBreadResource(2),
        baseImageRes = getBreadResource(2),
        basePrice = 19.0,
        ingredients = availableToppings
    ),
    Pizza(
        id = "vegetarian",
        name = "Vegetarian",
        image = getBreadResource(3),
        baseImageRes = getBreadResource(3),
        basePrice = 18.0,
        ingredients = availableToppings
    ),
    Pizza(
        id = "hawaiian",
        name = "Hawaiian",
        image = getBreadResource(4),
        baseImageRes = getBreadResource(4),
        basePrice = 20.0,
        ingredients = availableToppings
    )
)


enum class PizzaSize(val scale: Float, val priceMultiplier: Double) {
    SMALL(0.7f, 0.8),
    MEDIUM(0.85f, 1.0),
    LARGE(1.0f, 1.5)
}

// Pizza Indicators
@Composable
fun PizzaIndicators(
    pizzas: List<Pizza>,
    currentIndex: Int
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pizzas.forEachIndexed { index, _ ->
            val width by animateDpAsState(
                targetValue = if (index == currentIndex) 24.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 400f
                ),
                label = "indicatorWidth"
            )

            val color by animateColorAsState(
                targetValue = if (index == currentIndex) Color.Black else Color.LightGray,
                animationSpec = tween(200),
                label = "indicatorColor"
            )

            Box(
                modifier = Modifier
                    .size(width = width, height = 8.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}



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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(45.dp)
        ) {
            // Background circle with shadow (only visible when selected)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .shadow(4.dp, shape = CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            // Size text
            Text(
                text = when (size) {
                    PizzaSize.SMALL -> "S"
                    PizzaSize.MEDIUM -> "M"
                    PizzaSize.LARGE -> "L"
                },
                fontSize = 25.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Optional: Size label
        Text(
            text = when (size) {
                PizzaSize.SMALL -> "Small"
                PizzaSize.MEDIUM -> "Medium"
                PizzaSize.LARGE -> "Large"
            },
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}



@Composable
fun PizzaSizeElement(
    size: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = size,
        fontSize = 25.sp,
        color = Color.Black,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaOrderingScreen() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { availablePizzas.size }
    )

    var pizzas by remember { mutableStateOf(availablePizzas) }

    // Get current pizza
    val currentPizza = pizzas[pagerState.currentPage]

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            ) {
                Image(
                    painter = painterResource(R.drawable.plate),
                    contentDescription = "Plate image",
                    modifier = Modifier.align(Alignment.Center)
                )

                // HorizontalPizzaPager (exactly like reference)
                HorizontalPizzaPager(
                    state = pagerState,
                    pizzas = pizzas,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }

            // Price Display with simple animation
            val animatedPrice by animateFloatAsState(
                targetValue = currentPizza.totalPrice.toFloat(),
                animationSpec = tween(300),

                label = "price"
            )

            Text(
                text = "$${String.format("%.0f", animatedPrice)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                color = Color.Black,
                textAlign = TextAlign.Center

            )

            // Size Selector - Using circular shape design
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                PizzaSize.entries.forEach { size ->
                    SizeOption(
                        size = size,
                        isSelected = currentPizza.size == size,
                        onClick = {
                            pizzas = pizzas.mapIndexed { index, pizza ->
                                if (index == pagerState.currentPage) {
                                    pizza.copy(size = size)
                                } else pizza
                            }
                        }
                    )
                }
            }

            // Customize Pizza Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 32.dp)
            ) {
                Text(
                    text = "CUSTOMIZE YOUR PIZZA",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            // Toppings Row (matching reference layout)
            LazyRow(
                horizontalArrangement = Arrangement.SpaceEvenly,
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(currentPizza.ingredients) { droppingIndex, dropping ->
                    DroppingSelectionElement(
                        onDroppingClick = {
                            pizzas = pizzas.mapIndexed { pizzaIndex, pizza ->
                                if (pizzaIndex == pagerState.currentPage) {
                                    val updatedIngredients =
                                        pizza.ingredients.mapIndexed { ingredientIndex, ingredient ->
                                            if (ingredientIndex == droppingIndex) {
                                                ingredient.copy(isSelected = !ingredient.isSelected)
                                            } else ingredient
                                        }
                                    pizza.copy(ingredients = updatedIngredients)
                                } else pizza
                            }
                        },
                        isSelected = dropping.isSelected,
                        droppingImage = dropping.image,
                        modifier = Modifier.size(55.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Pizza Type Indicators
            PizzaIndicators(
                pizzas = pizzas,
                currentIndex = pagerState.currentPage
            )

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

// Enhanced ToppingOption with better animations and circular selection
@Composable
fun ToppingOption(
    topping: Topping,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "toppingScale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color(0xFFEEEEEE),
        animationSpec = tween(200),
        label = "borderColor"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFF5F5F5) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(topping.resourceGetter(1)),
                contentDescription = topping.name,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = topping.name,
                fontSize = 10.sp,
                color = if (isSelected) Color.Black else Color.Gray,
                maxLines = 1
            )
        }

        // Selection indicator - circular
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = androidx.compose.animation.scaleOut() + androidx.compose.animation.fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .offset(x = 4.dp, y = (-4).dp)
                    .background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}


@Composable
fun DroppingSelectionElement(
    onDroppingClick: () -> Unit,
    isSelected: Boolean,
    droppingImage: Int,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) Color.Green.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(400),
        label = "containerColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "droppingScale"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .scale(scale)
            .background(containerColor, shape = CircleShape)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.Green else Color.Transparent,
                shape = CircleShape
            )
    ) {
        Image(
            painter = painterResource(droppingImage),
            contentDescription = "Pizza Dropping item",
            modifier = Modifier
                .align(Alignment.Center)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = false), // Fixed: Use Material3 ripple
                    onClick = onDroppingClick
                )
                .size(35.dp)
        )

        // Selection indicator dot
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = androidx.compose.animation.scaleOut() + androidx.compose.animation.fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .offset(x = 2.dp, y = (-2).dp)
                    .background(Color.Green, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}


@Composable
fun HorizontalPizzaPager(
    state: PagerState,
    pizzas: List<Pizza>,
    modifier: Modifier = Modifier
) {
    val pizzaSize = pizzas[state.targetPage].size

    val pizzaScale by animateFloatAsState(
        targetValue = when (pizzaSize) {
            PizzaSize.LARGE -> 0.9f
            PizzaSize.MEDIUM -> 0.8f
            PizzaSize.SMALL -> 0.7f
        },
        animationSpec = tween(500),
        label = "pizzaScale"
    )

    val droppingAppearanceScale by animateFloatAsState(
        targetValue = when (pizzas[state.currentPage].size) {
            PizzaSize.LARGE -> 0.9f
            PizzaSize.MEDIUM -> 0.3f
            PizzaSize.SMALL -> 0.2f
        },
        label = "droppingScale"
    )

    HorizontalPager(
        state = state,
        modifier = modifier,
    ) { page ->
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .wrapContentSize()
            ) {
                Image(
                    painter = painterResource(pizzas[page].image),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .scale(pizzaScale)
                )

                pizzas[page].ingredients.forEach { dropping ->
                    dropping.ingredients.forEachIndexed { _, droppingImageRes ->
                        val randomX = remember { Random.nextInt(-250, 250) }
                        val randomY = remember { Random.nextInt(-250, 250) }

                        AnimatedVisibility(
                            visible = dropping.isSelected,
                            enter = scaleIn(
                                initialScale = 10f,
                                animationSpec = tween(700)
                            ) + fadeIn(),
                            exit = ExitTransition.None,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset { IntOffset(randomX, randomY) }
                                .scale(droppingAppearanceScale)
                        ) {
                            Image(
                                painter = painterResource(droppingImageRes),
                                contentDescription = "topping piece",
                                modifier = Modifier.scale(pizzaScale)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun EnhancedPizzaCarousel(
    pagerState: PagerState,
    pizzas: List<Pizza>,
    selectedSize: PizzaSize,
    selectedToppings: Set<Topping>,
    animatingToppings: List<ToppingAnimationState>
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Fixed plate and shadow
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

            // HorizontalPager for pizza navigation
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(0.8f)
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PizzaWithToppings(
                        pizza = pizzas[page],
                        size = selectedSize,
                        selectedToppings = if (page == pagerState.currentPage) selectedToppings else emptySet(),
                        animatingToppings = if (page == pagerState.currentPage) animatingToppings else emptyList()
                    )
                }
            }
        }
    }
}

@Composable
fun PizzaWithToppings(
    pizza: Pizza,
    size: PizzaSize,
    selectedToppings: Set<Topping>,
    animatingToppings: List<ToppingAnimationState>
) {
    val pizzaScale by animateFloatAsState(
        targetValue = size.scale,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "pizzaScale"
    )

    val toppingScale by animateFloatAsState(
        targetValue = when (size) {
            PizzaSize.LARGE -> 0.3f
            PizzaSize.MEDIUM -> 0.3f
            PizzaSize.SMALL -> 0.2f
        },
        animationSpec = tween(500),
        label = "toppingScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(pizzaScale),
        contentAlignment = Alignment.Center
    ) {
        // Pizza Base
        Image(
            painter = painterResource(pizza.baseImageRes),
            contentDescription = pizza.name,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )

        // Static Toppings (already placed)
        selectedToppings.forEach { topping ->
            val animatingState = animatingToppings.find { it.topping.id == topping.id }

            if (animatingState == null || !animatingState.isAnimating) {
                // Show static pieces
                val pieces = remember(topping.id) { generateToppingPositions(topping) }
                pieces.forEach { piece ->
                    key(topping.id, piece.id) {
                        StaticToppingPiece(
                            topping = topping,
                            pieceAnimation = piece,
                            scale = toppingScale
                        )
                    }
                }
            }
        }

        // Animating Toppings
        animatingToppings.filter { it.isAnimating }.forEach { animState ->
            animState.pieces.forEach { piece ->
                key(animState.topping.id, piece.id) {
                    AnimatedToppingPiece(
                        topping = animState.topping,
                        pieceAnimation = piece,
                        isAnimating = animState.isAnimating,
                        scale = toppingScale
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedToppingPiece(
    topping: Topping,
    pieceAnimation: ToppingPieceAnimation,
    isAnimating: Boolean,
    scale: Float
) {
    var hasStarted by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(pieceAnimation.startDelay)
            hasStarted = true
        }
    }

    val offsetY by animateFloatAsState(
        targetValue = if (hasStarted) 0f else -500f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 200f
        ),
        label = "pieceFall"
    )

    val pieceScale by animateFloatAsState(
        targetValue = if (hasStarted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "pieceScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (hasStarted) pieceAnimation.rotation else pieceAnimation.rotation - 180f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 150f
        ),
        label = "pieceRotation"
    )

    AnimatedVisibility(
        visible = hasStarted || !isAnimating,
        enter = scaleIn(
            initialScale = 10f,
            animationSpec = tween(700)
        ) + fadeIn(),
        exit = ExitTransition.None,
        modifier = Modifier.offset {
            IntOffset(
                pieceAnimation.targetX.toInt(),
                (pieceAnimation.targetY + offsetY).toInt()
            )
        }
    ) {
        Image(
            painter = painterResource(topping.resourceGetter(pieceAnimation.resourceIndex)),
            contentDescription = "topping piece",
            modifier = Modifier
                .size(35.dp * pieceAnimation.size)
                .scale(scale)
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = pieceScale * pieceAnimation.size
                    scaleY = pieceScale * pieceAnimation.size
                    alpha = if (offsetY < -400f) 0f else 1f
                }
        )
    }
}

@Composable
fun StaticToppingPiece(
    topping: Topping,
    pieceAnimation: ToppingPieceAnimation,
    scale: Float
) {
    Image(
        painter = painterResource(topping.resourceGetter(pieceAnimation.resourceIndex)),
        contentDescription = "topping piece",
        modifier = Modifier
            .size(35.dp * pieceAnimation.size)
            .scale(scale)
            .graphicsLayer {
                translationX = pieceAnimation.targetX
                translationY = pieceAnimation.targetY
                rotationZ = pieceAnimation.rotation
            }
    )
}

