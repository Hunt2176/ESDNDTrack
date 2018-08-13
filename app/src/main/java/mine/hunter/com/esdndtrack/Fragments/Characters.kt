package mine.hunter.com.esdndtrack.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_characters.*

import mine.hunter.com.esdndtrack.R
import mine.hunter.com.esdndtrack.utilities.ArrayAdapter
import mine.hunter.com.esdndtrack.utilities.ifNotNull


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Characters.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Characters.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Characters : Fragment()
{
	private var recycler: RecyclerView? = null
	private var listener: OnFragmentInteractionListener? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)


	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View?
	{
		// Inflate the layout for this fragment
		val viewToReturn = inflater.inflate(R.layout.fragment_characters, container, false)

		viewToReturn.ifNotNull {
			recycler = it.findViewById(R.id.CharacterList)
			recycler?.adapter = ArrayAdapter(it.context)
			recycler?.layoutManager = GridLayoutManager(it.context, 1)
			recycler?.addItemDecoration(DividerItemDecoration(it.context, DividerItemDecoration.VERTICAL))
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

	fun addToCharacterList(name: String)
	{
		(recycler?.adapter as? ArrayAdapter).ifNotNull {
			it.characters.add(name)
			it.notifyDataSetChanged()
			NoLoadTextView.visibility = View.INVISIBLE
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 *
	 *
	 * See the Android Training lesson [Communicating with Other Fragments]
	 * (http://developer.android.com/training/basics/fragments/communicating.html)
	 * for more information.
	 */
	interface OnFragmentInteractionListener
	{
		// TODO: Update argument type and name
		fun onFragmentInteraction(uri: Uri)
	}

	/**
	companion object
	{
		/**
		 * Use this factory method to create a new instance of
		 * this fragment using the provided parameters.
		 *
		 * @param param1 Parameter 1.
		 * @param param2 Parameter 2.
		 * @return A new instance of fragment Characters.
		 */
		// TODO: Rename and change types and number of parameters
		@JvmStatic
		fun newInstance(param1: String, param2: String) =
				Characters().apply {
					arguments = Bundle().apply {
						putString(ARG_PARAM1, param1)
						putString(ARG_PARAM2, param2)
					}
				}
	}
	**/
}
