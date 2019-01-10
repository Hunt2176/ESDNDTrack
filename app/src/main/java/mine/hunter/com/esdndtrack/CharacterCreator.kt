package mine.hunter.com.esdndtrack

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import mine.hunter.com.esdndtrack.Utilities.*
import java.io.File
import java.lang.Exception
import java.util.concurrent.ThreadLocalRandom

class CharacterCreator: AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
		super.onCreate(savedInstanceState)
	    setContentView(R.layout.activity_character_creater)

	    val char: DNDCharacter =
	        if (intent.hasExtra("charID"))
		        DNDCharacter.fromID(this, (intent.extras!!.getInt("charID"))) ?: DNDCharacter()
	        else DNDCharacter()

	    findViewById<RecyclerView>(R.id.AttribRecycler)
		    .use { recyclerView ->

			    recyclerView.adapter = AttributeRecycler(this, char)
			    recyclerView.layoutManager = GridLayoutManager(this, 2)
			    recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		    }

	    findViewById<TextView>(R.id.ChrName).text = char.name
	    findViewById<TextView>(R.id.ChrHealth).text = char.hp.toString()

	    findViewById<FloatingActionButton>(R.id.OnCompleteCreateFloater)
		    .use { fab ->
			    fab
			        .setOnClickListener {
				        findViewById<TextInputEditText>(R.id.ChrName)
					        .use {
						        char.name = it.text.toString()
					        }
				        findViewById<TextInputEditText>(R.id.ChrHealth)
					        .use {
						        char.hp = it.text.toString().toIntOrZero()
					        }
				        char.writeToFile(this)
				        this.onBackPressed()
			        }
		    }
    }
}

class AttributeRecycler(val context: Context, val character: DNDCharacter): RecyclerView.Adapter<AttributeViewHolder>()
{
	override fun getItemCount(): Int = DNDCharacter.Attribute.attributeList.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder =
			AttributeViewHolder(LayoutInflater.from(context).inflate(R.layout.attribute_cell, parent, false))

	override fun onBindViewHolder(holder: AttributeViewHolder, position: Int)
	{
		val pos = position
		val attrib = DNDCharacter.Attribute.attributeList[pos]
		holder.attributeName.text = attrib.readableName()
		holder.attributeLevel.setSelection(character.getAttrib(attrib) - 1)
		holder.attributeLevel.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
		{
			override fun onNothingSelected(parent: AdapterView<*>?){}
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
			{
				character.setAttrib(attrib, (1..20).toList()[position])
			}
		}
	}
}

class AttributeViewHolder(view: View): RecyclerView.ViewHolder(view)
{
	val attributeName: TextView = view.findViewById(R.id.AttribName)
	val attributeLevel: Spinner =
			view.findViewById<Spinner>(R.id.AttribLvlSpinner)
			.useAndReturn { spinner ->
				spinner.adapter = ArrayAdapter<Int>(view.context, android.R.layout.simple_spinner_item, (1..20).toList())
				return@useAndReturn spinner
			}
}



class DNDCharacter()
{
	private val attributes = mutableMapOf<Attribute, Int>()

	var id = ThreadLocalRandom.current().nextInt()
	var name = ""
	var hp = 1

	init
	{
		Attribute.forEach { attributes[it] = 1 }
	}

	constructor(map: Map<String, Any>) : this()
	{
		map["id"].ifNotNull { id = (it as Double).toInt() }
		map["name"].ifNotNull { name = it.toString() }
		map["hp"].ifNotNull { hp = (it as Double).toInt() }
		(map["attributes"] as? Map<String, Any>)
			.ifNotNull {  attribs ->
				attribs.keys.forEach {
					val attrib = Attribute.fromString(it)
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
		Level,
		BaseAC,
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

		companion object
		{
			val attributeList: Array<DNDCharacter.Attribute>
				get() = Attribute.values()

			private val attributeStrings: List<String>
				get() = attributeList.toList().map { it.toString() }

			fun fromString(value: String): Attribute
			{
				return (attributeList[attributeStrings.indexOf(value)])
			}

			fun forEach(completion: (Attribute) -> Unit)
			{
				Attribute.attributeList.iterator().forEach { completion(it) }
			}
		}
	}
}
