package kevin

import kevin.page.pages.*
import java.awt.GraphicsEnvironment
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JFrame


fun main() {
    Main.main()
}

object Main {
    private val adb by lazy {
        LogUtils.info("没有发现系统ADB或系统ADB异常，启用内置ADB")
        isInADBEnabled = true
        val tmp = System.getProperty("java.io.tmpdir")
        val tmpDir = Files.createTempDirectory(Paths.get(tmp),"KevinWSAToolBox-ADB")
        val tmpDirFile = tmpDir.toFile()
        LogUtils.info("内置ADB缓存路径:$tmpDirFile,缓存将在退出时删除")
        val adb = this::class.java.getResourceAsStream("/platform-tools/adb.exe")
        val fos = FileOutputStream(File("$tmpDirFile\\adb.exe"))
        fos.write(adb!!.readAllBytes())
        adb.close()
        fos.close()
        LogUtils.info("释放ADB成功")
        val winApi = this::class.java.getResourceAsStream("/platform-tools/AdbWinApi.dll")
        val winApiFos = FileOutputStream(File("$tmpDirFile\\AdbWinApi.dll"))
        winApiFos.write(winApi!!.readAllBytes())
        winApi.close()
        winApiFos.close()
        LogUtils.info("释放AdbWinApi成功")
        val winUsbApi = this::class.java.getResourceAsStream("/platform-tools/AdbWinUsbApi.dll")
        val winUsbApiFos = FileOutputStream(File("$tmpDirFile\\AdbWinUsbApi.dll"))
        winUsbApiFos.write(winUsbApi!!.readAllBytes())
        winUsbApi.close()
        winUsbApiFos.close()
        LogUtils.info("释放AdbWinUsbApi成功")
        LogUtils.info("释放文件成功!")
        File("$tmpDirFile\\adb.exe")
    }
    private var isInADBEnabled = false
    var aDBCommand = ""
    val window by lazy { JFrame() }
    var adbState = false
    private val displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayMode
    val width = displayMode.width/3
    val height = displayMode.height/3

    private val main by lazy { MainPage() }
    private val installAPK by lazy { InstallAPKPage() }
    val appManager by lazy { AppManagerPage() }
    val taskManager by lazy { TaskManagerPage() }
    val fileManager by lazy { FileManagerPage() }
    private val settings by lazy { SettingsPage() }

    fun main(){
        Thread.currentThread().name = "kevin.Main-Thread"
        LogUtils.debug("开始ADB连接...")
        val process = try {
            aDBCommand = "adb"
            Runtime.getRuntime().exec("adb connect 127.0.0.1:58526")
        } catch (e: IOException) {
            aDBCommand = "\"$adb\""
            Runtime.getRuntime().exec("\"$adb\" connect 58526")
        }
        val br = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        val lines = arrayListOf<String>()
        while (br.readLine().also { line = it } != null) {
            LogUtils.info("ADB: $line")
            lines += line!!
        }
        br.close()
        adbState = if (!lines.last().contains("connected to")&&!lines.last().contains("already connected to")){
            LogUtils.error("ADB连接错误,请启动WSA并打开开发人员模式然后尝试重新连接!")
            false
        } else true
        LogUtils.debug("ADB连接完成")
        loadWindow()
    }
    private fun loadWindow(){
        LogUtils.debug("初始化窗口")
        window.layout = null
        window.setSize(width, height)
        window.setLocationRelativeTo(null)
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        window.isResizable = false
        window.title = "Kevin WSA ToolBox|Kevin WSA 工具箱 V1.1"
        window.addWindowListener(object : WindowListener{
            override fun windowClosing(e: WindowEvent?) {
                if (isInADBEnabled) Runtime.getRuntime().exec("$aDBCommand kill-server")
                for (file in File(System.getProperty("java.io.tmpdir")).listFiles()?.filter { it.isDirectory && it.name.startsWith("KevinWSAToolBox",true) }?:return){
                    file.listFiles()!!.forEach { it.delete();LogUtils.info("清除$it") }
                    file.delete()
                    LogUtils.info("清除$file")
                }
                LogUtils.info("缓存清除完成.")
            }
            override fun windowOpened(e: WindowEvent?) {}
            override fun windowClosed(e: WindowEvent?) {}
            override fun windowIconified(e: WindowEvent?) {}
            override fun windowDeiconified(e: WindowEvent?) {}
            override fun windowActivated(e: WindowEvent?) {}
            override fun windowDeactivated(e: WindowEvent?) {}
        })
        main.load()
        installAPK.create()
        appManager.create()
        taskManager.create()
        fileManager.create()
        settings.create()
        window.isVisible = true
        LogUtils.debug("初始化窗口完成")
    }
    fun unloadAll(){
        main.unload()
        installAPK.unload()
        appManager.unload()
        taskManager.unload()
        fileManager.unload()
        settings.unload()
    }
}