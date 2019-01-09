package mine.hunter.com.esdndtrack

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView

class ArrayAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<CharacterViewRecycle>()
{
	var characters = arrayListOf<String>()
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewRecycle
	{
		val view = LayoutInflater.from(context).inflate(R.layout.character_cell, parent, false)
		return CharacterViewRecycle(view, context)
	}

	override fun getItemCount(): Int = characters.size

	override fun onBindViewHolder(holder: CharacterViewRecycle, position: Int)
	{
		holder.readFromStorage(characters[position])
	}
}

class CharacterViewRecycle(view: View, val context: Context) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
	val characterName = view.findViewById<TextView>(R.id.CharacterName)
	val healthBar: ProgressBar
	val healthText: TextView = view.findViewById(R.id.HealthText)
	var name = ""

	init
	{
		healthBar = view.findViewById(R.id.HealthBar)

		val healthAdd = view.findViewById<Button>(R.id.HealthAdd)
		val healthSubtract = view.findViewById<Button>(R.id.HealthSubtract)

		healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"

		healthAdd.setOnClickListener {
			changeProgressBar(healthBar, 1)
			healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
			saveToStorage(SavableItem.current_hp, healthBar.progress)
		}
		healthSubtract.setOnClickListener {
			changeProgressBar(healthBar, -1)
			healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
			saveToStorage(SavableItem.current_hp, healthBar.progress)
		}

		healthText.setOnLongClickListener {
			var SetLevelDialog = SetLevelDialog(context, healthBar) { bar ->
				copyBarDetails(healthBar, bar, "Health", healthText)
				saveToStorage(SavableItem.max_hp, healthBar.max)
				saveToStorage(SavableItem.current_hp, healthBar.progress)
			}
			SetLevelDialog.show()
			SetLevelDialog.setLevelTitle("Set Max Health $name")
			SetLevelDialog.window.setLayout((6 * view.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
			true
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
		}
	}

	private fun saveToStorage(preferenceTitle: SavableItem, valueToSave: Int)
	{
		val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
		val editor = preferences.edit()
		when (preferenceTitle)
		{
			SavableItem.max_hp -> editor.putInt("max_hp", valueToSave)
			SavableItem.current_hp -> editor.putInt("current_hp", valueToSave)
		}
		editor.apply()
	}

	fun readFromStorage(name: String)
	{
		this.name = name
		characterName.text = name
		val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

		arrayOf("max_hp", "current_hp", "max_magic", "current_magic", "uses_magic", "character_ac")
				.forEach { item ->
					when (item)
					{
						"max_hp" -> healthBar.max = preferences.getInt(item, 1)
						"current_hp" -> healthBar.progress = preferences.getInt(item, healthBar.max)
						"character_ac" -> itemView.findViewById<TextView>(R.id.ACTextView).text = "AC: ${preferences.getInt(SavableItem.character_ac.getStringKey(), 0)}"
					}
				}
		healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
	}
}

class SetLevelDialog(context: Context, val barToUpdate: ProgressBar, val onChange: (ProgressBar) -> Unit) : Dialog(context)
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_set)

        val addButton = findViewById<Button>(R.id.AddToLevel)
        val subtractButton = findViewById<Button>(R.id.SubtractFromLevel)
        val levelValue = findViewById<TextView>(R.id.LevelText)
        val doneButton = findViewById<TextView>(R.id.LevelDismiss)

        levelValue.text = "${barToUpdate.max}"

        addButton.setOnClickListener {
            if (barToUpdate.max >= 0)
            {
                barToUpdate.max += 1
            }
            levelValue.text = "${barToUpdate.max}"
            onChange(barToUpdate)
        }
        subtractButton.setOnClickListener {
            if (barToUpdate.max > 1)
            {
                barToUpdate.max -= 1
            }
            levelValue.text = "${barToUpdate.max}"
            onChange(barToUpdate)
        }
        doneButton.setOnClickListener {
            dismiss()
        }
    }

    fun setLevelTitle(title: String)
    {
        findViewById<TextView>(R.id.LevelSetText).text = title
    }
}

fun CreateCharacterMenu(view: View, sharedPreferences: SharedPreferences): PopupMenu
{
        val menu = PopupMenu(view.context, view)
		sharedPreferences.getStringSet("names", mutableSetOf<String>())!!
				.forEachIndexed { _, s ->
                    menu.menu.add(s)
				}
        return menu
}

enum class SavableItem
{
	max_hp,
	current_hp,
	character_ac,
	magicModifier,
	character_list,
	uses_magic;

	fun getStringKey(): String
	{
		return when (this)
		{
			max_hp -> "max_hp"
			current_hp -> "current_hp"
			character_ac -> "character_ac"
			magicModifier -> "magic_modifier"
			character_list -> "character_list"
			uses_magic -> "uses_magic"
		}
	}


}