package mine.hunter.com.esdndtrack.Database

import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import mine.hunter.com.esdndtrack.Objects.InventoryItem
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.createString

class CharacterDB(context: Context): SQLiteOpenHelper(context, "Characters", null, 1)
{
	override fun onCreate(db: SQLiteDatabase?)
	{
		db?.execSQL(
		"create table if not exists Characters(id int, name text, hp int ,Level int,BaseAC int,Strength int,Dexterity int,Constitution int,Intelligence int,Wisdom int,Charisma int,Acrobatics int,AnimalHandling int,Arcana int,Athletics int,Deception int,History int,Insight int,Intimidation int,Investigation int,Medicine int,Nature int,Perception int,Performance int,Persuasion int,Religion int,SlightOfHand int,Stealth int,Survival int)"
		)
	}

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
	{}

}

class SpellsDB(context: Context): SQLiteOpenHelper(context, "Spells", null, 1)
{
	override fun onCreate(db: SQLiteDatabase?)
	{
		db?.execSQL(
				"create table if not exists spells ( name text, desc text, range text, level int, school text);"
		)

	}

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
	{}

	fun updateFromJson(resource: Resources)
	{
		writableDatabase.execSQL("delete from Spells")
		Gson().fromJson(resource.openRawResource(R.raw.spellsource).bufferedReader().readLines().createString(), Array<DBSpell>::class.java)
			.forEach { writableDatabase.execSQL("insert into Spells values ${it.toDBString()}") }
	}
}

class DBSpell(val name: String, val desc: String, val range: String, val level: String, val school: String)
{
	fun toDBString(): String
	{
		return "(\'${name.replace("\'","\'\'")}\',\'${desc.replace("\'","\'\'")}\',\'$range\',${
		if (level == "Cantrip") 0.toString() else Regex("^\\d+").find(level)?.groups?.get(0)?.value
		},\'$school\')"
	}
}

class InventoryDB(context: Context): SQLiteOpenHelper(context, "ItemHistory", null, 1)
{
	override fun onCreate(db: SQLiteDatabase?)
	{
		db?.execSQL("create table if not exists InventoryItem (name text, description text)")
	}

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){}

	fun addTo(item: InventoryItem)
	{

	}

	fun itemList(): Array<InventoryItem>
	{
		this.readableDatabase.query
	}
}
