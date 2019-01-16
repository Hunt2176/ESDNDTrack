package mine.hunter.com.esdndtrack.UIObjects

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.R

abstract class RadioButtonAdapter<T>(val context: Context, val items: Array<T>): RecyclerView.Adapter<RadioButtonViewHolder>()
{
	private val radioButtons = arrayListOf<RadioButton>()
	var currentSelection: T? = null

	abstract fun setCellText(item: T): String
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioButtonViewHolder
		= RadioButtonViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_radio_button, parent, false))

	override fun getItemCount(): Int = items.count()

	override fun onBindViewHolder(holder: RadioButtonViewHolder, position: Int)
	{
		val item = items[position]
		holder.radioButton.gravity = Gravity.CENTER
		holder.radioButton.text = setCellText(item)
		radioButtons.add(holder.radioButton)
		holder.radioButton.setOnClickListener {
			radioButtons.forEach { if (it != holder.radioButton) it.isChecked = false }
			currentSelection = item
		}
	}
}

class RadioButtonViewHolder(view: View): RecyclerView.ViewHolder(view)
{
	val radioButton = view.findViewById<RadioButton>(R.id.radioButton)
}