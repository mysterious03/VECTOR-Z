package com.iqoo.vectorz.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DocumentEntity::class,
        FieldEntity::class,
        AuditEventEntity::class,
        AppPolicyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VectorZDatabase : RoomDatabase() {

    abstract fun vaultDao(): VaultDao
    abstract fun auditDao(): AuditDao
    abstract fun appPolicyDao(): AppPolicyDao

    companion object {
        @Volatile
        private var INSTANCE: VectorZDatabase? = null

        fun getInstance(context: Context): VectorZDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VectorZDatabase::class.java,
                    "vectorz_secure.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
