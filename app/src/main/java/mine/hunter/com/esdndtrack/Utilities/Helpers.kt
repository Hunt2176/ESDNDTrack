package mine.hunter.com.esdndtrack.Utilities

import android.content.res.ColorStateList
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import mine.hunter.com.esdndtrack.R

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

fun <T:Any> T.use(toDo: (T) -> Unit)
{
	toDo(this)
}