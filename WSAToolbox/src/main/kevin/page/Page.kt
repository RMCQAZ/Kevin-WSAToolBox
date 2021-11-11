package kevin.page

import kevin.Main
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JButton

open class Page(name: String,x: Int,y: Int,width: Int,height: Int) {
    val components = hashSetOf<Component>()
    val button = JButton(name)
    val width = Main.width
    val height = Main.height
    init {
        button.setBounds(x, y, width, height)
        button.isContentAreaFilled = false
        button.addMouseListener(object : MouseListener{
            override fun mouseClicked(e: MouseEvent?) {
                Main.unloadAll()
                load()
            }
            override fun mousePressed(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })
        Main.window.add(button)
    }
    fun addAll() = components.forEach { Main.window.add(it) }
    fun getComponent(name: String) = components.firstOrNull { it.name == name }
    fun load(){
        components.forEach { it.isVisible = true }
        button.isEnabled = false
    }
    fun unload(){
        components.forEach { it.isVisible = false }
        button.isEnabled = true
    }
    fun create() {}
}