# Database & Room Migration Guide

This document outlines how the **P2P Copier Android App** utilizes AndroidX Room for SQLite persistence, how DAOs interact with asynchronous Kotlin Coroutines, and how to execute database schema migrations safely.

---

## 1. Room Architecture

Room acts as an abstraction layer over SQLite. The architecture consists of:
- **`AppDatabase`**: Abstract singleton subclassing `RoomDatabase`.
- **Entities**: Data classes (`TextEntity`, `FileEntity`) mapping 1-to-1 with SQLite tables.
- **DAOs**: Interface classes (`TextDao`, `FileDao`) defining SQL queries, inserts, and stream flows.

```
app/src/main/java/com/niccher/p2p_copier_app/db/
├── AppDatabase.kt          # Database definition & singleton builder
├── dao/
│   ├── FileDao.kt          # File history CRUD operations & Flow queries
│   └── TextDao.kt          # Text/OCR record CRUD & session filtering
└── entity/
    ├── FileEntity.kt       # 'file_history' table model
    └── TextEntity.kt       # 'text_history' table model
```

---

## 2. DAO Implementations

### `TextDao.kt`
```kotlin
@Dao
interface TextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertText(text: TextEntity): Long

    @Query("SELECT * FROM text_history WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun getTextsBySession(sessionId: String): Flow<List<TextEntity>>

    @Query("DELETE FROM text_history WHERE textUuid = :uuid")
    suspend fun deleteByUuid(uuid: String)

    @Query("DELETE FROM text_history")
    suspend fun clearAll()
}
```

### `FileDao.kt`
```kotlin
@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity): Long

    @Query("SELECT * FROM file_history WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun getFilesBySession(sessionId: String): Flow<List<FileEntity>>

    @Query("DELETE FROM file_history WHERE fileUuid = :uuid")
    suspend fun deleteByUuid(uuid: String)

    @Query("DELETE FROM file_history")
    suspend fun clearAll()
}
```

---

## 3. Schema Migrations

When modifying database entities (e.g. adding columns or creating new indexes), you must increment the database version and provide a `Migration` object.

### Example: Migrating from Version 1 to Version 2
1. Update `version` in `AppDatabase.kt`:
   ```kotlin
   @Database(
       entities = [TextEntity::class, FileEntity::class],
       version = 2,
       exportSchema = true
   )
   ```
2. Define the migration script:
   ```kotlin
   val MIGRATION_1_2 = object : Migration(1, 2) {
       override fun migrate(db: SupportSQLiteDatabase) {
           db.execSQL("ALTER TABLE text_history ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
       }
   }
   ```
3. Attach migration to `Room.databaseBuilder()`:
   ```kotlin
   Room.databaseBuilder(context, AppDatabase::class.java, "p2p_copier_db")
       .addMigrations(MIGRATION_1_2)
       .build()
   ```

---

## 4. Inspection & Debugging

- **Android Studio App Inspection**: Connect a device/emulator and open **View > Tool Windows > App Inspection > Database Inspector** to view live tables in `p2p_copier_db`.
- **Unit Testing DAOs**: In-memory Room database setup using `Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()`.
