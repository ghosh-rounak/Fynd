package com.example.fynd.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fynd.data.db.entities.Movie

@Database(
    entities = [Movie::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMoviesDao(): MoviesDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fynd_db"
            ).build()
    }
}