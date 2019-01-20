package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.borrow
import mine.hunter.com.esdndtrack.Utilities.toIntOrZero
import mine.hunter.com.esdndtrack.Utilities.use
import mine.hunter.com.esdndtrack.Utilities.useAndReturn

class ProficiencyDialog(context: Context, val char: DNDCharacter): Dialog(context)
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_item_selection)
		findViewById<RecyclerView>(R.id.character_load_Recycler)
			.use { recycler ->
				recycler.adapter = ProficiencyAdapter(context)
						.borrow { adapter ->
						findViewById<ImageButton>(R.id.character_load_back).setOnClickListener {  }
						findViewById<ImageButton>(R.id.create_new_add)
							.setOnClickListener {
								adapter.addCell()
							}
				}
				recycler.layoutManager = GridLayoutManager(context, 1)
			}

	}

	override fun show()
	{
		super.show()
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 6, (6 * context.resources.displayMetrics.widthPixels) / 6)
	}

	class ProficiencyAdapter(val context: Context): RecyclerView.Adapter<ProficiencyViewHolder>()
	{
		private var proficiencyCells = 0
		private val proficiencies = mutableMapOf<DNDCharacter.Attribute, Int>()
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProficiencyViewHolder =
				ProficiencyViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_proficiency, parent, false))

		override fun getItemCount(): Int = proficiencyCells

		override fun onBindViewHolder(holder: ProficiencyViewHolder, position: Int)
		{
			val attrib = DNDCharacter.Attribute.attributes[position]

		}

		fun addCell()
		{
			proficiencyCells += 1
			notifyItemInserted(proficiencyCells - 1)
		}
	}

	class ProficiencyViewHolder(view: View): RecyclerView.ViewHolder(view)
	{
		val attribSpinner =
				view.findViewById<Spinner>(R.id.proficiency_spinner)
					.borrow { spinner ->
						spinner.adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, DNDCharacter.Attribute.attributes.map { it.readableName() })
					}
		val attribText = view.findViewById<EditText>(R.id.proficiency_toAdd)
	}
}