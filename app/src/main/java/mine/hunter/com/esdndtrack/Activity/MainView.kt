package mine.hunter.com.esdndtrack.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.main_activity.*
import mine.hunter.com.esdndtrack.Database.SpellsDB
import mine.hunter.com.esdndtrack.Dialogs.ItemSelectionDialog
import mine.hunter.com.esdndtrack.Fragments.*
import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.Utilities.*

class Main : AppCompatActivity(), CharactersFragment.OnFragmentInteractionListener
{
	private var fab: FloatingActionButton? = null
	private val currentTab: Int
		get()
		{
			return if (pager == null) 0
			else
			{
				pager!!.currentItem
			}
		}
	private var pager: androidx.viewpager.widget.ViewPager? = null

	@SuppressLint("ClickableViewAccessibility")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)

		window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
		fab = findViewById(R.id.MainFAB)

		StaticItems.ReadInSpellList(resources)
		StaticItems.ReadInCustomSpellList(this)

		pager = findViewById(R.id.TabPager)
		pager?.adapter = PageAdapter(supportFragmentManager)
		pager?.offscreenPageLimit = 3

		pager?.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener
		{
			override fun onPageScrollStateChanged(state: Int){}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
			{
				MainTabBar.setScrollPosition(position, positionOffset, false)

				//Hides Keyboard from view when moving the pager
				this@Main.currentFocus.ifNotNull {
					val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
					imm.hideSoftInputFromWindow(it.windowToken, 0)
				}
			}

			override fun onPageSelected(position: Int)
			{
				when (position)
				{
					0 -> fab?.show()
					1 -> fab?.hide()
					2 -> fab?.hide()
					3 -> fab?.show()
				}
			}
		})

		MainTabBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
		{
			override fun onTabReselected(tab: TabLayout.Tab?) { tab.ifNotNull { pager?.currentItem = it.position } }
			override fun onTabUnselected(tab: TabLayout.Tab?){}
			override fun onTabSelected(tab: TabLayout.Tab?) { tab.ifNotNull { pager?.currentItem = it.position } }
		})

		fab?.setOnClickListener {

			when (currentTab)
			{
				0 -> ItemSelectionDialog.dndCharacterList(this) {((pager?.adapter as? PageAdapter)?.getItem(0) as? CharactersFragment)?.addToCharacterList(it); }.show()

				3 -> DualInputDialog.show(this) { isReady, pair ->
						if (isReady) ((pager?.adapter as PageAdapter).getItem(3) as NotesFragment).addNoteToView(pair!!)
					}
			}
		}


		System.out.println("CREATED")
		Thread{SpellsDB(this).updateFromJson(resources)}.start()
	}


	override fun onFragmentInteraction(uri: Uri){}
}

class PageAdapter(private val fragmentManager: androidx.fragment.app.FragmentManager): androidx.fragment.app.FragmentPagerAdapter(fragmentManager)
{
	override fun getItem(position: Int): androidx.fragment.app.Fragment
	{
		if (fragmentManager.fragments.size != 0 && position <= fragmentManager.fragments.size)
		{
			return fragmentManager.fragments[position]
		}
		return when (position)
		{
			0 -> CharactersFragment()
			1 -> DiceFragment()
			2 -> SpellsFragment.create()
			3 -> NotesFragment()

			else -> CharactersFragment()

		}
	}
	override fun getCount(): Int = 4
}