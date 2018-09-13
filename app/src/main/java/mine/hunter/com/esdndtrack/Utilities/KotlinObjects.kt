package mine.hunter.com.esdndtrack.Utilities

import java.io.File

// Extension of Kotlin file to give Java classes the ability to read
// helpful Kotlin extensions
class KotlinFile: File
{

	companion object
	{
		fun fromFile(file: File): KotlinFile
		{
			return KotlinFile(file.absolutePath)
		}
	}

	constructor(absolutePath: String): super(absolutePath)
	constructor(parent: File, child: String) : super(parent, child)
	constructor(parent: String, child: String) : super(parent, child)

	fun getLines(): List<String>
	{
		return this.readLines()
	}

	fun writeString(toWrite: String, append: Boolean = false)
	{
		if (!append)
		{
			this.writeText(toWrite)

		} else
		{
			this.writeText(this.readToString() + toWrite)
		}
	}

	fun readToString(): String
	{
		return this.getLines().createString()
	}

}