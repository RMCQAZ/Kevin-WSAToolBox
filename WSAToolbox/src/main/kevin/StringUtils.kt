package kevin

object StringUtils {
    private fun String.remove(times: Int): String{
        var string = this
        repeat(times){
            string = string.removePrefix(string.split(" ")[0])
            while (string.startsWith(" ")) string = string.removePrefix(" ")
        }
        return string
    }
    private fun String.getSplitFirst() = this.split(" ")[0]
    fun String.stringGet(removeTimes: Array<Int>,removeSpaceFirst: Boolean,lastAll: Boolean): Array<String>{
        var string = this
        val arrayList = arrayListOf<String>()
        if (removeSpaceFirst){
            while (string.startsWith(" ")) string = string.removePrefix(" ")
            arrayList += string.getSplitFirst()
        }
        var c = 0
        removeTimes.forEach {
            c += 1
            string = string.remove(it)
            arrayList += if(c==removeTimes.size&&lastAll) string else string.getSplitFirst()
        }
        return arrayList.toTypedArray()
    }
    fun String.pathUp():String{
        var string = this
        string = string.removeSuffix("/")
        while (string.last()!='/') string = string.removeSuffix(string.last().toString())
        if (string!="/") string = string.removeSuffix("/")
        return string
    }
}