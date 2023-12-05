package org.plugin.plugin.components

import java.awt.Color
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import java.awt.Component


class IconCellRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component? {
        val label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        if (value is ImageIcon) {
            (label as JLabel).icon = value
            (label as JLabel).text = null
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
        }

        if (label is JComponent) {
            label.border = BorderFactory.createLineBorder(Color.GRAY, 1)
        }

        return label
    }
}