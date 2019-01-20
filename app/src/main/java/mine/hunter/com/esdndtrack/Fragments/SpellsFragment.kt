package mine.hunter.com.esdndtrack.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import mine.hunter.com.esdndtrack.Dialogs.CreateSpellDialog
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Dialogs.SpellDetailDialog
import mine.hunter.com.esdndtrack.Objects.AndroidTimer
import mine.hunter.com.esdndtrack.Objects.ReadInSpell
import mine.hunter.com.esdndtrack.Utilities.*

class SpellsFragment : androidx.fragment.app.Fragment()
{

	var recycler: androidx.recyclerview.widget.RecyclerView? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val newView = inflater.inflate(R.layout.fragment_spells, container, false)
		recycler = newView.findViewById(R.id.SpellsRecycle)
		recycler?.adapter = SpellsArrayAdapter(newView.context)
		recycler?.layoutManager = androidx.recyclerview.widget.GridLayoutManager(newView.context, 1)
		recycler?.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(newView.context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))

		newView.findViewById<ImageButton>(R.id.CreateSpellButton).setOnClickListener {
			val dialog = CreateSpellDialog(newView.context) {
				if (it)
				{
					(recycler?.adapter as SpellsArrayAdapter).spellList = StaticItems.MergeSpellLists()
					(recycler?.adapter?.notifyDataSetChanged())
				}
			}
			dialog.show()
			dialog.window?.setLayout((6 * resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
		}

		newView.findViewById<EditText>(R.id.SpellSearchText).addTextChangedListener(object : TextWatcher
		{
			override fun afterTextChanged(s: Editable?)
			{

			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
			{

			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
			{
				if (context.isNull() || s.isNull() || recycler.isNull()) return
				val newSpellList = StaticItems.MergeSpellLists().filter { it.name.toLowerCase().contains(s!!.toString().toLowerCase()) }
				recycler!!.adapter = SpellsArrayAdapter(context!!, newSpellList.toTypedArray())
			}
		})
		return newView

	}

	companion object
	{
		fun create(): SpellsFragment
		{
			return SpellsFragment()
		}
	}
}

class SpellsArrayAdapter(val context: Context, var spellList: Array<ReadInSpell> = StaticItems.MergeSpellLists()) : androidx.recyclerview.widget.RecyclerView.Adapter<SpellViewHolder>()
{

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.spell_cell, parent, false)
		return SpellViewHolder(view)
	}

	override fun getItemCount(): Int
	{
		return if (spellList.isNull()) 0 else spellList.size
	}

	//Holds if the user begins holding on a cell
	private var holding = false
	//The Dialog that the holding pops up if the user is still holding
	private var holdingView: SpellDetailDialog? = null
	override fun onBindViewHolder(holder: SpellViewHolder, position: Int)
	{
		spellList.ifNotNull {
			holder.spellNameView.text = it[position].name
			if (it[position].custom)
			{
				holder.customSpellButton.visibility = View.VISIBLE
				holder.customSpellButton.setOnClickListener { _ ->
					PopupMenu(context, holder.customSpellButton).use { popupMenu ->
						popupMenu.inflate(R.menu.custom_spell_overflow)
						popupMenu.setOnMenuItemClickListener { item ->
							when (item.itemId)
							{
								R.id.MENU_Delete_CSpell ->
								{
									StaticItems.RemoveCustomSpell(context, it[position])
									spellList = StaticItems.MergeSpellLists()
									notifyDataSetChanged()

								}
								R.id.MENU_Modify_CSpell ->
								{
									CreateSpellDialog.editCustomSpell(context, it[position]) {
										if (it)
										{
											spellList = StaticItems.MergeSpellLists()
											notifyDataSetChanged()
										}
									}
											.use { dialog ->
												dialog.show()
												dialog.window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
											}
								}

								else -> {}
							}
							true
						}
						popupMenu.show()
					}
				}
			} else
			{
				holder.customSpellButton.visibility = View.GONE
			}
			holder.itemView.setOnTouchListener { v, event ->
				when (event.action)
				{
					MotionEvent.ACTION_DOWN ->
					{
						v.setBackgroundColor(v.context.getColor(R.color.colorAccent))
						holding = true
						AndroidTimer(500) {
							if (holding)
							{
								StaticItems.executeTrigger("disablepager")
								holdingView = SpellDetailDialog(v.context, it[position]) { StaticItems.executeTrigger("enablepager") }
								holdingView?.show()
							}
						}.execute()
					}
					MotionEvent.ACTION_CANCEL ->
					{
						v.setBackgroundColor(v.context.getColor(android.R.color.white))
						holding = false
					}
					MotionEvent.ACTION_UP ->
					{
						v.setBackgroundColor(v.context.getColor(android.R.color.white))
						if (holding && holdingView != null && holdingView!!.isShowing)
						{
							holdingView?.dismiss()
							holding = false
						}
						else
						{
							holding = false
							val dialog = SpellDetailDialog(v.context, it[position])
							dialog.show()
							dialog.window?.setLayout((6 * v.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
						}
					}
				}
				true
			}
		}

	}
}

class SpellViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
	val spellNameView: TextView = view.findViewById(R.id.SpellName)
	val customSpellButton: ImageButton = view.findViewById(R.id.customSpell_button)
}