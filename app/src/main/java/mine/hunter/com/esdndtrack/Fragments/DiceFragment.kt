package mine.hunter.com.esdndtrack.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.dice_recycler.*
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Objects.ArraySlider
import mine.hunter.com.esdndtrack.Objects.Dice
import mine.hunter.com.esdndtrack.Objects.StandardDice
import mine.hunter.com.esdndtrack.Utilities.toIntOrZero

class DiceFragment : androidx.fragment.app.Fragment()
{
	lateinit var recycler: androidx.recyclerview.widget.RecyclerView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val newView = inflater.inflate(R.layout.dice_recycler, container, false)
		recycler = newView.findViewById(R.id.DiceRecycler)
		recycler.adapter = DiceArrayAdapter(newView.context)
		recycler.layoutManager = androidx.recyclerview.widget.GridLayoutManager(newView.context, 1)
		recycler.addItemDecoration((androidx.recyclerview.widget.DividerItemDecoration(newView.context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)))

		newView.findViewById<Button>(R.id.ResetDiceButton).setOnClickListener {
			(recycler.adapter as DiceArrayAdapter).resetDice()
			NoDiceInfo.visibility = View.VISIBLE
		}

		newView.findViewById<Button>(R.id.AddDice).setOnClickListener {
			(recycler.adapter as DiceArrayAdapter).createDiceCell()
			NoDiceInfo.visibility = View.INVISIBLE
		}

		newView.findViewById<Button>(R.id.RollAllDice).setOnClickListener {
			var total = 0
			(recycler.adapter as DiceArrayAdapter).diceCells.forEach { diceCell -> total += diceCell.RollDice() }
			Toast.makeText(context, "Total ${total}", Toast.LENGTH_SHORT).show()
		}

		return newView
	}
}

class DiceArrayAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<DiceViewHolder>()
{
	var count = 0
	val diceCells = ArrayList<DiceViewHolder>()

	fun createDiceCell()
	{
		count += 1
		notifyItemInserted(count - 1)
	}

	fun resetDice()
	{
		count = 0
		notifyDataSetChanged()
		diceCells.clear()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiceViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.dice_cell, parent, false)
		return DiceViewHolder(view)
	}

	override fun getItemCount(): Int
	{
		return count
	}

	override fun onBindViewHolder(holder: DiceViewHolder, position: Int)
	{
		diceCells.add(holder)
		holder.reset()
	}
}

class DiceViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
	val dieSides: EditText = view.findViewById(R.id.DiceSides)
	val addToSides: EditText = view.findViewById(R.id.AddToRoll)
	val result: TextView = view.findViewById(R.id.DiceResult)
	val slider = ArraySlider(StandardDice.availableSides)

	init
	{
		reset()
	}

	fun reset()
	{
		slider.reset()
		dieSides.setText("${slider.getCurrentItem()}")
		addToSides.setText("")
		result.text = "="
		itemView.findViewById<ImageButton>(R.id.SwitchDiceDown).setOnClickListener {
			dieSides.setText("${slider.moveLeft()}")
		}

		itemView.findViewById<ImageButton>(R.id.SwitchDiceUp).setOnClickListener {
			dieSides.setText("${slider.moveRight()}")
		}
	}

	fun RollDice(): Int
	{
		var roll = Dice(dieSides.text.toIntOrZero()).rollDice()
		var additional = addToSides.text.toIntOrZero()
		roll += additional
		result.text = "=  $roll"
		return roll
	}
}