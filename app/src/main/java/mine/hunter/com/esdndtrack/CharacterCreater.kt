package mine.hunter.com.esdndtrack

import android.content.Context
import android.content.res.ColorStateList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast

class CharacterCreater : AppCompatActivity()
{


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creater)

        val nameInput = findViewById<TextView>(R.id.NameCreateInput)
        val healthInput = findViewById<TextView>(R.id.HealthCreateInput)
        val magicPointInput = findViewById<TextView>(R.id.ManaCreateInput)
        val spellModifierInput = findViewById<TextView>(R.id.SpellBonusCreateInput)
        val useManaTickBox = findViewById<CheckBox>(R.id.UseManaCreateBox)
        val acInput = findViewById<TextView>(R.id.ACCreateInput)
        val floatingComplete = findViewById<FloatingActionButton>(R.id.OnCompleteCreateFloater)

        magicPointInput.isEnabled = useManaTickBox.isChecked
        spellModifierInput.isEnabled = useManaTickBox.isChecked
        floatingComplete.setFabEnabled(!nameInput.text.isNullOrEmpty())

       nameInput.addTextChangedListener(object: TextWatcher {
           override fun afterTextChanged(s: Editable?)
           {

           }

           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
           {
           }

           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
           {
               floatingComplete.setFabEnabled(!s.isNullOrEmpty())
           }

       })

        useManaTickBox.setOnCheckedChangeListener { _, isChecked ->
            magicPointInput.isEnabled = isChecked
            spellModifierInput.isEnabled = isChecked
        }

        floatingComplete.setOnClickListener {
            if (nameInput.text == "character_list") {
                Toast.makeText(this, "This is an invalid Character name", Toast.LENGTH_LONG).show()
            }
            else {
                val characterName = nameInput.text.toString()
                val charList = getSharedPreferences(SavableItem.character_list.getStringKey(), Context.MODE_PRIVATE)
                val charListEditor = charList.edit()
                var characterList = mutableSetOf<String>()
                charList.getStringSet("names", mutableSetOf()).forEach { value ->
                    characterList.add(value)
                }
                if (!characterList.contains(characterName)) characterList.add(characterName)

                charListEditor.putStringSet("names", characterList)
                charListEditor.apply()

                val prefs = getSharedPreferences(characterName, Context.MODE_PRIVATE)
                val editor = prefs.edit()

                editor.putInt(SavableItem.max_hp.getStringKey(), healthInput.text.toIntOrZero())
                editor.putInt(SavableItem.max_magic.getStringKey(), magicPointInput.text.toIntOrZero())
                editor.putInt(SavableItem.magicModifier.getStringKey(), spellModifierInput.text.toIntOrZero())
                editor.putInt(SavableItem.character_ac.getStringKey(), acInput.text.toIntOrZero())
                editor.putBoolean(SavableItem.uses_magic.getStringKey(), useManaTickBox.isChecked)
                editor.apply()

                Toast.makeText(this, "Saved $characterName to storage", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
    }

    fun FloatingActionButton.setFabEnabled(isEnabled: Boolean)
    {
        this.isEnabled = isEnabled
        if (isEnabled){
            this.backgroundTintList = ColorStateList.valueOf(getColor(android.R.color.holo_green_dark))
            this.setImageResource(R.drawable.check_mark)
        }
        else {
            this.backgroundTintList = ColorStateList.valueOf(getColor(android.R.color.holo_red_dark))
            this.setImageResource(R.drawable.cancel)
        }
    }
}

fun CharSequence.toIntOrZero():Int {
    return this.toString().toIntOrZero()
}

fun String.toIntOrZero(): Int
{
    var tempString = ""
    this.forEach {
        if (it.isDigit()) tempString += it
    }
    return if (tempString.isEmpty()) 0 else tempString.toInt()
}
