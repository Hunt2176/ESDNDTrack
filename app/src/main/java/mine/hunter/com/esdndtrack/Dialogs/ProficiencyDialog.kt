package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
import java.lang.IndexOutOfBoundsException

class ProficiencyDialog(context: Context, val char: DNDCharacter): Dialog(context)
{

	class ProficiencyAdapter(val context: Context, val character: DNDCharacter): RecyclerView.Adapter<ProficiencyViewHolder>()
	{
		private var isLoading = true
		private val cells = arrayListOf<ProficiencyViewHolder>()
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProficiencyViewHolder =
				ProficiencyViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_proficiency, parent, false))

		private var count = 0
		override fun getItemCount(): Int = count

		override fun onBindViewHolder(holder: ProficiencyViewHolder, position: Int)
		{
			cells.add(position, holder)

			holder.attribSpinner.setOnItemSelectedListener { _ ->
				character.proficiencies.remove(holder.currentAttrib)
				val attrib = holder.attribSpinner.selectedItem as DNDCharacter.Attribute
				holder.currentAttrib = attrib
				if (!character.proficiencies.contains(attrib))
				{
					character.proficiencies.add(attrib)
				}
			}

			holder.deleteBtn.setOnClickListener {
				character.proficiencies.remove(holder.attribSpinner.selectedItem as DNDCharacter.Attribute)
				removeCell(cells.indexOf(holder))
			}

			if (isLoading && position <= character.proficiencies.count())
			{
				if (character.proficiencies.isEmpty())
				{
					isLoading = false
				}
				else
				{
					isLoading = try
					{
						val attribute = character.proficiencies.toList()[position]
						holder.attribSpinner.setSelection(DNDCharacter.Attribute.attributes.indexOf(attribute))
						position != character.proficiencies.count()
					} catch (e: IndexOutOfBoundsException)
					{
						false
					}

				}

			}
		}

		fun updateFromChar()
		{
			character.proficiencies.forEach{addCell()}
		}

		fun addCell()
		{
			count += 1
			notifyItemInserted(count)
		}

		fun removeCell(index: Int)
		{
			count -= 1
			cells.removeAt(index)
			notifyItemRemoved(index)
		}
	}

	class ProficiencyViewHolder(val view: View): RecyclerView.ViewHolder(view)
	{
		var currentAttrib: DNDCharacter.Attribute? = null
		val attribSpinner =
				view.findViewById<Spinner>(R.id.proficiency_spinner)
					.borrow { spinner ->
						spinner.adapter = ArrayAdapter<DNDCharacter.Attribute>(view.context, android.R.layout.simple_spinner_item, DNDCharacter.Attribute.attributes)
					}
		val deleteBtn = view.findViewById<ImageButton>(R.id.cell_prof_cancel)
	}
}