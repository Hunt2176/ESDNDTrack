package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.UIObjects.RadioButtonAdapter
import mine.hunter.com.esdndtrack.Utilities.use
import mine.hunter.com.esdndtrack.Utilities.useAndReturn

class LevelSelectionDialog(context: Context, val title: String, val onDismiss: (Int?) -> Unit): Dialog(context)
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_character_picker)
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 6, ConstraintLayout.LayoutParams.WRAP_CONTENT)

		findViewById<TextView>(R.id.create_new_title).text = title
		findViewById<RecyclerView>(R.id.character_load_Recycler)
			.use { recycler ->
				recycler.adapter = object: RadioButtonAdapter<Int>(context, DNDCharacter.Attribute.abilitySkillRange.toList().toTypedArray())
				{
					override fun setCellText(item: Int): String = item.toString()
				}
						.useAndReturn {  adapter ->
							setOnDismissListener { onDismiss(adapter.currentSelection) }
							return@useAndReturn adapter
						}
				recycler.layoutManager = GridLayoutManager(context, 2)

			}
		findViewById<ImageButton>(R.id.character_load_back).setOnClickListener { dismiss() }
		findViewById<ImageButton>(R.id.create_new_add).visibility = View.GONE
	}
}


