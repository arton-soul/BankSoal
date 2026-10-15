package com.banksoalpro.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.banksoalpro.app.data.local.entity.PassageEntity

@Dao
interface PassageDao {

    @Query("SELECT * FROM passages WHERE id = :passageId LIMIT 1")
    suspend fun getPassageById(passageId: String): PassageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassages(passages: List<PassageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassage(passage: PassageEntity)
}
