package editor.about;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Trifindo, JackHack96
 */
public class AboutDialog extends JDialog {
    public AboutDialog(Window owner) {
        super(owner);
        initComponents();
    }

    private void jButton1ActionPerformed(ActionEvent e) {
        dispose();
    }

    private void label2MouseClicked(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/Trifindo/Pokemon-DS-Map-Studio"));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jlVersionName = new JLabel();
        label1 = new JLabel();
        label2 = new JLabel();
        jLabel2 = new JLabel();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        panel2 = new JPanel();
        jLabel6 = new JLabel();
        jButton1 = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About");
        setResizable(false);
        var contentPane = getContentPane();

        //---- jlVersionName ----
        jlVersionName.setFont(new Font("Tahoma", Font.PLAIN, 18));
        jlVersionName.setHorizontalAlignment(SwingConstants.CENTER);
        jlVersionName.setText("Pokemon DS Map Studio 1.20");

        //---- label1 ----
        label1.setText("-- by Trifindo --");
        label1.setHorizontalAlignment(SwingConstants.CENTER);

        //---- label2 ----
        label2.setText("<html><body><a href=\"https://github.com/Trifindo/Pokemon-DS-Map-Studio\">Official website</a></body></html>");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                label2MouseClicked(e);
            }
        });

        //---- jLabel2 ----
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setIcon(new ImageIcon(getClass().getResource("/icons/trifindo.gif")));

        //======== jScrollPane1 ========
        {

            //---- jTextArea1 ----
            jTextArea1.setEditable(false);
            jTextArea1.setColumns(20);
            jTextArea1.setRows(5);
            jTextArea1.setText("Pokemon DS Map Studio is a tool for creating gen 4 and gen 5 Pok\u00e9mon games' maps,\ndesigned to be used alongside SDSME.\n\nIt doesn't require 3D modeling, instead it provides a tilemap-like interface that is automatically \nconverted in a 3D model.\nNote that this tool DOES NOT import maps from the original games, neither it can modify them.\n\nSupported games:\n- Pokemon Diamond/Pearl\n- Pokemon Platinum\n- Pokemon Heart Gold/Soul Silver\n\nNot completely working:\n- Pokemon Black/White\n- Pokemon Black 2/ White 2");
            jTextArea1.setLineWrap(true);
            jTextArea1.setFont(UIManager.getFont("TextArea.font"));
            jScrollPane1.setViewportView(jTextArea1);
        }

        //======== panel2 ========
        {
            panel2.setLayout(new BorderLayout());

            //---- jLabel6 ----
            jLabel6.setForeground(new Color(0, 0, 204));
            jLabel6.setText("<html>\n    <body>\n        <font color=\"orange\">JackHack96</font><br/>\n        <font color=\"blue\">Mikelan98</font><br/>\n        <font color=\"red\">Driox</font><br/>\n        <font color=\"purple\">Jiboule</font><br/>\n        <font color=\"cyan\">Nextworld</font><br/>\n        <font color=\"pink\">Jay</font><br/>\n        <font color=\"brown\">Brom</font><br/>\n        <font color=\"yellow\">AdAstra</font><br/>\n        <font color=\"gray\">Ren\u00e9</font><br/>\n    </body>\n</html>\n");
            jLabel6.setBorder(new TitledBorder("Credits"));
            panel2.add(jLabel6, BorderLayout.CENTER);
        }

        //---- jButton1 ----
        jButton1.setText("OK");
        jButton1.addActionListener(e -> jButton1ActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(jlVersionName, GroupLayout.PREFERRED_SIZE, 788, GroupLayout.PREFERRED_SIZE)
                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 788, GroupLayout.PREFERRED_SIZE)
                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 788, GroupLayout.PREFERRED_SIZE)
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(jLabel2)
                    .addGap(5, 5, 5)
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 719, GroupLayout.PREFERRED_SIZE))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(69, 69, 69)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 719, GroupLayout.PREFERRED_SIZE))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(355, 355, 355)
                    .addComponent(jButton1))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(jlVersionName, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                    .addGap(6, 6, 6)
                    .addComponent(label1)
                    .addGap(7, 7, 7)
                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(5, 5, 5)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
                    .addGap(5, 5, 5)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
                    .addGap(6, 6, 6)
                    .addComponent(jButton1))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel jlVersionName;
    private JLabel label1;
    private JLabel label2;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    private JPanel panel2;
    private JLabel jLabel6;
    private JButton jButton1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
