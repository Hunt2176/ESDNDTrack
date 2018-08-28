package mine.hunter.com.esdndtrack.Utilities

import android.content.Context
import android.content.res.Resources
import mine.hunter.com.esdndtrack.R
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class StaticItems
{
	companion object
	{
		var staticMap = mutableMapOf<String, Any>()
		var spellList = arrayOf<ReadInSpell>()
		var customSpellList = arrayOf<ReadInSpell>()

		fun ReadInSpellList(resources: Resources)
		{
			spellList = GSONHelper().readInSpells(BufferedReader(InputStreamReader(resources.openRawResource(R.raw.spellsource))).readText())
		}

		fun ReadInCustomSpellList(context: Context)
		{
			customSpellList = GSONHelper().readInSpells(File(context.filesDir, "customspells.json").readText())
		}
	}
}