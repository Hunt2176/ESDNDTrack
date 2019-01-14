package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.*
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.UIObjects.CenterAdapter
import mine.hunter.com.esdndtrack.UIObjects.CenteredViewHolder
import mine.hunter.com.esdndtrack.Utilities.use

class AttributeViewDialog(context: Context, val character: DNDCharacter): Dialog(context)
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_character_picker)
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
		findViewById<RecyclerView>(R.id.character_load_Recycler)
			.use { recycler ->
				recycler.adapter = object: CenterAdapter(context)
				{
					override fun getItemCount(): Int = DNDCharacter.Attribute.attributeList.size
					override fun onBindViewHolder(holder: CenteredViewHolder, position: Int)
					{
						val attribute = DNDCharacter.Attribute.attributeList[position]
						holder.textView.text = "${attribute.readableName()}\n${character.getAttrib(attribute)}"
					}
				}
				recycler.layoutManager = GridLayoutManager(context, 2)
				recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
			}

		findViewById<ImageButton>(R.id.character_load_back).setOnClickListener { onBackPressed() }

	}
}

