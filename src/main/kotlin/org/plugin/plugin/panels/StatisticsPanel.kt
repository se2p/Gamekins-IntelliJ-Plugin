package org.plugin.plugin.panels

import com.google.gson.Gson
import com.intellij.ui.JBColor
import org.plugin.plugin.Constants
import org.plugin.plugin.data.RestClient
import org.plugin.plugin.data.Statistics
import org.plugin.plugin.data.UserList
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

val fontSize = 10
val lCustomFont = Font("SansSerif", Font.PLAIN, fontSize)

class StatisticsPanel : JPanel() {

    init {
        
        this.layout = GridBagLayout()

        val queryParams = mapOf(
            "job" to Constants.TEST_JOB
        )

        val response = RestClient().get(Constants.API_BASE_URL + Constants.GET_STATISTICS, queryParams)
        val lStatistics = Gson().fromJson(response, Statistics::class.java)

        val lTestxmlTextArea = JTextArea(lStatistics.toString())


        lTestxmlTextArea.font = lCustomFont;
        lTestxmlTextArea.isEditable = false

        val gbcc = GridBagConstraints()
        gbcc.gridx = 0
        gbcc.gridy = 0
        gbcc.gridwidth = 0;
        gbcc.gridwidth = 0;

        lTestxmlTextArea.setBackground(JBColor.GRAY)
        this.add(lTestxmlTextArea, gbcc)

        gbcc.gridy = 1
        gbcc.weightx = 0.0
        gbcc.weighty = 0.0


        this.add(JLabel("Statistics"), gbcc)
        
    }
}

