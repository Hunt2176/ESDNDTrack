package mine.hunter.com.esdndtrack.UIObjects

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mine.hunter.com.esdndtrack.R

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