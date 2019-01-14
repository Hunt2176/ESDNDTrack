package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Objects.ArraySlider
import mine.hunter.com.esdndtrack.Objects.Dice
import mine.hunter.com.esdndtrack.Objects.StandardDice
import mine.hunter.com.esdndtrack.Utilities.toIntOrZero

class AttributeRollDialog(context: Context, val level: Int): Dialog(context)
{

	lateinit var dieSides: EditText
	lateinit var addToSides: EditText
	lateinit var result: TextView
	val slider = ArraySlider(StandardDice.availableSides)

	fun reset()
	{
		slider.reset()
		dieSides.setText("${slider.getCurrentItem()}")
		addToSides.setText("")
		result.text = "="
		findViewById<ImageButton>(R.id.SwitchDiceDown).setOnClickListener {
			if (slider.hasLeft())
			{
				dieSides.setText("${slider.moveLeft()}")
				RollDice()
			}

		}

		findViewById<ImageButton>(R.id.SwitchDiceUp).setOnClickListener {
			if (slider.hasRight())
			{
				dieSides.setText("${slider.moveRight()}")
				RollDice()
			}
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

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dice_cell)
		window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 6, ConstraintLayout.LayoutParams.WRAP_CONTENT)

		dieSides = findViewById(mine.hunter.com.esdndtrack.R.id.DiceSides)
		addToSides = findViewById(R.id.AddToRoll)
		result = findViewById(R.id.DiceResult)

		reset()
		slider.toFarRight()
		dieSides.setText(slider.getCurrentItem().toString())
		addToSides.setText(level.toString())

		RollDice()

	}
}