package mine.hunter.com.esdndtrack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*

class Recycler : AppCompatActivity()
{
    var fabIsClicked = false
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)

        val recycler = findViewById<RecyclerView>(R.id.CharacterList)
        recycler.adapter = ArrayAdapter(this)
        recycler.layoutManager = GridLayoutManager(this, 1)

        val fab = findViewById<FloatingActionButton>(R.id.AddCharacterFab)
        fab.setOnClickListener {
            fabIsClicked = !fabIsClicked
            if (fabIsClicked)
            {
                fab.setImageResource(R.drawable.cancel)
                registerForContextMenu(fab)
                val menu = PopupMenu(this, fab)
                menu.menuInflater.inflate(R.menu.create_character_fab_menu, menu.menu)
                menu.show()

                menu.setOnMenuItemClickListener {
                    when (it.itemId){
                        R.id.MENUCreateCharacter -> startActivity(Intent(this, CharacterCreater::class.java))
                        R.id.MENULoadCharacter ->
                        {
                           CreateCharacterMenu(fab, getSharedPreferences(SavableItem.character_list.getStringKey(), 0)).show()
                        }
                    }
                    true
                }

                menu.setOnDismissListener {
                    fabIsClicked = false
                    fab.setImageResource(R.drawable.add_icon)
                }
            }
        }
    }

    class ArrayAdapter(val context: Context): RecyclerView.Adapter<CharacterViewRecycle>()
    {
        var characters = arrayListOf<String>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewRecycle
        {
            val view = LayoutInflater.from(context).inflate(R.layout.character_cell, parent, false)
            return CharacterViewRecycle(view, context)
        }

        override fun getItemCount(): Int
        {
            return characters.size
        }

        override fun onBindViewHolder(holder: CharacterViewRecycle, position: Int)
        {
            holder.ReadFromStorage(characters[position])
        }
    }

    class CharacterViewRecycle(view: View, val context: Context): RecyclerView.ViewHolder(view)
    {
        val characterName = view.findViewById<TextView>(R.id.CharacterName)
        val healthBar: ProgressBar
        val magicBar: ProgressBar
        val magicText: TextView = view.findViewById(R.id.MagicText)
        val healthText: TextView = view.findViewById(R.id.HealthText)
        var name = ""

        init
        {
            healthBar = view.findViewById(R.id.HealthBar)
            magicBar = view.findViewById(R.id.MagicBar)

            val healthAdd = view.findViewById<Button>(R.id.HealthAdd)
            val healthSubtract = view.findViewById<Button>(R.id.HealthSubtract)
            val magicAdd = view.findViewById<Button>(R.id.MagicAdd)
            val magicSubtract = view.findViewById<Button>(R.id.MagicSubtract)

            healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
            magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"

            healthAdd.setOnClickListener {
                ChangeProgressBar(healthBar, 1)
                healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
                SaveToStorage(SavableItem.current_hp, healthBar.progress)
            }
            healthSubtract.setOnClickListener {
                ChangeProgressBar(healthBar, -1)
                healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
                SaveToStorage(SavableItem.current_hp, healthBar.progress)
            }
            magicAdd.setOnClickListener {
                ChangeProgressBar(magicBar, 1)
                magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
                SaveToStorage(SavableItem.current_magic, magicBar.progress)
            }
            magicSubtract.setOnClickListener {
                ChangeProgressBar(magicBar, -1)
                magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
                SaveToStorage(SavableItem.current_magic, magicBar.progress)
            }

            healthText.setOnLongClickListener {
                var SetLevelDialog = Character_Cell.SetLevelDialog(context, healthBar) { bar ->
                    CopyBarDetails(healthBar, bar, "Health", healthText)
                    SaveToStorage(SavableItem.max_hp, healthBar.max)
                    SaveToStorage(SavableItem.current_hp, healthBar.progress)
                }
                SetLevelDialog.show()
                SetLevelDialog.SetLevelTitle("Set Max Health $name")
                SetLevelDialog.window.setLayout((6 * view.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                true
            }

            magicText.setOnLongClickListener {
                val SetLevelDialog = Character_Cell.SetLevelDialog(context, magicBar) { bar ->
                    CopyBarDetails(magicBar, bar, "Magic", magicText)
                    SaveToStorage(SavableItem.max_magic, magicBar.max)
                    SaveToStorage(SavableItem.current_magic, magicBar.progress)
                }
                SetLevelDialog.show()
                SetLevelDialog.SetLevelTitle("Set Max Magic for $name")
                SetLevelDialog.window.setLayout((6 * view.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                true
            }
        }

        fun CopyBarDetails(barToUpdate: ProgressBar, barToUpdateFrom: ProgressBar, barName: String, textView: TextView)
        {
            barToUpdate.max = barToUpdateFrom.max
            barToUpdate.progress = barToUpdateFrom.max
            textView.text = "${barName}: ${barToUpdateFrom.max}/${barToUpdateFrom.max}"
        }

        fun ChangeProgressBar(progressBar: ProgressBar, amount: Int)
        {
            if (!(progressBar.progress + amount > progressBar.max || progressBar.progress + amount < 0))
            {
                progressBar.progress += amount
            }
        }

        fun SaveToStorage(preferenceTitle: SavableItem, valueToSave: Int)
        {
            val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            when (preferenceTitle)
            {
                SavableItem.max_hp -> editor.putInt("max_hp", valueToSave)
                SavableItem.current_hp -> editor.putInt("current_hp", valueToSave)
                SavableItem.max_magic -> editor.putInt("max_magic", valueToSave)
                SavableItem.current_magic -> editor.putInt("current_magic", valueToSave)
            }
            editor.apply()
        }

        fun ReadFromStorage(name: String)
        {
            this.name = name
            characterName.text = name
            val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

            arrayOf("max_hp", "current_hp", "max_magic", "current_magic")
                    .forEach { item ->
                        when (item)
                        {
                            "max_hp" -> healthBar.max = preferences.getInt(item, 1)
                            "current_hp" -> healthBar.progress = preferences.getInt(item, 1)
                            "max_magic" -> magicBar.max = preferences.getInt(item, 1)
                            "current_magic" -> magicBar.progress = preferences.getInt(item, 1)
                        }
                    }
            healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
            magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
        }
    }
    fun CreateCharacterMenu(view: View, sharedPreferences: SharedPreferences): PopupMenu
    {
        val menu = PopupMenu(this, view)
        val characters = sharedPreferences.getStringSet("names", setOf<String>())
        characters.forEachIndexed { index, s ->
            menu.menu.add(s)
        }
        return menu
    }
}
enum class SavableItem
{
    max_hp,
    current_hp,
    character_ac,
    max_magic,
    current_magic,
    magicModifier,
    character_list,
    uses_magic;

    fun getStringKey(): String {
        return when (this)
        {
            max_hp -> "max_hp"
            current_hp -> "current_hp"
            max_magic -> "max_magic"
            current_magic -> "current_magic"
            character_ac -> "character_ac"
            magicModifier -> "magic_modifier"
            character_list -> "character_list"
            uses_magic -> "uses_magic"
        }
    }
}
