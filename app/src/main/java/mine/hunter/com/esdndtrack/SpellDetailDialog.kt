package mine.hunter.com.esdndtrack

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import mine.hunter.com.esdndtrack.Utilities.ReadInSpell

class SpellDetailDialog(context: Context, spell: ReadInSpell): Dialog(context)
{
	lateinit var spellNameView: TextView
	lateinit var spellDescView: TextView
	lateinit var spellLevelView: TextView
	lateinit var spellRangeView: TextView

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.spell_detail_popup)
		spellNameView = findViewById(R.id.SpellNameDetail)
		spellDescView = findViewById(R.id.SpellDescDetail)
		spellLevelView = findViewById(R.id.SpellLevelDetail)
		spellRangeView = findViewById(R.id.SpellRangeDetail)

	}
}