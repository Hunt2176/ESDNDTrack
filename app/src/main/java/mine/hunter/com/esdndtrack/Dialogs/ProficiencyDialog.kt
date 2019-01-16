package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.toIntOrZero
import mine.hunter.com.esdndtrack.Utilities.use
import mine.hunter.com.esdndtrack.Utilities.useAndReturn

class ProficiencyDialog(context: Context, val onDismiss: (Map<DNDCharacter.Attribute, Int>) -> Unit): Dialog(context)
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_character_picker)
		findViewById<ImageButton>(R.id.create_new_add).visibility = View.GONE
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)

		findViewById<RecyclerView>(R.id.character_load_Recycler)
			.use { recycler ->
				recycler.adapter = ProficiencyAdapter(context)
						.useAndReturn { adapter ->
						findViewById<ImageButton>(R.id.character_load_back).setOnClickListener { onDismiss(adapter.proficiencies); }
						adapter
				}
				recycler.layoutManager = GridLayoutManager(context, 1)

			}

	}

	class ProficiencyAdapter(val context: Context): RecyclerView.Adapter<ProficiencyViewHolder>()
	{
		val proficiencies = mutableMapOf<DNDCharacter.Attribute, Int>()
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProficiencyViewHolder =
				ProficiencyViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_proficiency, parent, false))

		override fun getItemCount(): Int = DNDCharacter.Attribute.attributes.size

		override fun onBindViewHolder(holder: ProficiencyViewHolder, position: Int)
		{
			val attrib = DNDCharacter.Attribute.attributes[position]
			holder.inputLayout.hint = attrib.readableName()
			holder.textView?.addTextChangedListener(object: TextWatcher
			{
				override fun afterTextChanged(s: Editable?){}
				override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
				override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
				{
					if (s == null) return
					if (s.isEmpty()) proficiencies.remove(attrib)
					else proficiencies[attrib] = s.toString().toIntOrZero()
				}
			})
		}
	}

	class ProficiencyViewHolder(view: View): RecyclerView.ViewHolder(view)
	{
		val inputLayout = view.findViewById<TextInputLayout>(R.id.attributeOutline)
		val textView = inputLayout.editText
	}
}