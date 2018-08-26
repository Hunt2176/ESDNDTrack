package mine.hunter.com.esdndtrack

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import mine.hunter.com.esdndtrack.Utilities.SavableItem
import mine.hunter.com.esdndtrack.Utilities.toIntOrZero

class CharacterCreater(context: Context, val onDismiss: (Boolean)->Unit) : Dialog(context)
{

    var ready = false
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

	    floatingComplete.isReadyForComplete(ready)

       nameInput.addTextChangedListener(object: TextWatcher {
           override fun afterTextChanged(s: Editable?)
           {

           }

           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
           {
           }

           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
           {
               floatingComplete.isReadyForComplete(!s.isNullOrEmpty())
           }

       })

        useManaTickBox.setOnCheckedChangeListener { _, isChecked ->
            magicPointInput.isEnabled = isChecked
            spellModifierInput.isEnabled = isChecked
        }

        floatingComplete.setOnClickListener {

	        if (!ready)
	        {
		        onDismiss(false)
		        dismiss()
		        return@setOnClickListener
	        }

            if (nameInput.text == "character_list") {
                Toast.makeText(context, "This is an invalid Character name", Toast.LENGTH_LONG).show()
            }
            else {
                val characterName = nameInput.text.toString()
                val charList = context.getSharedPreferences(SavableItem.character_list.getStringKey(), Context.MODE_PRIVATE)
                val charListEditor = charList.edit()
                var characterList = mutableSetOf<String>()
                charList.getStringSet("names", mutableSetOf()).forEach { value ->
                    characterList.add(value)
                }
                if (!characterList.contains(characterName)) characterList.add(characterName)

                charListEditor.putStringSet("names", characterList)
                charListEditor.apply()

                val prefs = context.getSharedPreferences(characterName, Context.MODE_PRIVATE)
                val editor = prefs.edit()

                editor.putInt(SavableItem.max_hp.getStringKey(), healthInput.text.toIntOrZero())
                editor.putInt(SavableItem.max_magic.getStringKey(), magicPointInput.text.toIntOrZero())
                editor.putInt(SavableItem.magicModifier.getStringKey(), spellModifierInput.text.toIntOrZero())
                editor.putInt(SavableItem.character_ac.getStringKey(), acInput.text.toIntOrZero())
                editor.putBoolean(SavableItem.uses_magic.getStringKey(), useManaTickBox.isChecked)
                editor.apply()

                Toast.makeText(context, "Saved $characterName to storage", Toast.LENGTH_LONG).show()
	            onDismiss(true)
                onBackPressed()
            }
        }
    }

    fun FloatingActionButton.isReadyForComplete(isEnabled: Boolean)
    {
        if (isEnabled){
            this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            this.setImageResource(R.drawable.check_mark)
            ready = true
        }
        else {
            this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            this.setImageResource(R.drawable.cancel)
            ready = false
        }
    }
}
