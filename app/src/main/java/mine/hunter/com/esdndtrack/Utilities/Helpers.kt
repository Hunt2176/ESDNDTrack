package mine.hunter.com.esdndtrack.Utilities

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