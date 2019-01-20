package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.Activity.CharacterCreator
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.Objects.InventoryItem
import mine.hunter.com.esdndtrack.UIObjects.ItemSelectionAdapter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.GSONHelper
import mine.hunter.com.esdndtrack.Utilities.use
import mine.hunter.com.esdndtrack.Utilities.useAndReturn
import java.io.File

abstract class ItemSelectionDialog<T>(context: Context, val titleText: String = "Title", var onAddButtonPressed: (() -> Unit)? = null): Dialog(context)
{
	lateinit var title: TextView

	open fun countPerLine(): Int = 1

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_item_selection)
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 6, (6 * context.resources.displayMetrics.widthPixels) / 5)
		title = findViewById(R.id.create_new_title)
		title.text = titleText

		findViewById<RecyclerView>(R.id.character_load_Recycler)
			.use {  recycler ->
				recycler.adapter = getRecyclerAdapter()
				recycler.layoutManager = GridLayoutManager(context, countPerLine())
				recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
			}

		findViewById<ImageButton>(R.id.create_new_add)
			.use {  button ->
				if (onAddButtonPressed == null) button.visibility = View.GONE
				else button.setOnClickListener { onAddButtonPressed?.invoke(); dismiss() }
			}

		findViewById<ImageButton>(R.id.character_load_back)
			.setOnClickListener { cancel() }
	}

	abstract fun getRecyclerAdapter(): ItemSelectionAdapter<T>

	companion object
	{
		fun <T> create(context: Context, dialogTitle: String, itemSelectionAdapter: ItemSelectionAdapter<T>, onAddButtonPressed: (() -> Unit)? = null): ItemSelectionDialog<T>
		{
			return object: ItemSelectionDialog<T>(context, dialogTitle, onAddButtonPressed)
			{
				override fun getRecyclerAdapter(): ItemSelectionAdapter<T>
				{
					return itemSelectionAdapter
				}
			}
		}

		fun dndCharacterList(context: Context, onSelect: (DNDCharacter) -> Unit): ItemSelectionDialog<DNDCharacter>
		{
			return object: ItemSelectionDialog<DNDCharacter>(context, "Characters", {context.startActivity(Intent(context, CharacterCreator::class.java))})
			{
				override fun getRecyclerAdapter(): ItemSelectionAdapter<DNDCharacter>
				{
					return object: ItemSelectionAdapter<DNDCharacter>(context, DNDCharacter.readFromJson(context), true, true)
					{
						override fun viewText(item: DNDCharacter): String = item.name

						override fun onItemSelect(item: DNDCharacter){onSelect(item); dismiss()}

						override fun modifyOverflowMenu(item: DNDCharacter)
						{
							context.startActivity(Intent(context, CharacterCreator::class.java)
									.useAndReturn { intent ->
										intent.putExtra("charID", item.id)
										return@useAndReturn intent
									})
						}
						override fun deleteOverflowMenu(item: DNDCharacter)
						{
							items.indexOf(item)
								.use { index ->
									items.removeAt(index)
									notifyItemRemoved(index)
									GSONHelper().writeToDisk(items.toTypedArray(), File(context.filesDir, "Characters.json"))
								}
						}
					}
				}
			}
		}

		fun inventoryItemsList(context: Context, items: Array<InventoryItem>): ItemSelectionDialog<InventoryItem>
		{
			return object: ItemSelectionDialog<InventoryItem>(context, "Inventory")
			{
				override fun getRecyclerAdapter(): ItemSelectionAdapter<InventoryItem>
				{
					return object: ItemSelectionAdapter<InventoryItem>(context, items, true, true)
					{
						override fun viewText(item: InventoryItem): String = item.name

						override fun onItemSelect(item: InventoryItem){}

						override fun modifyOverflowMenu(item: InventoryItem){}

						override fun deleteOverflowMenu(item: InventoryItem){}
					}
				}
			}
		}
	}
}