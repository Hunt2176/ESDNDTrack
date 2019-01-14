package mine.hunter.com.esdndtrack.Objects

import mine.hunter.com.esdndtrack.Utilities.GSONHelper
import java.io.File


class InventoryItem(val name: String, val details: String, amount: Int = 1)
{
	fun dbString(): String
	{
		return "(${name.replace("\'", "\'\'")}, ${details.replace("\'", "\'\'")})"
	}

	companion object
	{
		fun writeToFile(vararg items: InventoryItem, writeFile: File)
		{
			GSONHelper().writeToDisk(items, writeFile)
		}
	}
}


