package com.niccher.p2p_copier_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.niccher.p2p_copier_app.db.dao.FileDao
import com.niccher.p2p_copier_app.db.dao.TextDao
import com.niccher.p2p_copier_app.db.entity.FileEntity
import com.niccher.p2p_copier_app.db.entity.TextEntity

@Database(
    entities = [TextEntity::class, FileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun textDao(): TextDao
    abstract fun fileDao(): FileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "p2p_copier_db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
