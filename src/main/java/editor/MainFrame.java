package editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.jogamp.opengl.GLContext;
import editor.about.AboutDialog;
import editor.animationeditor.AnimationEditorDialog;
import editor.backsound.Backsound;
import editor.backsound.BacksoundEditorDialog;
import editor.bdhc.*;
import editor.bordermap.*;
import editor.buildingeditor.BuildingEditorDialog;
import editor.buildingeditor2.BuildingEditorChooser;
import editor.buildingeditor2.buildfile.BuildFile;
import editor.collisions.Collisions;
import editor.collisions.CollisionsEditorDialog;
import editor.converter.ConverterDialog;
import editor.converter.ConverterErrorDialog;
import editor.converter.ExportNsbmdResultDialog;
import editor.converter.ExportNsbtxResultDialog;
import editor.exceptions.WrongFormatException;
import editor.game.Game;
import editor.gameselector.GameChangerDialog;
import editor.gameselector.GameSelectorDialog;
import editor.gameselector.GameTsetSelectorDialog2;
import editor.handler.MapEditorHandler;
import editor.handler.MapGrid;
import editor.heightselector.*;
import editor.imd.ImdModel;
import editor.keyboard.KeyboardInfoDialog;
import editor.layerselector.*;
import editor.nsbtx.NsbtxEditorDialog;
import editor.nsbtx2.Nsbtx2;
import editor.nsbtx2.NsbtxEditorDialog2;
import editor.nsbtx2.NsbtxLoader2;
import editor.obj.ExportMapObjDialog;
import editor.obj.ObjWriter;
import editor.smartdrawing.*;
import editor.state.MapLayerState;
import editor.state.StateHandler;
import editor.tileselector.*;
import editor.tileseteditor.*;
import net.miginfocom.swing.*;
import tileset.NormalsNotFoundException;
import tileset.TextureNotFoundException;
import tileset.Tileset;
import tileset.TilesetIO;
import utils.Utils;

/**
 * @author Trifindo, JackHack96
 */
public class MainFrame extends JFrame {
    MapEditorHandler handler;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);

            if (args.length > 0) {
                try {
                    if (args[0].endsWith(MapGrid.fileExtension)) {
                        mainFrame.openMap(args[0]);
                    } else if (args[0].endsWith(Tileset.fileExtension)) {
                        mainFrame.openTileset(args[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MainFrame() {
        initComponents();

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(16);

        //jLabel1.setText(System.getProperty("java.version"));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/programIcon.png")));
        setLocationRelativeTo(null);

        //Tileset
        Tileset tileset = new Tileset();
        tileset.getSmartGridArray().add(new SmartGrid());

        TilesetRenderer tr = new TilesetRenderer(tileset);
        try {
            tr.renderTiles();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Border maps tileset
        Tileset borderMapsTileset = new Tileset();

        handler = new MapEditorHandler(this);
        handler.setTileset(tileset);
        handler.setBorderMapsTileset(borderMapsTileset);
        handler.setBdhc(new Bdhc());
        handler.setBacksound(new Backsound());
        handler.setCollisions(new Collisions(handler.getGameIndex()));
        handler.setBuildings(new BuildFile());

        mapDisplay.setHandler(handler);
        tileSelector.init(handler);
        heightSelector.init(handler);
        smartGridDisplay.init(handler, false);
        thumbnailLayerSelector.init(handler);
        borderMapsDisplay.init(handler);
        updateViewGame();
        tileDisplay.setHandler(handler);
        tileDisplay.setWireframe(true);

        setTitle(handler.getVersionName());
    }

    private void formWindowClosing(WindowEvent e) {
        int returnVal = JOptionPane.showConfirmDialog(this,
                "Do you want to exit Pokemon DS Map Studio?",
                "Closing Pokemon DS Map Studio", JOptionPane.YES_NO_OPTION);
        if (returnVal == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void jmiNewMapActionPerformed(ActionEvent e) {
        newMap();
    }

    private void jmiOpenMapActionPerformed(ActionEvent e) {
        openMapWithDialog();
    }

    private void jmiSaveMapActionPerformed(ActionEvent e) {
        if (handler.getGrid().filePath.isEmpty())
            saveMapWithDialog();
        else
            saveMap();
    }

    private void jmiSaveMapAsActionPerformed(ActionEvent e) {
        saveMapWithDialog();
    }

    private void jmiExportObjWithTextActionPerformed(ActionEvent e) {
        saveMapAsObjWithDialog();
    }

    private void jmiExportMapAsImdActionPerformed(ActionEvent e) {
        saveMapAsImdWithDialog();
    }

    private void jmiExportMapAsNsbActionPerformed(ActionEvent e) {
        saveMapAsNsbWithDialog();
    }

    private void jmiExportMapBtxActionPerformed(ActionEvent e) {
        saveMapBtxWithDialog();
    }

    private void jmiImportTilesetActionPerformed(ActionEvent e) {
        openTilesetWithDialog();
    }

    private void jmiExportTilesetActionPerformed(ActionEvent e) {
        saveTilesetWithDialog();
    }

    private void jmiExportAllTilesActionPerformed(ActionEvent e) {
        saveAllTilesAsObjWithDialog();
    }

    private void jmiUndoActionPerformed(ActionEvent e) {
        undoMapState();
    }

    private void jmiRedoActionPerformed(ActionEvent e) {
        redoMapState();
    }

    private void jmiClearTilesActionPerformed(ActionEvent e) {
        mapDisplay.toggleClearTile();
    }

    private void jmiUseSmartDrawingActionPerformed(ActionEvent e) {
        mapDisplay.toggleSmartGrid();
    }

    private void jmiClearLayerActionPerformed(ActionEvent e) {
        handler.addMapState(new MapLayerState("Clear Layer", handler));
        handler.getGrid().clearLayer(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.repaint();
    }

    private void jmiClearAllLayersActionPerformed(ActionEvent e) {
        handler.getGrid().clearAllLayers();
        thumbnailLayerSelector.drawAllLayerThumbnails();
        thumbnailLayerSelector.repaint();
        mapDisplay.repaint();
    }

    private void jmiCopyLayerActionPerformed(ActionEvent e) {
        if (handler.getTileset().size() > 0)
            handler.getGrid().copySelectedLayer();
    }

    private void jmiPasteLayerActionPerformed(ActionEvent e) {
        if (handler.getTileset().size() > 0) {
            if (handler.getGrid().getTileLayerCopy() != null && handler.getGrid().getHeightLayerCopy() != null) {
                handler.addMapState(new MapLayerState("Paste Tile and Height Layer", handler));
                handler.getGrid().pasteTileLayer();
                handler.getGrid().pasteHeightLayer();
                mapDisplay.repaint();
                handler.updateLayerThumbnail(handler.getActiveLayerIndex());
                thumbnailLayerSelector.repaint();
            }
        }
    }

    private void jmiPasteLayerTilesActionPerformed(ActionEvent e) {
        if (handler.getTileset().size() > 0) {
            if (handler.getGrid().getTileLayerCopy() != null) {
                handler.addMapState(new MapLayerState("Paste Tile Layer", handler));
                handler.getGrid().pasteTileLayer();
                mapDisplay.repaint();
                handler.updateLayerThumbnail(handler.getActiveLayerIndex());
                thumbnailLayerSelector.repaint();
            }
        }
    }

    private void jmiPasteLayerHeightsActionPerformed(ActionEvent e) {
        if (handler.getTileset().size() > 0) {
            if (handler.getGrid().getHeightLayerCopy() != null) {
                handler.addMapState(new MapLayerState("Paste Height Layer", handler));
                handler.getGrid().pasteHeightLayer();
                mapDisplay.repaint();
                handler.updateLayerThumbnail(handler.getActiveLayerIndex());
                thumbnailLayerSelector.repaint();
            }
        }
    }

    private void jmi3dViewActionPerformed(ActionEvent e) {
        mapDisplay.set3DView();
        mapDisplay.repaint();
    }

    private void jmiTopViewActionPerformed(ActionEvent e) {
        mapDisplay.setOrthoView();
        mapDisplay.repaint();
    }

    private void jmiToggleHeightViewActionPerformed(ActionEvent e) {
        mapDisplay.toggleHeightView();
        mapDisplay.repaint();
    }

    private void jmiToggleGridActionPerformed(ActionEvent e) {
        mapDisplay.toggleGridView();
        mapDisplay.repaint();
    }

    private void jmiLoadBackImgActionPerformed(ActionEvent e) {
        openBackImgWithDialog();
    }

    private void jcbUseBackImageActionPerformed(ActionEvent e) {
        mapDisplay.setBackImageEnabled(jcbUseBackImage.isSelected());
        mapDisplay.repaint();
    }

    private void jmiTilesetEditorActionPerformed(ActionEvent e) {
        openTilesetEditor();
    }

    private void jmiCollisionEditorActionPerformed(ActionEvent e) {
        openCollisionsEditor();
    }

    private void jmiBdhcEditorActionPerformed(ActionEvent e) {
        openBdhcEditor();
    }

    private void jmiNsbtxEditorActionPerformed(ActionEvent e) {
        openNsbtxEditor();
    }

    private void jmiKeyboardInfoActionPerformed(ActionEvent e) {
        openKeyboardInfoDialog();
    }

    private void jmiAboutActionPerformed(ActionEvent e) {
        openAboutDialog();
    }

    private void jbNewMapActionPerformed(ActionEvent e) {
        newMap();
    }

    private void jbOpenMapActionPerformed(ActionEvent e) {
        openMapWithDialog();
    }

    private void jbSaveMapActionPerformed(ActionEvent e) {
        if (handler.getGrid().filePath.isEmpty())
            saveMapWithDialog();
        else
            saveMap();
    }

    private void jbExportObjActionPerformed(ActionEvent e) {
        saveMapAsObjWithDialog();
    }

    private void jbExportImdActionPerformed(ActionEvent e) {
        saveMapAsImdWithDialog();
    }

    private void jbExportNsbActionPerformed(ActionEvent e) {
        saveMapAsNsbWithDialog();
    }

    private void jbExportNsb1ActionPerformed(ActionEvent e) {
        saveMapBtxWithDialog();
    }

    private void jbUndoActionPerformed(ActionEvent e) {
        undoMapState();
    }

    private void jbRedoActionPerformed(ActionEvent e) {
        redoMapState();
    }

    private void jb3DViewActionPerformed(ActionEvent e) {
        mapDisplay.set3DView();
        mapDisplay.repaint();
    }

    private void jbTopViewActionPerformed(ActionEvent e) {
        mapDisplay.setOrthoView();
        mapDisplay.repaint();
    }

    private void jbHeightViewActionPerformed(ActionEvent e) {
        mapDisplay.toggleHeightView();
        mapDisplay.repaint();
    }

    private void jbGridViewActionPerformed(ActionEvent e) {
        mapDisplay.toggleGridView();
        mapDisplay.repaint();
    }

    private void jbClearTileActionPerformed(ActionEvent e) {
        mapDisplay.toggleClearTile();
    }

    private void jbUseSmartGridActionPerformed(ActionEvent e) {
        mapDisplay.toggleSmartGrid();
    }

    private void jbTilelistEditorActionPerformed(ActionEvent e) {
        openTilesetEditor();
    }

    private void jbCollisionsEditorActionPerformed(ActionEvent e) {
        openCollisionsEditor();
    }

    private void jbBdhcEditorActionPerformed(ActionEvent e) {
        openBdhcEditor();
    }

    private void jbBacksoundEditorActionPerformed(ActionEvent e) {
        openBacksoundEditor();
    }

    private void jbNsbtxEditor1ActionPerformed(ActionEvent e) {
        openNsbtxEditor2();
    }

    private void jbBuildingEditorActionPerformed(ActionEvent e) {
        openBuildingEditor2();
    }

    private void jbAnimationEditorActionPerformed(ActionEvent e) {
        openAnimationEditor();
    }

    private void jbKeboardInfoActionPerformed(ActionEvent e) {
        openKeyboardInfoDialog();
    }

    private void jbHelpActionPerformed(ActionEvent e) {
        openAboutDialog(); //TODO move this to another button
    }

    private void jlGameIconMousePressed(MouseEvent e) {
        changeGame();
    }

    private void tileSelectorMousePressed(MouseEvent e) {
        repaintTileDisplay();
    }

    private void jsHeightMapAlphaStateChanged(ChangeEvent e) {
        mapDisplay.setHeightMapAlpha(jsHeightMapAlpha.getValue() / 100f);
        mapDisplay.repaint();
    }

    private void jsBackImageAlphaStateChanged(ChangeEvent e) {
        mapDisplay.setBackImageAlpha(jsBackImageAlpha.getValue() / 100f);
        mapDisplay.repaint();
    }

    private void jbMoveMapUpActionPerformed(ActionEvent e) {
        moveTilesUp();
    }

    private void jbMoveMapDownActionPerformed(ActionEvent e) {
        moveTilesDown();
    }

    private void jbMoveMapLeftActionPerformed(ActionEvent e) {
        moveTilesLeft();
    }

    private void jbMoveMapRightActionPerformed(ActionEvent e) {
        moveTilesRight();
    }

    private void pnlMapDisplayComponentResized(ComponentEvent e) {
        int size = Math.min(pnlMapDisplay.getWidth(), pnlMapDisplay.getHeight());
        mapDisplay.setPreferredSize(new Dimension(size, size));
        pnlMapDisplay.revalidate();
    }

    public void openMap(String path) {
        try {
            String folderPath = new File(path).getParent();
            String fileName = new File(path).getName();
            handler.setLastMapDirectoryUsed(folderPath);

            handler.getGrid().loadFromFile(path);
            handler.getGrid().filePath = path;

            setTitle(handler.getMapName() + " - " + handler.getVersionName());

            handler.resetMapStateHandler();
            jbUndo.setEnabled(false);
            jbRedo.setEnabled(false);

            try {
                Tileset tileset = TilesetIO.readTilesetFromFile(handler.getGrid().tilesetFilePath);
                handler.setTileset(tileset);
                System.out.println("Textures loaded from path: " + new File(path).getParent());

                GLContext context = mapDisplay.getContext();
                TilesetRenderer tr = new TilesetRenderer(handler.getTileset());
                try {
                    tr.renderTiles();
                } catch (NullPointerException e) {

                }
                tr.destroy();
                mapDisplay.setContext(context, false);

                handler.setIndexTileSelected(0);
                handler.setSmartGridIndexSelected(0);

                tileSelector.updateLayout();
                tileSelector.repaint();
                mapDisplay.requestUpdate();
                mapDisplay.repaint();
                tileDisplay.requestUpdate();
                tileDisplay.repaint();

                smartGridDisplay.updateSize();
                smartGridDisplay.repaint();
                thumbnailLayerSelector.drawAllLayerThumbnails();
                thumbnailLayerSelector.repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error opening map", JOptionPane.ERROR_MESSAGE);
            } catch (TextureNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error opening map", JOptionPane.ERROR_MESSAGE);
            }

            try {
                String filename = Utils.removeExtensionFromPath(fileName);
                String bdhcPath = folderPath + "/" + filename + "." + Bdhc.fileExtension;
                int game = handler.getGameIndex();
                if (game == Game.DIAMOND || game == Game.PEARL) {
                    handler.setBdhc(new BdhcLoaderDP().loadBdhcFromFile(bdhcPath));
                } else {
                    handler.setBdhc(new BdhcLoaderHGSS().loadBdhcFromFile(bdhcPath));
                }
            } catch (IOException ex) {
                handler.setBdhc(new Bdhc());
            }

            try {
                String filename = Utils.removeExtensionFromPath(fileName);
                String backsoundPath = folderPath + "/" + filename + "." + Backsound.fileExtension;

                System.out.println("Backsound path: " + backsoundPath);
                int game = handler.getGameIndex();
                if (game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
                    handler.setBacksound(new Backsound(backsoundPath));
                } else {
                    handler.setBacksound(new Backsound());
                }
            } catch (IOException | WrongFormatException ex) {
                handler.setBacksound(new Backsound());
            }

            try {
                String filename = Utils.removeExtensionFromPath(fileName);
                String collisionsPath = folderPath + "/" + filename + "." + Collisions.fileExtension;
                handler.setCollisions(new Collisions(collisionsPath));
            } catch (IOException ex) {
                handler.setCollisions(new Collisions(handler.getGameIndex()));
            }

            try {
                String filename = Utils.removeExtensionFromPath(fileName);
                String buildingsPath = folderPath + "/" + filename + "." + BuildFile.fileExtension;
                handler.setBuildings(new BuildFile(buildingsPath));
            } catch (IOException ex) {
                handler.setBuildings(new BuildFile());
            }

            updateViewGame();

            repaintHeightSelector();
            repaintTileSelector();
            repaintMapDisplay();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Can't open file", "Error opening map", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openMapWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }

        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS map (*.pdsmap)", MapGrid.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Map");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            openMap(fc.getSelectedFile().getPath());
        }
    }

    public void openTilesetEditor() {
        final TilesetEditorDialog dialog = new TilesetEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (handler.getTileset().size() > 0) {
            handler.getTileset().removeUnusedTextures();
            dialog.fixIndices();
            tileSelector.updateLayout();
            mapDisplay.requestUpdate();
            mapDisplay.repaint();
            tileDisplay.requestUpdate();
            tileDisplay.repaint();
            smartGridDisplay.updateSize();
            smartGridDisplay.repaint();
            thumbnailLayerSelector.drawAllLayerThumbnails();
            thumbnailLayerSelector.repaint();
        }

        repaint();
    }

    public void openCollisionsEditor() {
        mapDisplay.requestScreenshot();
        mapDisplay.setOrthoView();
        boolean gridEnabled = mapDisplay.isGridEnabled();
        mapDisplay.disableGridView();
        mapDisplay.display();
        final CollisionsEditorDialog dialog = new CollisionsEditorDialog(this, true);
        dialog.init(handler, mapDisplay.getScreenshot());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        mapDisplay.setGridEnabled(gridEnabled);
        mapDisplay.display();
    }

    public void openBdhcEditor() {
        if (handler.getGame().gameSelected < Game.BLACK) {
            mapDisplay.requestScreenshot();
            mapDisplay.setOrthoView();
            boolean gridEnabled = mapDisplay.isGridEnabled();
            mapDisplay.disableGridView();
            mapDisplay.display();
            final BdhcEditorDialog dialog = new BdhcEditorDialog(this, true);
            dialog.init(handler, mapDisplay.getScreenshot());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            mapDisplay.setGridEnabled(gridEnabled);
            mapDisplay.display();
        } else {
            JOptionPane.showMessageDialog(this, "Gen V Games do not have BDHC files",
                    "BDHC editor is not available", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openBacksoundEditor() {
        if (handler.getGame().gameSelected == Game.HEART_GOLD || handler.getGame().gameSelected == Game.SOUL_SILVER) {
            mapDisplay.requestScreenshot();
            mapDisplay.setOrthoView();
            boolean gridEnabled = mapDisplay.isGridEnabled();
            mapDisplay.disableGridView();
            mapDisplay.display();
            final BacksoundEditorDialog dialog = new BacksoundEditorDialog(this, true);
            dialog.init(handler, mapDisplay.getScreenshot());
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            mapDisplay.setGridEnabled(gridEnabled);
            mapDisplay.display();
        } else {
            JOptionPane.showMessageDialog(this, "Only HGSS have Backsound files",
                    "Backsound Editor not available", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openNsbtxEditor() {
        final NsbtxEditorDialog dialog = new NsbtxEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openNsbtxEditor2() {
        final NsbtxEditorDialog2 dialog = new NsbtxEditorDialog2(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openBuildingEditor() {
        final BuildingEditorDialog dialog = new BuildingEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openBuildingEditor2() {
        BuildingEditorChooser.loadGame(handler);
    }

    public void openAnimationEditor() {
        final AnimationEditorDialog dialog = new AnimationEditorDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openKeyboardInfoDialog() {
        final KeyboardInfoDialog dialog = new KeyboardInfoDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openTileset(String path) {
        String folderPath = new File(path).getParent();

        handler.setLastTilesetDirectoryUsed(folderPath);
        try {
            Tileset tileset = TilesetIO.readTilesetFromFile(path);
            handler.getGrid().tilesetFilePath = path;
            handler.setTileset(tileset);
            System.out.println("Textures loaded from path: " + new File(path).getParent());

            GLContext context = mapDisplay.getContext();
            TilesetRenderer tr = new TilesetRenderer(handler.getTileset());
            try {
                tr.renderTiles();
            } catch (NullPointerException e) {

            }
            tr.destroy();
            mapDisplay.setContext(context, false);

            handler.setIndexTileSelected(0);
            handler.setSmartGridIndexSelected(0);

            tileSelector.updateLayout();
            tileSelector.repaint();
            smartGridDisplay.updateSize();
            smartGridDisplay.repaint();
            mapDisplay.requestUpdate();
            mapDisplay.repaint();
            tileDisplay.requestUpdate();
            tileDisplay.repaint();
            thumbnailLayerSelector.drawAllLayerThumbnails();
            thumbnailLayerSelector.repaint();
        } catch (/*ParserConfigurationException | SAXException*/IOException ex) {

        } catch (TextureNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error opening tilset", JOptionPane.ERROR_MESSAGE);
        }

        repaintHeightSelector();
        repaintTileSelector();
        repaintMapDisplay();
    }

    public void openTilesetWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastTilesetDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS Tileset (*.pdsts)", Tileset.fileExtension));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getPath();
            openTileset(path);
        }
    }

    private void openBackImgWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
        fc.setApproveButtonText("Open");
        fc.setDialogTitle("Open Background Image");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fc.getSelectedFile());

                mapDisplay.setBackImage(img);
                mapDisplay.setBackImageEnabled(true);
                jcbUseBackImage.setSelected(true); //Redundant

                mapDisplay.repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't open file", "Error opening image", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void newMap() {
        int returnVal = JOptionPane.showConfirmDialog(this, "Do you want to close current map?", "Create new map", JOptionPane.YES_NO_OPTION);
        if (returnVal == JOptionPane.YES_OPTION) {
            final GameTsetSelectorDialog2 dialog = new GameTsetSelectorDialog2(this, true);
            dialog.init(handler);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.getReturnValue() == GameTsetSelectorDialog2.ACEPTED) {
                handler.setIndexTileSelected(0);
                handler.setSmartGridIndexSelected(0);

                handler.setCollisions(new Collisions(handler.getGameIndex()));

                handler.setBdhc(new Bdhc());

                handler.setBacksound(new Backsound());

                handler.setBuildings(new BuildFile());

                handler.setGrid(new MapGrid(handler));
                handler.resetMapStateHandler();
                jbUndo.setEnabled(false);
                jbRedo.setEnabled(false);

                //handler.setTileset(new Tileset());
                //handler.getSmartGridArray().add(new SmartGrid());
                tileSelector.updateLayout();
                tileSelector.repaint();

                smartGridDisplay.updateSize();
                smartGridDisplay.repaint();

                mapDisplay.requestUpdate();
                repaintMapDisplay();
                tileDisplay.requestUpdate();
                tileDisplay.repaint();
                thumbnailLayerSelector.drawAllLayerThumbnails();
                thumbnailLayerSelector.repaint();

                updateViewGame();

                setTitle(handler.getVersionName());
            }
        }
    }

    private void saveMap() {
        try {
            handler.getGrid().saveToFile(handler.getGrid().filePath);

            setTitle(handler.getMapName() + " - " + handler.getVersionName());

            saveTileset();
            saveBdhc();
            saveBacksound();
            saveCollisions();
            saveBuildings();

            saveMapThumbnail();
        } catch (ParserConfigurationException | TransformerException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Can't save file", "Error saving map", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMapWithDialog() {
        final JFileChooser fc = new JFileChooser();
        if (handler.getLastMapDirectoryUsed() != null) {
            fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS map (*.pdsmap)", MapGrid.fileExtension));
        fc.setApproveButtonText("Save");
        fc.setDialogTitle("Save");
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
            try {
                String path = fc.getSelectedFile().getPath();
                handler.getGrid().saveToFile(path);
                handler.getGrid().filePath = path;

                setTitle(handler.getMapName() + " - " + handler.getVersionName());

                saveTileset();
                saveCollisions();
                saveBacksound();
                saveBdhc();
                saveBuildings();

                saveMapThumbnail();
            } catch (ParserConfigurationException | TransformerException | IOException ex) {
                JOptionPane.showMessageDialog(this, "Can't save file", "Error saving map", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void saveTilesetWithDialog() {
        if (handler.getTileset().size() > 0) {
            final JFileChooser fc = new JFileChooser();
            if (handler.getLastTilesetDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastTilesetDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("Pokemon DS tileset (*.pdsts)", Tileset.fileExtension));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save Tileset");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    File file = fc.getSelectedFile();
                    String path = file.getParent();
                    String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Tileset.fileExtension;
                    TilesetIO.saveTilesetToFile(path + File.separator + filename, handler.getTileset());
                    handler.getTileset().saveImagesToFile(path);

                    saveTilesetThumbnail(path + File.separator + "TilesetThumbnail.png");

                    JOptionPane.showMessageDialog(this, "Tileset succesfully exported.", "Tileset saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Can't save file", "Error saving tileset", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "The tileset is empty", "Error saving tileset", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveAllTilesAsObjWithDialog() {
        if (handler.getTileset().size() > 0) {
            final ExportTileDialog exportTileDialog = new ExportTileDialog(handler.getMainFrame(), true, "Export Tile Settings");
            exportTileDialog.setLocationRelativeTo(this);
            exportTileDialog.setVisible(true);
            if (exportTileDialog.getReturnValue() == AddTileDialog.APPROVE_OPTION) {
                float scale = exportTileDialog.getScale();
                boolean flip = exportTileDialog.flip();
                boolean includeVertexColors = exportTileDialog.includeVertexColors();

                final JFileChooser fc = new JFileChooser();
                if (handler.getLastTileObjDirectoryUsed() != null) {
                    fc.setCurrentDirectory(new File(handler.getLastTileObjDirectoryUsed()));
                }
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setApproveButtonText("Save");
                fc.setDialogTitle("Select folder for saving all tiles as OBJ");
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    handler.setLastTileObjDirectoryUsed(fc.getSelectedFile().getPath());
                    try {
                        ObjWriter objWriter = new ObjWriter(handler.getTileset(),
                                handler.getGrid(), fc.getSelectedFile().getPath(),
                                handler.getGameIndex(), true, includeVertexColors);
                        objWriter.writeAllTilesObj(scale, flip);
                        JOptionPane.showMessageDialog(this, "Tiles succesfully exported.", "Tiles saved", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Can't save tiles", "Error saving tiles", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "The tileset is empty", "Error saving tiles", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMapAsObjWithDialog() {
        final ExportMapObjDialog exportMapDialog = new ExportMapObjDialog(this, true, "Export OBJ Map Settings");
        exportMapDialog.setLocationRelativeTo(null);
        exportMapDialog.setVisible(true);
        if (exportMapDialog.getReturnValue() == ExportMapObjDialog.APPROVE_OPTION) {
            boolean includeVertexColors = exportMapDialog.includeVertexColors();

            final JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getGrid().filePath)));
            if (handler.getLastMapDirectoryUsed() != null) {
                fc.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
            }
            fc.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
            fc.setApproveButtonText("Save");
            fc.setDialogTitle("Save map as OBJ");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                handler.setLastMapDirectoryUsed(fc.getSelectedFile().getParent());
                try {
                    String path = fc.getSelectedFile().getPath();
                    handler.getGrid().saveMapToOBJ(handler.getTileset(), path, true, includeVertexColors);
                    JOptionPane.showMessageDialog(this, "OBJ map succesfully exported.", "Map saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "Can't save file.", "Error saving map", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void saveTileset() throws FileNotFoundException, ParserConfigurationException, TransformerException, IOException {
        File file = new File(handler.getGrid().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Tileset.fileExtension;
        TilesetIO.saveTilesetToFile(path + File.separator + filename, handler.getTileset());
        handler.getTileset().saveImagesToFile(path);

        saveTilesetThumbnail(path + File.separator + "TilesetThumbnail.png");
    }

    public void saveTilesetThumbnail(String path) throws IOException {
        BufferedImage img = tileSelector.getTilesetImage();
        if (img != null) {
            File file = new File(path);
            ImageIO.write(img, "png", file);
        }
    }

    public void saveMapThumbnail() throws IOException {
        mapDisplay.requestScreenshot();
        mapDisplay.display();

        String path = new File(handler.getGrid().filePath).getParent();
        File file = new File(path + File.separator + "MapThumbnail.png");
        ImageIO.write(mapDisplay.getScreenshot(), "png", file);
    }

    public void saveBdhc() throws IOException {
        File file = new File(handler.getGrid().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Bdhc.fileExtension;

        int game = handler.getGameIndex();
        if (game == Game.DIAMOND || game == Game.PEARL) {
            BdhcWriterDP.writeBdhc(handler.getBdhc(), path + File.separator + filename);
        } else {
            BdhcWriterHGSS.writeBdhc(handler.getBdhc(), path + File.separator + filename);
        }

    }

    public void saveBacksound() throws IOException {
        int game = handler.getGameIndex();
        if (game == Game.HEART_GOLD || game == Game.SOUL_SILVER) {
            File file = new File(handler.getGrid().filePath);
            String path = file.getParent();
            String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Backsound.fileExtension;

            System.out.println("Backsound OUT: " + filename);

            handler.getBacksound().writeToFile(path + File.separator + filename);
        }
    }

    public void saveCollisions() throws IOException {
        File file = new File(handler.getGrid().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + Collisions.fileExtension;
        handler.getCollisions().saveToFile(path + File.separator + filename);
    }

    public void saveBuildings() throws IOException {
        File file = new File(handler.getGrid().filePath);
        String path = file.getParent();
        String filename = Utils.removeExtensionFromPath(file.getName()) + "." + BuildFile.fileExtension;
        handler.getBuildings().saveToFile(path + File.separator + filename);
    }

    public void saveMapAsImdWithDialog() {
        if (handler.getTileset().size() == 0) {
            JOptionPane.showMessageDialog(this,
                    "There is no tileset loaded.\n"
                            + "The IMD can be exported but the materials will be set to default.\n",
                    "No tileset loaded",
                    JOptionPane.WARNING_MESSAGE);
        }

        final JFileChooser fcOpen = new JFileChooser();
        fcOpen.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getGrid().filePath) + ".obj"));
        if (handler.getLastMapDirectoryUsed() != null) {
            fcOpen.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fcOpen.setFileFilter(new FileNameExtensionFilter("OBJ (*.obj)", "obj"));
        fcOpen.setApproveButtonText("Open");
        fcOpen.setDialogTitle("Open OBJ Map for converting into IMD");
        int returnValOpen = fcOpen.showOpenDialog(this);
        if (returnValOpen == JFileChooser.APPROVE_OPTION) {
            if (fcOpen.getSelectedFile().exists()) {
                String pathOpen = fcOpen.getSelectedFile().getPath();

                final JFileChooser fcSave = new JFileChooser();
                fcSave.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getGrid().filePath)));
                fcSave.setCurrentDirectory(fcOpen.getSelectedFile().getParentFile());
                fcSave.setFileFilter(new FileNameExtensionFilter("IMD (*.imd)", "imd"));
                fcSave.setApproveButtonText("Save");
                fcSave.setDialogTitle("Save");
                int returnValSave = fcSave.showOpenDialog(this);
                if (returnValSave == JFileChooser.APPROVE_OPTION) {
                    String pathSave = fcSave.getSelectedFile().getPath();

                    try {
                        ImdModel model = new ImdModel(pathOpen, pathSave, handler.getTileset().getMaterials());
                        final int numVertices = model.getNumVertices();
                        final int numPolygons = model.getNumPolygons();
                        final int numTris = model.getNumTris();
                        final int numQuads = model.getNumQuads();
                        JOptionPane.showMessageDialog(this, "IMD map succesfully exported.\n\n"
                                        + "Number of Materials: " + model.getNumMaterials() + "\n"
                                        + "Number of Vertices: " + numVertices + "\n"
                                        + "Number of Polygons: " + numPolygons + "\n"
                                        + "Number of Triangles: " + numTris + "\n"
                                        + "Number of Quads: " + numQuads,
                                "Map saved", JOptionPane.INFORMATION_MESSAGE);
                        final int maxNumPolygons = 1800;
                        final int maxNumTris = 1200;
                        if (numTris > maxNumTris) {
                            JOptionPane.showMessageDialog(this, "The map might not work properly in game.\n\n"
                                            + "The map contains " + numTris + " triangles" + "\n"
                                            + "Try to use less than " + maxNumTris + " triangles" + "\n"
                                            + "Or try to use quads instead of triangles" + "\n",
                                    "Too many triangles", JOptionPane.INFORMATION_MESSAGE);
                        } else if (numPolygons > maxNumPolygons) {
                            JOptionPane.showMessageDialog(this, "The map may not work properly in game.\n\n"
                                            + "The map contains " + numPolygons + " polygons" + "\n"
                                            + "Try to use less than " + maxNumPolygons + " polygons",
                                    "Too many polygons", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (ParserConfigurationException | TransformerException ex) {
                        JOptionPane.showMessageDialog(this,
                                "There was a problem parsing the XML data of the IMD",
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this,
                                "There was a problem exporting the IMD",
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (TextureNotFoundException ex) {
                        JOptionPane.showMessageDialog(this,
                                ex.getMessage(),
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (NormalsNotFoundException ex) {
                        JOptionPane.showMessageDialog(this,
                                ex.getMessage(),
                                "Can't export IMD",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "The selected OBJ file could not be opened",
                        "Can't open OBJ",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveMapAsNsbWithDialog() {
        final ConverterDialog convDialog = new ConverterDialog(this, true);
        convDialog.setLocationRelativeTo(this);
        convDialog.setVisible(true);
        if (convDialog.getReturnValue() == ConverterDialog.APPROVE_OPTION) {
            boolean includeNsbtx = convDialog.includeNsbtxInNsbmd();
            try {
                final JFileChooser fcOpen = new JFileChooser();
                fcOpen.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getGrid().filePath) + ".imd"));
                if (handler.getLastMapDirectoryUsed() != null) {
                    fcOpen.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
                }
                fcOpen.setFileFilter(new FileNameExtensionFilter("IMD (*.imd)", "imd"));
                fcOpen.setApproveButtonText("Open");
                fcOpen.setDialogTitle("Open IMD Map for converting into NSBMD");
                int returnVal = fcOpen.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String imdPath;
                    if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                        imdPath = fcOpen.getSelectedFile().getPath();
                    } else {
                        String cwd = System.getProperty("user.dir"); // get current user directory
                        imdPath = new File(cwd).toURI().relativize(fcOpen.getSelectedFile().toPath().toRealPath().toUri()).getPath(); //this is some serious java shit
                    }
                    final JFileChooser fcSave = new JFileChooser();
                    fcSave.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getGrid().filePath)));
                    fcSave.setCurrentDirectory(fcOpen.getSelectedFile().getParentFile());
                    fcSave.setFileFilter(new FileNameExtensionFilter("NSBMD (*.nsbmd)", "nsbmd"));
                    fcSave.setApproveButtonText("Save");
                    fcSave.setDialogTitle("Save");
                    int returnValSave = fcSave.showOpenDialog(this);

                    if (returnValSave == JFileChooser.APPROVE_OPTION) {
                        String nsbPath = fcSave.getSelectedFile().getPath();
                        String filename = new File(nsbPath).getName();

                        try {
                            String converterPath = "converter/g3dcvtr.exe";
                            String[] cmd;
                            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                                if (includeNsbtx) {
                                    cmd = new String[]{converterPath, imdPath, "-eboth", "-o", filename};
                                } else {
                                    cmd = new String[]{converterPath, imdPath, "-emdl", "-o", filename};
                                }

                            } else {
                                if (includeNsbtx) {
                                    cmd = new String[]{"wine", converterPath, imdPath, "-eboth", "-o", filename};
                                } else {
                                    cmd = new String[]{"wine", converterPath, imdPath, "-emdl", "-o", filename};
                                }
                                // NOTE: wine call works only with relative path
                            }

                            if (!Files.exists(Paths.get(converterPath))) {
                                throw new IOException();
                            }

                            Process p = new ProcessBuilder(cmd).start();

                            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                            String outputString = "";
                            String line = null;
                            while ((line = stdError.readLine()) != null) {
                                outputString += line + "\n";
                            }

                            p.waitFor();
                            p.destroy();

                            if (!filename.endsWith("nsbmd")) {
                                filename += ".nsbmd";
                            }
                            if (!nsbPath.endsWith("nsbmd")) {
                                nsbPath += ".nsbmd";
                            }

                            System.out.println(System.getProperty("user.dir"));
                            File srcFile = new File(System.getProperty("user.dir") + File.separator + filename);
                            File dstFile = new File(nsbPath);
                            if (srcFile.exists()) {
                                try {
                                    Files.move(srcFile.toPath(), dstFile.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING);

                                    try {
                                        byte[] nsbmdData = Files.readAllBytes(dstFile.toPath());

                                        ExportNsbmdResultDialog resultDialog = new ExportNsbmdResultDialog(this, true);
                                        resultDialog.init(nsbmdData);
                                        resultDialog.setLocationRelativeTo(this);
                                        resultDialog.setVisible(true);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(this, "NSBMD succesfully exported.",
                                                "NSBMD saved", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(this,
                                            "File was not moved to the save directory. \n"
                                                    + "Reopen Pokemon DS Map Studio and try again.",
                                            "Problem saving generated file",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                ConverterErrorDialog dialog = new ConverterErrorDialog(this, true);
                                dialog.init("There was a problem creating the NSBMD file. \n"
                                                + "The output from the converter is:",
                                        outputString);
                                dialog.setTitle("Problem generating file");
                                dialog.setLocationRelativeTo(this);
                                dialog.setVisible(true);
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "The program \"g3dcvtr.exe\" is not found in the \"converter\" folder.\n"
                                            + "Put the program and its *.dll files in the folder and try again.",
                                    "Converter not found",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (InterruptedException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "The model was not converted",
                                    "Problem converting the model",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "There was a problem reading the IMD file",
                        "Error loading the IMD file",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveMapBtxWithDialog() {
        final JFileChooser fcOpen = new JFileChooser();
        fcOpen.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getGrid().filePath) + ".imd"));
        if (handler.getLastMapDirectoryUsed() != null) {
            fcOpen.setCurrentDirectory(new File(handler.getLastMapDirectoryUsed()));
        }
        fcOpen.setFileFilter(new FileNameExtensionFilter("IMD (*.imd)", "imd"));
        fcOpen.setApproveButtonText("Open");
        fcOpen.setDialogTitle("Open IMD Map for converting into NSBTX");
        int returnVal = fcOpen.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String imdPath = fcOpen.getSelectedFile().getPath();

            final JFileChooser fcSave = new JFileChooser();
            fcSave.setSelectedFile(new File(Utils.removeExtensionFromPath(handler.getGrid().filePath)));
            fcSave.setCurrentDirectory(fcOpen.getSelectedFile().getParentFile());
            fcSave.setFileFilter(new FileNameExtensionFilter("NSBTX (*.nsbtx)", "nsbtx"));
            fcSave.setApproveButtonText("Save");
            fcSave.setDialogTitle("Save");
            int returnValSave = fcSave.showOpenDialog(this);

            if (returnValSave == JFileChooser.APPROVE_OPTION) {
                String nsbPath = fcSave.getSelectedFile().getPath();
                String filename = new File(nsbPath).getName();

                System.out.println(filename);
                String converterPath = "converter/g3dcvtr.exe";
                String[] cmd = {converterPath, imdPath, "-etex", "-o", filename};
                Process p;
                try {
                    p = new ProcessBuilder(cmd).start();

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    String outputString = "";
                    String line = null;
                    while ((line = stdError.readLine()) != null) {
                        outputString += line + "\n";
                    }

                    p.waitFor();
                    p.destroy();

                    if (!filename.endsWith("nsbtx")) {
                        filename += ".nsbtx";
                    }
                    if (!nsbPath.endsWith("nsbtx")) {
                        nsbPath += ".nsbtx";
                    }

                    System.out.println(System.getProperty("user.dir"));
                    File srcFile = new File(System.getProperty("user.dir") + "/" + filename);
                    File dstFile = new File(nsbPath);
                    if (srcFile.exists()) {
                        try {
                            Files.move(srcFile.toPath(), dstFile.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING);
                            try {
                                byte[] nsbtxData = Files.readAllBytes(dstFile.toPath());
                                Nsbtx2 nsbtx = NsbtxLoader2.loadNsbtx(nsbtxData);

                                ExportNsbtxResultDialog resultDialog = new ExportNsbtxResultDialog(this, true);
                                resultDialog.init(nsbtx);
                                resultDialog.setLocationRelativeTo(this);
                                resultDialog.setVisible(true);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(this, "NSBTX succesfully exported.",
                                        "NSBTX saved", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "File was not moved to the save directory. \n"
                                            + "Reopen Pokemon DS Map Studio and try again.",
                                    "Problem saving generated file",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        ConverterErrorDialog dialog = new ConverterErrorDialog(this, true);
                        dialog.init("There was a problem creating the NSBTX file. \n"
                                        + "The output from the converter is:",
                                outputString);
                        dialog.setTitle("Problem generating file");
                        dialog.setLocationRelativeTo(this);
                        dialog.setVisible(true);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "The program \"g3dcvtr.exe\" is not found in the \"converter\" folder.\n"
                                    + "Put the program and its *.dll files in the folder and try again.",
                            "Converter not found",
                            JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(this,
                            "The model was not converted",
                            "Problem converting the model",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void changeGame() {
        final GameChangerDialog dialog = new GameChangerDialog(this, true);
        dialog.init(handler);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getReturnValue() == GameSelectorDialog.ACEPTED) {
            updateViewGame();
        }
    }

    public void openAboutDialog() {
        final AboutDialog dialog = new AboutDialog(this);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void repaintHeightSelector() {
        heightSelector.repaint();
    }

    public void repaintTileSelector() {
        tileSelector.repaint();
    }

    public void repaintTileDisplay() {
        tileDisplay.repaint();
    }

    public void updateTileSelectorScrollBar() {
        int y = tileSelector.getTileSelectedY() - jScrollPane1.getHeight() / 2;
        jScrollPane1.getVerticalScrollBar().setValue(y);
    }

    public void repaintThumbnailLayerSelector() {
        thumbnailLayerSelector.repaint();
    }

    public void repaintMapDisplay() {
        mapDisplay.repaint();
    }

    public ThumbnailLayerSelector getThumbnailLayerSelector() {
        return thumbnailLayerSelector;
    }

    private void updateViewGame() {
        lblGameName.setText(Game.gameNames[handler.getGameIndex()]);
        lblGameIcon.setIcon(new ImageIcon(handler.getGame().gameIcons[handler.getGameIndex()]));
    }

    public void undoMapState() {
        StateHandler mapStateHandler = handler.getMapStateHandler();
        if (mapStateHandler.canGetPreviousState()) {
            MapLayerState state = (MapLayerState) mapStateHandler.getPreviousState(new MapLayerState("Map Edit", handler));
            state.revertState();
            jbRedo.setEnabled(true);
            if (!mapStateHandler.canGetPreviousState()) {
                jbUndo.setEnabled(false);
            }
            mapDisplay.repaint();
            thumbnailLayerSelector.drawLayerThumbnail(state.getLayerIndex());
            thumbnailLayerSelector.repaint();
        }
    }

    public void redoMapState() {
        StateHandler mapStateHandler = handler.getMapStateHandler();
        if (mapStateHandler.canGetNextState()) {
            MapLayerState state = (MapLayerState) mapStateHandler.getNextState();
            state.revertState();
            jbUndo.setEnabled(true);
            mapDisplay.repaint();
            thumbnailLayerSelector.drawLayerThumbnail(state.getLayerIndex());
            thumbnailLayerSelector.repaint();
            if (!mapStateHandler.canGetNextState()) {
                jbRedo.setEnabled(false);
            }
        }
    }

    public void moveTilesUp() {
        handler.addMapState(new MapLayerState("Move tiles up", handler));
        handler.getGrid().moveTilesUp(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.repaint();
    }

    public void moveTilesDown() {
        handler.addMapState(new MapLayerState("Move tiles down", handler));
        handler.getGrid().moveTilesDown(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.repaint();
    }

    public void moveTilesLeft() {
        handler.addMapState(new MapLayerState("Move tiles left", handler));
        handler.getGrid().moveTilesLeft(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.repaint();
    }

    public void moveTilesRight() {
        handler.addMapState(new MapLayerState("Move tiles right", handler));
        handler.getGrid().moveTilesRight(handler.getActiveLayerIndex());
        thumbnailLayerSelector.drawLayerThumbnail(handler.getActiveLayerIndex());
        thumbnailLayerSelector.repaint();
        mapDisplay.repaint();
    }

    public JButton getUndoButton() {
        return jbUndo;
    }

    public JButton getRedoButton() {
        return jbRedo;
    }

    public MapDisplay getMapDisplay() {
        return mapDisplay;
    }

    public TileDisplay getTileDisplay() {
        return tileDisplay;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jMenu = new JMenuBar();
        jMenu1 = new JMenu();
        jmiNewMap = new JMenuItem();
        jmiOpenMap = new JMenuItem();
        jmiSaveMap = new JMenuItem();
        jmiSaveMapAs = new JMenuItem();
        jmiExportObjWithText = new JMenuItem();
        jmiExportMapAsImd = new JMenuItem();
        jmiExportMapAsNsb = new JMenuItem();
        jmiExportMapBtx = new JMenuItem();
        jmiImportTileset = new JMenuItem();
        jmiExportTileset = new JMenuItem();
        jmiExportAllTiles = new JMenuItem();
        jMenu2 = new JMenu();
        jmiUndo = new JMenuItem();
        jmiRedo = new JMenuItem();
        jmiClearTiles = new JMenuItem();
        jmiUseSmartDrawing = new JMenuItem();
        jmiClearLayer = new JMenuItem();
        jmiClearAllLayers = new JMenuItem();
        jmiCopyLayer = new JMenuItem();
        jmiPasteLayer = new JMenuItem();
        jmiPasteLayerTiles = new JMenuItem();
        jmiPasteLayerHeights = new JMenuItem();
        jMenu3 = new JMenu();
        jmi3dView = new JMenuItem();
        jmiTopView = new JMenuItem();
        jmiToggleHeightView = new JMenuItem();
        jmiToggleGrid = new JMenuItem();
        jmiLoadBackImg = new JMenuItem();
        jcbUseBackImage = new JCheckBoxMenuItem();
        jMenu4 = new JMenu();
        jmiTilesetEditor = new JMenuItem();
        jmiCollisionEditor = new JMenuItem();
        jmiBdhcEditor = new JMenuItem();
        jmiNsbtxEditor = new JMenuItem();
        jmHelp = new JMenu();
        jmiKeyboardInfo = new JMenuItem();
        jmiAbout = new JMenuItem();
        jToolBar = new JToolBar();
        jbNewMap = new JButton();
        jbOpenMap = new JButton();
        jbSaveMap = new JButton();
        jbExportObj = new JButton();
        jbExportImd = new JButton();
        jbExportNsb = new JButton();
        jbExportNsb1 = new JButton();
        jbUndo = new JButton();
        jbRedo = new JButton();
        jb3DView = new JButton();
        jbTopView = new JButton();
        jbHeightView = new JButton();
        jbGridView = new JButton();
        jbClearTile = new JButton();
        jbUseSmartGrid = new JButton();
        jbTilelistEditor = new JButton();
        jbCollisionsEditor = new JButton();
        jbBdhcEditor = new JButton();
        jbBacksoundEditor = new JButton();
        jbNsbtxEditor1 = new JButton();
        jbBuildingEditor = new JButton();
        jbAnimationEditor = new JButton();
        jbKeboardInfo = new JButton();
        jbHelp = new JButton();
        pnlGameInfo = new JPanel();
        lblGame = new JLabel();
        lblGameIcon = new JLabel();
        lblGameName = new JLabel();
        splitPane = new JSplitPane();
        pnlMainWindow = new JPanel();
        pnlLayer = new JPanel();
        thumbnailLayerSelector = new ThumbnailLayerSelector();
        pnlZ = new JPanel();
        heightSelector = new HeightSelector();
        pnlTileList = new JPanel();
        jScrollPane1 = new JScrollPane();
        tileSelector = new TileSelector();
        pnlSmartDrawing = new JPanel();
        jScrollPane2 = new JScrollPane();
        smartGridDisplay = new SmartGridDisplay();
        pnlMapDisplay = new JPanel();
        mapDisplay = new MapDisplay();
        pnlRightPanel = new JPanel();
        pnlBorderMap = new JPanel();
        borderMapsDisplay = new BorderMapsDisplay();
        pnlHeightMapAlpha = new JPanel();
        jsHeightMapAlpha = new JSlider();
        pnlBackImageAlpha = new JPanel();
        jsBackImageAlpha = new JSlider();
        pnlMoveLayer = new JPanel();
        jbMoveMapUp = new JButton();
        jbMoveMapLeft = new JButton();
        jbMoveMapDown = new JButton();
        jbMoveMapRight = new JButton();
        pnlSelectedTile = new JPanel();
        tileDisplay = new TileDisplay();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Pokemon DS Map Studio");
        setName("this");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
        });
        var contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "insets 0,hidemode 3,gap 5 5",
            // columns
            "[grow,fill]" +
            "[fill]",
            // rows
            "[fill]" +
            "[grow,fill]"));

        //======== jMenu ========
        {
            jMenu.setName("jMenu");

            //======== jMenu1 ========
            {
                jMenu1.setText("File");
                jMenu1.setName("jMenu1");

                //---- jmiNewMap ----
                jmiNewMap.setIcon(new ImageIcon(getClass().getResource("/icons/newMapIcon_s.png")));
                jmiNewMap.setText("New Map...");
                jmiNewMap.setName("jmiNewMap");
                jmiNewMap.addActionListener(e -> jmiNewMapActionPerformed(e));
                jMenu1.add(jmiNewMap);
                jMenu1.addSeparator();

                //---- jmiOpenMap ----
                jmiOpenMap.setIcon(new ImageIcon(getClass().getResource("/icons/openMapIcon_s.png")));
                jmiOpenMap.setText("Open Map...");
                jmiOpenMap.setName("jmiOpenMap");
                jmiOpenMap.addActionListener(e -> jmiOpenMapActionPerformed(e));
                jMenu1.add(jmiOpenMap);
                jMenu1.addSeparator();

                //---- jmiSaveMap ----
                jmiSaveMap.setText("Save Map...");
                jmiSaveMap.setName("jmiSaveMap");
                jmiSaveMap.addActionListener(e -> jmiSaveMapActionPerformed(e));
                jMenu1.add(jmiSaveMap);

                //---- jmiSaveMapAs ----
                jmiSaveMapAs.setText("Save Map as...");
                jmiSaveMapAs.setName("jmiSaveMapAs");
                jmiSaveMapAs.addActionListener(e -> jmiSaveMapAsActionPerformed(e));
                jMenu1.add(jmiSaveMapAs);
                jMenu1.addSeparator();

                //---- jmiExportObjWithText ----
                jmiExportObjWithText.setText("Export Map as OBJ with textures...");
                jmiExportObjWithText.setName("jmiExportObjWithText");
                jmiExportObjWithText.addActionListener(e -> jmiExportObjWithTextActionPerformed(e));
                jMenu1.add(jmiExportObjWithText);

                //---- jmiExportMapAsImd ----
                jmiExportMapAsImd.setText("Export Map as IMD...");
                jmiExportMapAsImd.setName("jmiExportMapAsImd");
                jmiExportMapAsImd.addActionListener(e -> jmiExportMapAsImdActionPerformed(e));
                jMenu1.add(jmiExportMapAsImd);

                //---- jmiExportMapAsNsb ----
                jmiExportMapAsNsb.setText("Export Map as NSBMD...");
                jmiExportMapAsNsb.setName("jmiExportMapAsNsb");
                jmiExportMapAsNsb.addActionListener(e -> jmiExportMapAsNsbActionPerformed(e));
                jMenu1.add(jmiExportMapAsNsb);

                //---- jmiExportMapBtx ----
                jmiExportMapBtx.setText("Export Map's NSBTX...");
                jmiExportMapBtx.setName("jmiExportMapBtx");
                jmiExportMapBtx.addActionListener(e -> jmiExportMapBtxActionPerformed(e));
                jMenu1.add(jmiExportMapBtx);
                jMenu1.addSeparator();

                //---- jmiImportTileset ----
                jmiImportTileset.setText("Import Tileset...");
                jmiImportTileset.setName("jmiImportTileset");
                jmiImportTileset.addActionListener(e -> jmiImportTilesetActionPerformed(e));
                jMenu1.add(jmiImportTileset);

                //---- jmiExportTileset ----
                jmiExportTileset.setText("Export Tileset...");
                jmiExportTileset.setName("jmiExportTileset");
                jmiExportTileset.addActionListener(e -> jmiExportTilesetActionPerformed(e));
                jMenu1.add(jmiExportTileset);

                //---- jmiExportAllTiles ----
                jmiExportAllTiles.setText("Export All Tiles as OBJ...");
                jmiExportAllTiles.setName("jmiExportAllTiles");
                jmiExportAllTiles.addActionListener(e -> jmiExportAllTilesActionPerformed(e));
                jMenu1.add(jmiExportAllTiles);
            }
            jMenu.add(jMenu1);

            //======== jMenu2 ========
            {
                jMenu2.setText("Edit");
                jMenu2.setName("jMenu2");

                //---- jmiUndo ----
                jmiUndo.setText("Undo");
                jmiUndo.setName("jmiUndo");
                jmiUndo.addActionListener(e -> jmiUndoActionPerformed(e));
                jMenu2.add(jmiUndo);

                //---- jmiRedo ----
                jmiRedo.setText("Redo");
                jmiRedo.setName("jmiRedo");
                jmiRedo.addActionListener(e -> jmiRedoActionPerformed(e));
                jMenu2.add(jmiRedo);
                jMenu2.addSeparator();

                //---- jmiClearTiles ----
                jmiClearTiles.setText("Clear Tiles");
                jmiClearTiles.setName("jmiClearTiles");
                jmiClearTiles.addActionListener(e -> jmiClearTilesActionPerformed(e));
                jMenu2.add(jmiClearTiles);

                //---- jmiUseSmartDrawing ----
                jmiUseSmartDrawing.setText("Use Smart Drawing");
                jmiUseSmartDrawing.setName("jmiUseSmartDrawing");
                jmiUseSmartDrawing.addActionListener(e -> jmiUseSmartDrawingActionPerformed(e));
                jMenu2.add(jmiUseSmartDrawing);
                jMenu2.addSeparator();

                //---- jmiClearLayer ----
                jmiClearLayer.setText("Clear Layer");
                jmiClearLayer.setName("jmiClearLayer");
                jmiClearLayer.addActionListener(e -> jmiClearLayerActionPerformed(e));
                jMenu2.add(jmiClearLayer);

                //---- jmiClearAllLayers ----
                jmiClearAllLayers.setText("Clear All Layers");
                jmiClearAllLayers.setEnabled(false);
                jmiClearAllLayers.setName("jmiClearAllLayers");
                jmiClearAllLayers.addActionListener(e -> jmiClearAllLayersActionPerformed(e));
                jMenu2.add(jmiClearAllLayers);
                jMenu2.addSeparator();

                //---- jmiCopyLayer ----
                jmiCopyLayer.setText("Copy Layer");
                jmiCopyLayer.setName("jmiCopyLayer");
                jmiCopyLayer.addActionListener(e -> jmiCopyLayerActionPerformed(e));
                jMenu2.add(jmiCopyLayer);

                //---- jmiPasteLayer ----
                jmiPasteLayer.setText("Paste Layer");
                jmiPasteLayer.setName("jmiPasteLayer");
                jmiPasteLayer.addActionListener(e -> jmiPasteLayerActionPerformed(e));
                jMenu2.add(jmiPasteLayer);

                //---- jmiPasteLayerTiles ----
                jmiPasteLayerTiles.setText("Paste Layer Tiles");
                jmiPasteLayerTiles.setName("jmiPasteLayerTiles");
                jmiPasteLayerTiles.addActionListener(e -> jmiPasteLayerTilesActionPerformed(e));
                jMenu2.add(jmiPasteLayerTiles);

                //---- jmiPasteLayerHeights ----
                jmiPasteLayerHeights.setText("Paste Layer Heights");
                jmiPasteLayerHeights.setName("jmiPasteLayerHeights");
                jmiPasteLayerHeights.addActionListener(e -> jmiPasteLayerHeightsActionPerformed(e));
                jMenu2.add(jmiPasteLayerHeights);
            }
            jMenu.add(jMenu2);

            //======== jMenu3 ========
            {
                jMenu3.setText("View");
                jMenu3.setName("jMenu3");

                //---- jmi3dView ----
                jmi3dView.setText("3D View");
                jmi3dView.setName("jmi3dView");
                jmi3dView.addActionListener(e -> jmi3dViewActionPerformed(e));
                jMenu3.add(jmi3dView);

                //---- jmiTopView ----
                jmiTopView.setText("Top View");
                jmiTopView.setName("jmiTopView");
                jmiTopView.addActionListener(e -> jmiTopViewActionPerformed(e));
                jMenu3.add(jmiTopView);

                //---- jmiToggleHeightView ----
                jmiToggleHeightView.setText("Toggle Height View");
                jmiToggleHeightView.setName("jmiToggleHeightView");
                jmiToggleHeightView.addActionListener(e -> jmiToggleHeightViewActionPerformed(e));
                jMenu3.add(jmiToggleHeightView);

                //---- jmiToggleGrid ----
                jmiToggleGrid.setText("Toggle Grid");
                jmiToggleGrid.setName("jmiToggleGrid");
                jmiToggleGrid.addActionListener(e -> jmiToggleGridActionPerformed(e));
                jMenu3.add(jmiToggleGrid);
                jMenu3.addSeparator();

                //---- jmiLoadBackImg ----
                jmiLoadBackImg.setText("Open Background Image");
                jmiLoadBackImg.setName("jmiLoadBackImg");
                jmiLoadBackImg.addActionListener(e -> jmiLoadBackImgActionPerformed(e));
                jMenu3.add(jmiLoadBackImg);

                //---- jcbUseBackImage ----
                jcbUseBackImage.setText("Use Background Image");
                jcbUseBackImage.setName("jcbUseBackImage");
                jcbUseBackImage.addActionListener(e -> jcbUseBackImageActionPerformed(e));
                jMenu3.add(jcbUseBackImage);
            }
            jMenu.add(jMenu3);

            //======== jMenu4 ========
            {
                jMenu4.setText("Tools");
                jMenu4.setName("jMenu4");

                //---- jmiTilesetEditor ----
                jmiTilesetEditor.setText("Tileset Editor");
                jmiTilesetEditor.setName("jmiTilesetEditor");
                jmiTilesetEditor.addActionListener(e -> jmiTilesetEditorActionPerformed(e));
                jMenu4.add(jmiTilesetEditor);

                //---- jmiCollisionEditor ----
                jmiCollisionEditor.setText("Collision Editor");
                jmiCollisionEditor.setName("jmiCollisionEditor");
                jmiCollisionEditor.addActionListener(e -> jmiCollisionEditorActionPerformed(e));
                jMenu4.add(jmiCollisionEditor);

                //---- jmiBdhcEditor ----
                jmiBdhcEditor.setText("BDHC Editor");
                jmiBdhcEditor.setName("jmiBdhcEditor");
                jmiBdhcEditor.addActionListener(e -> jmiBdhcEditorActionPerformed(e));
                jMenu4.add(jmiBdhcEditor);

                //---- jmiNsbtxEditor ----
                jmiNsbtxEditor.setText("NSBTX Editor");
                jmiNsbtxEditor.setName("jmiNsbtxEditor");
                jmiNsbtxEditor.addActionListener(e -> jmiNsbtxEditorActionPerformed(e));
                jMenu4.add(jmiNsbtxEditor);
            }
            jMenu.add(jMenu4);

            //======== jmHelp ========
            {
                jmHelp.setText("Help");
                jmHelp.setName("jmHelp");

                //---- jmiKeyboardInfo ----
                jmiKeyboardInfo.setText("Keyboard Shortcuts");
                jmiKeyboardInfo.setName("jmiKeyboardInfo");
                jmiKeyboardInfo.addActionListener(e -> jmiKeyboardInfoActionPerformed(e));
                jmHelp.add(jmiKeyboardInfo);

                //---- jmiAbout ----
                jmiAbout.setText("About");
                jmiAbout.setName("jmiAbout");
                jmiAbout.addActionListener(e -> jmiAboutActionPerformed(e));
                jmHelp.add(jmiAbout);
            }
            jMenu.add(jmHelp);
        }
        setJMenuBar(jMenu);

        //======== jToolBar ========
        {
            jToolBar.setFloatable(false);
            jToolBar.setMargin(null);
            jToolBar.setMaximumSize(null);
            jToolBar.setMinimumSize(null);
            jToolBar.setPreferredSize(null);
            jToolBar.setRollover(true);
            jToolBar.setName("jToolBar");

            //---- jbNewMap ----
            jbNewMap.setIcon(new ImageIcon(getClass().getResource("/icons/newMapIcon.png")));
            jbNewMap.setToolTipText("New Map");
            jbNewMap.setBorderPainted(false);
            jbNewMap.setFocusable(false);
            jbNewMap.setHorizontalTextPosition(SwingConstants.CENTER);
            jbNewMap.setIconTextGap(0);
            jbNewMap.setMargin(new Insets(0, 0, 0, 0));
            jbNewMap.setMaximumSize(new Dimension(38, 38));
            jbNewMap.setMinimumSize(new Dimension(38, 38));
            jbNewMap.setPreferredSize(new Dimension(38, 38));
            jbNewMap.setName("jbNewMap");
            jbNewMap.addActionListener(e -> jbNewMapActionPerformed(e));
            jToolBar.add(jbNewMap);

            //---- jbOpenMap ----
            jbOpenMap.setIcon(new ImageIcon(getClass().getResource("/icons/openMapIcon.png")));
            jbOpenMap.setToolTipText("Open Map");
            jbOpenMap.setFocusable(false);
            jbOpenMap.setHorizontalTextPosition(SwingConstants.CENTER);
            jbOpenMap.setMaximumSize(new Dimension(38, 38));
            jbOpenMap.setMinimumSize(new Dimension(38, 38));
            jbOpenMap.setName("");
            jbOpenMap.setPreferredSize(new Dimension(38, 38));
            jbOpenMap.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbOpenMap.addActionListener(e -> jbOpenMapActionPerformed(e));
            jToolBar.add(jbOpenMap);

            //---- jbSaveMap ----
            jbSaveMap.setIcon(new ImageIcon(getClass().getResource("/icons/saveMapIcon.png")));
            jbSaveMap.setToolTipText("Save Map");
            jbSaveMap.setFocusable(false);
            jbSaveMap.setHorizontalTextPosition(SwingConstants.CENTER);
            jbSaveMap.setMaximumSize(new Dimension(38, 38));
            jbSaveMap.setMinimumSize(new Dimension(38, 38));
            jbSaveMap.setName("");
            jbSaveMap.setPreferredSize(new Dimension(38, 38));
            jbSaveMap.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbSaveMap.addActionListener(e -> jbSaveMapActionPerformed(e));
            jToolBar.add(jbSaveMap);

            //---- jbExportObj ----
            jbExportObj.setIcon(new ImageIcon(getClass().getResource("/icons/exportObjIcon.png")));
            jbExportObj.setToolTipText("Export Map as OBJ with Textures");
            jbExportObj.setFocusable(false);
            jbExportObj.setHorizontalTextPosition(SwingConstants.CENTER);
            jbExportObj.setMaximumSize(new Dimension(38, 38));
            jbExportObj.setMinimumSize(new Dimension(38, 38));
            jbExportObj.setName("");
            jbExportObj.setPreferredSize(new Dimension(38, 38));
            jbExportObj.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbExportObj.addActionListener(e -> jbExportObjActionPerformed(e));
            jToolBar.add(jbExportObj);

            //---- jbExportImd ----
            jbExportImd.setIcon(new ImageIcon(getClass().getResource("/icons/exportImdIcon.png")));
            jbExportImd.setToolTipText("Export Map as IMD");
            jbExportImd.setFocusable(false);
            jbExportImd.setHorizontalTextPosition(SwingConstants.CENTER);
            jbExportImd.setMaximumSize(new Dimension(38, 38));
            jbExportImd.setMinimumSize(new Dimension(38, 38));
            jbExportImd.setName("");
            jbExportImd.setPreferredSize(new Dimension(38, 38));
            jbExportImd.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbExportImd.addActionListener(e -> jbExportImdActionPerformed(e));
            jToolBar.add(jbExportImd);

            //---- jbExportNsb ----
            jbExportNsb.setIcon(new ImageIcon(getClass().getResource("/icons/exportNsbIcon.png")));
            jbExportNsb.setToolTipText("Export Map as NSBMD");
            jbExportNsb.setFocusable(false);
            jbExportNsb.setHorizontalTextPosition(SwingConstants.CENTER);
            jbExportNsb.setMaximumSize(new Dimension(38, 38));
            jbExportNsb.setMinimumSize(new Dimension(38, 38));
            jbExportNsb.setName("");
            jbExportNsb.setPreferredSize(new Dimension(38, 38));
            jbExportNsb.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbExportNsb.addActionListener(e -> jbExportNsbActionPerformed(e));
            jToolBar.add(jbExportNsb);

            //---- jbExportNsb1 ----
            jbExportNsb1.setIcon(new ImageIcon(getClass().getResource("/icons/exportBtxIcon.png")));
            jbExportNsb1.setToolTipText("Export Map NSBTX");
            jbExportNsb1.setFocusable(false);
            jbExportNsb1.setHorizontalTextPosition(SwingConstants.CENTER);
            jbExportNsb1.setMaximumSize(new Dimension(38, 38));
            jbExportNsb1.setMinimumSize(new Dimension(38, 38));
            jbExportNsb1.setName("");
            jbExportNsb1.setPreferredSize(new Dimension(38, 38));
            jbExportNsb1.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbExportNsb1.addActionListener(e -> jbExportNsb1ActionPerformed(e));
            jToolBar.add(jbExportNsb1);
            jToolBar.addSeparator();

            //---- jbUndo ----
            jbUndo.setIcon(new ImageIcon(getClass().getResource("/icons/undoIcon.png")));
            jbUndo.setToolTipText("Undo (Ctrl+Z)");
            jbUndo.setDisabledIcon(new ImageIcon(getClass().getResource("/icons/undoDisabledIcon.png")));
            jbUndo.setEnabled(false);
            jbUndo.setFocusable(false);
            jbUndo.setHorizontalTextPosition(SwingConstants.CENTER);
            jbUndo.setMaximumSize(new Dimension(38, 38));
            jbUndo.setMinimumSize(new Dimension(38, 38));
            jbUndo.setName("");
            jbUndo.setPreferredSize(new Dimension(38, 38));
            jbUndo.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbUndo.addActionListener(e -> jbUndoActionPerformed(e));
            jToolBar.add(jbUndo);

            //---- jbRedo ----
            jbRedo.setIcon(new ImageIcon(getClass().getResource("/icons/redoIcon.png")));
            jbRedo.setToolTipText("Redo (Ctrl+Y)");
            jbRedo.setDisabledIcon(new ImageIcon(getClass().getResource("/icons/redoDisabledIcon.png")));
            jbRedo.setEnabled(false);
            jbRedo.setFocusable(false);
            jbRedo.setHorizontalTextPosition(SwingConstants.CENTER);
            jbRedo.setMaximumSize(new Dimension(38, 38));
            jbRedo.setMinimumSize(new Dimension(38, 38));
            jbRedo.setName("");
            jbRedo.setPreferredSize(new Dimension(38, 38));
            jbRedo.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbRedo.addActionListener(e -> jbRedoActionPerformed(e));
            jToolBar.add(jbRedo);
            jToolBar.addSeparator();

            //---- jb3DView ----
            jb3DView.setIcon(new ImageIcon(getClass().getResource("/icons/3DViewIcon.png")));
            jb3DView.setToolTipText("3D View");
            jb3DView.setFocusable(false);
            jb3DView.setHorizontalTextPosition(SwingConstants.CENTER);
            jb3DView.setMaximumSize(new Dimension(38, 38));
            jb3DView.setMinimumSize(new Dimension(38, 38));
            jb3DView.setName("");
            jb3DView.setPreferredSize(new Dimension(38, 38));
            jb3DView.setVerticalTextPosition(SwingConstants.BOTTOM);
            jb3DView.addActionListener(e -> jb3DViewActionPerformed(e));
            jToolBar.add(jb3DView);

            //---- jbTopView ----
            jbTopView.setIcon(new ImageIcon(getClass().getResource("/icons/topViewIcon.png")));
            jbTopView.setToolTipText("Ortho View");
            jbTopView.setFocusable(false);
            jbTopView.setHorizontalTextPosition(SwingConstants.CENTER);
            jbTopView.setMaximumSize(new Dimension(38, 38));
            jbTopView.setMinimumSize(new Dimension(38, 38));
            jbTopView.setName("");
            jbTopView.setPreferredSize(new Dimension(38, 38));
            jbTopView.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbTopView.addActionListener(e -> jbTopViewActionPerformed(e));
            jToolBar.add(jbTopView);

            //---- jbHeightView ----
            jbHeightView.setIcon(new ImageIcon(getClass().getResource("/icons/heightViewIcon.png")));
            jbHeightView.setToolTipText("Height View");
            jbHeightView.setFocusable(false);
            jbHeightView.setHorizontalTextPosition(SwingConstants.CENTER);
            jbHeightView.setMaximumSize(new Dimension(38, 38));
            jbHeightView.setMinimumSize(new Dimension(38, 38));
            jbHeightView.setName("");
            jbHeightView.setPreferredSize(new Dimension(38, 38));
            jbHeightView.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbHeightView.addActionListener(e -> jbHeightViewActionPerformed(e));
            jToolBar.add(jbHeightView);

            //---- jbGridView ----
            jbGridView.setIcon(new ImageIcon(getClass().getResource("/icons/gridViewIcon.png")));
            jbGridView.setToolTipText("View 3D Grid");
            jbGridView.setFocusable(false);
            jbGridView.setHorizontalTextPosition(SwingConstants.CENTER);
            jbGridView.setMaximumSize(new Dimension(38, 38));
            jbGridView.setMinimumSize(new Dimension(38, 38));
            jbGridView.setName("");
            jbGridView.setPreferredSize(new Dimension(38, 38));
            jbGridView.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbGridView.addActionListener(e -> jbGridViewActionPerformed(e));
            jToolBar.add(jbGridView);
            jToolBar.addSeparator();

            //---- jbClearTile ----
            jbClearTile.setIcon(new ImageIcon(getClass().getResource("/cursors/clearTileCursor.png")));
            jbClearTile.setToolTipText("Clear Tile");
            jbClearTile.setFocusable(false);
            jbClearTile.setHorizontalTextPosition(SwingConstants.CENTER);
            jbClearTile.setMaximumSize(new Dimension(38, 38));
            jbClearTile.setMinimumSize(new Dimension(38, 38));
            jbClearTile.setName("");
            jbClearTile.setPreferredSize(new Dimension(38, 38));
            jbClearTile.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbClearTile.addActionListener(e -> jbClearTileActionPerformed(e));
            jToolBar.add(jbClearTile);

            //---- jbUseSmartGrid ----
            jbUseSmartGrid.setIcon(new ImageIcon(getClass().getResource("/cursors/smartGridCursor.png")));
            jbUseSmartGrid.setToolTipText("Use Smart Drawing");
            jbUseSmartGrid.setFocusable(false);
            jbUseSmartGrid.setHorizontalTextPosition(SwingConstants.CENTER);
            jbUseSmartGrid.setMaximumSize(new Dimension(38, 38));
            jbUseSmartGrid.setMinimumSize(new Dimension(38, 38));
            jbUseSmartGrid.setName("");
            jbUseSmartGrid.setPreferredSize(new Dimension(38, 38));
            jbUseSmartGrid.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbUseSmartGrid.addActionListener(e -> jbUseSmartGridActionPerformed(e));
            jToolBar.add(jbUseSmartGrid);
            jToolBar.addSeparator();

            //---- jbTilelistEditor ----
            jbTilelistEditor.setIcon(new ImageIcon(getClass().getResource("/icons/tilelistEditorIcon.png")));
            jbTilelistEditor.setToolTipText("Tile List Editor");
            jbTilelistEditor.setFocusable(false);
            jbTilelistEditor.setHorizontalTextPosition(SwingConstants.CENTER);
            jbTilelistEditor.setMaximumSize(new Dimension(38, 38));
            jbTilelistEditor.setMinimumSize(new Dimension(38, 38));
            jbTilelistEditor.setName("");
            jbTilelistEditor.setPreferredSize(new Dimension(38, 38));
            jbTilelistEditor.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbTilelistEditor.addActionListener(e -> jbTilelistEditorActionPerformed(e));
            jToolBar.add(jbTilelistEditor);

            //---- jbCollisionsEditor ----
            jbCollisionsEditor.setIcon(new ImageIcon(getClass().getResource("/icons/collisionEditorIcon.png")));
            jbCollisionsEditor.setToolTipText("Collisions Editor");
            jbCollisionsEditor.setFocusable(false);
            jbCollisionsEditor.setHorizontalTextPosition(SwingConstants.CENTER);
            jbCollisionsEditor.setMaximumSize(new Dimension(38, 38));
            jbCollisionsEditor.setMinimumSize(new Dimension(38, 38));
            jbCollisionsEditor.setName("");
            jbCollisionsEditor.setPreferredSize(new Dimension(38, 38));
            jbCollisionsEditor.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbCollisionsEditor.addActionListener(e -> jbCollisionsEditorActionPerformed(e));
            jToolBar.add(jbCollisionsEditor);

            //---- jbBdhcEditor ----
            jbBdhcEditor.setIcon(new ImageIcon(getClass().getResource("/icons/bdhcEditorIcon.png")));
            jbBdhcEditor.setToolTipText("BDHC Editor");
            jbBdhcEditor.setFocusable(false);
            jbBdhcEditor.setHorizontalTextPosition(SwingConstants.CENTER);
            jbBdhcEditor.setMaximumSize(new Dimension(38, 38));
            jbBdhcEditor.setMinimumSize(new Dimension(38, 38));
            jbBdhcEditor.setName("");
            jbBdhcEditor.setPreferredSize(new Dimension(38, 38));
            jbBdhcEditor.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbBdhcEditor.addActionListener(e -> jbBdhcEditorActionPerformed(e));
            jToolBar.add(jbBdhcEditor);

            //---- jbBacksoundEditor ----
            jbBacksoundEditor.setIcon(new ImageIcon(getClass().getResource("/icons/backsoundEditorIcon.png")));
            jbBacksoundEditor.setToolTipText("Backsound Editor");
            jbBacksoundEditor.setFocusable(false);
            jbBacksoundEditor.setHorizontalTextPosition(SwingConstants.CENTER);
            jbBacksoundEditor.setMaximumSize(new Dimension(38, 38));
            jbBacksoundEditor.setMinimumSize(new Dimension(38, 38));
            jbBacksoundEditor.setName("");
            jbBacksoundEditor.setPreferredSize(new Dimension(38, 38));
            jbBacksoundEditor.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbBacksoundEditor.addActionListener(e -> jbBacksoundEditorActionPerformed(e));
            jToolBar.add(jbBacksoundEditor);

            //---- jbNsbtxEditor1 ----
            jbNsbtxEditor1.setIcon(new ImageIcon(getClass().getResource("/icons/nsbtxEditorIcon.png")));
            jbNsbtxEditor1.setToolTipText("NSBTX Editor");
            jbNsbtxEditor1.setFocusable(false);
            jbNsbtxEditor1.setHorizontalTextPosition(SwingConstants.CENTER);
            jbNsbtxEditor1.setMaximumSize(new Dimension(38, 38));
            jbNsbtxEditor1.setMinimumSize(new Dimension(38, 38));
            jbNsbtxEditor1.setName("");
            jbNsbtxEditor1.setPreferredSize(new Dimension(38, 38));
            jbNsbtxEditor1.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbNsbtxEditor1.addActionListener(e -> jbNsbtxEditor1ActionPerformed(e));
            jToolBar.add(jbNsbtxEditor1);

            //---- jbBuildingEditor ----
            jbBuildingEditor.setIcon(new ImageIcon(getClass().getResource("/icons/buildingEditorIcon.png")));
            jbBuildingEditor.setToolTipText("Building Editor");
            jbBuildingEditor.setFocusable(false);
            jbBuildingEditor.setHorizontalTextPosition(SwingConstants.CENTER);
            jbBuildingEditor.setMaximumSize(new Dimension(38, 38));
            jbBuildingEditor.setMinimumSize(new Dimension(38, 38));
            jbBuildingEditor.setName("");
            jbBuildingEditor.setPreferredSize(new Dimension(38, 38));
            jbBuildingEditor.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbBuildingEditor.addActionListener(e -> jbBuildingEditorActionPerformed(e));
            jToolBar.add(jbBuildingEditor);

            //---- jbAnimationEditor ----
            jbAnimationEditor.setIcon(new ImageIcon(getClass().getResource("/icons/animationEditorIcon.png")));
            jbAnimationEditor.setToolTipText("Animation editor");
            jbAnimationEditor.setFocusable(false);
            jbAnimationEditor.setHorizontalTextPosition(SwingConstants.CENTER);
            jbAnimationEditor.setMaximumSize(new Dimension(38, 38));
            jbAnimationEditor.setMinimumSize(new Dimension(38, 38));
            jbAnimationEditor.setName("");
            jbAnimationEditor.setPreferredSize(new Dimension(38, 38));
            jbAnimationEditor.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbAnimationEditor.addActionListener(e -> jbAnimationEditorActionPerformed(e));
            jToolBar.add(jbAnimationEditor);
            jToolBar.addSeparator();

            //---- jbKeboardInfo ----
            jbKeboardInfo.setIcon(new ImageIcon(getClass().getResource("/icons/keyboardInfoIcon.png")));
            jbKeboardInfo.setToolTipText("Keyboard Shortcuts");
            jbKeboardInfo.setFocusable(false);
            jbKeboardInfo.setHorizontalTextPosition(SwingConstants.CENTER);
            jbKeboardInfo.setMaximumSize(new Dimension(38, 38));
            jbKeboardInfo.setMinimumSize(new Dimension(38, 38));
            jbKeboardInfo.setName("");
            jbKeboardInfo.setPreferredSize(new Dimension(38, 38));
            jbKeboardInfo.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbKeboardInfo.addActionListener(e -> jbKeboardInfoActionPerformed(e));
            jToolBar.add(jbKeboardInfo);

            //---- jbHelp ----
            jbHelp.setIcon(new ImageIcon(getClass().getResource("/icons/helpIcon.png")));
            jbHelp.setToolTipText("Help");
            jbHelp.setFocusable(false);
            jbHelp.setHorizontalTextPosition(SwingConstants.CENTER);
            jbHelp.setMaximumSize(new Dimension(38, 38));
            jbHelp.setMinimumSize(new Dimension(38, 38));
            jbHelp.setName("");
            jbHelp.setPreferredSize(new Dimension(38, 38));
            jbHelp.setVerticalTextPosition(SwingConstants.BOTTOM);
            jbHelp.addActionListener(e -> jbHelpActionPerformed(e));
            jToolBar.add(jbHelp);
        }
        contentPane.add(jToolBar, "cell 0 0");

        //======== pnlGameInfo ========
        {
            pnlGameInfo.setName("pnlGameInfo");
            pnlGameInfo.setLayout(new GridBagLayout());
            ((GridBagLayout)pnlGameInfo.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout)pnlGameInfo.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout)pnlGameInfo.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout)pnlGameInfo.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

            //---- lblGame ----
            lblGame.setText("Map for: ");
            lblGame.setName("lblGame");
            pnlGameInfo.add(lblGame, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- lblGameIcon ----
            lblGameIcon.setText(" ");
            lblGameIcon.setMaximumSize(new Dimension(32, 32));
            lblGameIcon.setMinimumSize(new Dimension(32, 32));
            lblGameIcon.setPreferredSize(new Dimension(32, 32));
            lblGameIcon.setName("lblGameIcon");
            lblGameIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    jlGameIconMousePressed(e);
                }
            });
            pnlGameInfo.add(lblGameIcon, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

            //---- lblGameName ----
            lblGameName.setFont(new Font("Tahoma", Font.BOLD, 11));
            lblGameName.setText("Game Name");
            lblGameName.setName("lblGameName");
            pnlGameInfo.add(lblGameName, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        }
        contentPane.add(pnlGameInfo, "cell 1 0,grow");

        //======== splitPane ========
        {
            splitPane.setName("splitPane");

            //======== pnlMainWindow ========
            {
                pnlMainWindow.setMinimumSize(new Dimension(800, 550));
                pnlMainWindow.setName("pnlMainWindow");
                pnlMainWindow.setLayout(new MigLayout(
                    "filly,hidemode 3",
                    // columns
                    "[fill]" +
                    "[grow,fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[grow,fill]"));

                //======== pnlLayer ========
                {
                    pnlLayer.setBorder(new TitledBorder(null, "Layer", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, new Color(204, 102, 0)));
                    pnlLayer.setName("pnlLayer");

                    //======== thumbnailLayerSelector ========
                    {
                        thumbnailLayerSelector.setName("thumbnailLayerSelector");

                        GroupLayout thumbnailLayerSelectorLayout = new GroupLayout(thumbnailLayerSelector);
                        thumbnailLayerSelector.setLayout(thumbnailLayerSelectorLayout);
                        thumbnailLayerSelectorLayout.setHorizontalGroup(
                            thumbnailLayerSelectorLayout.createParallelGroup()
                                .addGap(0, 64, Short.MAX_VALUE)
                        );
                        thumbnailLayerSelectorLayout.setVerticalGroup(
                            thumbnailLayerSelectorLayout.createParallelGroup()
                                .addGap(0, 512, Short.MAX_VALUE)
                        );
                    }

                    GroupLayout pnlLayerLayout = new GroupLayout(pnlLayer);
                    pnlLayer.setLayout(pnlLayerLayout);
                    pnlLayerLayout.setHorizontalGroup(
                        pnlLayerLayout.createParallelGroup()
                            .addGroup(pnlLayerLayout.createSequentialGroup()
                                .addComponent(thumbnailLayerSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                    );
                    pnlLayerLayout.setVerticalGroup(
                        pnlLayerLayout.createParallelGroup()
                            .addComponent(thumbnailLayerSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    );
                }
                pnlMainWindow.add(pnlLayer, "cell 0 0,growy");

                //======== pnlZ ========
                {
                    pnlZ.setBorder(new TitledBorder(null, "Z", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, null, Color.blue));
                    pnlZ.setName("pnlZ");

                    //======== heightSelector ========
                    {
                        heightSelector.setPreferredSize(new Dimension(16, 496));
                        heightSelector.setName("heightSelector");

                        GroupLayout heightSelectorLayout = new GroupLayout(heightSelector);
                        heightSelector.setLayout(heightSelectorLayout);
                        heightSelectorLayout.setHorizontalGroup(
                            heightSelectorLayout.createParallelGroup()
                                .addGap(0, 16, Short.MAX_VALUE)
                        );
                        heightSelectorLayout.setVerticalGroup(
                            heightSelectorLayout.createParallelGroup()
                                .addGap(0, 496, Short.MAX_VALUE)
                        );
                    }

                    GroupLayout pnlZLayout = new GroupLayout(pnlZ);
                    pnlZ.setLayout(pnlZLayout);
                    pnlZLayout.setHorizontalGroup(
                        pnlZLayout.createParallelGroup()
                            .addGroup(pnlZLayout.createSequentialGroup()
                                .addComponent(heightSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                    );
                    pnlZLayout.setVerticalGroup(
                        pnlZLayout.createParallelGroup()
                            .addGroup(pnlZLayout.createSequentialGroup()
                                .addComponent(heightSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                    );
                }
                pnlMainWindow.add(pnlZ, "cell 2 0,growy");

                //======== pnlTileList ========
                {
                    pnlTileList.setBorder(new TitledBorder(null, "Tile List", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
                    pnlTileList.setName("pnlTileList");

                    //======== jScrollPane1 ========
                    {
                        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                        jScrollPane1.setName("jScrollPane1");

                        //======== tileSelector ========
                        {
                            tileSelector.setPreferredSize(new Dimension(128, 0));
                            tileSelector.setName("tileSelector");
                            tileSelector.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mousePressed(MouseEvent e) {
                                    tileSelectorMousePressed(e);
                                }
                            });

                            GroupLayout tileSelectorLayout = new GroupLayout(tileSelector);
                            tileSelector.setLayout(tileSelectorLayout);
                            tileSelectorLayout.setHorizontalGroup(
                                tileSelectorLayout.createParallelGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                            );
                            tileSelectorLayout.setVerticalGroup(
                                tileSelectorLayout.createParallelGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                            );
                        }
                        jScrollPane1.setViewportView(tileSelector);
                    }

                    GroupLayout pnlTileListLayout = new GroupLayout(pnlTileList);
                    pnlTileList.setLayout(pnlTileListLayout);
                    pnlTileListLayout.setHorizontalGroup(
                        pnlTileListLayout.createParallelGroup()
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    );
                    pnlTileListLayout.setVerticalGroup(
                        pnlTileListLayout.createParallelGroup()
                            .addGroup(pnlTileListLayout.createSequentialGroup()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                                .addGap(0, 0, 0))
                    );
                }
                pnlMainWindow.add(pnlTileList, "cell 3 0,growy");

                //======== pnlSmartDrawing ========
                {
                    pnlSmartDrawing.setBorder(new TitledBorder(null, "Smart Drawing", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
                    pnlSmartDrawing.setName("pnlSmartDrawing");

                    //======== jScrollPane2 ========
                    {
                        jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                        jScrollPane2.setName("jScrollPane2");

                        //======== smartGridDisplay ========
                        {
                            smartGridDisplay.setName("smartGridDisplay");

                            GroupLayout smartGridDisplayLayout = new GroupLayout(smartGridDisplay);
                            smartGridDisplay.setLayout(smartGridDisplayLayout);
                            smartGridDisplayLayout.setHorizontalGroup(
                                smartGridDisplayLayout.createParallelGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                            );
                            smartGridDisplayLayout.setVerticalGroup(
                                smartGridDisplayLayout.createParallelGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                            );
                        }
                        jScrollPane2.setViewportView(smartGridDisplay);
                    }

                    GroupLayout pnlSmartDrawingLayout = new GroupLayout(pnlSmartDrawing);
                    pnlSmartDrawing.setLayout(pnlSmartDrawingLayout);
                    pnlSmartDrawingLayout.setHorizontalGroup(
                        pnlSmartDrawingLayout.createParallelGroup()
                            .addGroup(pnlSmartDrawingLayout.createSequentialGroup()
                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                    );
                    pnlSmartDrawingLayout.setVerticalGroup(
                        pnlSmartDrawingLayout.createParallelGroup()
                            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    );
                }
                pnlMainWindow.add(pnlSmartDrawing, "cell 4 0,growy");

                //======== pnlMapDisplay ========
                {
                    pnlMapDisplay.setName("pnlMapDisplay");
                    pnlMapDisplay.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            pnlMapDisplayComponentResized(e);
                        }
                    });
                    pnlMapDisplay.setLayout(new FlowLayout());

                    //======== mapDisplay ========
                    {
                        mapDisplay.setBorder(new LineBorder(new Color(102, 102, 102)));
                        mapDisplay.setMaximumSize(new Dimension(544, 544));
                        mapDisplay.setName("mapDisplay");

                        GroupLayout mapDisplayLayout = new GroupLayout(mapDisplay);
                        mapDisplay.setLayout(mapDisplayLayout);
                        mapDisplayLayout.setHorizontalGroup(
                            mapDisplayLayout.createParallelGroup()
                                .addGap(0, 542, Short.MAX_VALUE)
                        );
                        mapDisplayLayout.setVerticalGroup(
                            mapDisplayLayout.createParallelGroup()
                                .addGap(0, 542, Short.MAX_VALUE)
                        );
                    }
                    pnlMapDisplay.add(mapDisplay);
                }
                pnlMainWindow.add(pnlMapDisplay, "cell 1 0,grow");
            }
            splitPane.setLeftComponent(pnlMainWindow);

            //======== pnlRightPanel ========
            {
                pnlRightPanel.setPreferredSize(new Dimension(400, 818));
                pnlRightPanel.setName("pnlRightPanel");
                pnlRightPanel.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]",
                    // rows
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[grow]"));

                //======== pnlBorderMap ========
                {
                    pnlBorderMap.setBorder(new TitledBorder(null, "Border Maps", TitledBorder.LEFT, TitledBorder.ABOVE_TOP));
                    pnlBorderMap.setName("pnlBorderMap");

                    //======== borderMapsDisplay ========
                    {
                        borderMapsDisplay.setName("borderMapsDisplay");

                        GroupLayout borderMapsDisplayLayout = new GroupLayout(borderMapsDisplay);
                        borderMapsDisplay.setLayout(borderMapsDisplayLayout);
                        borderMapsDisplayLayout.setHorizontalGroup(
                            borderMapsDisplayLayout.createParallelGroup()
                                .addGap(0, 96, Short.MAX_VALUE)
                        );
                        borderMapsDisplayLayout.setVerticalGroup(
                            borderMapsDisplayLayout.createParallelGroup()
                                .addGap(0, 96, Short.MAX_VALUE)
                        );
                    }

                    GroupLayout pnlBorderMapLayout = new GroupLayout(pnlBorderMap);
                    pnlBorderMap.setLayout(pnlBorderMapLayout);
                    pnlBorderMapLayout.setHorizontalGroup(
                        pnlBorderMapLayout.createParallelGroup()
                            .addComponent(borderMapsDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    );
                    pnlBorderMapLayout.setVerticalGroup(
                        pnlBorderMapLayout.createParallelGroup()
                            .addGroup(pnlBorderMapLayout.createSequentialGroup()
                                .addComponent(borderMapsDisplay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                    );
                }
                pnlRightPanel.add(pnlBorderMap, "cell 0 0,alignx center,growx 0");

                //======== pnlHeightMapAlpha ========
                {
                    pnlHeightMapAlpha.setBorder(new TitledBorder(null, "Height Map Alpha", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
                    pnlHeightMapAlpha.setName("pnlHeightMapAlpha");

                    //---- jsHeightMapAlpha ----
                    jsHeightMapAlpha.setValue(99);
                    jsHeightMapAlpha.setFocusable(false);
                    jsHeightMapAlpha.setName("jsHeightMapAlpha");
                    jsHeightMapAlpha.addChangeListener(e -> jsHeightMapAlphaStateChanged(e));

                    GroupLayout pnlHeightMapAlphaLayout = new GroupLayout(pnlHeightMapAlpha);
                    pnlHeightMapAlpha.setLayout(pnlHeightMapAlphaLayout);
                    pnlHeightMapAlphaLayout.setHorizontalGroup(
                        pnlHeightMapAlphaLayout.createParallelGroup()
                            .addComponent(jsHeightMapAlpha, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    );
                    pnlHeightMapAlphaLayout.setVerticalGroup(
                        pnlHeightMapAlphaLayout.createParallelGroup()
                            .addComponent(jsHeightMapAlpha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    );
                }
                pnlRightPanel.add(pnlHeightMapAlpha, "cell 0 1");

                //======== pnlBackImageAlpha ========
                {
                    pnlBackImageAlpha.setBorder(new TitledBorder(null, "Back Image Alpha", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
                    pnlBackImageAlpha.setName("pnlBackImageAlpha");

                    //---- jsBackImageAlpha ----
                    jsBackImageAlpha.setFocusable(false);
                    jsBackImageAlpha.setName("jsBackImageAlpha");
                    jsBackImageAlpha.addChangeListener(e -> jsBackImageAlphaStateChanged(e));

                    GroupLayout pnlBackImageAlphaLayout = new GroupLayout(pnlBackImageAlpha);
                    pnlBackImageAlpha.setLayout(pnlBackImageAlphaLayout);
                    pnlBackImageAlphaLayout.setHorizontalGroup(
                        pnlBackImageAlphaLayout.createParallelGroup()
                            .addComponent(jsBackImageAlpha, GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                    );
                    pnlBackImageAlphaLayout.setVerticalGroup(
                        pnlBackImageAlphaLayout.createParallelGroup()
                            .addComponent(jsBackImageAlpha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    );
                }
                pnlRightPanel.add(pnlBackImageAlpha, "cell 0 2");

                //======== pnlMoveLayer ========
                {
                    pnlMoveLayer.setBorder(new TitledBorder(null, "Move Layer", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
                    pnlMoveLayer.setName("pnlMoveLayer");
                    pnlMoveLayer.setLayout(new MigLayout(
                        "insets 0,hidemode 3,gap 5 5",
                        // columns
                        "[fill]" +
                        "[fill]" +
                        "[fill]",
                        // rows
                        "[fill]" +
                        "[fill]" +
                        "[fill]"));

                    //---- jbMoveMapUp ----
                    jbMoveMapUp.setText("\u25b2");
                    jbMoveMapUp.setFocusable(false);
                    jbMoveMapUp.setName("jbMoveMapUp");
                    jbMoveMapUp.addActionListener(e -> jbMoveMapUpActionPerformed(e));
                    pnlMoveLayer.add(jbMoveMapUp, "cell 1 0");

                    //---- jbMoveMapLeft ----
                    jbMoveMapLeft.setText("\u25c4");
                    jbMoveMapLeft.setFocusable(false);
                    jbMoveMapLeft.setName("jbMoveMapLeft");
                    jbMoveMapLeft.addActionListener(e -> jbMoveMapLeftActionPerformed(e));
                    pnlMoveLayer.add(jbMoveMapLeft, "cell 0 1");

                    //---- jbMoveMapDown ----
                    jbMoveMapDown.setText("\u25bc");
                    jbMoveMapDown.setFocusable(false);
                    jbMoveMapDown.setName("jbMoveMapDown");
                    jbMoveMapDown.addActionListener(e -> jbMoveMapDownActionPerformed(e));
                    pnlMoveLayer.add(jbMoveMapDown, "cell 1 2");

                    //---- jbMoveMapRight ----
                    jbMoveMapRight.setText("\u25ba");
                    jbMoveMapRight.setFocusable(false);
                    jbMoveMapRight.setName("jbMoveMapRight");
                    jbMoveMapRight.addActionListener(e -> jbMoveMapRightActionPerformed(e));
                    pnlMoveLayer.add(jbMoveMapRight, "cell 2 1");
                }
                pnlRightPanel.add(pnlMoveLayer, "cell 0 3,alignx center,growx 0");

                //======== pnlSelectedTile ========
                {
                    pnlSelectedTile.setBorder(new TitledBorder(null, "Tile Selected", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));
                    pnlSelectedTile.setName("pnlSelectedTile");

                    //======== tileDisplay ========
                    {
                        tileDisplay.setFocusable(false);
                        tileDisplay.setPreferredSize(new Dimension(140, 140));
                        tileDisplay.setName("tileDisplay");

                        GroupLayout tileDisplayLayout = new GroupLayout(tileDisplay);
                        tileDisplay.setLayout(tileDisplayLayout);
                        tileDisplayLayout.setHorizontalGroup(
                            tileDisplayLayout.createParallelGroup()
                                .addGap(0, 517, Short.MAX_VALUE)
                        );
                        tileDisplayLayout.setVerticalGroup(
                            tileDisplayLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                        );
                    }

                    GroupLayout pnlSelectedTileLayout = new GroupLayout(pnlSelectedTile);
                    pnlSelectedTile.setLayout(pnlSelectedTileLayout);
                    pnlSelectedTileLayout.setHorizontalGroup(
                        pnlSelectedTileLayout.createParallelGroup()
                            .addComponent(tileDisplay, GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                    );
                    pnlSelectedTileLayout.setVerticalGroup(
                        pnlSelectedTileLayout.createParallelGroup()
                            .addComponent(tileDisplay, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                    );
                }
                pnlRightPanel.add(pnlSelectedTile, "cell 0 4,grow");
            }
            splitPane.setRightComponent(pnlRightPanel);
        }
        contentPane.add(splitPane, "cell 0 1 2 1");

        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar jMenu;
    private JMenu jMenu1;
    private JMenuItem jmiNewMap;
    private JMenuItem jmiOpenMap;
    private JMenuItem jmiSaveMap;
    private JMenuItem jmiSaveMapAs;
    private JMenuItem jmiExportObjWithText;
    private JMenuItem jmiExportMapAsImd;
    private JMenuItem jmiExportMapAsNsb;
    private JMenuItem jmiExportMapBtx;
    private JMenuItem jmiImportTileset;
    private JMenuItem jmiExportTileset;
    private JMenuItem jmiExportAllTiles;
    private JMenu jMenu2;
    private JMenuItem jmiUndo;
    private JMenuItem jmiRedo;
    private JMenuItem jmiClearTiles;
    private JMenuItem jmiUseSmartDrawing;
    private JMenuItem jmiClearLayer;
    private JMenuItem jmiClearAllLayers;
    private JMenuItem jmiCopyLayer;
    private JMenuItem jmiPasteLayer;
    private JMenuItem jmiPasteLayerTiles;
    private JMenuItem jmiPasteLayerHeights;
    private JMenu jMenu3;
    private JMenuItem jmi3dView;
    private JMenuItem jmiTopView;
    private JMenuItem jmiToggleHeightView;
    private JMenuItem jmiToggleGrid;
    private JMenuItem jmiLoadBackImg;
    private JCheckBoxMenuItem jcbUseBackImage;
    private JMenu jMenu4;
    private JMenuItem jmiTilesetEditor;
    private JMenuItem jmiCollisionEditor;
    private JMenuItem jmiBdhcEditor;
    private JMenuItem jmiNsbtxEditor;
    private JMenu jmHelp;
    private JMenuItem jmiKeyboardInfo;
    private JMenuItem jmiAbout;
    private JToolBar jToolBar;
    private JButton jbNewMap;
    private JButton jbOpenMap;
    private JButton jbSaveMap;
    private JButton jbExportObj;
    private JButton jbExportImd;
    private JButton jbExportNsb;
    private JButton jbExportNsb1;
    private JButton jbUndo;
    private JButton jbRedo;
    private JButton jb3DView;
    private JButton jbTopView;
    private JButton jbHeightView;
    private JButton jbGridView;
    private JButton jbClearTile;
    private JButton jbUseSmartGrid;
    private JButton jbTilelistEditor;
    private JButton jbCollisionsEditor;
    private JButton jbBdhcEditor;
    private JButton jbBacksoundEditor;
    private JButton jbNsbtxEditor1;
    private JButton jbBuildingEditor;
    private JButton jbAnimationEditor;
    private JButton jbKeboardInfo;
    private JButton jbHelp;
    private JPanel pnlGameInfo;
    private JLabel lblGame;
    private JLabel lblGameIcon;
    private JLabel lblGameName;
    private JSplitPane splitPane;
    private JPanel pnlMainWindow;
    private JPanel pnlLayer;
    private ThumbnailLayerSelector thumbnailLayerSelector;
    private JPanel pnlZ;
    private HeightSelector heightSelector;
    private JPanel pnlTileList;
    private JScrollPane jScrollPane1;
    private TileSelector tileSelector;
    private JPanel pnlSmartDrawing;
    private JScrollPane jScrollPane2;
    private SmartGridDisplay smartGridDisplay;
    private JPanel pnlMapDisplay;
    private MapDisplay mapDisplay;
    private JPanel pnlRightPanel;
    private JPanel pnlBorderMap;
    private BorderMapsDisplay borderMapsDisplay;
    private JPanel pnlHeightMapAlpha;
    private JSlider jsHeightMapAlpha;
    private JPanel pnlBackImageAlpha;
    private JSlider jsBackImageAlpha;
    private JPanel pnlMoveLayer;
    private JButton jbMoveMapUp;
    private JButton jbMoveMapLeft;
    private JButton jbMoveMapDown;
    private JButton jbMoveMapRight;
    private JPanel pnlSelectedTile;
    private TileDisplay tileDisplay;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
