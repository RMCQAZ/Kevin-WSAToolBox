package kevin.page.pages

import kevin.LogUtils
import kevin.Main
import kevin.page.Page
import java.awt.Font
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.swing.JButton
import javax.swing.JLabel

class MainPage : Page("首页",0,0,Main.width/10,Main.height/40*3) {
    private val restartServer = JButton("重启ADB服务")
    private val startSettings = JButton("打开安卓设置")
    private val adbVersion = JLabel()
    private val messageConnected = JLabel()
    private val wsaVersion = JLabel()
    private val wsaKernel = JLabel()
    private val wsaMemory = JLabel()
    private val wsaCPU = JLabel()
    init {
        //RestartServer
        restartServer.setBounds(width/100*3,height/10,width/10*3,height/40*3)
        restartServer.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                try {
                    LogUtils.debug("执行Restart ADB server...")
                    val process = Runtime.getRuntime().exec("${Main.aDBCommand} kill-server")
                    val br = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        LogUtils.info("ADB: $line")
                    }
                    br.close()
                    LogUtils.info("Kill ADB Server 成功!")
                    messageConnected.text = "连接已断开..."
                    wsaVersion.text = "Reloading..."
                    wsaKernel.text = "Reloading..."
                    wsaMemory.text = "Reloading..."
                    wsaCPU.text = "Reloading..."
                    Main.window.update(Main.window.graphics)
                    val processStart = Runtime.getRuntime().exec("${Main.aDBCommand} connect 127.0.0.1:58526")
                    val brStart = BufferedReader(InputStreamReader(processStart.inputStream))
                    val lines = arrayListOf<String>()
                    while (brStart.readLine().also { line = it } != null) {
                        LogUtils.info("ADB: $line")
                        lines += line!!
                    }
                    brStart.close()
                    Main.adbState = if (!lines.last().contains("connected to")&&!lines.last().contains("already connected to")){
                        LogUtils.error("ADB连接错误,请启动WSA并打开开发人员模式然后尝试重新连接!")
                        false
                    } else {
                        LogUtils.info("Start ADB Server 成功!")
                        true
                    }
                    messageConnected.text = if (Main.adbState) "已连接到WSA!" else "未连接"
                    wsaVersion.text = if (Main.adbState) getWSAVersion() else "No connection"
                    wsaKernel.text = if (Main.adbState) getWSAKernel() else "No connection"
                    wsaMemory.text = if (Main.adbState) getWSAMemory() else "No connection"
                    wsaCPU.text = if (Main.adbState) getWSACPU() else "No connection"
                    Main.window.update(Main.window.graphics)
                    Main.appManager.update()
                    Main.taskManager.update()
                    Main.fileManager.update()
                    LogUtils.debug("Restart ADB server执行完成")
                }catch (e: IOException){
                    LogUtils.error("Restart ADB server error: $e")
                }
            }
            override fun mousePressed(e: MouseEvent) {}
            override fun mouseReleased(e: MouseEvent) {}
            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        })
        restartServer.isVisible = false
        //Start settings
        startSettings.setBounds(width/50*3 + width/10*3,height/10,width/10*3,height/40*3)
        startSettings.addMouseListener(object : MouseListener{
            override fun mouseClicked(e: MouseEvent?) {
                try {
                    LogUtils.info("尝试启动设置...")
                    if (!Main.adbState) {
                        LogUtils.info("未连接WSA,无法启动设置")
                        return
                    }
                    val process = Runtime.getRuntime().exec("${Main.aDBCommand}  shell monkey -p com.android.settings -c android.intent.category.LAUNCHER 1")
                    val br = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        LogUtils.info("ADB: $line")
                    }
                    br.close()
                    LogUtils.info("设置启动成功!")
                }catch (e: Exception){
                    LogUtils.error("启动设置时出现异常,$e")
                }
            }
            override fun mousePressed(e: MouseEvent) {}
            override fun mouseReleased(e: MouseEvent) {}
            override fun mouseEntered(e: MouseEvent) {}
            override fun mouseExited(e: MouseEvent) {}
        })
        startSettings.isVisible = false
        //ADBVersion
        adbVersion.setBounds(width/100*3,height/5+height/40*3,width/4*3,height/40*3)
        adbVersion.font = Font("宋体",1,20)
        adbVersion.text = getAdbVersion()
        adbVersion.isVisible = false
        //MessageConnected
        messageConnected.setBounds(width/100*3,height/5,width/4*3,height/40*3)
        messageConnected.font = Font("宋体",1,25)
        messageConnected.text = if (Main.adbState) "已连接到WSA!" else "未连接"
        messageConnected.isVisible = false
        //WSAVersion
        wsaVersion.setBounds(width/50*3,height/5+height/20*3,width/4*3,height/40*3)
        wsaVersion.font = Font("宋体",1,18)
        wsaVersion.text = if (Main.adbState) getWSAVersion() else "No connection"
        wsaVersion.isVisible = false
        //WSAKernel
        wsaKernel.setBounds(width/50*3,height/5+height/40*9,width,height/40*3)
        wsaKernel.font = Font("宋体",1,18)
        wsaKernel.text = if (Main.adbState) getWSAKernel() else "No connection"
        wsaKernel.isVisible = false
        //WSAMemory
        wsaMemory.setBounds(width/50*3,height/5+height/10*3,width,height/40*3)
        wsaMemory.font = Font("宋体",1,18)
        wsaMemory.text = if (Main.adbState) getWSAMemory() else "No connection"
        wsaMemory.isVisible = false
        //WSACPU
        wsaCPU.setBounds(width/50*3,height/5+height/8*3,width,height/40*3)
        wsaCPU.font = Font("宋体",1,18)
        wsaCPU.text = if (Main.adbState) getWSACPU() else "No connection"
        wsaCPU.isVisible = false

        components.add(restartServer)
        components += startSettings
        components.add(adbVersion)
        components.add(messageConnected)
        components.add(wsaVersion)
        components.add(wsaKernel)
        components.add(wsaMemory)
        components.add(wsaCPU)
        addAll()
    }
    private fun getWSACPU(): String{
        return try {
            val process = Runtime.getRuntime().exec("${Main.aDBCommand} shell cat /proc/cpuinfo")
            val br = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val lines = arrayListOf<String>()
            while (br.readLine().also { line = it } != null) {
                LogUtils.info("ADB: $line")
                lines += line!!
            }
            br.close()
            val core = lines.first { it.startsWith("cpu cores") }.replace(" ","").replace("\t","").replace("cpucores:","").toInt()
            val thread = lines.filter { it.startsWith("processor") }.size
            val cpuName = lines.first { it.startsWith("model name") }.replace("model name\t: ","")
            val frequency = lines.first { it.startsWith("cpu MHz") }.replace("cpu MHz\t\t: ","")
            "处理器: $cpuName (${core}核${thread}线程,基准频率:${frequency}MHz)"
        } catch (e: Exception){
            LogUtils.error("获取WSA处理器错误,$e")
            "处理器: Error"
        }
    }
    private fun getWSAMemory(): String{
        return try {
            val process = Runtime.getRuntime().exec("${Main.aDBCommand} shell cat /proc/meminfo")
            val br = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val lines = arrayListOf<String>()
            while (br.readLine().also { line = it } != null) {
                LogUtils.info("ADB: $line")
                lines += line!!
            }
            br.close()
            val mem = lines.first().replace(" ","").replace("MemTotal:","").replace("kB","",true).toInt()
            "内存: ${mem/1024}MB(${mem}KB)"
        } catch (e: Exception){
            LogUtils.error("获取WSA内存错误,$e")
            "内存: Error"
        }
    }
    private fun getWSAKernel(): String{
        return try {
            val process = Runtime.getRuntime().exec("${Main.aDBCommand} shell cat /proc/version")
            val br = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val lines = arrayListOf<String>()
            while (br.readLine().also { line = it } != null) {
                LogUtils.info("ADB: $line")
                lines += line!!
            }
            br.close()
            val message = lines.first().split(" ")
            "安卓内核: ${message[0]} ${message[1]} ${message[2]}"
        } catch (e: Exception){
            LogUtils.error("获取WSA内核错误,$e")
            "安卓内核: Error"
        }
    }
    private fun getWSAVersion(): String{
        return try {
            val process = Runtime.getRuntime().exec("${Main.aDBCommand} shell getprop ro.build.version.release")
            val br = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val lines = arrayListOf<String>()
            while (br.readLine().also { line = it } != null) {
                LogUtils.info("ADB: $line")
                lines += line!!
            }
            br.close()
            "安卓版本: ${lines.first()}"
        } catch (e: Exception){
            LogUtils.error("获取WSA版本错误,$e")
            "安卓版本: Error"
        }
    }
    private fun getAdbVersion(): String{
        return try {
            val process = Runtime.getRuntime().exec("${Main.aDBCommand} version")
            val br = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val lines = arrayListOf<String>()
            while (br.readLine().also { line = it } != null) {
                LogUtils.info("ADB: $line")
                lines += line!!
            }
            br.close()
            "${lines.first()} (${if(Main.aDBCommand =="adb")"系统" else "内置"})"
        } catch (e: Exception){
            LogUtils.error("获取ADB版本错误,$e")
            "Adb Version: Error"
        }
    }
}