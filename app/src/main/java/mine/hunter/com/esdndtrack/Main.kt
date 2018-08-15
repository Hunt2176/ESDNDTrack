package mine.hunter.com.esdndtrack

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.main_activity.*
import mine.hunter.com.esdndtrack.Fragments.CharactersFragment
import mine.hunter.com.esdndtrack.Fragments.DiceFragment
import mine.hunter.com.esdndtrack.Fragments.SpellsFragment
import mine.hunter.com.esdndtrack.Utilities.*
import java.io.BufferedReader
import java.io.InputStreamReader

class Main : AppCompatActivity(), CharactersFragment.OnFragmentInteractionListener
{
	private var fab: FloatingActionButton? = null
	private var currentTab = 0
	private var spellList = arrayOf<ReadInSpell>()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
		setSupportActionBar(toolbar)

		supportActionBar?.title = "ES DND"
		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
		fab = MainFAB

		val pager = findViewById<ViewPager>(R.id.TabPager)
		pager.adapter = PageAdapter(supportFragmentManager,
				GSONHelper().readInSpells(BufferedReader(InputStreamReader(resources.openRawResource(R.raw.spellsource))).readText()))

		pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
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
			}
		})

		MainTabBar.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
			override fun onTabReselected(tab: TabLayout.Tab?)
			{
				tab.ifNotNull { pager.currentItem = it.position }
			}

			override fun onTabUnselected(tab: TabLayout.Tab?)
			{

			}

			override fun onTabSelected(tab: TabLayout.Tab?)
			{
				tab.ifNotNull { pager.currentItem = it.position }
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
									((pager.adapter as PageAdapter).fragments[0] as CharactersFragment)
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


class PageAdapter(fragmentManager: FragmentManager, private val spells: Array<ReadInSpell>): FragmentPagerAdapter(fragmentManager)
{
	var fragments = ArrayList<Fragment>()
	override fun getItem(position: Int): Fragment
	{
		val fragmentToReturn = when (position)
		{
			0 -> CharactersFragment()
			1 -> DiceFragment()
			2 -> SpellsFragment.create(spells)
			else ->
			{
				CharactersFragment()
			}
		}
		fragments.add(fragmentToReturn)
		return fragmentToReturn
	}

	override fun getCount(): Int = 3
}