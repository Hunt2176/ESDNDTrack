package mine.hunter.com.esdndtrack.utilities

fun <T:Any> T?.ifNotNull(onNotNull:(T) -> Unit)
{
	if (this != null)
	{
		onNotNull(this)
	}
}