package mine.hunter.com.esdndtrack

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import mine.hunter.com.esdndtrack.utilities.*

class DiceRoller : AppCompatActivity() {

    private val slider = ArraySlider(StandardDice.availableSides)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dice_roller)

        val leftButton = findViewById<ImageButton>(R.id.DiceSwitchLeft)
        val rightButton = findViewById<ImageButton>(R.id.DiceSwitchRight)
        val diceInput = findViewById<EditText>(R.id.DiceSideInput)
        val numToRollView = findViewById<EditText>(R.id.NumberToRoll)
        val rollOutButton = findViewById<Button>(R.id.RollDiceButton)
        val resultView = findViewById<TextView>(R.id.RollResult)

        diceInput.setText("${slider.getCurrentItem()}")

        leftButton.setOnClickListener {
            diceInput.setText("${slider.moveLeft()}")
        }
        rightButton.setOnClickListener {
            diceInput.setText("${slider.moveRight()}")
        }

        rollOutButton.setOnClickListener {
            var numberToRoll = numToRollView.text.toIntOrZero()
            if (numberToRoll == 0)
            {
                numberToRoll = 1
            }
            resultView.text = Dice(diceInput.text.toIntOrZero()).rollMultiple(numberToRoll).toString()
        }

    }
}
