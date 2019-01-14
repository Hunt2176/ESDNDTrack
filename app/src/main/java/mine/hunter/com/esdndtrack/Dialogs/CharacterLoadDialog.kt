package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.Activity.CharacterManageAdapter
import mine.hunter.com.esdndtrack.Activity.CharacterViewHolder
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.use

class CharacterLoadDialog(context: Context, val onDismiss: (DNDCharacter?) -> Unit): Dialog(context)
{
	var characterSelected: DNDCharacter? = null
	val chars = DNDCharacter.readFromJson(context)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_character_picker)
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)

		setOnDismissListener { onDismiss(characterSelected) }

		findViewById<RecyclerView>(R.id.character_load_Recycler)
			.use {  recycler ->
				recycler.adapter =
					object: CharacterManageAdapter(context)
					{
						override fun onBindViewHolder(holder: CharacterViewHolder, position: Int, payloads: MutableList<Any>)
						{
							holder.nameView.text = chars[position].name
							holder.overflowButton.visibility = View.GONE
							holder.itemView.setOnClickListener { characterSelected = chars[position]; dismiss() }
						}
					}
				recycler.layoutManager = GridLayoutManager(context, 1)
				recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
			}

		findViewById<ImageButton>(R.id.character_load_back)
			.setOnClickListener { cancel() }
	}
}