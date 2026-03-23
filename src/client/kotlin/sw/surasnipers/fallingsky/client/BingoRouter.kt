package sw.surasnipers.fallingsky.client

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.item.ItemStack
import sw.surasnipers.fallingsky.client.utils.InventoryUtils
import sw.surasnipers.fallingsky.client.utils.getSkyblockId

object BingoRouter {
    
    private val gson = Gson()
    
    /** The currently scanned bingo goals. */
    var currentGoals: List<BingoGoal> = emptyList()

    /** If currently showing template goals. */
    var isUsingTemplates: Boolean = false

    /** Data loaded from the goals.json resource. */
    private var goalsDataMap: Map<String, GoalData> = emptyMap()

    /** Loads the bingo goals from a JSON string. */
    fun loadGoals(json: String) {
        val listType = object : TypeToken<List<GoalData>>() {}.type
        val dataList: List<GoalData> = gson.fromJson(json, listType)
        // Using lowercase tile names as keys for easier matching.
        goalsDataMap = dataList.associateBy { it.tile.lowercase() }
    }

    /** Checks if the Bingo Card GUI is currently open */
    fun isBingoCardOpen(): Boolean {
        val title = InventoryUtils.getInventoryName() ?: return false
        return title.contains("Bingo Card")
    }

    /** Updates the current bingo goals from the open Bingo Card menu */
    fun updateGoals() {
        if (!isBingoCardOpen()) return
        
        val goals = scanBingoGoals()
        if (goals.isNotEmpty()) {
            currentGoals = goals
            isUsingTemplates = false
        }
    }

    /** Loads template goals to show when no real goals are scanned. */
    fun loadTemplateGoals() {
        val templateNames = listOf(
            "At your service",
            "Bayou Bobbin'",
            "Chocolatier",
            "Commissioned",
            "Deep Sea Angler"
        )
        
        currentGoals = templateNames.map { name ->
            val data = goalsDataMap[name.lowercase()]
            BingoGoal(
                id = data?.id ?: "template",
                name = name,
                itemStack = ItemStack.EMPTY,
                data = data
            )
        }
        isUsingTemplates = true
    }

    /** Scans the current Bingo Card for goals.
     * In Hypixel, Bingo goals are items in the Bingo Card menu. */
    fun scanBingoGoals(): List<BingoGoal> {
        if (!isBingoCardOpen()) return emptyList()

        val goals = mutableListOf<BingoGoal>()
        val items = InventoryUtils.getContainerItems()

        for (item in items) {
            val skyblockId = item.getSkyblockId()
            if (skyblockId != null) {
                val itemName = item.name.string
                val data = goalsDataMap[itemName.lowercase()]
                
                goals.add(BingoGoal(skyblockId, itemName, item, data))
            }
        }
        return goals
    }

    data class BingoGoal(
        val id: String,
        val name: String,
        val itemStack: ItemStack,
        val data: GoalData? = null
    )

    data class GoalData(
        val id: String,
        val tile: String,
        val primary_method: String,
        val description: String
    )
}
