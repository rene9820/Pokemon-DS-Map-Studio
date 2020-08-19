/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.handler;

import editor.bdhc.Bdhc;
import editor.bdhc.Plate;
import editor.obj.ObjWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import models.Layer;
import tileset.Tile;
import tileset.Tileset;
import utils.Utils;

/**
 *
 * @author Trifindo
 */
public class MapGrid {

    private MapEditorHandler handler;

    public String filePath = "";
    public String tilesetFilePath = "";
    public static final String fileExtension = "pdsmap";

    private static final String gameIndexTag = "gameindex";
    private static final String tileGridTag = "tilegrid";
    private static final String heightGridTag = "heightgrid";
    private static final String tilesetTag = "tileset";
    private static final String bdhcTag = "bdhc";

    public static final int cols = 32;
    public static final int rows = 32;
    public static final int DEFAULT_TILE_SIZE = 16;
    public static final int width = cols * DEFAULT_TILE_SIZE;
    public static final int height = rows * DEFAULT_TILE_SIZE;
    public static final float gridTileSize = 1.0f;

    public int activeLayer = 0;
    public static final int numLayers = 8;

    public Layer[] tileLayers = new Layer[numLayers];
    // public int[][][] heightLayers = new int[numLayers][cols][rows];

    private int[][] tileLayerCopy = null;
    private int[][] heightLayerCopy = null;

    public MapGrid(MapEditorHandler handler) {
        this.handler = handler;

        filePath = "";
        tilesetFilePath = "";

        clearCopyLayer();

        for (int k = 0; k < numLayers; k++) {
            //int[][] tileGrid = new int[cols][rows];
            tileLayers[k] = new Layer(rows, cols);
        }

    }

    public void saveToFile(String path) throws FileNotFoundException {
        if (!path.endsWith("." + fileExtension)) {
            path = path.concat("." + fileExtension);
        }
        PrintWriter out = new PrintWriter(path);

        out.println(gameIndexTag);
        out.println(handler.getGameIndex());

        out.println(tilesetTag);
        String filename = Utils.removeExtensionFromPath(new File(path).getName());
        out.println(filename + "." + Tileset.fileExtension);

        for (Layer layer : tileLayers) {
            out.println(tileGridTag);
            printMatrixInFile(out, layer.getTiles()); //Todo change this

            out.println(heightGridTag);
            printMatrixInFile(out, layer.getHeightTiles()); //Todo change this
        }

        out.close();
    }

    public void loadFromFile(String path) throws FileNotFoundException, IOException {
        InputStream input = new FileInputStream(new File(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        int numTileLayersRead = 0;
        int numHeightLayersRead = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(gameIndexTag)) {
                handler.setGameIndex(Integer.valueOf(br.readLine()));
            } else if (line.startsWith(tilesetTag)) {
                String folderPath = new File(path).getParent();
                tilesetFilePath = folderPath + File.separator + br.readLine();
                System.out.println("Tileset path: " + tilesetFilePath);
            } else if (line.startsWith(tileGridTag)) {
                loadMatrixFromFile(br, tileLayers[numTileLayersRead].getTiles());
                numTileLayersRead++;
            } else if (line.startsWith(heightGridTag)) {
                loadMatrixFromFile(br, tileLayers[numHeightLayersRead].getHeightTiles());
                numHeightLayersRead++;
            }
        }
        br.close();
        input.close();
    }

    public void saveMapToOBJ(Tileset tset, String path, boolean saveTextures,
            boolean saveVertexColors) throws FileNotFoundException {
        ObjWriter writer = new ObjWriter(tset, this, path, handler.getGameIndex(),
                saveTextures, saveVertexColors);
        writer.writeMapObj();
    }

    private void loadMatrixFromFile(BufferedReader br, int[][] matrix) throws IOException {
        for (int i = 0; i < rows; i++) {
            String line = br.readLine();
            String[] lineSplitted = line.split(" ");
            for (int j = 0; j < cols; j++) {
                matrix[j][i] = Integer.parseInt(lineSplitted[j]);
            }
        }
    }

    private Integer[][] loadLayerFromFile(BufferedReader br) throws IOException {
        Integer[][] layer = new Integer[cols][rows];
        for (int i = 0; i < rows; i++) {
            String line = br.readLine();
            String[] lineSplitted = line.split(" ");
            for (int j = 0; j < cols; j++) {
                layer[j][i] = Integer.parseInt(lineSplitted[j]);
            }
        }
        return layer;
    }

    public void replaceTilesUsingIndices(int[] indices) {
        for (Layer layer : tileLayers) {
            layer.replaceTiles(indices);
        }
    }

    public void replaceTileWithOldLayer(int[][][] oldTileLayers, int oldIndex, int newIndex) {
        for (int i = 0; i < numLayers; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < rows; k++) {
                    if (oldTileLayers[i][j][k] == oldIndex) {
                        tileLayers[i].getTiles()[j][k] = newIndex;
                    }
                }
            }
        }
    }

    public int[][][] cloneTileLayers() {
        int[][][] newTileLayers = new int[numLayers][cols][rows];
        for (int i = 0; i < numLayers; i++) {
            for (int j = 0; j < cols; j++) {
                System.arraycopy(tileLayers[i].getTiles()[j], 0, newTileLayers[i][j], 0, rows);
            }
        }
        return newTileLayers;
    }

    public void removeTileFromMap(int tileIndex) {
        for (Layer layer : tileLayers) {
            layer.removeTile(tileIndex);
        }
    }

    public void removeTileFromLayer(int layerIndex, int tileIndex) {
        tileLayers[layerIndex].removeTile(tileIndex);
    }

    public void clearLayer(int layerIndex) {
        tileLayers[layerIndex].clearLayer();
    }

    public void clearAllLayers() {
        for (Layer layer : tileLayers) {
            layer.clearLayer();
        }
    }

    public void moveTilesUp(int layerIndex) {
        tileLayers[layerIndex].shiftLayer(0, -1);
    }

    public void moveTilesDown(int layerIndex) {
        tileLayers[layerIndex].shiftLayer(0, 1);
    }

    public void moveTilesRight(int layerIndex) {
        tileLayers[layerIndex].shiftLayer(-1, 0);
    }

    public void moveTilesLeft(int layerIndex) {
        tileLayers[layerIndex].shiftLayer(1, 0);
    }

    private void printMatrixInFile(PrintWriter out, Integer[][] matrix) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                out.print(matrix[j][i] + " ");
            }
            out.println();
        }
    }

    private void printBdhcInFile(PrintWriter out, Bdhc bdhc) {
        for (int i = 0; i < bdhc.getPlates().size(); i++) {
            printPlateInFile(out, bdhc.getPlate(i));
        }
    }

    private void printPlateInFile(PrintWriter out, Plate plate) {
        out.print(plate.type + " ");
        out.print(plate.x + " ");
        out.print(plate.y + " ");
        out.print(plate.z + " ");
        out.print(plate.width + " ");
        out.print(plate.height + " ");
        out.println();
    }

    private void printMatrixInFile(PrintWriter out, int[][] matrix) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                out.print(matrix[j][i] + " ");
            }
            out.println();
        }
    }

    public void floodFillTileGrid(int x, int y, int value, int tileWidth, int tileHeight) {
        final int prevC = handler.getActiveTileLayer()[x][y];

        //Generate mask
        boolean[][] mask = new boolean[MapGrid.cols][MapGrid.rows];
        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                mask[i][j] = true;
            }
        }

        for (int i = 0; i < MapGrid.cols; i++) {
            for (int j = 0; j < MapGrid.rows; j++) {
                final int tileIndex = handler.getActiveTileLayer()[i][j];
                if (tileIndex != -1 && tileIndex != prevC) {
                    try {
                        Tile tile = handler.getTileset().get(handler.getActiveTileLayer()[i][j]);
                        int xSize = tile.getWidth() - Math.max(0, i + tile.getWidth() - MapGrid.cols);
                        int ySize = tile.getHeight() - Math.max(0, j + tile.getHeight() - MapGrid.rows);
                        for (int m = 0; m < xSize; m++) {
                            for (int n = 0; n < ySize; n++) {
                                mask[i + m][j + n] = false;
                            }
                        }
                    } catch (Exception ex) {
                        mask[i][j] = false;
                    }
                }
            }
        }

        Utils.floodFillMatrix(tileLayers[activeLayer].getTiles(), mask, x, y, value, tileWidth, tileHeight);
    }

    public void floodFillHeightGrid(int x, int y, int value) {
        Utils.floodFillMatrix(tileLayers[activeLayer].getHeightTiles(), x, y, value);
    }

    public void clearCopyLayer() {
        tileLayerCopy = null;
        heightLayerCopy = null;
    }

    public void copySelectedLayer() {
        tileLayerCopy = cloneTileLayer(activeLayer);
        heightLayerCopy = cloneHeightLayer(activeLayer);
    }

    public void pasteTileLayer() {
        if (tileLayerCopy != null) {
            tileLayers[activeLayer].setTiles(tileLayerCopy);
        }
    }

    public void pasteHeightLayer() {
        if (heightLayerCopy != null) {
            tileLayers[activeLayer].setHeightTiles(heightLayerCopy);
        }
    }

    public int[][] getTileLayerCopy() {
        return tileLayerCopy;
    }

    public int[][] getHeightLayerCopy() {
        return heightLayerCopy;
    }

    private int[][] cloneLayer(int[][] layer) {
        int[][] copy = new int[cols][rows];
        for (int i = 0; i < layer.length; i++) {
            System.arraycopy(layer[i], 0, copy[i], 0, layer[i].length);
        }
        return copy;
    }

    public int[][] cloneTileLayer(int index) {
        return cloneLayer(tileLayers[index].getTiles());
    }

    public int[][] cloneHeightLayer(int index) {
        return cloneLayer(tileLayers[index].getHeightTiles());
    }

    public void setTileLayer(int index, int[][] tileLayer) {
        this.tileLayers[index].setTiles(tileLayer);
    }

    public void setHeightLayer(int index, int[][] heightLayer) {
        this.tileLayers[index].setHeightTiles(heightLayer);
    }

}
