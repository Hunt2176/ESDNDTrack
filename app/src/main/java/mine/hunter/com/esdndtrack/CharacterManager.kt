package mine.hunter.com.esdndtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import kotlinx.android.synthetic.main.character_manager.*
import mine.hunter.com.esdndtrack.Utilities.SavableItem
import mine.hunter.com.esdndtrack.Utilities.ifNotNull

class CharacterManager: AppCompatActivity()
{
	private var recycler: RecyclerView? = null
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.character_manager)
		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

		recycler = findViewById(R.id.CharacterManageRecycler)
		recycler?.adapter = CharacterManageAdapter(this)
		recycler?.layoutManager = GridLayoutManager(this, 1)
		recycler?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

		AddCharacterButton.setOnClickListener {
			startActivity(Intent(this, CharacterCreater::class.java))
		}
	}

	override fun onResume()
	{
		super.onResume()
		recycler?.adapter = CharacterManageAdapter(this)

	}
}

class CharacterManageAdapter(val context: Context): RecyclerView.Adapter<CharacterViewHolder>()
{
	private var names = arrayListOf<String>()

	init
	{
		context.getSharedPreferences(SavableItem.character_list.getStringKey(), Context.MODE_PRIVATE)
				.getStringSet("names", setOf())
				.ifNotNull {
					it.forEach { name -> names.add(name) }
				}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.manage_character_cell, parent, false)
		return CharacterViewHolder(view)
	}

	override fun getItemCount(): Int
	{
		return names.size
	}

	override fun onBindViewHolder(holder: CharacterViewHolder, position: Int)
	{
		holder.nameView.text = names.elementAt(position)
		holder.overflowButton.setOnClickListener {
			val popupMenu = PopupMenu(holder.itemView.context, it)
			popupMenu.inflate(R.menu.character_manage_overflow)
			popupMenu.show()

			popupMenu.setOnMenuItemClickListener {
				when (it.itemId)
				{
					R.id.MENUDeleteCharacter ->
					{
						//TODO: Fix issue when removing item and then removing an item before the deleted one
						this.names.removeAt(position)
						this.notifyItemRemoved(position)
						val charList = context.getSharedPreferences(SavableItem.character_list.getStringKey(), Context.MODE_PRIVATE)
						val charListEditor = charList.edit()
						charListEditor.putStringSet("names", names.toSet())
						charListEditor.apply()
					}

					R.id.MENUModifyCharacter ->
					{
						//TODO: Add way to hook into Character Creator to allow editing of already made character
					}
				}
				true
			}
		}
	}
}

class CharacterViewHolder(view: View): RecyclerView.ViewHolder(view)
{
	val nameView: TextView
	val overflowButton: ImageButton

	init
	{
		nameView = view.findViewById(R.id.CharacterManageName)
		overflowButton = view.findViewById(R.id.CharacterManageOverFlow)
	}
}