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
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.main_activity.*
import mine.hunter.com.esdndtrack.Fragments.Characters
import mine.hunter.com.esdndtrack.Fragments.Dice
import mine.hunter.com.esdndtrack.utilities.CreateCharacterMenu
import mine.hunter.com.esdndtrack.utilities.SavableItem
import mine.hunter.com.esdndtrack.utilities.ifNotNull

class Main : AppCompatActivity(), Characters.OnFragmentInteractionListener, Dice.OnFragmentInteractionListener
{
	private var fab: FloatingActionButton? = null
	private var currentTab = 0

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
		setSupportActionBar(toolbar)

		supportActionBar?.title = "ES DND"
		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
		fab = MainFAB

		val pager = findViewById<ViewPager>(R.id.TabPager)
		pager.adapter = PageAdapter(supportFragmentManager)

		pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int)
			{

			}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
			{
				MainTabBar.setScrollPosition(position, positionOffset, false)
			}

			override fun onPageSelected(position: Int)
			{
				when (position){
					0 -> MainFAB.show()
					1 -> MainFAB.hide()
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
							R.id.MENUCreateCharacter ->
							{
								startActivity(Intent(this, CharacterCreater::class.java))
							}
							R.id.MENULoadCharacter ->
							{
								val charMenu = CreateCharacterMenu(it,
										it.context.getSharedPreferences(SavableItem.character_list.getStringKey(),
												Context.MODE_PRIVATE))
								charMenu.show()
								charMenu.setOnMenuItemClickListener{ charItem ->
									((pager.adapter as PageAdapter).fragments[0] as Characters)
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


class PageAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager)
{
	var fragments = ArrayList<Fragment>()
	override fun getItem(position: Int): Fragment
	{
		val fragmentToReturn = when (position)
		{
			0 -> Characters()
			1 -> Dice()
			else ->
			{
				Characters()
			}
		}
		fragments.add(fragmentToReturn)
		return fragmentToReturn
	}

	override fun getCount(): Int = 3
}