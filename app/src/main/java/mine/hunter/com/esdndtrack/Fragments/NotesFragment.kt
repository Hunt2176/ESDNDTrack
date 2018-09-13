package mine.hunter.com.esdndtrack.Fragments

import android.app.Dialog
import mine.hunter.com.esdndtrack.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import mine.hunter.com.esdndtrack.SpellCTextWatcher
import mine.hunter.com.esdndtrack.Utilities.*

class NotesFragment: Fragment()
{
	var notesView: RecyclerView? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(R.layout.notes_fragment, container, false)
		notesView = view.findViewById(R.id.NotesList)
		notesView?.adapter = NotesArrayAdapter(view.context, createNotePairArray(view.context))
		notesView?.layoutManager = GridLayoutManager(view.context, 1)
		notesView?.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
		return view
	}

	fun addNoteToView(notePair: Pair <String, String>)
	{
		(notesView?.adapter as NotesArrayAdapter).use {
			it.addNote(notePair)
			StaticItems.writeNotePairs(this.context!!, it.notePairs.toTypedArray())
		}

	}

	fun createNotePairArray(context: Context): ArrayList<Pair<String, String>>
	{
		var toReturn = arrayListOf<Pair<String, String>>()

		var index = 0
		var toPut = StaticItems.getNotePair(index, context)
		while (toPut != null)
		{
			toReturn.add(toPut)
			index += 1
			toPut = StaticItems.getNotePair(index, context)
		}
		return toReturn
	}
}

class NotesArrayAdapter(val context: Context, var notePairs: ArrayList<Pair<String, String>>): RecyclerView.Adapter<NoteViewCell>()
{
	val noteCount: Int
		get() = notePairs.count()

	fun addNote(notePair: Pair<String, String>)
	{
		notePairs.add(notePair)
		notifyItemInserted(noteCount- 1)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewCell
	{
		val view = LayoutInflater.from(context).inflate(R.layout.notes_cell, parent, false)
		return NoteViewCell(view)
	}

	override fun getItemCount(): Int
	{
		return noteCount
	}

	override fun onBindViewHolder(cell: NoteViewCell, position: Int)
	{
		notePairs.get(position).use {
			cell.title.setText(it.first)
			cell.body.setText(it.second)
		}
	}
}

class NoteViewCell(view: View): RecyclerView.ViewHolder(view)
{
	val title: TextView
	val body: TextView
	var bodyVisible = false
	init
	{
		title = view.findViewById(R.id.NoteTitle)
		body = view.findViewById(R.id.NoteBody)

		view.findViewById<ImageButton>(R.id.NoteVisibleChangeButton)
				.use { button ->
					button.setOnClickListener {
						bodyVisible = !bodyVisible
						if (bodyVisible)
						{
							it.setBackgroundResource(R.drawable.arrow_collapse)
							body.visibility = View.VISIBLE
						}
						else
						{
							it.setBackgroundResource(R.drawable.arrow_expand)
							body.visibility = View.GONE
						}
					}
					button.setBackgroundResource(R.drawable.arrow_expand)
				}

	}
}

class NoteMaker(context: Context, val onComplete: (Boolean, Pair<String, String>?) -> Unit): Dialog(context)
{
	companion object
	{
		fun show(context: Context, onComplete: (Boolean, Pair<String, String>?) -> Unit): NoteMaker
		{
			return NoteMaker(context, onComplete).useAndReturn {
				it.show()
				it.window?.setLayout((6 * context.resources.displayMetrics.widthPixels) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)
				it
			}
		}
	}

	var isReady = false

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(LayoutInflater.from(context).inflate(R.layout.notes_maker, null))

		val title = findViewById<TextInputEditText>(R.id.MakeNoteTitle)
		val body = findViewById<TextInputEditText>(R.id.MakeNoteBody)
		val fab = findViewById<FloatingActionButton>(R.id.MakeNoteButton)

		fab.isReadyForComplete(isReady)

		val fields = arrayOf(title, body)
		SpellCTextWatcher.setup { _ ->
			isReady = fields.ifAllTrue { !it.text.isNullOrEmpty() }
			fab.isReadyForComplete(isReady)
		}
				.use { watcher ->
					title.addTextChangedListener(watcher)
					body.addTextChangedListener(watcher)
				}

		fab.setOnClickListener {
			if (isReady)
			{
				onComplete(isReady, Pair(title.text.toString(), body.text.toString()))
			}
			else
			{
				onComplete(isReady, null)
			}
			dismiss()
		}
	}

}