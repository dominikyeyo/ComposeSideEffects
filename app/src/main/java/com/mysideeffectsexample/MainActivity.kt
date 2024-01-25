package com.mysideeffectsexample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mysideeffectsexample.ui.theme.MySideEffectsExampleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySideEffectsExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    //LaunchEffect One Time
    //FirstCompositionLaunchEffect()

    //LaunchEffect with key
    //KeyLaunchEffect()

    //sideEffect
    //WithoutSideEffect()
    //WithSideEffect()

    //DisposableEffect
    //DisposableEffectExample()


    //RememberCoroutineScope
    //MyRememberCoroutineScopeComposable()

    //ProduceState
    //ProduceStateExample()


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MySideEffectsExampleTheme {
        Greeting("Android")
    }
}

@Composable
fun FirstCompositionLaunchEffect() {
    var timer by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Time $timer")
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(1000)
            timer++
        }
    }
}


@Composable
fun KeyLaunchEffect() {
    val trigger = remember { mutableStateOf(0) }
    val data = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(onClick = { trigger.value++ }) {
            Text("Update")
        }

        LaunchedEffect(key1 = trigger.value) {
            data.value = loadData(trigger.value)
        }

        Text(text = data.value)
    }
}

fun loadData(key: Int): String {
    return "Data to the key $key"
}


@Composable
fun WithoutSideEffect() {
    var timer by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Time $timer")
    }

    Thread.sleep(1000) //Bad practice, only for demo
    timer++ // Without SideEffect, the timer++ will have not
    // to trigger a recomposition, as it was changed
    //while recomposition.

}

@Composable
fun WithSideEffect() {
    var timer by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Time $timer")
    }

    SideEffect {
        Thread.sleep(1000)
        timer++ // With SideEffect, the timer++ will trigger
        // recomposition. This will make it a timer loop
        // (without the use of while(true) as in the LaunchedEffect).
    }

    Thread.sleep(1000)
    timer++

}

@Composable
fun DisposableEffectExample() {
    var showMessage by remember { mutableStateOf(true) }

    Column {
        Button(onClick = { showMessage = true }) {
            Text("SHOW MESSAGE")
        }

        if (showMessage) {
            Box {
                Text("¡Welcome to the best app!")

                DisposableEffect(Unit) {
                    val handler = Handler(Looper.getMainLooper())
                    val runnable = Runnable { showMessage = false }
                    handler.postDelayed(runnable, 4000)
                    Log.d("DisposableEffect", "DisposableEffect WAS LAUNCHED")

                    onDispose {
                        handler.removeCallbacks(runnable)
                        Log.d("WelcomeMessage", "BOX WITH TEXT WAS DELETE " +
                                "OF COMPOSITION THIS MESSAGE IS IN onDispose")
                    }
                }
            }
        }
    }
}

//rememberCoroutineScope
@Composable
fun MyRememberCoroutineScopeComposable() {
    val scope = rememberCoroutineScope()
    val (text, setText) = remember { mutableStateOf("Starting...") }
    val (isLoading, setLoading) = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            setLoading(true)
            scope.launch {
                delay(2000) // Simulates a task that takes time
                setText("¡Task completed successfully!")
                setLoading(false)
            }
        }) {
            Text("Start task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text("Loading...")
        } else {
            Text(text)
        }
    }
}

//Simulating a small database of users
val usersDb = mapOf(
    "123" to User("Alice"),
    "456" to User("Bob"),
    "789" to User("Charlie")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduceStateExample() {
    var userId by remember { mutableStateOf("123") }
    val userData = produceState<User?>(initialValue = null, key1 = userId) {
        value = fetchDataForUser(userId) //Suspended function that obtains data
    }

    Column {
        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("ID User") }
        )
        if (userData.value != null) {
            Text("User: ${userData.value!!.name}")
        } else {
            Text("Loading data of user...")
        }
    }
}

// Suspended function that obtains data of a user from a simulated "database".
suspend fun fetchDataForUser(userId: String): User? {
    delay(2000)
    return usersDb[userId]
}

data class User(val name: String)


@Composable
fun Test() {
    var showMessage by remember { mutableStateOf(true) }

    Column {
        Button(onClick = { showMessage = true }) {
            Text("SHOW MESSAGE")
        }

        if (showMessage) {


            Box {
                Text("¡Welcome to the best app!")

                DisposableEffect(Unit) {

                    Log.d("DisposableEffect", "DisposableEffect WAS LAUNCHED")

                    onDispose {

                        Log.d("WelcomeMessage", "BOX WITH TEXT WAS DELETE " +
                                "OF COMPOSITION THIS MESSAGE IS IN onDispose")
                    }
                }
            }
        }
    }
}

@Composable
fun MyProduceState() {
    var trigger by remember { mutableStateOf(0) }
    val myState = produceState(initialValue = 0, key1 = trigger) {

        val job = launch {
            delay(1000)
            value = (100..200).random()
        }

        awaitDispose {
            job.cancel()
        }
    }

    Column {
        Button(onClick = { trigger++ }) {
            Text("Actualizar")
        }
        Text("Valor: ${myState.value}")
    }
}








