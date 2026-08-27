package com.impresa.pulizie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.room.Room

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "pulizie_db"
        ).allowMainThreadQueries().build()

        setContent {
            MaterialTheme {
                Surface {
                    MainScreen(database = database)
                }
            }
        }
    }
}
