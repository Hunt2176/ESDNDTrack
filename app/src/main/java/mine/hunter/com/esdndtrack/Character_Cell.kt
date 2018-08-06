package mine.hunter.com.esdndtrack

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class Character_Cell : AppCompatActivity()
{

    lateinit var healthBar: ProgressBar
    lateinit var magicBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.character_cell)

        val healthText = findViewById<TextView>(R.id.HealthText)
        val magicText = findViewById<TextView>(R.id.MagicText)

        healthBar = findViewById(R.id.HealthBar)
        magicBar = findViewById(R.id.MagicBar)
        val healthAdd = findViewById<Button>(R.id.HealthAdd)
        val healthSubtract = findViewById<Button>(R.id.HealthSubtract)
        val magicAdd = findViewById<Button>(R.id.MagicAdd)
        val magicSubtract = findViewById<Button>(R.id.MagicSubtract)

        ReadFromStorage()

        healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
        magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"

        healthAdd.setOnClickListener {
            ChangeProgressBar(healthBar, 1)
            healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
            SaveToStorage(SavableItem.current_hp, healthBar.progress)
        }
        healthSubtract.setOnClickListener {
            ChangeProgressBar(healthBar, -1)
            healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
            SaveToStorage(SavableItem.current_hp, healthBar.progress)
        }
        magicAdd.setOnClickListener {
            ChangeProgressBar(magicBar, 1)
            magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
            SaveToStorage(SavableItem.current_magic, magicBar.progress)
        }
        magicSubtract.setOnClickListener {
            ChangeProgressBar(magicBar, -1)
            magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
            SaveToStorage(SavableItem.current_magic, magicBar.progress)
        }

        healthText.setOnLongClickListener {
            var SetLevelDialog = SetLevelDialog(this, healthBar, { bar ->
                CopyBarDetails(healthBar, bar, "Health", healthText)
                SaveToStorage(SavableItem.max_hp, healthBar.max)
            })
            SetLevelDialog.show()
            SetLevelDialog.SetLevelTitle("Set Max Health")
            SetLevelDialog.window.setLayout((6 * resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            true
        }

        magicText.setOnLongClickListener {
            val SetLevelDialog = SetLevelDialog(this, magicBar, { bar ->
                CopyBarDetails(magicBar, bar, "Magic", magicText)
                SaveToStorage(SavableItem.max_magic, magicBar.max)
            })
            SetLevelDialog.show()
            SetLevelDialog.SetLevelTitle("Set Max Magic")
            SetLevelDialog.window.setLayout((6 * resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            true
        }

    }

    fun CopyBarDetails(barToUpdate: ProgressBar, barToUpdateFrom: ProgressBar, barName: String, textView: TextView)
    {
        barToUpdate.max = barToUpdateFrom.max
        barToUpdate.progress = barToUpdateFrom.max
        textView.text = "${barName}: ${barToUpdateFrom.max}/${barToUpdateFrom.max}"
    }

    fun ChangeProgressBar(progressBar: ProgressBar, amount: Int)
    {
        if (!(progressBar.progress + amount > progressBar.max || progressBar.progress + amount < 0))
        {
            progressBar.progress += amount
        }
    }

    fun SaveToStorage(prefrenceTitle: SavableItem, valueToSave: Int)
    {
        val preferences = getSharedPreferences("character", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        when (prefrenceTitle)
        {
            SavableItem.max_hp -> editor.putInt("max_hp", valueToSave)
            SavableItem.current_hp -> editor.putInt("current_hp", valueToSave)
            SavableItem.max_magic -> editor.putInt("max_magic", valueToSave)
            SavableItem.current_magic -> editor.putInt("current_magic", valueToSave)
        }
        editor.apply()
    }

    fun ReadFromStorage()
    {
        healthBar.max = 1
        healthBar.progress = 1
        magicBar.max = 1
        magicBar.progress = 1

        val preferences = getSharedPreferences("character", Context.MODE_PRIVATE)

        arrayOf("max_hp", "current_hp", "max_magic", "current_magic")
                .forEach { item ->
                    when (item)
                    {
                        "max_hp" -> healthBar.max = preferences.getInt(item, 1)
                        "current_hp" -> healthBar.progress = preferences.getInt(item, 1)
                        "max_magic" -> magicBar.max = preferences.getInt(item, 1)
                        "current_magic" -> magicBar.progress = preferences.getInt(item, 1)
                    }
                }
    }

    enum class SavableItem
    {
        max_hp,
        current_hp,
        max_magic,
        current_magic
    }

    class SetLevelDialog(context: Context, val barToUpdate: ProgressBar, val onChange: (ProgressBar) -> Unit) : Dialog(context)
    {

        override fun onCreate(savedInstanceState: Bundle?)
        {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_level_set)

            val AddButton = findViewById<Button>(R.id.AddToLevel)
            val SubtractButton = findViewById<Button>(R.id.SubtractFromLevel)
            val LevelValue = findViewById<TextView>(R.id.LevelText)
            val DoneButton = findViewById<TextView>(R.id.LevelDismiss)

            LevelValue.text = "${barToUpdate.max}"

            AddButton.setOnClickListener {
                if (barToUpdate.max > 0)
                {
                    barToUpdate.max += 1
                }
                LevelValue.text = "${barToUpdate.max}"
                onChange(barToUpdate)
            }
            SubtractButton.setOnClickListener {
                if (barToUpdate.max > 1)
                {
                    barToUpdate.max -= 1
                }
                LevelValue.text = "${barToUpdate.max}"
                onChange(barToUpdate)
            }
            DoneButton.setOnClickListener {
                dismiss()
            }
        }

        fun SetLevelTitle(title: String)
        {
            findViewById<TextView>(R.id.LevelSetText).text = title
        }
    }
}
