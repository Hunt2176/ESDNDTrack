package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

abstract class ItemSelectionDialog<T>(context: Context, val titleText: String = "Title"): Dialog(context)
{
	lateinit var title: TextView

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_character_picker)
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
		title = findViewById(R.id.create_new_title)
		title.text = titleText

		findViewById<RecyclerView>(R.id.character_load_Recycler)
				.use {  recycler ->
					recycler.adapter = getRecyclerAdapter()
					recycler.layoutManager = GridLayoutManager(context, 1)
					recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
				}

		findViewById<ImageButton>(R.id.create_new_add)
			.setOnClickListener { context.startActivity(Intent(context, CharacterCreator::class.java)); dismiss() }

		findViewById<ImageButton>(R.id.character_load_back)
			.setOnClickListener { cancel() }
	}
	abstract fun getRecyclerAdapter(): ItemSelectionAdapter<T>

	companion object
	{
		fun dndCharacterList(context: Context, onSelect: (DNDCharacter) -> Unit): ItemSelectionDialog<DNDCharacter>
		{
			return object: ItemSelectionDialog<DNDCharacter>(context, "Characters")
			{
				override fun getRecyclerAdapter(): ItemSelectionAdapter<DNDCharacter>
				{
					return object: ItemSelectionAdapter<DNDCharacter>(context, DNDCharacter.readFromJson(context), true, true)
					{
						override fun setTitle(item: DNDCharacter): String = item.name

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
						override fun setTitle(item: InventoryItem): String = item.name

						override fun onItemSelect(item: InventoryItem){}

						override fun modifyOverflowMenu(item: InventoryItem){}

						override fun deleteOverflowMenu(item: InventoryItem){}
					}
				}
			}
		}
	}
}