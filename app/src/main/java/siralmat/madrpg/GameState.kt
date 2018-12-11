package siralmat.madrpg

import java.util.*

import android.content.Context
import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.LiveData

import siralmat.madrpg.db.GameDb
import siralmat.madrpg.util.runOnIoThread
import siralmat.madrpg.model.*
import siralmat.madrpg.util.generateItems

val random = Random()

class GameState private constructor (context: Context) {
    private val db = GameDb.getInstance(context)
    private val playerDao = db.playerDao()
    private val areaDao = db.areaDao()
    private val itemDao = db.itemDao()

    val currentArea = Transformations.switchMap(playerDao.get()) {
        it?.let { areaDao.get(it.currentX, it.currentY) } }
    val areaItems = Transformations.switchMap(currentArea) {
        it?.let { itemDao.getItemsForArea(it.id) } }

    val areaList = areaDao.getAll()
    val playerInventory = itemDao.getItemsForPlayer(Player.PLAYER_ID)
    val player = playerDao.get()

    init {
        runOnIoThread {
            if (playerDao.isEmpty()) {
                Log.w(TAG, "No player data found. Creating new player.")
                playerDao.insert(Player())
            }
            if (areaDao.isEmpty()) {
                Log.w(TAG, "No map data found. Generating new map.")
                regenerateMap()
            }
        }
    }

    /** Delete and regenerate all player and area data. **/
    fun restartGame() {
        Log.d(TAG, "Restarting game.")
        runOnIoThread {
            itemDao.deleteAll()
            regenerateMap()
            playerDao.update(Player(currentX = 0, currentY = 0))
        }
    }

    /** Rebuild the map, generating new items for all areas.
     * Any items currently owned by a player will not be affected.
     */
    fun regenerateMap() {
        Log.d(TAG, "Regenerating map.")
        runOnIoThread {
            itemDao.deleteUnownedItems()
            val areas = generateAreas()
            val items = generateItems(COLS, ROWS)
            for (item in items) {
                item.area = areas[random.nextInt(areas.size)].id
            }
            Log.d(TAG, "Adding new areas and items to map")
            //areaDao.deleteAndReplaceAll(areas)
            areaDao.insert(areas)
            itemDao.insert(items)
        }
    }

    fun updateArea(area: Area) {
        runOnIoThread { areaDao.update(area) }
    }

    fun updatePlayer(player: Player) {
        runOnIoThread { playerDao.update(player) }
    }


    /** Update player position to new location, if location is valid. **/
    fun moveTo(newX: Int, newY: Int): Boolean {
        Log.d(TAG, "Received move request to $newX,$newY")
        if (newY in 0..(ROWS - 1) && newX in 0..(COLS - 1)) {
            runOnIoThread {
                val player = playerDao.getStatic()
                player.move(newX, newY)
                playerDao.update(player) }
            return true
        }
        return false
    }

    fun getArea(areaID: String): LiveData<Area> {
        return areaDao.get(areaID)
    }

    fun pickupItem(item: Item, player: Player): Boolean {
        Log.d(TAG, "Picking up item: item.description")
        runOnIoThread {
            player.pickup(item)
            item.area = null
            item.owner = player.id
            itemDao.update(item)
            playerDao.update(player)
        }
        return true
    }

    fun buyItem(item: Item, player: Player): Boolean {
        if (player.buy(item.value)) {
            Log.d(TAG, "Buying item: ${item.description}")
            pickupItem(item, player)
            return true
        }
        return false
    }

    fun dropItem(item: Item, player: Player, area: Area): Boolean {
        Log.d(TAG, "Dropping item: ${item.description}")
        runOnIoThread {
            item.owner = null
            item.area = area.id
            itemDao.update(item)
            player.drop(item)
            playerDao.update(player)
        }
        return true
    }

    fun destroyItem(item: Item) {
        runOnIoThread { itemDao.delete(item) }
    }

    fun sellItem(item: Item, player: Player, area: Area): Boolean {
        Log.d(TAG, "Selling item: ${item.description}")
        player.sell(item.salePrice())
        dropItem(item, player, area)
        runOnIoThread {
            playerDao.update(player)
        }
        return true

    }

    fun getSimpleItemsInRange(range: Int, a: Area): LiveData<List<SimpleItem>> {
        return itemDao.getSimpleItemsInRange(
                a.x - range, a.x + range,
                a.y - range, a.y + range)
    }

    private fun generateAreas(): List<Area> {
        Log.d(TAG, "Generating random areas")
        val list = ArrayList<Area>(COLS * ROWS)
        for (x in 0 until ROWS) {
            for (y in 0 until COLS) {
                list.add(Area(x, y, random.nextBoolean()))
            }
        }
        return list
    }

    companion object {
        private var INSTANCE: GameState? = null
        private const val TAG = "GameState"

        // Return instance of GameState, creating it if it doesn't exist.
        // Uses double-check locking - ref:
        // https://github.com/googlesamples/android-architecture-components/blob/master/BasicRxJavaSampleKotlin/app/src/main/java/com/example/android/observability/persistence/UsersDatabase.kt
        // https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin/45943282
        fun getInstance(context: Context): GameState {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: startGame(context).also { INSTANCE = it }
            }
        }
        private fun startGame(context: Context): GameState {
            return GameState(context)
        }

        const val COLS = 7
        const val ROWS = 7
    }


}