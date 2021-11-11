package kevin

import java.text.SimpleDateFormat

object LogUtils {
    val stringBuilder = StringBuilder()
    fun info(msg: String) = log("INFO",msg)
    fun warn(msg: String) = log("WARN",msg)
    fun error(msg: String) = log("ERROR",msg)
    fun debug(msg: String) = log("DEBUG",msg)
    fun noConnectionInfo() = info("未连接到WSA")
    private fun log(string: String,msg: String){
        val text = "[${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())}] [${Thread.currentThread().name}/$string]: $msg"
        println(text)
        stringBuilder.append(text).appendLine()
    }
}