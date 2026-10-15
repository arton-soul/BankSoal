package com.banksoalpro.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.banksoalpro.app.core.common.AppConstants
import com.banksoalpro.app.data.local.dao.ExamAttemptDao
import com.banksoalpro.app.data.local.dao.ExamPackageDao
import com.banksoalpro.app.data.local.dao.PassageDao
import com.banksoalpro.app.data.local.dao.QuestionDao
import com.banksoalpro.app.data.local.entity.ExamAttemptEntity
import com.banksoalpro.app.data.local.entity.ExamPackageEntity
import com.banksoalpro.app.data.local.entity.PassageEntity
import com.banksoalpro.app.data.local.entity.QuestionEntity

@Database(
    entities = [
        ExamPackageEntity::class,
        QuestionEntity::class,
        PassageEntity::class,
        ExamAttemptEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class BankSoalDatabase : RoomDatabase() {

    abstract fun examPackageDao(): ExamPackageDao
    abstract fun questionDao(): QuestionDao
    abstract fun passageDao(): PassageDao
    abstract fun examAttemptDao(): ExamAttemptDao

    companion object {
        @Volatile
        private var INSTANCE: BankSoalDatabase? = null

        fun getInstance(context: Context): BankSoalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BankSoalDatabase::class.java,
                    AppConstants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
