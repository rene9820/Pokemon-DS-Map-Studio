/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.tileseteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import tileset.Tile;
import tileset.TilesetMaterial;

/**
 *
 * @author Trifindo
 */
public class TextureDisplayConfigurable extends javax.swing.JPanel {

    private TilesetEditorHandler tileHandler;
    private int index = 0;
    private static final int size = 128;
    private BufferedImage backImg;
    
    /**
     * Creates new form TextureDisplayConfigurable
     */
    public TextureDisplayConfigurable() {
        initComponents();
        
        setPreferredSize(new Dimension(size, size));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(backImg != null){
            g.drawImage(backImg, 0, 0, null);
        }
        
        if (tileHandler != null) {
            if (tileHandler.getMapEditorHandler().getTileset().size() > 0) {
                TilesetMaterial material = tileHandler.getMapEditorHandler().getTileset().getMaterial(index);
                BufferedImage img = material.getTextureImg();
                int x = getWidth() / 2 - img.getWidth() / 2;
                int y = getHeight() / 2 - img.getHeight() / 2;

                g.drawImage(img, x, y, null);
            }
        }
    }

    public void init(TilesetEditorHandler tileHandler) {
        this.tileHandler = tileHandler;
        this.backImg = createBackImg();
    }
    
    public void setImageIndex(int index){
        this.index = index;
    }
    
    public BufferedImage createBackImg(){
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        int tileSize = 8;
        int numCells = size / tileSize;
        Color[] colors = new Color[]{Color.white, Color.lightGray};
        for(int i = 0; i < numCells; i++){
            for(int j = 0; j < numCells; j++){
                g.setColor(colors[(i + j) % 2]);
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
        return img;
    }
}
