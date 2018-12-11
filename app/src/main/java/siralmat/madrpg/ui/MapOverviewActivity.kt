package siralmat.madrpg.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_map_overview.*

import siralmat.madrpg.GameState
import siralmat.madrpg.R
import siralmat.madrpg.model.Area

class MapOverviewActivity : AppCompatActivity(), GameActivity {
    private lateinit var mapModel: MapViewModel
    private lateinit var areaModel: AreaViewModel
    private lateinit var adapter: MapGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedID = intent.getStringExtra(CURRENT_ID)

        val game = GameState.getInstance(this.applicationContext)
        mapModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        mapModel.init(game)
        areaModel = ViewModelProviders.of(this).get(AreaViewModel::class.java)
        areaModel.init(game, selectedID)

        adapter = MapGridAdapter(this::onAreaSelect)

        mapModel.mapData.observe(this, Observer {
            it?.let { list ->
                adapter.submitList(list) }})

        setContentView(R.layout.activity_map_overview)


        areaGrid.layoutManager = GridLayoutManager(this, GameState.COLS)
        areaGrid.adapter = adapter

        btnLeave.setOnClickListener { finish() }
    }

    fun onAreaSelect(a: Area) {
        Log.d(TAG, "Updating area model")
        areaModel.setAreaSelection(a.id)
    }

    override fun onGameOverEvent() { finish() }
    override fun onRestartEvent() { finish() }

    companion object {
        const val TAG = "MapOverviewActivity"
        const val CURRENT_ID = "siralmat.madrpg.model.area.id"

        fun getIntent(context: Context, currentArea: Area): Intent {
            val intent = Intent(context, MapOverviewActivity::class.java)
            intent.putExtra(CURRENT_ID, currentArea.id)
            return intent
        }
    }
}
