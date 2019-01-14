package mine.hunter.com.esdndtrack.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.*

class CreateSpellDialog(context: Context, val onDismiss: (Boolean) -> Unit): Dialog(context)
{

	companion object
	{
		fun editCustomSpell(context: Context, spell: ReadInSpell, onDismiss: (Boolean) -> Unit): CreateSpellDialog
		{
			return CreateSpellDialog(context, onDismiss).useAndReturn {
				it.isEditingCustom = true
				it.editingSpell = spell
				return@useAndReturn it
			}
		}
	}

	private lateinit var spellName: EditText
	private lateinit var spellDesc: EditText
	private lateinit var spellLevel: EditText
	private lateinit var spellRange: EditText
	private lateinit var fab: FloatingActionButton
	private var isEditingCustom = false
	private var editingSpell: ReadInSpell? = null

	private var ready = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.spell_creation)

		spellName = findViewById(R.id.spellC_name)
		spellDesc = findViewById(R.id.spellC_Desc)
		spellLevel = findViewById(R.id.spellC_Level)
		spellRange = findViewById(R.id.spellC_Range)
		fab = findViewById(R.id.spellC_FAB)

		editingSpell.ifNotNull { spell ->
			spellName.setText(spell.name)
			spellDesc.setText(spell.desc)
			spellLevel.setText("${spell.level.toIntOrZero()}")
			spellRange.setText("${spell.range.toIntOrZero()}")
		}

		val textfields = arrayOf(spellName, spellDesc, spellLevel, spellRange)

		val onTextChange: (Boolean) -> Unit = { notEmpty ->
			ready = (notEmpty && textfields.ifAllTrue { !it.text.isNullOrEmpty()})
			fab.isReadyForComplete(ready)
		}
		onTextChange.invoke(true)
		textfields.forEach { text -> text.addTextChangedListener(SpellCTextWatcher.setup(onTextChange)) }

		fab.setOnClickListener {
			if (ready)
			{
				val range: String = when (spellRange.text.toIntOrZero())
				{
					0 -> "Self"
					else -> "${spellRange.text.toIntOrZero()} feet"
				}
				if (isEditingCustom && !editingSpell.isNull())
				{
					StaticItems.RemoveCustomSpell(context, editingSpell!!)
				}
				ReadInSpell(
						spellName.text.toString(),
						spellDesc.text.toString(),
						range,
						"",
						spellLevel.text.toIntOrZero()
								.useAndReturnDifferent {
									return@useAndReturnDifferent when (it)
									{
										0 -> "Cantrip"
										else -> "$it"
									}
								},
						"",
						"",
						true
				).saveWithGson(context)
				StaticItems.ReadInCustomSpellList(context)
			}
			onDismiss(ready)
			this.dismiss()
		}
	}
}

class SpellCTextWatcher: TextWatcher
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