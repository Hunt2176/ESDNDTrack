package mine.hunter.com.esdndtrack.Activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import mine.hunter.com.esdndtrack.Dialogs.LevelSelectionDialog
import mine.hunter.com.esdndtrack.Dialogs.ProficiencyDialog
import mine.hunter.com.esdndtrack.Dialogs.SpellCTextWatcher
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
			    {
				    this.currentFocus.ifNotNull {  view ->
					    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
						    .use { imm ->
							    imm.hideSoftInputFromWindow(view.windowToken, 0)
						    }
				    }
			    }
			    recyclerView.layoutManager = GridLayoutManager(this, 2)
			    recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		    }

	    findViewById<RecyclerView>(R.id.ProfRecycler)
		    .use { recycler ->
			    recycler.adapter = ProficiencyDialog.ProficiencyAdapter(this, char)
					    .borrow { adapter ->
						    adapter.updateFromChar()
						    findViewById<Button>(R.id.add_prof_btn)
							    .setOnClickListener { adapter.addCell() }
					    }
			    recycler.layoutManager = GridLayoutManager(this, 1)
			    recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
		    }

	    findViewById<EditText>(R.id.prof_bonus_text)
		    .use { editText ->
			    editText.setText(char.proficiencyBonus.toString())
			    editText.hint = "Proficiency Bonus"
			    editText.addTextChangedListener(SpellCTextWatcher.setup {
				    if (!it) return@setup
				    editText.text.toString().toIntOrNull()
					    .ifNotNull {
						    char.proficiencyBonus = it
					    }
			    })
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

class AttributeRecycler(val context: Context, val character: DNDCharacter, val onClick: () -> Unit): RecyclerView.Adapter<AttributeViewHolder>()
{
	override fun getItemCount(): Int = DNDCharacter.Attribute.coreAttributes.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder =
			AttributeViewHolder(LayoutInflater.from(context).inflate(R.layout.attribute_cell, parent, false))

	override fun onBindViewHolder(holder: AttributeViewHolder, position: Int)
	{
		val attrib = DNDCharacter.Attribute.coreAttributes[position]
		holder.attributeName.text = attrib.readableName()
		holder.attributeLevel.setSelection(character.getAttrib(attrib) - 1)
		holder.attributeLevel
			.setOnItemSelectedListener { it -> character.setAttrib(attrib, it + 1) }
	}
}

class AttributeViewHolder(view: View): RecyclerView.ViewHolder(view)
{
	val attributeName: TextView = view.findViewById(R.id.AttribName)
	val attributeLevel =
			view.findViewById<Spinner>(R.id.cell_attrib_lvl_spinner)
				.borrow { spinner ->
					spinner.adapter = ArrayAdapter<Int>(view.context, android.R.layout.simple_spinner_item, Array(20){it + 1})
				}
}
