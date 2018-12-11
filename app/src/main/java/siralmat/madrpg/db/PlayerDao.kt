package siralmat.madrpg.db

import androidx.lifecycle.LiveData
import androidx.room.*
import siralmat.madrpg.model.Area
import siralmat.madrpg.model.Player

@Dao
abstract class PlayerDao {
    @Query("SELECT count(*) FROM player")
    abstract fun getSize(): Int

    fun isEmpty() = getSize() == 0

    @Query("SELECT * FROM player WHERE id = '${Player.PLAYER_ID}' LIMIT 1")
    abstract fun get(): LiveData<Player>

    @Query("SELECT * FROM player LIMIT 1")
    abstract fun getStatic(): Player

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(player: Player)

    @Update
    abstract fun update(player: Player)

    @Query("DELETE FROM player")
    abstract fun delete()
}

