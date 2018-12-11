package siralmat.madrpg.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_visit.*

import siralmat.madrpg.GameState
import siralmat.madrpg.R
import siralmat.madrpg.model.Area
import siralmat.madrpg.model.Item
import siralmat.madrpg.model.Player

class VisitActivity: AppCompatActivity(), GameActivity {
    private lateinit var visitModel: VisitViewModel
    private lateinit var playerListAdapter: ItemListAdapter
    private lateinit var areaListAdapter: ItemListAdapter
    private lateinit var area: Area
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit)

        area = intent.getParcelableExtra(AREA)

        // set up display & initialize list adapters
        visitModel = ViewModelProviders.of(this).get(VisitViewModel::class.java)
        visitModel.init(GameState.getInstance(this.applicationContext))
        setupDisplay()


        visitModel.areaItems.observe(this, Observer {
            it?.let {i -> areaListAdapter.submitList(i) } })

        visitModel.playerItems.observe(this, Observer {
            it?.let {i -> playerListAdapter.submitList(i) } })

        visitModel.player.observe(this, Observer {
            it?.let { p -> player = p } })
    }

    private fun setupDisplay() {
        btnLeave.setOnClickListener { finish() }

        if (area.town) {
            playerListAdapter = ItemListAdapter(ItemListAdapter.SELL, visitModel, this::onClick,
                    this::onUse)
            areaListAdapter = ItemListAdapter(ItemListAdapter.BUY, visitModel, this::onClick,
                    this::onUse)
            labelAreaItems.text = "Buy"
            labelPlayerItems.text = "Sell"
        } else {
            playerListAdapter = ItemListAdapter(ItemListAdapter.DROP, visitModel, this::onClick,
                    this::onUse)
            areaListAdapter = ItemListAdapter(ItemListAdapter.PICKUP, visitModel, this::onClick,
            this::onUse)
            labelAreaItems.text = "Pick up"
            labelPlayerItems.text = "Drop"
        }
        areaItemList.adapter = areaListAdapter
        areaItemList.layoutManager = LinearLayoutManager(this)
        playerItemList.adapter = playerListAdapter
        playerItemList.layoutManager = LinearLayoutManager(this)
    }

    fun onClick(item: Item, sourceList: Int) {
        val success = visitModel.getDrop(item, player, area, sourceList)
        if (!success) {
            showToast("I'm sorry, Dave; I'm afraid I can't do that...", this)
        }
    }

    fun onUse(item: Item) {
        showToast(visitModel.useItem(item), this)
        if (item.description == Item.SMELLOSCOPE) { // only item that requires view interaction
            startActivity(SmelloScopeActivity.getIntent(this, area))
        }
    }

    override fun onGameOverEvent() { finish() }
    override fun onRestartEvent() { finish() }


    companion object {
        const val AREA = "siralmat.madrpg.model.Area"

        fun getIntent(context: Context, area: Area): Intent {
            val intent = Intent(context, VisitActivity::class.java)
            intent.putExtra(AREA, area)
            return intent
        }
    }

}
