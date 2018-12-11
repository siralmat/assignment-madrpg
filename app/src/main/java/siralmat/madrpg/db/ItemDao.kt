package siralmat.madrpg.db

import androidx.lifecycle.LiveData
import androidx.room.*

import siralmat.madrpg.model.Item
import siralmat.madrpg.model.SimpleItem


@Dao
abstract class ItemDao {
    @Query("SELECT * FROM items WHERE owner = :playerID")
    abstract fun getItemsForPlayer(playerID: String): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE area = :areaID")
    abstract fun getItemsForArea(areaID: String): LiveData<List<Item>>

    /** Return a susbet of item data, where item locations are within the given range. **/
    @Query("SELECT i.description, i.effectType, a.x, a.y FROM items AS i " +
            "INNER JOIN areas AS a ON i.area = a.id " +
            "WHERE (a.x BETWEEN :minX AND :maxX) AND (a.y BETWEEN :minY AND :maxY)")
    abstract fun getSimpleItemsInRange(minX: Int, maxX: Int, minY: Int, maxY: Int):
    LiveData<List<SimpleItem>>

    @Query("SELECT * FROM items WHERE iid = :id")
    abstract fun get(id: Int): LiveData<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(items: List<Item>)

    @Update
    abstract fun update(item: Item)

    @Delete
    abstract fun delete(item: Item)

    @Query("DELETE FROM items")
    abstract fun deleteAll()

    @Query("DELETE FROM items WHERE owner IS NULL")
    abstract fun deleteUnownedItems()
}