package mine.hunter.com.esdndtrack.Activity

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.character_manager.*
import mine.hunter.com.esdndtrack.Objects.DNDCharacter
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.UIObjects.ItemSelectionAdapter
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
		recycler?.adapter = getAdapter()
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
		recycler?.adapter = getAdapter()
	}

	private fun getAdapter(): ItemSelectionAdapter<DNDCharacter>
	{
		val context = this
		return object: ItemSelectionAdapter<DNDCharacter>(context, DNDCharacter.readFromJson(context), true, true)
		{
			override fun viewText(item: DNDCharacter): String = item.name

			override fun onItemSelect(item: DNDCharacter){}

			override fun modifyOverflowMenu(item: DNDCharacter)
			{
				context.startActivity(Intent(context, CharacterCreator::class.java)
						.useAndReturn { intent ->
							intent.putExtra("charID", item.id)
							return@useAndReturn intent
						})
			}

			override fun deleteOverflowMenu(item: DNDCharacter)
			{
				items.toMutableList()
						.use { items ->
							items.remove(item)
							GSONHelper().writeToDisk(items.toTypedArray(), File(context.filesDir, "Characters.json"))
						}
				notifyDataSetChanged()
			}
		}
	}
}

