package org.plugin.plugin.panels

import java.awt.Component
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer


// Custom TableCellRenderer for displaying avatars in the second column
public class AvatarTableCellRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        if (column == 1 && value is String) {
            val label = JLabel(ImageIcon("/avatars/001-actress.png"))
            label.setHorizontalAlignment(CENTER)
            return label
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
    }
}