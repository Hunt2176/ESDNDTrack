package mine.hunter.com.esdndtrack.Utilities

import android.os.AsyncTask

class AndroidTimer(private val delay: Long, private val onComplete: () -> Unit)
{
	fun execute()
	{
		task(delay, onComplete).execute()
	}

	private class task(val delay: Long, val onComplete: () -> Unit): AsyncTask<Void, Void, Void>()
	{
		override fun doInBackground(vararg params: Void?): Void?
		{
			Thread.sleep(delay)
			return null
		}

		override fun onPostExecute(result: Void?)
		{
			onComplete()
		}
	}
}