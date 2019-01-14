//package mine.hunter.com.esdndtrack
//
//import android.app.Dialog
//import android.content.Context
//import android.content.SharedPreferences
//import android.net.Uri
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import android.support.constraint.ConstraintLayout
//import android.support.v4.content.ContextCompat
//import android.support.v7.widget.RecyclerView
//import android.view.*
//import android.widget.*
//import kotlinx.android.synthetic.main.main_activity.*
//import mine.hunter.com.esdndtrack.Fragments.Dice
//
//class Recycler : AppCompatActivity(), Dice.OnFragmentInteractionListener {
//    private var fabIsClicked = false
//	override fun onFragmentInteraction(uri: Uri)
//	{
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onCreate(savedInstanceState: Bundle?) {
//
//	    setSupportActionBar(toolbar)
//	    supportActionBar?.title = "Elder Scrolls DND"
//
//	    window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
//
//
//
////        val recycler = findViewById<RecyclerView>(R.id.CharacterList)
////        recycler.adapter = CharacterViewAdapter(this)
////        recycler.layoutManager = GridLayoutManager(this, 1)
////        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
////
////        val fab = findViewById<FloatingActionButton>(R.id.AddCharacterFab)
////        fab.setOnClickListener {
////            fabIsClicked = !fabIsClicked
////            if (fabIsClicked) {
////                fab.setImageResource(R.drawable.cancel)
////                registerForContextMenu(fab)
////                val menu = PopupMenu(this, fab)
////                menu.menuInflater.inflate(R.menu.create_character_fab_menu, menu.menu)
////                menu.show()
////
////                menu.setOnMenuItemClickListener {
////                    when (it.itemId) {
////                        R.id.MENUCreateCharacter -> startActivity(Intent(this, CharacterCreator::class.java))
////                        R.id.MENULoadCharacter -> {
////                            val characterMenu = CreateCharacterMenu(fab, getSharedPreferences(SavableItem.character_list.getStringKey(), 0))
////                            characterMenu.show()
////                            characterMenu.setOnMenuItemClickListener {
////                                (recycler.adapter as CharacterViewAdapter).characters.add(it.title.toString())
////                                (recycler.adapter as CharacterViewAdapter).notifyDataSetChanged()
////                                true
////                            }
////                        }
////                    }
////                    true
////                }
////
////                menu.setOnDismissListener {
////                    fabIsClicked = false
////                    fab.setImageResource(R.drawable.add_icon)
////                }
////            }
////        }
//    }
//
//    class CharacterViewAdapter(val context: Context) : RecyclerView.Adapter<CharacterViewHolder>() {
//        var characters = arrayListOf<String>()
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
//            val view = LayoutInflater.from(context).inflate(R.layout.character_cell, parent, false)
//            return CharacterViewHolder(view, context)
//        }
//
//        override fun getItemCount(): Int {
//            return characters.size
//        }
//
//        override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
//            holder.readFromStorage(characters[position])
//        }
//    }
//
//    class CharacterViewHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {
//        val characterName = view.findViewById<TextView>(R.id.CharacterName)
//        val healthBar: ProgressBar
//        val magicBar: ProgressBar
//        val magicText: TextView = view.findViewById(R.id.MagicText)
//        val healthText: TextView = view.findViewById(R.id.HealthText)
//        val magicAdd = view.findViewById<Button>(R.id.MagicAdd)
//        val magicSubtract = view.findViewById<Button>(R.id.MagicSubtract)
//        var name = ""
//
//        init {
//            healthBar = view.findViewById(R.id.HealthBar)
//            magicBar = view.findViewById(R.id.MagicBar)
//
//            val healthAdd = view.findViewById<Button>(R.id.HealthAdd)
//            val healthSubtract = view.findViewById<Button>(R.id.HealthSubtract)
//
//
//            healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
//            magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
//
//            healthAdd.setOnClickListener {
//                changeProgressBar(healthBar, 1)
//                healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
//                SaveToStorage(SavableItem.current_hp, healthBar.progress)
//            }
//            healthSubtract.setOnClickListener {
//                changeProgressBar(healthBar, -1)
//                healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
//                SaveToStorage(SavableItem.current_hp, healthBar.progress)
//            }
//            magicAdd.setOnClickListener {
//                changeProgressBar(magicBar, 1)
//                magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
//                SaveToStorage(SavableItem.current_magic, magicBar.progress)
//            }
//            magicSubtract.setOnClickListener {
//                changeProgressBar(magicBar, -1)
//                magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
//                SaveToStorage(SavableItem.current_magic, magicBar.progress)
//            }
//
//            healthText.setOnLongClickListener {
//                var SetLevelDialog = SetLevelDialog(context, healthBar) { bar ->
//                    copyBarDetails(healthBar, bar, "Health", healthText)
//                    SaveToStorage(SavableItem.max_hp, healthBar.max)
//                    SaveToStorage(SavableItem.current_hp, healthBar.progress)
//                }
//                SetLevelDialog.show()
//                SetLevelDialog.setLevelTitle("Set Max Health $name")
//                SetLevelDialog.window.setLayout((6 * view.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//                true
//            }
//
//            magicText.setOnLongClickListener {
//                val SetLevelDialog = SetLevelDialog(context, magicBar) { bar ->
//                    copyBarDetails(magicBar, bar, "Magic", magicText)
//                    SaveToStorage(SavableItem.max_magic, magicBar.max)
//                    SaveToStorage(SavableItem.current_magic, magicBar.progress)
//                }
//                SetLevelDialog.show()
//                SetLevelDialog.setLevelTitle("Set Max Magic for $name")
//                SetLevelDialog.window.setLayout((6 * view.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//                true
//            }
//        }
//
//        fun copyBarDetails(barToUpdate: ProgressBar, barToUpdateFrom: ProgressBar, barName: String, textView: TextView) {
//            barToUpdate.max = barToUpdateFrom.max
//            barToUpdate.progress = barToUpdateFrom.max
//            textView.text = "${barName}: ${barToUpdateFrom.max}/${barToUpdateFrom.max}"
//        }
//
//        fun changeProgressBar(progressBar: ProgressBar, amount: Int) {
//            if (!(progressBar.progress + amount > progressBar.max || progressBar.progress + amount < 0)) {
//                progressBar.progress += amount
//            }
//        }
//
//        fun SaveToStorage(preferenceTitle: SavableItem, valueToSave: Int) {
//            val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
//            val editor = preferences.edit()
//            when (preferenceTitle) {
//                SavableItem.max_hp -> editor.putInt("max_hp", valueToSave)
//                SavableItem.current_hp -> editor.putInt("current_hp", valueToSave)
//                SavableItem.max_magic -> editor.putInt("max_magic", valueToSave)
//                SavableItem.current_magic -> editor.putInt("current_magic", valueToSave)
//            }
//            editor.apply()
//        }
//
//        fun readFromStorage(name: String) {
//            this.name = name
//            characterName.text = name
//            val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
//
//            arrayOf("max_hp", "current_hp", "max_magic", "current_magic", "uses_magic")
//                    .forEach { item ->
//                        when (item) {
//                            "max_hp" -> healthBar.max = preferences.getInt(item, 1)
//                            "current_hp" -> healthBar.progress = preferences.getInt(item, healthBar.max)
//                            "max_magic" -> magicBar.max = preferences.getInt(item, 1)
//                            "current_magic" -> magicBar.progress = preferences.getInt(item, magicBar.max)
//                            "uses_magic" ->
//                            {
//                                if (!preferences.getBoolean("uses_magic", false)){
//                                    magicBar.visibility = View.GONE
//                                    magicText.visibility = View.GONE
//                                    magicAdd.visibility = View.GONE
//                                    magicSubtract.visibility = View.GONE
//                                }
//                            }
//                        }
//                    }
//            healthText.text = "Health: ${healthBar.progress}/${healthBar.max}"
//            magicText.text = "Magic: ${magicBar.progress}/${magicBar.max}"
//        }
//    }
//
//    fun CreateCharacterMenu(view: View, sharedPreferences: SharedPreferences): PopupMenu {
//        val menu = PopupMenu(this, view)
//        val characters = sharedPreferences.getStringSet("names", setOf<String>())
//        characters.forEachIndexed { index, s ->
//            menu.menu.add(s)
//        }
//        return menu
//    }
//}
//
//class SetLevelDialog(context: Context, val barToUpdate: ProgressBar, val onChange: (ProgressBar) -> Unit) : Dialog(context)
//{
//
//    override fun onCreate(savedInstanceState: Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_level_set)
//
//        val AddButton = findViewById<Button>(R.id.AddToLevel)
//        val SubtractButton = findViewById<Button>(R.id.SubtractFromLevel)
//        val LevelValue = findViewById<TextView>(R.id.LevelText)
//        val DoneButton = findViewById<TextView>(R.id.LevelDismiss)
//
//        LevelValue.text = "${barToUpdate.max}"
//
//        AddButton.setOnClickListener {
//            if (barToUpdate.max >= 0)
//            {
//                barToUpdate.max += 1
//            }
//            LevelValue.text = "${barToUpdate.max}"
//            onChange(barToUpdate)
//        }
//        SubtractButton.setOnClickListener {
//            if (barToUpdate.max > 1)
//            {
//                barToUpdate.max -= 1
//            }
//            LevelValue.text = "${barToUpdate.max}"
//            onChange(barToUpdate)
//        }
//        DoneButton.setOnClickListener {
//            dismiss()
//        }
//    }
//
//    fun setLevelTitle(title: String)
//    {
//        findViewById<TextView>(R.id.LevelSetText).text = title
//    }
//}
//
//enum class SavableItem {
//    max_hp,
//    current_hp,
//    character_ac,
//    max_magic,
//    current_magic,
//    magicModifier,
//    character_list,
//    uses_magic;
//
//    fun getStringKey(): String {
//        return when (this) {
//            max_hp -> "max_hp"
//            current_hp -> "current_hp"
//            max_magic -> "max_magic"
//            current_magic -> "current_magic"
//            character_ac -> "character_ac"
//            magicModifier -> "magic_modifier"
//            character_list -> "character_list"
//            uses_magic -> "uses_magic"
//        }
//    }
//}
