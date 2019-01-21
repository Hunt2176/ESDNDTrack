package mine.hunter.com.esdndtrack.Objects

import android.content.Context
import mine.hunter.com.esdndtrack.Utilities.*
import java.io.File
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.roundToInt

class DNDCharacter()
{
	val attributes = mutableMapOf<Attribute, Int>()
	val proficiencies = ArrayList<Attribute>()

	var id = ThreadLocalRandom.current().nextInt()
	var name = ""
	var hp = 1
	var currenthp = hp
	var proficiencyBonus = 0
	var inventory = arrayListOf<InventoryItem>()

	init
	{
		Attribute.forEach { attributes[it] = 1 }
	}

	constructor(map: Map<String, Any>) : this()
	{
		map["id"].ifNotNull { id = (it as Double).toInt() }
		map["name"].ifNotNull { name = it.toString() }
		map["hp"].ifNotNull { hp = (it as Double).toInt() }
		map["proficiencyBonus"].ifNotNull { proficiencyBonus = (it as Double).toInt() }
		map["currenthp"].ifNotNull { currenthp = (it as Double).toInt() }
		map["inventory"].ifNotNull { (it as? Array<InventoryItem>)?.toCollection(inventory) }
		(map["proficiencies"] as? ArrayList<String>)
			.ifNotNull { profs ->
				profs.forEach {
					Attribute.fromString(it).ifNotNull { proficiencies.add(it) }
				}
			}
		(map["attributes"] as? Map<String, Any>)
				.ifNotNull {  attribs ->
					attribs.keys.forEach {
						val attrib = Attribute.fromString(it) ?: return@forEach
						attributes[attrib] = (attribs[it] as Double).toInt()
					}
				}
	}

	fun getAttrib(attrib: Attribute): Int
	{
		return attributes[attrib]!!
	}

	fun setAttrib(attrib: Attribute, level: Int)
	{
		attributes[attrib] = level
	}

	fun getAttributes(): Array<Pair<Attribute, Int>> = Attribute.attributes.map { Pair(it, getAttrib(it)) }.toTypedArray()

	fun getCoreAttributes(): Array<Pair<Attribute, Int>> = Attribute.coreAttributes.map { Pair(it, getAttrib(it)) }.toTypedArray()

	fun getProficiencyAttrib(attrib: Attribute): Int
	{
		return if (proficiencies.contains(attrib)) return Attribute.advCalculator(getAttrib(attrib.matchingAttribute())) + proficiencyBonus
		else Attribute.advCalculator(getAttrib(attrib.matchingAttribute()))
	}

	/**
	 * Updates the character in the characters.json file or writes new if not existent
	 */
	fun writeToFile(context: Context)
	{
		GSONHelper()
				.use { gson ->
					gson.writeToDisk(DNDCharacter.readFromJson(context).toMutableList()
							.useAndReturn { list ->
								if (
										list.getWhere({it.id == this.id}, { char ->
											val index = list.indexOf(char)
											list.removeAt(index)
											list.add(index, this)
										})
								) else {list.add(this)}
								return@useAndReturn list
							}.toTypedArray(), File(context.filesDir, "Characters.json"))
				}
		println(dbString())
	}

	private fun dbString(): String
	{
		var toReturn = "($id,\'${name.replace("\'","\'\'")}\',$hp"
		attributes.forEach { _, value ->toReturn +=  ",$value" }
		return ("$toReturn)")
	}

	companion object
	{
		fun readFromJson(context: Context): Array<DNDCharacter>
		{
			val list = arrayListOf<DNDCharacter>()
			GSONHelper()
					.use {gson ->
						(gson.readInCharacterArray(File(context.filesDir, "Characters.json")) ?: arrayOf())
								.use {
									it.forEach { list.add(DNDCharacter(it as Map<String, Any>)) }
								}
					}
			return list.toTypedArray()
		}

		fun fromID(context: Context, id: Int): DNDCharacter?
		{
			var character: DNDCharacter? = null
			this.readFromJson(context).toList().getWhere({it.id == id}){
				character = it
			}
			return character
		}


	}

	enum class Attribute
	{
		Strength,
		Dexterity,
		Constitution,
		Intelligence,
		Wisdom,
		Charisma,
		Acrobatics,
		AnimalHandling,
		Arcana,
		Athletics,
		Deception,
		History,
		Insight,
		Intimidation,
		Investigation,
		Medicine,
		Nature,
		Perception,
		Performance,
		Persuasion,
		Religion,
		SlightOfHand,
		Stealth,
		Survival
		;

		override fun toString(): String = this.name

		fun readableName(): String
		{
			return this.name.split(Regex("(?=[A-Z])")).toList().createString(true).trim()
		}

		fun levelFor(character: DNDCharacter): Int = character.getAttrib(this)

		fun setLevelFor(character: DNDCharacter, newLevel: Int) = character.setAttrib(this, newLevel)

		fun matchingAttribute(): Attribute
		{
			return when (this)
			{
				Strength -> Strength
				Dexterity -> Dexterity
				Constitution -> Constitution
				Intelligence -> Intelligence
				Wisdom -> Wisdom
				Charisma -> Charisma
				Acrobatics -> Dexterity
				AnimalHandling -> Wisdom
				Arcana -> Intelligence
				Athletics -> Strength
				Deception -> Charisma
				History -> Intelligence
				Insight -> Wisdom
				Intimidation -> Charisma
				Investigation -> Intelligence
				Medicine -> Wisdom
				Nature -> Intelligence
				Perception -> Wisdom
				Performance -> Charisma
				Persuasion -> Charisma
				Religion -> Intelligence
				SlightOfHand -> Dexterity
				Stealth -> Dexterity
				Survival -> Wisdom
			}
		}



		companion object
		{
			val abilitySkillRange: IntRange get() = (RangeBottom..RangeTop)

			const val RangeTop = 20
			const val RangeBottom = 1

			val attributes: Array<DNDCharacter.Attribute>
				get() = Attribute.values()

			val coreAttributes: List<Attribute>
				get() = listOf(Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma)

			private val attributeStrings: List<String>
				get() = attributes.toList().map { it.toString() }

			fun fromString(value: String): Attribute?
			{
				if (attributeStrings.indexOf(value) < 0) return null
				return (attributes[attributeStrings.indexOf(value)])
			}

			fun forEach(completion: (Attribute) -> Unit)
			{
				Attribute.attributes.iterator().forEach { completion(it) }
			}

			fun forEachIndexed(completion: (Int, Attribute) -> Unit)
			{
				Attribute.attributes.toList().forEachIndexed(completion)
			}

			fun advCalculator(level: Int): Int
			{
				var x: Double = (level - 10.0)/2.0
				return if ((x - x.toInt()) != 0.0 && x != 0.0)
				{
					x.roundToInt() - 1
				}
				else
				{
					x.roundToInt()
				}
			}
		}
	}
}