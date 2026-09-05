package com.rnlkav.dicoevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rnlkav.dicoevent.data.local.entity.FavoriteEvent

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Delete
    suspend fun delete(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_event")
    fun getAllFavorite(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favorite_event WHERE id = :id")
    fun getFavoriteById(id: String): LiveData<FavoriteEvent>
}
