package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.*
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

open class CenterAdapter(val context: Context): RecyclerView.Adapter<CenteredViewHolder>()
{
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenteredViewHolder
		= CenteredViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_center_text, parent, false))

	override fun getItemCount(): Int = 0

	override fun onBindViewHolder(holder: CenteredViewHolder, position: Int){}
}

class CenteredViewHolder(view: View): RecyclerView.ViewHolder(view)
{
	val textView: TextView = view.findViewById(R.id.centered_text)
}