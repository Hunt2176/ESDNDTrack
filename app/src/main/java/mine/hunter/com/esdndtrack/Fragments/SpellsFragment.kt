package mine.hunter.com.esdndtrack.Fragments

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import mine.hunter.com.esdndtrack.CreateSpellDialog
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.SpellDetailDialog
import mine.hunter.com.esdndtrack.Utilities.*

class SpellsFragment : Fragment()
{

	var recycler: RecyclerView? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val newView = inflater.inflate(R.layout.fragment_spells, container, false)
		recycler = newView.findViewById(R.id.SpellsRecycle)
		recycler?.adapter = SpellsArrayAdapter(newView.context)
		recycler?.layoutManager = GridLayoutManager(newView.context, 1)
		recycler?.addItemDecoration(DividerItemDecoration(newView.context, DividerItemDecoration.VERTICAL))

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

class SpellsArrayAdapter(val context: Context, var spellList: Array<ReadInSpell> = StaticItems.MergeSpellLists()) : RecyclerView.Adapter<SpellViewHolder>()
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
					}
					MotionEvent.ACTION_CANCEL ->
					{
						v.setBackgroundColor(v.context.getColor(android.R.color.white))
					}
					MotionEvent.ACTION_UP ->
					{
						v.setBackgroundColor(v.context.getColor(android.R.color.white))
						val dialog = SpellDetailDialog(v.context, it[position])
						dialog.show()
						dialog.spellNameView.text = it[position].name
						dialog.spellDescView.text = Html.fromHtml("<br>${it[position].desc}", Html.FROM_HTML_MODE_LEGACY)
						dialog.spellLevelView.text = Html.fromHtml("<b>Level</b><br>${it[position].level}", Html.FROM_HTML_MODE_LEGACY)
						dialog.spellRangeView.text = Html.fromHtml("<b>Range</b><br>${it[position].range}", Html.FROM_HTML_MODE_LEGACY)
						dialog.window?.setLayout((6 * v.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)


					}
				}
				true
			}
		}

	}
}

class SpellViewHolder(view: View) : RecyclerView.ViewHolder(view)
{
	val spellNameView: TextView = view.findViewById(R.id.SpellName)
	val customSpellButton: ImageButton = view.findViewById(R.id.customSpell_button)
}