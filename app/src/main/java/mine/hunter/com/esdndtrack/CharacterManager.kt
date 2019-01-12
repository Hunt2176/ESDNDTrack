package mine.hunter.com.esdndtrack

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import kotlinx.android.synthetic.main.character_manager.*
import mine.hunter.com.esdndtrack.Utilities.GSONHelper
import mine.hunter.com.esdndtrack.Utilities.use
import mine.hunter.com.esdndtrack.Utilities.useAndReturn
import java.io.File

class CharacterManager: AppCompatActivity()
{
	private var recycler: androidx.recyclerview.widget.RecyclerView? = null
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.character_manager)
		setSupportActionBar(ManageCharacterToolbar)

		supportActionBar?.elevation = 8F
		val arrow = ContextCompat.getDrawable(this, R.drawable.arrow_backward)
		arrow?.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP)
		supportActionBar?.setHomeAsUpIndicator(arrow)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

		recycler = findViewById(R.id.CharacterManageRecycler)
		recycler?.adapter = CharacterManageAdapter(this)
		recycler?.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 1)
		recycler?.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))

		AddCharacterButton.setOnClickListener {
			startActivity(Intent(this, CharacterCreator::class.java))
		}
	}

	override fun onSupportNavigateUp(): Boolean
	{
		onBackPressed()
		return true
	}

	override fun onResume()
	{
		super.onResume()
		recycler?.adapter = CharacterManageAdapter(this)

	}
}

open class CharacterManageAdapter(val context: Context): androidx.recyclerview.widget.RecyclerView.Adapter<CharacterViewHolder>()
{
	private var names = mutableMapOf<Int, Pair<Int, String>>()

	init
	{
		DNDCharacter.readFromJson(context).toList().forEachIndexed {index, char -> names[index] = Pair(char.id, char.name)}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder
	{
		val view = LayoutInflater.from(context).inflate(R.layout.manage_character_cell, parent, false)
		return CharacterViewHolder(view)
	}

	override fun getItemCount(): Int = names.keys.size

	override fun onBindViewHolder(holder: CharacterViewHolder, position: Int)
	{
		holder.nameView.text = names[position]?.second
		holder.overflowButton.setOnClickListener {
			val popupMenu = PopupMenu(holder.itemView.context, it)
			popupMenu.inflate(R.menu.character_manage_overflow)
			popupMenu.show()

			popupMenu.setOnMenuItemClickListener {
				when (it.itemId)
				{
					R.id.MENUDeleteCharacter ->
					{
						DNDCharacter.readFromJson(context).toMutableList()
							.use {list ->
								list.removeAt(position)
								names.remove(position)
								notifyDataSetChanged()
								GSONHelper().writeToDisk(list.toTypedArray(), File(context.filesDir, "Characters.json"))
							}
					}

					R.id.MENUModifyCharacter ->
					{
						context.startActivity(Intent(context, CharacterCreator::class.java)
							.useAndReturn { intent ->
								intent.putExtra("charID", names[position]?.first)
								intent
							})
					}
				}
				true
			}
		}
		holder.itemView.setOnLongClickListener {
			holder.overflowButton.callOnClick()
			true
		}
	}
}

class CharacterViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
	val nameView = view.findViewById<TextView>(R.id.CharacterManageName)
	val overflowButton = view.findViewById<ImageButton>(R.id.CharacterManageOverFlow)
}