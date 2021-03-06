/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.animationeditor;

import editor.handler.MapEditorHandler;
import editor.nsbtx2.Nsbtx2;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import utils.Utils;
import utils.Utils.MutableBoolean;

/**
 *
 * @author Trifindo
 */
public class AnimationEditorDialog extends javax.swing.JDialog {

    private MapEditorHandler handler;
    private AnimationHandler animHandler;

    private static final String playButtonIcon = "▶";
    private static final String stopButtonIcon = "◼";
    private static final Color playButtonColor = new Color(0, 153, 0);
    private static final Color stopButtonColor = new Color(255, 51, 51);

    private static final Color editingColor = new Color(255, 200, 200);
    private static final Color rightColor = new Color(200, 255, 200);

    private MutableBoolean jtfAnimNameEnabled = new MutableBoolean(true);
    private boolean animationListEnabled = true;
    private boolean textureListEnabled = true;

    /**
     * Creates new form AnimationEditorDialog
     */
    public AnimationEditorDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        jScrollPane1.getHorizontalScrollBar().setUnitIncrement(AnimationFramesDisplay.cellSize);

        Utils.addListenerToJTextFieldColor(jtfAnimationName, jtfAnimNameEnabled, editingColor);

        System.out.println(jbPlay.getText());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        animationDisplay = new AnimationDisplay();
        jbPlay = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        animationFramesDisplay = new AnimationFramesDisplay();
        jPanel4 = new javax.swing.JPanel();
        jbOpenNsbtx = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jlTextureNames = new javax.swing.JList<>();
        jbAddFrame = new javax.swing.JButton();
        jbRemoveFrame = new javax.swing.JButton();
        jsDelay = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlAnimationNames = new javax.swing.JList<>();
        jbOpenAnimationFile = new javax.swing.JButton();
        jbSaveAnimationFile = new javax.swing.JButton();
        jbAddAnimation = new javax.swing.JButton();
        jbRemoveAnimation = new javax.swing.JButton();
        jtfAnimationName = new javax.swing.JTextField();
        jbApply = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Animation Editor");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Animation Display"));

        animationDisplay.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(102, 102, 102)));

        javax.swing.GroupLayout animationDisplayLayout = new javax.swing.GroupLayout(animationDisplay);
        animationDisplay.setLayout(animationDisplayLayout);
        animationDisplayLayout.setHorizontalGroup(
            animationDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 158, Short.MAX_VALUE)
        );
        animationDisplayLayout.setVerticalGroup(
            animationDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 158, Short.MAX_VALUE)
        );

        jbPlay.setForeground(new Color(0, 153, 0));
        jbPlay.setText("▶");
        jbPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbPlayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(animationDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jbPlay)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(animationDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jbPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Frames"));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        animationFramesDisplay.setPreferredSize(new java.awt.Dimension(510, 96));

        javax.swing.GroupLayout animationFramesDisplayLayout = new javax.swing.GroupLayout(animationFramesDisplay);
        animationFramesDisplay.setLayout(animationFramesDisplayLayout);
        animationFramesDisplayLayout.setHorizontalGroup(
            animationFramesDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
        );
        animationFramesDisplayLayout.setVerticalGroup(
            animationFramesDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(animationFramesDisplay);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("NSBTX File"));

        jbOpenNsbtx.setText("Open NSBTX");
        jbOpenNsbtx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOpenNsbtxActionPerformed(evt);
            }
        });

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(130, 130));

        jlTextureNames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlTextureNames.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlTextureNamesValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jlTextureNames);

        jbAddFrame.setText("Add Frame");
        jbAddFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddFrameActionPerformed(evt);
            }
        });

        jbRemoveFrame.setText("Remove Frame");
        jbRemoveFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveFrameActionPerformed(evt);
            }
        });

        jsDelay.setModel(new javax.swing.SpinnerNumberModel(0, 0, 254, 1));
        jsDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsDelayStateChanged(evt);
            }
        });

        jLabel2.setText("Delay:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbRemoveFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbAddFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbOpenNsbtx, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jsDelay)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jbOpenNsbtx)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbAddFrame)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveFrame)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jsDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Animation List"));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(130, 130));

        jlAnimationNames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jlAnimationNames.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jlAnimationNamesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jlAnimationNames);

        jbOpenAnimationFile.setText("Open Animation File");
        jbOpenAnimationFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOpenAnimationFileActionPerformed(evt);
            }
        });

        jbSaveAnimationFile.setText("Save Animation File");
        jbSaveAnimationFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSaveAnimationFileActionPerformed(evt);
            }
        });

        jbAddAnimation.setText("Add Animation");
        jbAddAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddAnimationActionPerformed(evt);
            }
        });

        jbRemoveAnimation.setText("Remove Animation");
        jbRemoveAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRemoveAnimationActionPerformed(evt);
            }
        });

        jtfAnimationName.setText(" ");

        jbApply.setText("Apply");
        jbApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbApplyActionPerformed(evt);
            }
        });

        jLabel1.setText("Animation name:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbOpenAnimationFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbSaveAnimationFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbAddAnimation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbRemoveAnimation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jtfAnimationName, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbApply))
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jbOpenAnimationFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbSaveAnimationFile)
                        .addGap(18, 18, 18)
                        .addComponent(jbAddAnimation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveAnimation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtfAnimationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbApply))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbOpenAnimationFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOpenAnimationFileActionPerformed
        if (animHandler != null) {
            openAnimationFileWithDialog();
        }
    }//GEN-LAST:event_jbOpenAnimationFileActionPerformed

    private void jbSaveAnimationFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSaveAnimationFileActionPerformed
        if (animHandler != null) {
            saveAnimationFileWithDialog();
        }
    }//GEN-LAST:event_jbSaveAnimationFileActionPerformed

    private void jbAddAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddAnimationActionPerformed
        if (animHandler != null) {
            if (animationListEnabled && !animHandler.isAnimationRunning()) {
                if (animHandler.getAnimationFile() != null) {
                    animHandler.addAnimation("New animation");
                    updateViewAnimationListNames(jlAnimationNames.getModel().getSize());
                    repaintFrames();
                }
            }
        }
    }//GEN-LAST:event_jbAddAnimationActionPerformed

    private void jbRemoveAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveAnimationActionPerformed
        if (animHandler != null) {
            if (animationListEnabled && !animHandler.isAnimationRunning()) {
                if (animHandler.getAnimationSelected() != null) {
                    int index = getAnimationSelectedIndex();
                    animHandler.getAnimationFile().removeAnimation(getAnimationSelectedIndex());
                    updateViewAnimationListNames(index);
                    repaintFrames();
                }
            }
        }

    }//GEN-LAST:event_jbRemoveAnimationActionPerformed

    private void jlAnimationNamesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlAnimationNamesValueChanged
        updateView();
    }//GEN-LAST:event_jlAnimationNamesValueChanged

    private void jbOpenNsbtxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOpenNsbtxActionPerformed
        openNsbtxWithDialog();
    }//GEN-LAST:event_jbOpenNsbtxActionPerformed

    private void jbPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbPlayActionPerformed
        togglePlay();
    }//GEN-LAST:event_jbPlayActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        animHandler.pauseAnimation();
    }//GEN-LAST:event_formWindowClosed

    private void jsDelayStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsDelayStateChanged
        if (animHandler != null) {
            if (animationListEnabled && !animHandler.isAnimationRunning()) {
                animHandler.setCurrentDelay((Integer) jsDelay.getValue());
                repaintFrames();
            }
        }

    }//GEN-LAST:event_jsDelayStateChanged

    private void jlTextureNamesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jlTextureNamesValueChanged
        if (animHandler != null) {
            if (textureListEnabled && !animHandler.isAnimationRunning()) {
                animHandler.setCurrentTexture(jlTextureNames.getSelectedIndex());
                repaintFrames();
            }
        }
    }//GEN-LAST:event_jlTextureNamesValueChanged

    private void jbApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbApplyActionPerformed
        if (animHandler != null) {
            if (animHandler.getAnimationSelected() != null) {
                changeAnimationName();
            }
        }
    }//GEN-LAST:event_jbApplyActionPerformed

    private void jbAddFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddFrameActionPerformed
        addFrame();
    }//GEN-LAST:event_jbAddFrameActionPerformed

    private void jbRemoveFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRemoveFrameActionPerformed
        removeFrame();
    }//GEN-LAST:event_jbRemoveFrameActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnimationEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnimationEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnimationEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnimationEditorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AnimationEditorDialog dialog = new AnimationEditorDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private AnimationDisplay animationDisplay;
    private AnimationFramesDisplay animationFramesDisplay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton jbAddAnimation;
    private javax.swing.JButton jbAddFrame;
    private javax.swing.JButton jbApply;
    private javax.swing.JButton jbOpenAnimationFile;
    private javax.swing.JButton jbOpenNsbtx;
    private javax.swing.JButton jbPlay;
    private javax.swing.JButton jbRemoveAnimation;
    private javax.swing.JButton jbRemoveFrame;
    private javax.swing.JButton jbSaveAnimationFile;
    private javax.swing.JList<String> jlAnimationNames;
    private javax.swing.JList<String> jlTextureNames;
    private javax.swing.JSpinner jsDelay;
    private javax.swing.JTextField jtfAnimationName;
    // End of variables declaration//GEN-END:variables

    public void init(MapEditorHandler handler) {
        this.handler = handler;
        this.animHandler = new AnimationHandler(this);

        this.animationDisplay.init(animHandler);
        this.animationFramesDisplay.init(animHandler);
    }

    public void updateView() {
        if (animHandler.getAnimationFile() != null) {
            if (animHandler.getAnimationSelected() != null) {
                jtfAnimNameEnabled.value = false;
                jtfAnimationName.setText(animHandler.getAnimationSelected().getName());
                jtfAnimationName.setBackground(Color.white);
                jtfAnimNameEnabled.value = true;
            }

            animationDisplay.repaint();

            animationFramesDisplay.updateSize();
            animationFramesDisplay.repaint();

            updateViewDelayDisplay();
        }

    }

    public void repaintFrames() {
        animationDisplay.repaint();

        animationFramesDisplay.repaint();

        updateViewTexturesNsbxt();

        updateViewDelayDisplay();
    }

    public void updateViewTexturesNsbxt() {
        jlTextureNames.setSelectedIndex(animHandler.getCurrentNsbtxTextureIndex());
    }

    public void updateViewDelayDisplay() {
        jsDelay.setValue(animHandler.getCurrentDelay());
    }

    public void updateViewAnimationListNames(int indexSelected) {
        if (animHandler.getAnimationFile() != null) {
            animationListEnabled = false;
            DefaultListModel demoList = new DefaultListModel();
            for (int i = 0; i < animHandler.getAnimationFile().size(); i++) {
                String name = animHandler.getAnimationFile().getAnimation(i).getName();
                demoList.addElement(name);
            }
            jlAnimationNames.setModel(demoList);
            if (indexSelected > demoList.size() - 1) {
                indexSelected = demoList.size() - 1;
            } else if (indexSelected < 0) {
                indexSelected = 0;
            }
            jlAnimationNames.setSelectedIndex(indexSelected);
            jlAnimationNames.ensureIndexIsVisible(indexSelected);
            animationListEnabled = true;
        }
    }

    public void updateViewTextureNames(int indexSelected) {
        if (animHandler.getNsbtx() != null) {
            textureListEnabled = false;
            DefaultListModel demoList = new DefaultListModel();
            for (int i = 0; i < animHandler.getNsbtx().getTextures().size(); i++) {
                String name = animHandler.getNsbtx().getTexture(i).getName();
                demoList.addElement(name);
            }
            jlTextureNames.setModel(demoList);
            if (indexSelected > demoList.size() - 1) {
                indexSelected = demoList.size() - 1;
            } else if (indexSelected < 0) {
                indexSelected = 0;
            }
            jlTextureNames.setSelectedIndex(indexSelected);
            textureListEnabled = true;
        }
    }

    private void togglePlay() {
        if (animHandler.getAnimationFile() != null) {
            if (animHandler.isAnimationRunning()) {
                animHandler.pauseAnimation();
                jbPlay.setText(playButtonIcon);
                jbPlay.setForeground(playButtonColor);
                setComponentsEnabled(true);
            } else {
                animHandler.playAnimation();
                jbPlay.setText(stopButtonIcon);
                jbPlay.setForeground(stopButtonColor);
                setComponentsEnabled(false);
            }
        }
    }

    private void setComponentsEnabled(boolean enabled) {
        jbAddAnimation.setEnabled(enabled);
        jbAddFrame.setEnabled(enabled);
        jbApply.setEnabled(enabled);
        jbOpenAnimationFile.setEnabled(enabled);
        jbOpenNsbtx.setEnabled(enabled);
        jbRemoveAnimation.setEnabled(enabled);
        jbRemoveFrame.setEnabled(enabled);
        jbSaveAnimationFile.setEnabled(enabled);
        jsDelay.setEnabled(enabled);
        jtfAnimationName.setEnabled(enabled);
        jlTextureNames.setEnabled(enabled);
    }

    public void changeAnimationName() {
        String name = jtfAnimationName.getText();
        if (name.length() <= Nsbtx2.maxNameSize) {
            jtfAnimNameEnabled.value = false;
            animHandler.getAnimationSelected().setName(name);
            jtfAnimationName.setBackground(rightColor);
            jtfAnimNameEnabled.value = true;

            updateViewAnimationListNames(jlAnimationNames.getSelectedIndex());
        } else {
            JOptionPane.showMessageDialog(this,
                    "The animation name has more than 16 characters",
                    "The name is too long",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void openAnimationFileWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastNsbtxDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
        }
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Animation File File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                animHandler.readAnimationFile(fc.getSelectedFile().getPath());

                updateView();

                updateViewAnimationListNames(0);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening animation file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openNsbtxWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastNsbtxDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open NSBTX File");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                animHandler.readNsbtx(fc.getSelectedFile().getPath());

                updateView();
                updateViewTextureNames(0);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file.",
                        "Error opening NSBTX", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAnimationFileWithDialog() {
        if (animHandler.getAnimationFile() != null) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastNsbtxDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastNsbtxDirectoryUsed()));
            }
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save Animation File");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastNsbtxDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    String path = fc.getSelectedFile().getPath();

                    animHandler.saveAnimationFile(path);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "There was an error saving the IMD",
                            "Error saving IMD", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void addFrame() {
        if (animHandler != null) {
            if (animHandler.getAnimationSelected() != null) {
                animHandler.getAnimationSelected().addFrame(
                        jlTextureNames.getSelectedIndex(),
                        (Integer) jsDelay.getValue());
                repaintFrames();
            }
        }
    }

    public void removeFrame() {
        if (animHandler != null) {
            if (animHandler.getAnimationSelected() != null) {
                if (animHandler.getAnimationSelected().removeFrame(animHandler.getCurrentFrameIndex())) {
                    int index = animHandler.getCurrentFrameIndex() - 1;
                    if (index < 0) {
                        index = 0;
                    }
                    animHandler.setCurrentFrameIndex(index);
                }
                repaintFrames();
            }
        }
    }

    public int getAnimationSelectedIndex() {
        if (jlAnimationNames != null) {
            return jlAnimationNames.getSelectedIndex();
        } else {
            return -1;
        }
    }

}
