package siralmat.madrpg.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kotlinx.android.synthetic.main.list_item_detail.view.*
import siralmat.madrpg.model.Item
import siralmat.madrpg.R

/**
 * Adapter for a list of items in an area or player inventory.
 * listType determines the list elements' UI - eg showing buy/sell icons, sale
 * price, etc.
 */
class ItemListAdapter(
        private val listType: Int,
        private val viewModel: VisitViewModel,
        private val onGetDrop: (Item, Int) -> Unit,
        private val onUse: (Item) -> Unit)
    : ListAdapter<Item, ItemListAdapter.ItemHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val holder = ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_detail, parent, false))
        holder.view.btnUse.setOnClickListener { onUse(holder.item) } // todo: test
        holder.view.btnGetDrop.setOnClickListener { onGetDrop(holder.item, listType) }
        return holder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        const val BUY = R.string.buy
        const val SELL = R.string.sell
        const val PICKUP = R.string.pickup
        const val DROP = R.string.drop
    }

    inner class ItemHolder(val view: View): RecyclerView.ViewHolder(view) {
        lateinit var item: Item
        var btnGetDrop = view.btnGetDrop
        var itemName = view.itemName
        var itemValue = view.itemValue
        var itemMass = view.itemMass
        var itemIcon = view.itemIcon
        var btnUse = view.btnUse

        fun bind(newItem: Item) {
            item = newItem
            // todo
            itemName.text = item.description
            itemValue.text = (if (listType == SELL) item.salePrice() else item.value).toString()
            itemMass.text = "%.2f".format(item.mass)
            itemIcon.text = view.context.resources.getString(item.effectType)
            btnGetDrop.text = view.resources.getString(listType)
            btnUse.visibility = if (viewModel.canUse(item)) View.VISIBLE else View.INVISIBLE
        }
    }
}


class ItemDiffCallback: DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.iid == newItem.iid
    }
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}