package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.*

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
						setOnDismissListener {
							char.proficiencies.clear()
							adapter.proficiencies.forEach{char.proficiencies[it.key] = it.value}
						}
					findViewById<ImageButton>(R.id.character_load_back).setOnClickListener {
						dismiss()
					}
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
		val proficiencies: MutableMap<DNDCharacter.Attribute, Int>
			get() = mutableMapOf<DNDCharacter.Attribute, Int>()
					.borrow { map ->
						cells.forEach { map[it.attribSpinner.selectedItem as DNDCharacter.Attribute] = it.attribText.text.toIntOrZero() }
					}

		private val usedAttribs: List<DNDCharacter.Attribute>
			get() = cells.map { it.attribSpinner.selectedItem as DNDCharacter.Attribute }

		private val cells = arrayListOf<ProficiencyViewHolder>()
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProficiencyViewHolder =
				ProficiencyViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_proficiency, parent, false))

		private var count = 0
		override fun getItemCount(): Int = count

		override fun onBindViewHolder(holder: ProficiencyViewHolder, position: Int)
		{
			cells.add(holder)
			holder.removeUsed(usedAttribs)
			holder.attribSpinner.setOnItemSelectedListener { index ->
				updateCellSpinnerLists()
			}
			holder.deleteBtn.setOnClickListener { removeCell(cells.indexOf(holder)) }
		}

		private fun updateCellSpinnerLists()
		{
			cells.forEach{ cell ->
				cell.removeUsed(usedAttribs)
			}
		}

		fun addCell()
		{
			count += 1
			notifyItemInserted(cells.size)
		}

		fun removeCell(index: Int)
		{
			count -= 1
			cells.removeAt(index)
			notifyItemRemoved(index)
		}
	}

	class ProficiencyViewHolder(view: View): RecyclerView.ViewHolder(view)
	{
		val attribSpinner =
				view.findViewById<Spinner>(R.id.proficiency_spinner)
					.borrow { spinner ->
						spinner.adapter = ArrayAdapter<DNDCharacter.Attribute>(view.context, android.R.layout.simple_spinner_item, DNDCharacter.Attribute.attributes)
					}
		val attribText = view.findViewById<EditText>(R.id.proficiency_toAdd)
		val deleteBtn = view.findViewById<ImageButton>(R.id.cell_prof_cancel)

		fun removeUsed(usedItems: List<DNDCharacter.Attribute>)
		{
			usedItems.forEach {
				if (it != attribSpinner.selectedItem as DNDCharacter.Attribute)
				{
					//TODO: Way to Remove already Selected Items
				}
			}
		}
	}
}