package mine.hunter.com.esdndtrack.Utilities

import android.content.Context
import android.content.res.Resources
import mine.hunter.com.esdndtrack.Objects.ReadInSpell
import mine.hunter.com.esdndtrack.R
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

class StaticItems
{
	companion object
	{
		private var spellList = arrayOf<ReadInSpell>()
		private var customSpellList = arrayOf<ReadInSpell>()
		private var listenTriggers = mutableMapOf<String, TriggerListener>()

		fun ReadInSpellList(resources: Resources)
		{
			spellList = GSONHelper().readInSpells(BufferedReader(InputStreamReader(resources.openRawResource(R.raw.spellsource))).readText())
		}

		fun ReadInCustomSpellList(context: Context)
		{
			try
			{
				customSpellList = GSONHelper().readInSpells(File(context.filesDir, "customspells.json").readText())
			}
			catch (error: FileNotFoundException)
			{
				File(context.filesDir, "customspells.json").use {
					it.createNewFile()
					it.writeText("[]")
				}
			}
		}

		fun MergeSpellLists(): Array<ReadInSpell>
		{
			return spellList.toMutableList()
					.useAndReturn { mutableList ->
						mutableList.addAll(0, customSpellList.toList())
						return@useAndReturn mutableList
					}
					.toTypedArray()
		}

		fun RemoveCustomSpell(context: Context, spell: ReadInSpell)
		{
			customSpellList.toMutableList().use {
				it.remove(spell)
				GSONHelper().writeToDisk(it.toTypedArray(), File(context.filesDir, "customspells.json"))
			}
			ReadInCustomSpellList(context)
		}

		fun getNotePair(index: Int, context: Context): Pair<String, String>?
		{
			val file = File(context.filesDir, "notes.json")
			if (file.exists())
			{
				val lines = GSONHelper().readInArray(KotlinFile.fromFile(file))
				if (lines != null && index < lines.count())
				{
					return Pair(lines[index][0], lines[index][1])

				}
				return null

			}
			file.createNewFile()
			file.writeText("[]")
			return null
		}

		fun writeNotePairs(context: Context, notePairs: Array<Pair<String, String>>)
		{
			GSONHelper().writeToDisk(notePairs.map { it.toArray() }.toTypedArray(), File(context.filesDir, "notes.json"))
		}

		fun storeTrigger(trigger: TriggerListener)
		{
			listenTriggers[trigger.id] = trigger
		}

		fun removeTrigger(id: String)
		{
			listenTriggers.remove(id)
		}

		fun executeTrigger(id: String)
		{
			listenTriggers[id]?.execute()
		}

	}
}

class TriggerListener(val id: String, private val trigger: () -> Unit)
{
	fun execute()
	{
		trigger.invoke()
	}
}