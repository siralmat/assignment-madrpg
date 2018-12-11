package siralmat.madrpg.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_smelloscope.*

import siralmat.madrpg.GameState
import siralmat.madrpg.R
import siralmat.madrpg.model.Area

class SmelloScopeActivity : AppCompatActivity(), GameActivity {
    private lateinit var mapModel: MapViewModel
    private lateinit var listAdapter: SimpleItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smelloscope)
        mapModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        mapModel.init(GameState.getInstance(this.applicationContext))

        listAdapter = SimpleItemListAdapter()
        mapModel.nearbyItems.observe(this, Observer {
            it?.let { list -> listAdapter.submitList(list) }})

        itemList.adapter = listAdapter
        itemList.layoutManager = LinearLayoutManager(this)

        btnLeave.setOnClickListener { finish() }
    }

    override fun onGameOverEvent() { finish() }
    override fun onRestartEvent() { finish() }

    companion object {
        fun getIntent(context: Context, area: Area): Intent {
            val intent = Intent(context, SmelloScopeActivity::class.java)
            return intent
        }
    }
}
