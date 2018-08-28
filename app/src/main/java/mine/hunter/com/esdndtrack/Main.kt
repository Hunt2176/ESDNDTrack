package mine.hunter.com.esdndtrack

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.main_activity.*
import mine.hunter.com.esdndtrack.Fragments.CharactersFragment
import mine.hunter.com.esdndtrack.Fragments.DiceFragment
import mine.hunter.com.esdndtrack.Fragments.SpellsArrayAdapter
import mine.hunter.com.esdndtrack.Fragments.SpellsFragment
import mine.hunter.com.esdndtrack.Utilities.*
import java.io.BufferedReader
import java.io.InputStreamReader

class Main : AppCompatActivity(), CharactersFragment.OnFragmentInteractionListener
{
	private var fab: FloatingActionButton? = null
	private var currentTab = 0
	private var pager: ViewPager? = null

	override fun onCreateOptionsMenu(menu: Menu?): Boolean
	{

		when (pager?.currentItem)
		{
			2 ->
			{
				menuInflater.inflate(R.menu.spellbook_overflow, menu)
				return true
			}

		}
		return false
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean
	{
		val dialog = CreateSpellDialog(this) {
			if (it)
			{
				StaticItems.ReadInSpellList(resources)
				StaticItems.ReadInCustomSpellList(this)

				StaticItems.spellList.toMutableList()
						.use { mutableList -> mutableList.addAll(0, StaticItems.customSpellList.toList()); StaticItems.spellList = mutableList.toTypedArray() }


				(((pager?.adapter as PageAdapter).getItem(2) as SpellsFragment).recycler?.adapter as SpellsArrayAdapter).spellList = StaticItems.spellList
				((pager?.adapter as PageAdapter).getItem(2) as SpellsFragment).recycler?.adapter?.notifyDataSetChanged()
			}
		}
		dialog.show()
		dialog.window?.setLayout((6 * resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
		return true
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
		setSupportActionBar(toolbar)

		supportActionBar?.title = "ES DND"

		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
		fab = MainFAB

		StaticItems.ReadInSpellList(resources)
		StaticItems.ReadInCustomSpellList(this)

		StaticItems.spellList.toMutableList()
				.use { it.addAll(0, StaticItems.customSpellList.toList()); StaticItems.spellList = it.toTypedArray() }


		pager = findViewById(R.id.TabPager)
		pager?.adapter = PageAdapter(supportFragmentManager)
		pager?.offscreenPageLimit = 3

		pager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int)
			{

			}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
			{
				MainTabBar.setScrollPosition(position, positionOffset, false)
				this@Main.currentFocus.ifNotNull {
					val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
					imm.hideSoftInputFromWindow(it.windowToken, 0)
				}
			}

			override fun onPageSelected(position: Int)
			{
				when (position){
					0 -> MainFAB.show()
					1 -> MainFAB.hide()
					2 -> MainFAB.hide()
				}
				currentTab = position
				this@Main.invalidateOptionsMenu()
			}
		})

		MainTabBar.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
			override fun onTabReselected(tab: TabLayout.Tab?)
			{
				tab.ifNotNull { pager?.currentItem = it.position }
			}

			override fun onTabUnselected(tab: TabLayout.Tab?)
			{

			}

			override fun onTabSelected(tab: TabLayout.Tab?)
			{
				tab.ifNotNull { pager?.currentItem = it.position }
			}
		})

		fab?.setOnClickListener {
			fab?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cancel))
			when (currentTab)
			{
				0 ->
				{
					val menu = PopupMenu(this, it)
					menu.inflate(R.menu.create_character_fab_menu)
					menu.show()

					menu.setOnMenuItemClickListener { item ->
						when (item.itemId)
						{
							R.id.MENUManageCharacter ->
							{
								startActivity(Intent(this, CharacterManager::class.java))
							}
							R.id.MENULoadCharacter ->
							{
								val charMenu = CreateCharacterMenu(it,
										it.context.getSharedPreferences(SavableItem.character_list.getStringKey(),
												Context.MODE_PRIVATE))
								charMenu.show()
								charMenu.setOnMenuItemClickListener{ charItem ->
									((pager?.adapter as PageAdapter).getItem(0) as CharactersFragment)
											.addToCharacterList(charItem.title.toString())

									true
								}
							}
						}

						true
					}

					menu.setOnDismissListener { _ ->
						fab?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_icon))
					}
				}
			}
		}

	}

	override fun onFragmentInteraction(uri: Uri)
	{

	}
}

class PageAdapter(private val fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager)
{
	override fun getItem(position: Int): Fragment
	{
		if (fragmentManager.fragments.size != 0 && position <= fragmentManager.fragments.size)
		{
			return fragmentManager.fragments[position]
		}
		val fragmentToReturn = when (position)
		{
			0 -> CharactersFragment()
			1 -> DiceFragment()
			2 -> SpellsFragment.create()
			else ->
			{
				CharactersFragment()
			}
		}
		return fragmentToReturn
	}

	override fun getCount(): Int = 3
}