package siralmat.madrpg.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

import siralmat.madrpg.GameState
import siralmat.madrpg.model.Area
import siralmat.madrpg.model.SimpleItem
import java.nio.file.Files.move

/**
 * Provides access to overview information about the map.
 */
class MapViewModel: ViewModel() {
    private lateinit var game: GameState
    private var initialized = false
    lateinit var mapData: LiveData<List<Area>>
    lateinit var current: LiveData<Area>
    lateinit var nearbyItems: LiveData<List<SimpleItem>>


    fun init(inGame: GameState) {
        if (!initialized) {
            game = inGame
            current = game.currentArea
            mapData = game.areaList
            initialized = true
            nearbyItems = Transformations.switchMap(current) {
                game.getSimpleItemsInRange(2, it) }
        }
    }

    fun north() = move(0, 1)
    fun south() = move(0, -1)
    fun east() = move(1, 0)
    fun west() = move(-1, 0)


    private fun move(xMove: Int, yMove: Int) {
        current.value?.let {a ->
            a.explored = true
            val newX = a.x + xMove
            val newY = a.y + yMove
            game.updateArea(a)
            game.moveTo(newX, newY)
        }
    }

}