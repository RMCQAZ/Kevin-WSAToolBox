package kevin.page.pages

import kevin.LogUtils
import kevin.Main
import kevin.StringUtils.stringGet
import kevin.page.Page
import java.awt.Component
import java.awt.Font
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class TaskManagerPage : Page("查看进程", Main.width/10*3,0,Main.width/10,Main.height/40*3) {
    private val taskTable = JTable(arrayOf(),arrayOf("进程名称","用户","PID","RES","%CPU","%MEM","状态"))
    private val taskTablePane by lazy {
        val scrollPane = JScrollPane(taskTable)
        scrollPane.setBounds(0,height/16*3,width - width/50,height - height/16*5)
        scrollPane.isVisible = false
        return@lazy scrollPane
    }
    private val refresh = JButton("刷新")
    //private val killTask = JButton("杀死")
    private val message = JLabel("Idle...")
    //private val taskPIDS = arrayListOf<String>()
    init {
        taskTable.columnSelectionAllowed = false
        taskTable.enableInputMethods(false)
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        taskTable.rowHeight = (height - height/16*5)/10
        taskTable.autoResizeMode = JTable.AUTO_RESIZE_OFF
        //taskTable.selectionModel.addListSelectionListener { killTask.isEnabled = taskTable.selectedRow != -1 && Main.adbState }
        taskTable.font = Font("微软雅黑",1,16)
        taskTable.columnModel.getColumn(0).preferredWidth = width/12*5
        taskTable.columnModel.getColumn(1).preferredWidth = width/6
        taskTable.columnModel.getColumn(2).preferredWidth = width/16
        taskTable.columnModel.getColumn(3).preferredWidth = width/16
        taskTable.columnModel.getColumn(4).preferredWidth = width/16
        taskTable.columnModel.getColumn(5).preferredWidth = width/16
        taskTable.columnModel.getColumn(6).preferredWidth = width/10
        val cellRenderer = object : DefaultTableCellRenderer(){
            override fun getTableCellRendererComponent(
                table: JTable?,
                value: Any?,
                isSelected: Boolean,
                hasFocus: Boolean,
                row: Int,
                column: Int
            ): Component {
                if (value is String) {
                    this.horizontalAlignment = CENTER
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            }
        }
        taskTable.columnModel.getColumn(0).cellRenderer = cellRenderer
        taskTable.columnModel.getColumn(1).cellRenderer = cellRenderer
        taskTable.columnModel.getColumn(2).cellRenderer = cellRenderer
        taskTable.columnModel.getColumn(3).cellRenderer = cellRenderer
        taskTable.columnModel.getColumn(4).cellRenderer = cellRenderer
        taskTable.columnModel.getColumn(5).cellRenderer = cellRenderer
        taskTable.columnModel.getColumn(6).cellRenderer = cellRenderer
        update()


        refresh.setBounds(width/50,height/10,width/20*3, height/40*3)
        refresh.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {
                message.text = "刷新中..."
                Main.window.update(Main.window.graphics)
                update()
                message.text = if (Main.adbState) "刷新完成" else "未连接到WSA"
            }
            override fun mousePressed(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })
        refresh.isVisible = false
/*
        killTask.setBounds(width/25 + width/20*3,height/10,width/20*3, height/40*3)
        killTask.addMouseListener(object : MouseListener{
            override fun mouseClicked(e: MouseEvent?) {
                if (!killTask.isEnabled) return
                message.text = "尝试杀死进程..."
                Main.window.update(Main.window.graphics)
                message.text = if (Main.adbState) {
                    val process = Runtime.getRuntime().exec("${Main.aDBCommand} shell kill ${taskPIDS[taskTable.selectedRow]}")
                    val br = BufferedReader(InputStreamReader(process.inputStream))
                    var line: String?
                    val lines = arrayListOf<String>()
                    while (br.readLine().also { line = it } != null) {
                        LogUtils.info("ADB: $line")
                        lines += line!!
                    }
                    br.close()
                    message.text = "${lines.last()},刷新中..."
                    Main.window.update(Main.window.graphics)
                    update()
                    "${lines.last()},刷新完成."
                } else "未连接到WSA"
            }
            override fun mousePressed(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })
        killTask.isVisible = false
*/
        message.setBounds(width/25 + width/20*3,height/10,width, height/40*3)
        message.font = Font("宋体",1,18)
        message.isVisible = false

        components += taskTablePane
        components += refresh
        //components += killTask
        components += message
        addAll()
    }
    fun update(){
        val columns = taskTable.columnModel.columns.toList()
        val taskList = if (Main.adbState) getTasks() else {
            LogUtils.noConnectionInfo()
            null
        }
        //taskPIDS.clear()
        //taskList?.forEach { taskPIDS += it[2] }
        taskTable.model = object : DefaultTableModel(taskList,arrayOf("进程名称","用户","PID","RES","%CPU","%MEM","状态")){
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }
        columns.forEach {
            taskTable.columnModel.removeColumn(taskTable.columnModel.getColumn(0))
            taskTable.columnModel.addColumn(it)
        }
        //killTask.isEnabled = taskTable.selectedRow != -1 && Main.adbState
    }
    private fun getTasks():Array<Array<String>>?{
        return try {
            LogUtils.info("获取所有进程信息")
            val process = Runtime.getRuntime().exec("${Main.aDBCommand} shell top -b -n 1")
            val br = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            val lines = arrayListOf<String>()
            while (br.readLine().also { line = it } != null) {
                lines += line!!
            }
            br.close()
            val taskList = arrayListOf<Array<String>>()
            repeat(5) {lines.removeFirst()}
            for (l in lines){
                //if (!l.removeRange(0..6).startsWith("u0_")) continue
                //var string = l
                //while (string.startsWith(" ")) string = string.removePrefix(" ")
                val array = l.stringGet(arrayOf(1,4,2,1,1,2),true,lastAll = true)
                val pid = array[0]//string.split(" ")[0]
                //string = string.remove()
                val user = array[1]//string.split(" ")[0]
                //string = string.remove(4)
                val res = array[2]//string.split(" ")[0]
                //string = string.remove(2)
                var state = array[3]//string.split(" ")[0]
                state = when(state){
                    "R" -> "运行中"
                    "Z" -> "僵尸"
                    "S" -> "睡眠"
                    "D" -> "睡眠(不可中断)"
                    "T" -> "跟踪/停止"
                    else -> state
                }
                //if (state == "Z") continue
                //string = string.remove()
                val cpu = array[4]//string.split(" ")[0]
                //string = string.remove()
                val mem = array[5]//string.split(" ")[0]
                //string = string.remove(2)
                val name = array[6]//string
                if (name.contains("top -b -n 1")) continue
                taskList += arrayOf(name,user,pid,res,cpu,mem,state)
            }
            LogUtils.info("获取所有进程信息成功")
            if (taskList.isEmpty()) null else taskList.toTypedArray()
        } catch (e: Exception) {
            LogUtils.error("获取所有进程信息时出错,$e")
            null
        }
    }
}