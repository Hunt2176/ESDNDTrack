package mine.hunter.com.esdndtrack

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import kotlinx.android.synthetic.main.character_manager.*
import mine.hunter.com.esdndtrack.Utilities.ifNotNull

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
//			startActivity(Intent(this, CharacterCreater::class.java))
			val charCreateDialog = CharacterCreater(this, {addedchar -> if (addedchar) {onResume()}})
			charCreateDialog.show()
			charCreateDialog.window.setLayout((6 * resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
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

class CharacterManageAdapter(val context: Context): androidx.recyclerview.widget.RecyclerView.Adapter<CharacterViewHolder>()
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
		holder.itemView.setOnLongClickListener {
			holder.overflowButton.callOnClick()
			true
		}
	}
}

class CharacterViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
{
	val nameView: TextView
	val overflowButton: ImageButton

	init
	{
		nameView = view.findViewById(R.id.CharacterManageName)
		overflowButton = view.findViewById(R.id.CharacterManageOverFlow)
	}
}