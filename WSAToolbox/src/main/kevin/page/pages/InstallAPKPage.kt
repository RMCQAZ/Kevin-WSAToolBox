package kevin.page.pages

import kevin.LogUtils
import kevin.Main
import kevin.page.Page
import java.awt.Color
import java.awt.Font
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.filechooser.FileFilter

class InstallAPKPage : Page("安装APK", Main.width/10,0,Main.width/10,Main.height/40*3) {
    private val pathText by lazy { JLabel() }
    private val apkPath by lazy { JTextField() }
    private val installButton by lazy { JButton() }
    private val pathButton by lazy { JButton() }
    private val infoText by lazy { JLabel() }
    init {
        pathText.setBounds(width/100*3,height/10,width/10*3,height/40*3)
        pathText.font = Font("宋体",1,20)
        pathText.text = "路径:"
        pathText.isVisible = false

        apkPath.setBounds(width/10,height/10,width - width/20*3,height/40*3)
        apkPath.font = Font("宋体",1,18)
        apkPath.addCaretListener { if(File(apkPath.text).isFile&& apkPath.text.endsWith(".apk",true)) apkPath.foreground = Color(100,200,100) else apkPath.foreground = Color(255,100,100) }
        apkPath.isVisible = false

        installButton.setBounds(width/100*3,height/10 + height/40*3,width - width/20*3 + width/10 - width/100*3,height/40*3)
        installButton.text = "安装"
        installButton.isVisible = false
        installButton.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                try {
                    LogUtils.debug("尝试安装...")
                    if(File(apkPath.text).isFile&& apkPath.text.endsWith(".apk",true)) {
                        if (!Main.adbState) {
                            infoText.text = "未连接至WSA,请连接后重试"
                            LogUtils.debug("未连接至WSA,请连接后重试")
                            return
                        }
                        infoText.text = "安装中..."
                        Main.window.update(Main.window.graphics)
                        val process = Runtime.getRuntime().exec("${Main.aDBCommand} install \"${apkPath.text}\"")
                        val br = BufferedReader(InputStreamReader(process.inputStream))
                        var line: String?
                        val lines = arrayListOf<String>()
                        while (br.readLine().also { line = it } != null) {
                            LogUtils.info("ADB: $line")
                            lines += line!!
                        }
                        br.close()
                        infoText.text = "${lines.first()},${lines.last()}"
                        LogUtils.debug("安装结束")
                    } else {
                        infoText.text = "路径不正确或不支持的文件类型"
                        LogUtils.debug("路径不正确或不支持的文件类型")
                    }
                }catch (e:Exception){
                    LogUtils.error("安装错误,$e")
                    infoText.text = "安装错误,$e"
                }
            }
            override fun mousePressed(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })

        pathButton.setBounds(width/100*3,height/10 + height/20*3,width - width/20*3 + width/10 - width/100*3,height/40*3)
        pathButton.text = "选择APK"
        pathButton.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {
                try {
                    LogUtils.debug("选择APK")
                    val jFileChooser = JFileChooser()
                    jFileChooser.dialogTitle = "请选择APK文件"
                    jFileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    jFileChooser.isMultiSelectionEnabled = false
                    jFileChooser.isAcceptAllFileFilterUsed = false
                    jFileChooser.addChoosableFileFilter(object : FileFilter() {
                        override fun accept(pathname: File) = pathname.isDirectory || pathname.toString().endsWith(".apk")
                        override fun getDescription() = "安装包(*.apk)"
                    })
                    if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                        apkPath.text = jFileChooser.selectedFile.toString()
                    } else LogUtils.info("取消")
                    LogUtils.debug("选择完成")
                } catch (e:Exception){
                    LogUtils.error("选择错误,$e")
                }
            }
            override fun mousePressed(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })
        pathButton.isVisible = false

        infoText.setBounds(width/100*3,height/10 + height/40*9,width - width/20*3 + width/10 - width/100*3,height/40*3)
        infoText.isVisible = false
        infoText.text = "Idle..."

        components.add(pathText)
        components.add(apkPath)
        components.add(installButton)
        components.add(pathButton)
        components.add(infoText)
        addAll()
    }
}