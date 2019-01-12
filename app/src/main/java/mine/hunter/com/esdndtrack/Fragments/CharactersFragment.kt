package mine.hunter.com.esdndtrack.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_characters.*

import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.ArrayAdapter
import mine.hunter.com.esdndtrack.DNDCharacter
import mine.hunter.com.esdndtrack.Utilities.ifNotNull


class CharactersFragment : androidx.fragment.app.Fragment()
{
	private var recycler: androidx.recyclerview.widget.RecyclerView? = null
	private var listener: OnFragmentInteractionListener? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View?
	{
		val viewToReturn = inflater.inflate(R.layout.fragment_characters, container, false)

		viewToReturn.ifNotNull {
			recycler = it.findViewById(R.id.CharacterList)
			recycler?.adapter = ArrayAdapter(it.context)
			recycler?.layoutManager = androidx.recyclerview.widget.GridLayoutManager(it.context, 1)
			recycler?.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(it.context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
		}
		return viewToReturn
	}

	override fun onAttach(context: Context)
	{
		super.onAttach(context)
		if (context is OnFragmentInteractionListener)
		{
			listener = context
		} else
		{
			throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
		}
	}

	override fun onDetach()
	{
		super.onDetach()
		listener = null
	}

	fun addToCharacterList(character: DNDCharacter)
	{
		((recycler?.adapter) as? ArrayAdapter)?.addCharacter(character)
		view?.findViewById<TextView>(R.id.NoLoadTextView)?.text = ""
	}

	interface OnFragmentInteractionListener
	{
		fun onFragmentInteraction(uri: Uri)
	}
}
