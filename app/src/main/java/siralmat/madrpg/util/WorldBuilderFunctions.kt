package siralmat.madrpg.util

import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

import siralmat.madrpg.model.Item

/**
 * Misc functions for world generation.
 * Code is pretty terrible, but more or less functional.
 **/

fun generateItems(cols: Int, rows: Int): List<Item> {
    val list = ArrayList<Item>()
    appendEquipment((cols * rows), list)
    appendFoods((cols * rows) * 2, list)

    appendSpecialItems(cols, list)
    return list
}

private fun appendEquipment(quantity: Int, list: MutableList<Item>) {
    val descriptions = listOf("Chainmail", "Sturdy boots", "Wooden bucket", "Fishing rod",
            "Leather gloves", "Threadbare socks", "Pinafore", "Umbrella","Towel", "Sunglasses",
            "Saddle","Doll","Blouse","Helmet","Slippers")
    for (i in 1..quantity) {
        val mass = ThreadLocalRandom.current().nextDouble(10.0)+0.1
        val value = (mass * 3).toInt() + ThreadLocalRandom.current().nextInt(40)
        list.add(Item(
                description = descriptions[i%descriptions.size], value = value, mass = mass))
    }
}


private fun appendFoods(quantity: Int, list: MutableList<Item>) {
    val mods = listOf("Warm","Wholesome","Appetising","Hot","Flaky","Fresh","Aromatic",
           "Bland", "Spiced","Minty","Fragrant","Preservative-free","Broiled").shuffled()
    val badMods = listOf("Greenish","Rotting", "Plastic","Rotting",
            "Disgusting","Acrid","Bitter","Raw").shuffled()
    val foodItems = listOf("loaf", "apple", "fish", "meat", "sandwich", "potato", "chicken",
            "soup","pear","tomato","banana","lobster","pie","stew","scone")
    for (i in 1..quantity) {
        val foodType = foodItems[ThreadLocalRandom.current().nextInt(foodItems.size)]
        val quality = ThreadLocalRandom.current().nextInt(35)

        if (quality <= 10) {
            val desc = "${badMods[i%badMods.size]} $foodType"
            val health =  (quality - 15).toDouble()
            list.add(Item(description = desc, value = quality, effectType = Item.E_HEALTH,
                    effectAmount = health))
        } else {
            val desc = "${mods[i%mods.size]} $foodType"
            list.add(Item(description = desc, value = quality*2, effectType = Item.E_HEALTH,
                    effectAmount = quality.toDouble()))
        }
    }
}

private fun appendSpecialItems(count: Int, list: MutableList<Item>) {
    for (i in 1..count) {
        list.add(Item(description = Item.IMPROBABILITY_DRIVE, value = 200, mass = -3.1415,
                effectType = Item.E_SPECIAL))
        list.add(Item(description = Item.BEN_KENOBI, value = 150, mass = 0.0, effectType = Item
                .E_SPECIAL))
        list.add(Item(description = Item.SMELLOSCOPE, value = 100, mass = 5.0, effectType = Item
                .E_SPECIAL))
        list.add(Item(description = Item.JADE_MONKEY, value = 250, mass = 2.0, effectType = Item
                .E_VICTORY))
        list.add(Item(description = Item.ICE_SCRAPER, value = 25, mass = 1.5, effectType = Item
                .E_VICTORY))
        list.add(Item(description = Item.ROADMAP, value = 75, mass = 0.1, effectType = Item
                .E_VICTORY))
    }
}