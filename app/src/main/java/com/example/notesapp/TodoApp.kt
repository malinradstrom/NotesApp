package com.example.notesapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

data class TodoItem(
    val id: Int,
    var title: String,
    var subtitle: String,
    val check: MutableState<Boolean> = mutableStateOf(false)
)

fun addOrUpdateNote(
    title: String,
    text: String,
    todoList: MutableList<TodoItem>,
    navController: NavController,
    todoItem: TodoItem? = null 
) {
    if (title.isNotBlank() && text.isNotBlank() && title.length in 3..50 && text.length <= 120) {
        if (todoItem != null) {
            // Uppdatera befintlig anteckning
            todoItem.title = title
            todoItem.subtitle = text
        } else {
            // Lägg till ny anteckning
            todoList.add(TodoItem(id = todoList.size, title = title, subtitle = text))
        }
        navController.popBackStack()
    }
}

@Composable
fun TodoApp() {
    val navController = rememberNavController()
    val todoList = remember { mutableStateListOf<TodoItem>() }

    NavHost(navController = navController, startDestination = "todoList") {
        composable("todoList") { TodoListScreen(navController, todoList) }
        composable("addTodo") { AddTodoScreen(navController, todoList) }
        composable("editTodo/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
            val todoItem = todoList.find { it.id == itemId }
            todoItem?.let { EditTodoScreen(navController, it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(navController: NavController, todoList: MutableList<TodoItem>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Todo List") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addTodo") },
                containerColor = Color(0xFF006400), // Mörkgrön bakgrundsfärg
                contentColor = Color.White // Vit färg för ikonen
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Todo")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(todoList) { item ->
                ListItem(
                    leadingContent = {
                        Checkbox(
                            checked = item.check.value,
                            onCheckedChange = {
                                item.check.value = !item.check.value
                            })},
                    headlineContent = { Text(item.title) },
                    supportingContent = { Text(item.subtitle)},
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = { navController.navigate("editTodo/${item.id}") }
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Todo")
                            }
                            IconButton(
                                onClick = { todoList.remove(item) }
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Todo")
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(navController: NavController, todoList: MutableList<TodoItem>) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a new Todo") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (title.isNotBlank() && subtitle.isNotBlank() && title.length in 3..50 && subtitle.length <= 120) {
                                todoList.add(TodoItem(id = todoList.size, title = title, subtitle = subtitle))
                                navController.popBackStack()
                            }
                            /*if (title.isNotBlank() && subtitle.isNotBlank()) {
                                todoList.add(TodoItem(id = todoList.size, title = title, subtitle = subtitle))
                                navController.popBackStack()
                            }*/
                        }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Todo") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Details") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (title.isNotBlank() && subtitle.isNotBlank() && title.length in 3..50 && subtitle.length <= 120) {
                    todoList.add(TodoItem(id = todoList.size, title = title, subtitle = subtitle))
                    navController.popBackStack()
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006411), // Bakgrundsfärg på knappen
                    contentColor = Color.White // Textfärg på knappen
                )
            ) {
                Text("Add Todo")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(navController: NavController, todoItem: TodoItem) {
    var title by remember { mutableStateOf(todoItem.title) }
    var subtitle by remember { mutableStateOf(todoItem.subtitle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Todo") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (title.isNotBlank() && subtitle.isNotBlank() && title.length in 3..50 && subtitle.length <= 120) {
                            todoItem.title = title
                            todoItem.subtitle = subtitle
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Todo") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Details") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (title.isNotBlank() && subtitle.isNotBlank() && title.length in 3..50 && subtitle.length <= 120) {
                    todoItem.title = title
                    todoItem.subtitle = subtitle
                    navController.popBackStack()
                }
            }) {
                Text("Save TodoList")
            }
        }
    }
}

