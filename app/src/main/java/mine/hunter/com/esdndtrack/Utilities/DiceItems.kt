package mine.hunter.com.esdndtrack.Utilities
import java.util.Random

class Dice(val sides: Int)
{
	fun rollDice(): Int
	{
		return try
		{
			Random().nextInt(sides) + 1
		} catch (exc: IllegalArgumentException)
		{
			0
		}
	}
	
	fun rollMultiple(times: Int): Int
	{
		var result = 0
		for (i in 1..times)
		{
			result += rollDice()
		}
		return result
	}
}

enum class StandardDice
{
	d4,d6,d8,d10,d12,d20;
	
	private fun getSides(): Int
	{
		return when (this)
		{
			d4 -> 4
			d6 -> 6
			d8 -> 8
			d10 -> 10
			d12 -> 12
			d20 -> 20
		}
	}

	companion object {
	    val availableSides = arrayOf(4,6,8,10,12,20)
	}
	
	fun roll(): Int
	{
		return Dice(getSides()).rollDice()
	}
	
	fun rollMultiple(times: Int): Int
	{
		return Dice(getSides()).rollMultiple(times)
	}
	
}