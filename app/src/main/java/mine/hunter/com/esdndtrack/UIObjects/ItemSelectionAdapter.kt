package mine.hunter.com.esdndtrack.UIObjects

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import mine.hunter.com.esdndtrack.R

abstract class ItemSelectionAdapter <T> (val context: Context, items: Array<T>, private val usesModify: Boolean, private val usesDelete: Boolean):
		androidx.recyclerview.widget.RecyclerView.Adapter<SelectableViewHolder>()
{
	var items = items.toMutableList()

	abstract fun viewText(item: T): String
	abstract fun onItemSelect(item: T)
	abstract fun modifyOverflowMenu(item: T)
	abstract fun deleteOverflowMenu(item: T)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.manage_character_cell, parent, false)
		return SelectableViewHolder(view, (usesModify && usesDelete))
	}
	override fun getItemCount(): Int = items.count()
	override fun onBindViewHolder(holder: SelectableViewHolder, position: Int)
	{
		val item: T = items[position]
		holder.nameView.text = viewText(items[position])
		holder.itemView.setOnClickListener { onItemSelect(item) }
		holder.overflowButton.setOnClickListener {
			val popupMenu = PopupMenu(holder.itemView.context, it)
			popupMenu.inflate(R.menu.character_manage_overflow)
			if (!usesModify) popupMenu.menu.removeItem(R.id.MENUModifyCharacter)
			if (!usesDelete) popupMenu.menu.removeItem(R.id.MENUDeleteCharacter)
			popupMenu.show()

			popupMenu.setOnMenuItemClickListener {
				when (it.itemId)
				{
					R.id.MENUDeleteCharacter -> deleteOverflowMenu(items[position])
					R.id.MENUModifyCharacter -> modifyOverflowMenu(items[position])
				}
				return@setOnMenuItemClickListener true
			}
		}
	}

	companion object
	{
		fun <T> create(context: Context, items: Array<T>, text: (T) -> String, itemSelected: ((T) -> Unit)? = null, modifySelected: ((T) -> Unit)? = null, deleteSelected: ((T) -> Unit)? = null): ItemSelectionAdapter<T>
		{
			return object: ItemSelectionAdapter<T>(context, items, modifySelected != null, deleteSelected != null)
			{
				override fun viewText(item: T): String = text(item)
				override fun onItemSelect(item: T) {itemSelected?.invoke(item)}
				override fun modifyOverflowMenu(item: T){modifySelected?.invoke(item)}
				override fun deleteOverflowMenu(item: T){deleteSelected?.invoke(item)}
			}
		}
	}
}

open class SelectableViewHolder(view: View, usesOverflow: Boolean): androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
	val nameView = view.findViewById<TextView>(R.id.CharacterManageName)
	val overflowButton = view.findViewById<ImageButton>(R.id.CharacterManageOverFlow)

	init { if (!usesOverflow) overflowButton.visibility = View.GONE }
}