package mine.hunter.com.esdndtrack

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import mine.hunter.com.esdndtrack.Utilities.*

class CreateSpellDialog(context: Context, val onDismiss: (Boolean) -> Unit): Dialog(context)
{

	private var ready = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.spell_creation)

		val spellName = findViewById<EditText>(R.id.spellC_name)
		val spellDesc = findViewById<EditText>(R.id.spellC_Desc)
		val spellLevel = findViewById<EditText>(R.id.spellC_Level)
		val spellRange = findViewById<EditText>(R.id.spellC_Range)
		val fab = findViewById<FloatingActionButton>(R.id.spellC_FAB)

		val textfields = arrayOf(spellName, spellDesc, spellLevel, spellRange)

		val onTextChange: (Boolean) -> Unit = { notEmpty ->
			ready = (notEmpty && textfields.ifAllTrue { !it.text.isNullOrEmpty()})
			fab.isReadyForComplete(ready)
		}
		textfields.forEach { text -> text.addTextChangedListener(SpellCTextWatcher.setup(onTextChange)) }

		fab.setOnClickListener {
			if (ready)
			{
				var range: String = when (spellRange.text.toIntOrZero())
				{
					0 -> "self"
					else -> "${spellRange.text.toIntOrZero()} feet"
				}
				ReadInSpell(
						spellName.text.toString(),
						spellDesc.text.toString(),
						range,
						"",
						"${spellLevel.text.toIntOrZero()}",
						"",
						"",
						true
						).saveWithGson(context)
			}
			onDismiss(ready)
			this.dismiss()
		}
	}
}

private class SpellCTextWatcher: TextWatcher
{
	var onTextIsNotEmpty: ((Boolean) -> Unit)? = null

	override fun afterTextChanged(s: Editable?)
	{

	}

	override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
	{

	}

	override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
	{
		onTextIsNotEmpty?.invoke(!s.isNullOrEmpty())
	}

	companion object
	{
		fun setup(onUpdatedText: (Boolean) -> Unit): SpellCTextWatcher
		{
			val watcher = SpellCTextWatcher()
			watcher.onTextIsNotEmpty = onUpdatedText
			return watcher
		}
	}
}

fun ReadInSpell.saveWithGson(context: Context)
{
	GSONHelper().addCustomSpell(context, this)
}