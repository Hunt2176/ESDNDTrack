package mine.hunter.com.esdndtrack.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.dice_recycler.*
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.utilities.ArraySlider
import mine.hunter.com.esdndtrack.utilities.Dice
import mine.hunter.com.esdndtrack.utilities.StandardDice
import mine.hunter.com.esdndtrack.utilities.toIntOrZero

class DiceFragment: Fragment()
{
	lateinit var recycler: RecyclerView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val newView = inflater.inflate(R.layout.dice_recycler, container, false)
		recycler = newView.findViewById(R.id.DiceRecycler)
		recycler.adapter = DiceArrayAdapter(newView.context)
		recycler.layoutManager = GridLayoutManager(newView.context, 1)
		recycler.addItemDecoration((DividerItemDecoration(newView.context, DividerItemDecoration.VERTICAL)))

		newView.findViewById<Button>(R.id.ResetDiceButton).setOnClickListener {
			(recycler.adapter as DiceArrayAdapter).resetDice()
			NoDiceInfo.visibility = View.VISIBLE
		}

		newView.findViewById<Button>(R.id.AddDice).setOnClickListener {
			(recycler.adapter  as DiceArrayAdapter).createDiceCell()
			NoDiceInfo.visibility = View.INVISIBLE
		}

		newView.findViewById<Button>(R.id.RollAllDice).setOnClickListener {
			for (i in (recycler.adapter as DiceArrayAdapter).diceCells)
			{
				i.RollDice()
			}
		}

		return newView
	}
}

class DiceArrayAdapter(val context: Context): RecyclerView.Adapter<DiceViewHolder>()
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
		holder.result.text = "="
	}
}

class DiceViewHolder(view: View): RecyclerView.ViewHolder(view)
{
	val slider = ArraySlider(StandardDice.availableSides)
	val dieSides: EditText = view.findViewById(R.id.DiceSides)
	val addToSides: EditText = view.findViewById(R.id.AddToRoll)
	val result: TextView = view.findViewById(R.id.DiceResult)

	init
	{
		dieSides.setText("${slider.getCurrentItem()}")
		view.findViewById<ImageButton>(R.id.SwitchDiceDown).setOnClickListener {
			dieSides.setText("${slider.moveLeft()}")
		}

		view.findViewById<ImageButton>(R.id.SwitchDiceUp).setOnClickListener {
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