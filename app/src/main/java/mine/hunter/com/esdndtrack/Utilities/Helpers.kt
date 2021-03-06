package mine.hunter.com.esdndtrack.Utilities

import android.content.res.ColorStateList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import mine.hunter.com.esdndtrack.R


// Native Kotlin Extensions
fun <T:Any> T?.ifNotNull(onNotNull:(T) -> Unit)
{
	if (this != null)
	{
		onNotNull(this)
	}
}

fun <T:Any> ifAllNotNull(vararg items: T?, onNoneNull:(Array<T>) -> Unit)
{
	for (i in items)
	{
		if (i == null) return
	}
	onNoneNull(items as Array<T>)
}

fun Any?.isNull(): Boolean
{
	return this == null
}

fun CharSequence.toIntOrZero():Int {
	return this.toString().toIntOrZero()
}

fun String.toIntOrZero(): Int
{
	return if (this.toIntOrNull() == null) 0 else this.toInt()
}

fun <T> Iterable<T>.ifAllTrue(predicate: (T) -> Boolean): Boolean
{
	this.forEach { if (!predicate(it)) return false  }
	return true
}

fun <T> Array<T>.ifAllTrue(predicate: (T) -> Boolean): Boolean
{
	this.forEach { if (!predicate(it)) return false }
	return true
}

fun <T:Any> T.use(toDo: (T) -> Unit)
{
	toDo(this)
}

fun <T:Any> T.useAndReturn(toDo: (T) -> T): T
{
	return toDo(this)
}

fun <E, T:Any> T.useAndReturnDifferent(toDo: (T) -> E): E
{
	return toDo(this)
}

inline fun <reified T> Pair<T,T>.toArray(): Array<T>
{
	return arrayOf(this.first, this.second)
}

fun List<String>.createString(): String
{
	var toReturn = ""
	this.forEach { toReturn += it }
	return toReturn
}

// Android Kotlin Extensions

fun FloatingActionButton.isReadyForComplete(isEnabled: Boolean)
{
	if (isEnabled)
	{
		this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_green_dark))
		this.setImageResource(R.drawable.check_mark)
	}
	else
	{
		this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_dark))
		this.setImageResource(R.drawable.cancel)
	}
}

