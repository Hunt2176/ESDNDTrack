package mine.hunter.com.esdndtrack.UIObjects

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.Dialogs.AttributeViewDialog
import mine.hunter.com.esdndtrack.Dialogs.CharacterLoadDialog
import mine.hunter.com.esdndtrack.Dialogs.SetLevelDialog
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.use

class CharacterViewAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<CharacterViewHolder>()
{
	private var characters = arrayListOf<DNDCharacter>()
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.character_cell, parent, false)
		return CharacterViewHolder(view, context)
	}

	override fun getItemCount(): Int = characters.size

	override fun onBindViewHolder(holder: CharacterViewHolder, position: Int)
	{
		holder.setup(characters[position])
	}

	fun addCharacter(char: DNDCharacter)
	{
		characters.add(char)
		notifyDataSetChanged()
	}

	fun removeCharacter(index: Int)
	{
		characters.removeAt(index)
		notifyDataSetChanged()
	}
}

class CharacterViewHolder(view: View, val context: Context) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
	lateinit var character: DNDCharacter
	val characterName = view.findViewById<TextView>(R.id.CharacterName)
	val healthBar: ProgressBar = view.findViewById(R.id.HealthBar)
	val healthText: TextView = view.findViewById(R.id.HealthText)
	var attributeDetails: RecyclerView = view.findViewById(R.id.character_cell_attribute_recycler)
	var toggleButton: ImageButton = view.findViewById(R.id.attributes_cell_toggle)
	var name = ""

	init
	{
		attributeDetails.visibility = View.GONE
		val healthAdd = view.findViewById<Button>(R.id.HealthAdd)
		val healthSubtract = view.findViewById<Button>(R.id.HealthSubtract)

		healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"

		healthAdd.setOnClickListener {
			changeProgressBar(healthBar, 1)
			healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"

		}
		healthSubtract.setOnClickListener {
			changeProgressBar(healthBar, -1)
			healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"

		}
		healthText.setOnLongClickListener {
			var SetLevelDialog = SetLevelDialog(context, healthBar) { bar ->
				copyBarDetails(healthBar, bar, "Health", healthText)
			}
			SetLevelDialog.show()
			SetLevelDialog.setLevelTitle("Set Max Health $name")
			SetLevelDialog.window?.setLayout((6 * view.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
			true
		}

		toggleButton
			.setOnClickListener {
				when (attributeDetails.visibility)
				{
					View.GONE ->
					{
						attributeDetails.visibility = View.VISIBLE
						toggleButton.setBackgroundResource(R.drawable.arrow_collapse)
						toggleButton.setImageDrawable(context.getDrawable(R.drawable.arrow_collapse))
					}
					View.VISIBLE ->
					{
						attributeDetails.visibility = View.GONE
						toggleButton.setImageDrawable(context.getDrawable(R.drawable.arrow_expand))
					}
				}
			}
	}

	private fun copyBarDetails(barToUpdate: ProgressBar, barToUpdateFrom: ProgressBar, barName: String, textView: TextView)
	{
		barToUpdate.max = barToUpdateFrom.max
		barToUpdate.progress = barToUpdateFrom.max
		textView.text = "${barName}: ${barToUpdateFrom.max}/${barToUpdateFrom.max}"
	}

	private fun changeProgressBar(progressBar: ProgressBar, amount: Int)
	{
		if (!(progressBar.progress + amount > progressBar.max || progressBar.progress + amount < 0))
		{
			progressBar.progress += amount
			character.currenthp = progressBar.progress
			character.writeToFile(context)
		}
	}

	fun setup(char: DNDCharacter)
	{
		character = char
		characterName.text = char.name
		healthText.text = "Health: ${character.currenthp}/${character.hp}"
		healthBar.max = character.hp
		healthBar.progress = character.currenthp
		itemView.findViewById<TextView>(R.id.ACTextView).text = "Base AC: ${char.getAttrib(DNDCharacter.Attribute.BaseAC)}"
		attributeDetails
			.use { recycler ->
				recycler.adapter = AttributeViewRecycler(context, character)
				recycler.layoutManager = GridLayoutManager(context, 2)
				recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
			}

		itemView.setOnLongClickListener { AttributeViewDialog(context, char).show(); true }
		itemView.findViewById<ImageButton>(R.id.character_inventory_button)
			.setOnClickListener { CharacterLoadDialog(context, "Inventory"){}.show() }
	}
}

