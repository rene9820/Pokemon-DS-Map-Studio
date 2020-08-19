/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.smartdrawing;

import editor.handler.MapEditorHandler;
import editor.handler.MapGrid;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class SmartGridDisplay extends javax.swing.JPanel {

    private static BufferedImage gridImage = Utils.loadTexImageAsResource("/imgs/smartGrid.png");

    private MapEditorHandler handler;
    private boolean editable = true;

    /**
     * Creates new form SmartDrawingDisplay
     */
    public SmartGridDisplay() {
        initComponents();

        //gridImage = Utils.loadImageAsResource("/imgs/smartGrid.png");
        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.DEFAULT_TILE_SIZE,
                SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        if (editable) {
            if (handler.getTileset().size() > 0) {
                int x = evt.getX() / MapGrid.DEFAULT_TILE_SIZE;
                int y = evt.getY() / MapGrid.DEFAULT_TILE_SIZE;
                int gridIndex = y / SmartGrid.height;
                y %= SmartGrid.height;
                //System.out.println(x + "  " + y);
                if (gridIndex < handler.getSmartGridArray().size() && gridIndex >= 0) {
                    if (!((y == 2) && (x == 4 || x == 3))) {
                        int[][] grid = handler.getSmartGrid(gridIndex).sgrid;
                        if (new Rectangle(SmartGrid.width, SmartGrid.height).contains(x, y)) {
                            if (SwingUtilities.isLeftMouseButton(evt)) {
                                if (handler.getTileSelected().isSizeOne()) {
                                    grid[x][y] = handler.getTileIndexSelected();
                                }
                            }
                            /*else if (SwingUtilities.isRightMouseButton(evt)) {
                        grid[x][y] = -1;
                    }*/
                            repaint();
                        }
                    } else {
                        handler.setSmartGridIndexSelected(gridIndex);
                        handler.getMainFrame().getMapDisplay().enableSmartGrid();
                        repaint();
                    }
                }

                if (SwingUtilities.isRightMouseButton(evt) && handler != null) {
                    if (gridIndex >= 0 && gridIndex < handler.getSmartGridArray().size()) {
                        handler.setSmartGridIndexSelected(gridIndex);
                    }

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item1 = new JMenuItem("Add Smart Painter");
                    JMenuItem item2 = new JMenuItem("Remove Smart Painter");
                    item1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handler.getSmartGridArray().add(new SmartGrid());
                            updateSize();
                            repaint();
                        }
                    });
                    item2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (handler.getSmartGridArray().size() > 1) {
                                if (gridIndex >= 0 && gridIndex < handler.getSmartGridArray().size()) {
                                    handler.getSmartGridArray().remove(gridIndex);
                                    handler.setSmartGridIndexSelected(Math.max(0, gridIndex - 1));
                                    updateSize();
                                    repaint();
                                }
                            } else {
                                System.out.println("No se puede");
                                JOptionPane.showMessageDialog(menu,
                                        "There must me at least one Smart Painter",
                                        "Can't delete Smart Painter",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    menu.add(item1);
                    menu.add(item2);

                    menu.show(this, evt.getX(), evt.getY());
                }
            }
        } else {
            if (handler.getTileset().size() > 0) {
                int y = evt.getY() / MapGrid.DEFAULT_TILE_SIZE;
                int gridIndex = y / SmartGrid.height;
                if (gridIndex < handler.getSmartGridArray().size() && gridIndex >= 0) {
                    handler.setSmartGridIndexSelected(gridIndex);
                    handler.getMainFrame().getMapDisplay().enableSmartGrid();
                    repaint();
                }
            }
        }
    }//GEN-LAST:event_formMousePressed

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        if (editable) {
            if (handler.getTileset().size() > 0) {
                int x = evt.getX() / MapGrid.DEFAULT_TILE_SIZE;
                int y = evt.getY() / MapGrid.DEFAULT_TILE_SIZE;
                int gridIndex = y / SmartGrid.height;
                y %= SmartGrid.height;;
                System.out.println(x + "  " + y);
                if (gridIndex < handler.getSmartGridArray().size() && gridIndex >= 0) {
                    if (!((y == 2) && (x == 4 || x == 3))) {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        setToolTipText(null);
                    } else {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        setToolTipText("Select Smart Drawing");
                    }
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    setToolTipText("Right click for adding or removing Smart Drawing");
                }
            }
        }
    }//GEN-LAST:event_formMouseMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gridImage != null && handler != null) {
            for (int k = 0; k < handler.getSmartGridArray().size(); k++) {
                g.drawImage(gridImage, 0,
                        SmartGrid.height * k * MapGrid.DEFAULT_TILE_SIZE, null);
            }

        }

        if (handler != null) {
            for (int k = 0; k < handler.getSmartGridArray().size(); k++) {
                SmartGrid sg = handler.getSmartGrid(k);
                int[][] grid = sg.sgrid;
                for (int i = 0; i < SmartGrid.width; i++) {
                    for (int j = 0; j < SmartGrid.height; j++) {
                        int indexTile = grid[i][j];
                        if (indexTile != -1) {
                            try {
                                BufferedImage img = handler.getTileset().get(indexTile).getThumbnail();
                                g.drawImage(
                                        img,
                                        i * MapGrid.DEFAULT_TILE_SIZE,
                                        (j + SmartGrid.height * k) * MapGrid.DEFAULT_TILE_SIZE,
                                        null);
                            } catch (Exception ex) {

                            }
                        }
                    }
                }
            }

            int index = handler.getSmartGridIndexSelected();
            g.setColor(Color.red);
            g.drawRect(
                    0,
                    index * SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE,
                    SmartGrid.width * MapGrid.DEFAULT_TILE_SIZE - 1,
                    SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE - 1);
            g.setColor(new Color(255, 100, 100, 50));
            g.fillRect(0,
                    index * SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE,
                    SmartGrid.width * MapGrid.DEFAULT_TILE_SIZE - 1,
                    SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE - 1);
        }

    }

    public void updateSize() {
        int numSmartGrids = handler.getSmartGridArray().size();
        System.out.println("Smart grid size: " + numSmartGrids);
        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.DEFAULT_TILE_SIZE,
                SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE * numSmartGrids));
        this.setSize(new Dimension(
                SmartGrid.width * MapGrid.DEFAULT_TILE_SIZE,
                SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE * numSmartGrids));
    }

    public void init(MapEditorHandler handler, boolean editable) {
        this.handler = handler;
        this.editable = editable;

        this.setPreferredSize(new Dimension(
                SmartGrid.width * MapGrid.DEFAULT_TILE_SIZE,
                SmartGrid.height * MapGrid.DEFAULT_TILE_SIZE * handler.getSmartGridArray().size()));
    }

}
