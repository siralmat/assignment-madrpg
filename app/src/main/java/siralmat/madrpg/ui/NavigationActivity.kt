package siralmat.madrpg.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_navigation.*
import siralmat.madrpg.GameState
import siralmat.madrpg.R
import siralmat.madrpg.model.Area

class NavigationActivity : AppCompatActivity(), GameActivity {
    private lateinit var map: MapViewModel
    var maxX = GameState.COLS - 1
    var maxY = GameState.ROWS - 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        map = ViewModelProviders.of(this).get(MapViewModel::class.java)
        map.init(GameState.getInstance(applicationContext))


        setContentView(R.layout.activity_navigation)
        map.current.observe(this, Observer {
            it?.let { a -> updateDisplay(a) } })

        btnOptions.setOnClickListener {
            map.current.value?.let { a->
                Log.d(TAG, "Visiting area ${a.x},${a.y}")
                startActivity(VisitActivity.getIntent(this, a))
            }?: Log.e(TAG, "Attempted to visit area while in an invalid state") }

        btnOverview.setOnClickListener {
            map.current.value?.let {a ->
                Log.d(TAG, "Launching overview activity")
                startActivity(MapOverviewActivity.getIntent(this, a))
            }?: Log.e(TAG, "Attempted to launch map overview while in an invalid state") }

        btnNorth.setOnClickListener { map.north() }
        btnSouth.setOnClickListener { map.south() }
        btnEast.setOnClickListener { map.east() }
        btnWest.setOnClickListener { map.west() }
    }

    /** Update button visibility based on current location. Terribly hacky! **/
    private fun updateDisplay(area: Area) {
        btnNorth.visibility = if (area.y < maxY) View.VISIBLE else View.INVISIBLE
        btnSouth.visibility = if (area.y > 0) View.VISIBLE else View.INVISIBLE
        btnEast.visibility = if (area.x < maxX) View.VISIBLE else View.INVISIBLE
        btnWest.visibility = if (area.x > 0) View.VISIBLE else View.INVISIBLE
    }

    override fun onRestartEvent() { }

    override fun onGameOverEvent() { }

    companion object {
        private const val TAG = "NavigationActivity"
    }
}

