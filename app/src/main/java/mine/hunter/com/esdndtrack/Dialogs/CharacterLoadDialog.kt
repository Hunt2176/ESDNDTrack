package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.Activity.CharacterCreator
import mine.hunter.com.esdndtrack.Activity.CharacterManageAdapter
import mine.hunter.com.esdndtrack.Activity.CharacterManager
import mine.hunter.com.esdndtrack.Activity.CharacterViewHolder
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.use

class CharacterLoadDialog(context: Context, val titleText: String = "Title", val onDismiss: (DNDCharacter?) -> Unit): Dialog(context)
{
	var characterSelected: DNDCharacter? = null
	val chars = DNDCharacter.readFromJson(context)

	lateinit var title: TextView

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_character_picker)
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)

		title = findViewById(R.id.create_new_title)
		title.text = titleText
		setOnDismissListener { onDismiss(characterSelected) }

		//TODO: Add Recycler to Scrollview
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

		findViewById<ImageButton>(R.id.create_new_add)
			.setOnClickListener { context.startActivity(Intent(context, CharacterCreator::class.java)); dismiss() }

		findViewById<ImageButton>(R.id.character_load_back)
			.setOnClickListener { cancel() }
	}
}