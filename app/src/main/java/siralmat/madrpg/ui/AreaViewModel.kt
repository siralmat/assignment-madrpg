package siralmat.madrpg.ui

import android.util.Log
import androidx.lifecycle.*

import siralmat.madrpg.GameState
import siralmat.madrpg.model.Area


/** Get detailed info about an area. including methods to update its data.
 * If area ID is provided in init(), the model will show live updates to that area,
 * unless area is updated with the setArea() method.
 * If no area is provided, the model will show the player's current location.
 */
class AreaViewModel: ViewModel() {
    private lateinit var game: GameState
    private var areaSelection = MutableLiveData<String>()
    var area: LiveData<Area> = Transformations.switchMap(areaSelection) {
            game.getArea(it) }
    var initialized = false

    fun init(inGame: GameState, areaID: String?) {
        if (!initialized) {
            game = inGame
            if (areaID != null) {
                setAreaSelection(areaID)
            } else {
                area = game.currentArea
            }
            initialized = true
        }
    }

    fun setAreaSelection(areaID: String) {
        areaSelection.value = areaID
    }

    fun updateStarred(areaID: String, starred: Boolean) {
        area.value?.let {
            if (areaID == it.id) {
                Log.d(TAG, "Updating star status for area ${it.x},${it.y}")
                it.starred = starred
                game.updateArea(it)
            } else { Log.e(TAG, "Invalid update request received") }
        }
    }

    fun updateDescription(areaID: String, desc: String) {
        area.value?.let {
            if (areaID == it.id) {
                Log.d(TAG, "Updating description for area ${it.x},${it.y}")
                it.description = desc
                game.updateArea(it)
            } else { Log.e(TAG, "Invalid update request received") }
        }
    }

    fun north() = move(0, 1)
    fun south() = move(0, -1)
    fun east() = move(1, 0)
    fun west() = move(-1, 0)

    private fun move(xMove: Int, yMove: Int) {
        area.value?.let {a ->
            a.explored = true
            val newX = a.x + xMove
            val newY = a.y + yMove
            game.updateArea(a)
            game.moveTo(newX, newY)
        }
    }

    companion object {
        const val TAG = "AreaViewModel"
    }
}