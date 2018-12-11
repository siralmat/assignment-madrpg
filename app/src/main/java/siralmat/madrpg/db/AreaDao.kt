package siralmat.madrpg.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*

import siralmat.madrpg.model.Area

@Dao
abstract class AreaDao {
    @Query("SELECT count(*) FROM areas")
    abstract fun getSize(): Int

    fun isEmpty() = getSize() == 0

    @Query("SELECT * FROM areas WHERE x = :x AND y = :y LIMIT 1")
    abstract fun get(x: Int, y: Int): LiveData<Area>

    @Query("SELECT * FROM areas WHERE id = :id")
    abstract fun get(id: String): LiveData<Area>

    // When fetching all areas, they should be ordered with 0,0 at 'bottom-left'
    @Query("SELECT * FROM areas ORDER BY y DESC, x ASC")
    abstract fun getAll(): LiveData<List<Area>>

    @Update
    abstract fun update(area: Area)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(area: Area)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(areas: List<Area>)

    @Transaction
    open fun deleteAndReplaceAll(areas: List<Area>) {
        // Transaction required here to stop null LiveData updates
        deleteAllAreas()
        insert(areas)
    }

    @Delete
    abstract fun delete(area: Area)

    @Query("DELETE FROM areas")
    abstract fun deleteAllAreas()


}