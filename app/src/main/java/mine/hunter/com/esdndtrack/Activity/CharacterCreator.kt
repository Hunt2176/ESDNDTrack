package mine.hunter.com.esdndtrack.Activity

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
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.*

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
						        char.currenthp = char.hp
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