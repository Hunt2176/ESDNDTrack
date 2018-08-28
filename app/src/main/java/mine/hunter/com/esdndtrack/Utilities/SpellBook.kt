package mine.hunter.com.esdndtrack.Utilities


class SpellBook {
    var spells = ArrayList<Spell>()

    fun sortByLevel() {

        fun swap(indexA: Int, indexB: Int){
            val temp = spells[indexA]
            spells[indexA] = spells[indexB]
            spells[indexB] = temp
        }

        for(i in 0..spells.size - 1) {
            var j = i
            while (j > 0 && spells[j-1].level > spells[j].level){
                swap(j, j - 1)
                j -= 1
            }
        }
    }
}

class Spell(name: String, description: String, level: Int, element: String? = null, range: String, duration: String? = null, prepared: Boolean = false) {
    val name: String
    val desc: String
    var element: String?
    var duration: String?
    var level: Int
    var range: String
    var prepared = prepared

    init {
        this.name = name
        this.desc = description
        this.level = level
        this.element = element
        this.range = range
        this.duration = duration
    }

}

open class ReadInSpell(name: String, desc: String, range: String, duration: String, level: String, ritual: String, casting_time: String, custom: Boolean = false){
    val name = name
    val desc = desc
    val range = range
    val duration = duration
    val level = level
    val ritual = ritual
    val casting_time = casting_time
    var custom = custom
}
