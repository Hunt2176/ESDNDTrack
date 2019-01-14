package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import mine.hunter.com.esdndtrack.R

class SetLevelDialog(context: Context, val barToUpdate: ProgressBar, val onChange: (ProgressBar) -> Unit) : Dialog(context)
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_set)

        val addButton = findViewById<Button>(R.id.AddToLevel)
        val subtractButton = findViewById<Button>(R.id.SubtractFromLevel)
        val levelValue = findViewById<TextView>(R.id.LevelText)
        val doneButton = findViewById<TextView>(R.id.LevelDismiss)

        levelValue.text = "${barToUpdate.max}"

        addButton.setOnClickListener {
            if (barToUpdate.max >= 0)
            {
                barToUpdate.max += 1
            }
            levelValue.text = "${barToUpdate.max}"
            onChange(barToUpdate)
        }
        subtractButton.setOnClickListener {
            if (barToUpdate.max > 1)
            {
                barToUpdate.max -= 1
            }
            levelValue.text = "${barToUpdate.max}"
            onChange(barToUpdate)
        }
        doneButton.setOnClickListener {
            dismiss()
        }
    }

    fun setLevelTitle(title: String)
    {
        findViewById<TextView>(R.id.LevelSetText).text = title
    }
}