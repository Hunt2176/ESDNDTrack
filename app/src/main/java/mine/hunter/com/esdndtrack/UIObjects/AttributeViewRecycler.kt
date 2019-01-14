package mine.hunter.com.esdndtrack.UIObjects

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.Dialogs.AttributeRollDialog
import mine.hunter.com.esdndtrack.Fragments.SpellViewHolder
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R

open class AttributeViewRecycler(val context: Context, val char: DNDCharacter): RecyclerView.Adapter<SpellViewHolder>()
{
	override fun getItemCount(): Int = 6

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellViewHolder
	{
		return SpellViewHolder(LayoutInflater.from(context).inflate(R.layout.spell_cell, parent, false))
	}

	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: SpellViewHolder, position: Int)
	{
		val attribute = char.getCoreAttributes()[position]
		holder.spellNameView.text = "${attribute.first.readableName()}: ${DNDCharacter.Attribute.advCalculator(attribute.second)}"
		holder.itemView.setOnClickListener { AttributeRollDialog(context, DNDCharacter.Attribute.advCalculator(attribute.second)).show() }
	}
}