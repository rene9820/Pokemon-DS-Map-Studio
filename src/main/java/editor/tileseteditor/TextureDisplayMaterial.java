/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.tileseteditor;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import tileset.Tile;
import tileset.Tileset;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class TextureDisplayMaterial extends javax.swing.JPanel {

    private TilesetEditorHandler tileHandler;
    private TilesetEditorDialog dialog;

    private static final int size = 128;
    private BufferedImage backImg;

    private static final int reloadButtonSize = 32;
    private BufferedImage reloadIcon;

    /**
     * Creates new form TextureDisplay
     */
    public TextureDisplayMaterial() {
        initComponents();

        try {
            reloadIcon = Utils.loadImageAsResource("/icons/reloadIcon.png");
        } catch (IOException | IllegalArgumentException ex) {

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setToolTipText("Edit Texture in Image Editor");
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        if (tileHandler != null) {
            Tileset tset = tileHandler.getMapEditorHandler().getTileset();
            if (tset.size() > 0) {
                File file = new File(tset.tilesetFolderPath + "/" + tileHandler.getMaterialSelectedTextureName());
                if (file.exists()) {
                    if (new Rectangle(size - reloadButtonSize,
                            size - reloadButtonSize, reloadButtonSize,
                            reloadButtonSize).contains(evt.getX(), evt.getY())) {
                        dialog.replaceTexture(tileHandler.getMaterialIndexSelected(), file.getPath());
                    } else {
                        try {
                            Desktop.getDesktop().edit(file);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "There is not default program for editing images.",
                                    "Can't edit texture image", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "There was a problem opening the image. \n"
                            + "Save the map, open it and try again.",
                            "Can't open texture image", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


    }//GEN-LAST:event_formMousePressed

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        if (new Rectangle(size - reloadButtonSize,
                size - reloadButtonSize, reloadButtonSize,
                reloadButtonSize).contains(evt.getX(), evt.getY())) {
            setToolTipText("Reload texture");
        }else{
            setToolTipText("Edit Texture in Image Editor");
        }
    }//GEN-LAST:event_formMouseMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backImg != null) {
            g.drawImage(backImg, 0, 0, null);
        }

        if (tileHandler != null) {
            if (tileHandler.getMapEditorHandler().getTileset().size() > 0) {
                Tile tile = tileHandler.getMapEditorHandler().getTileSelected();
                BufferedImage img = tile.getTileset().getTextureImg(tileHandler.getMaterialIndexSelected());
                int x = getWidth() / 2 - img.getWidth() / 2;
                int y = getHeight() / 2 - img.getHeight() / 2;

                g.drawImage(img, x, y, null);
            }
        }

        if (reloadIcon != null) {
            g.drawImage(reloadIcon, size - reloadButtonSize, size - reloadButtonSize, null);
        }
    }

    public void init(TilesetEditorHandler tileHandler, TilesetEditorDialog dialog) {
        this.tileHandler = tileHandler;
        this.backImg = createBackImg();
        this.dialog = dialog;
    }

    public BufferedImage createBackImg() {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        int tileSize = 8;
        int numCells = size / tileSize;
        Color[] colors = new Color[]{Color.white, Color.lightGray};
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                g.setColor(colors[(i + j) % 2]);
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
        return img;
    }

}
