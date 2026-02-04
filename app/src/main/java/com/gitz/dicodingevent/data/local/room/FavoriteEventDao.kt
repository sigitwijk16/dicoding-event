package com.gitz.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gitz.dicodingevent.data.local.entity.FavoriteEvent

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEvent)

    @Delete
    suspend fun deleteFavorite(event: FavoriteEvent)

    @Query("SELECT * FROM favorite_event WHERE id = :id")
    fun getFavoriteEventById(id: Int): LiveData<FavoriteEvent?>

    @Query("SELECT * FROM favorite_event")
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>>
}