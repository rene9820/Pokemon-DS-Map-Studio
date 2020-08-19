package models;

public class Layer {
    private static final int EMPTY_TILE = -1;
    private static final int DEFAULT_HEIGHT = 0;

    private final int columns;
    private final int rows;

    private int[][] tiles;
    private int[][] heightTiles;

    public Layer(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;

        tiles = new int[columns][rows];
        heightTiles = new int[columns][rows];

        // Clears layer to set tiles and height tiles to their empty/default value
        clearLayer();
    }

    public void replaceTiles(int[] indices) {
        int[][] oldTileLayers = tiles;

        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                int index = oldTileLayers[column][row];

                try {
                    if (index == -1) {
                        tiles[column][row] = -1;
                    } else {
                        tiles[column][row] = indices[index];
                    }
                } catch (Exception ex) {
                    tiles[column][row] = -1;
                }
            }
        }
    }

    public void removeTile(int tileIndex) {
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                if (tiles[column][row] == tileIndex) {
                    tiles[column][row] = -1;
                }
            }
        }
    }

    public void shiftLayer(int xOffset, int yOffSet) {
        Layer shiftedLayer = new Layer(columns, rows);

        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {

                // Continue if sum of column + offset is out of bounds to avoid out of bounds exception
                if (column + xOffset < 0 || column + xOffset >= columns ||
                        row + yOffSet < 0 || row + yOffSet >= rows) {
                    continue;
                }

                shiftedLayer.getTiles()[column][row] = tiles[column + xOffset][row + yOffSet];
                shiftedLayer.getHeightTiles()[column][row] = heightTiles[column + xOffset][row + yOffSet];
            }
        }

        setTiles(shiftedLayer.getTiles());
        setHeightTiles(shiftedLayer.getHeightTiles());
    }

    public void clearLayer() {
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                tiles[column][row] = EMPTY_TILE;
                heightTiles[column][row] = DEFAULT_HEIGHT;
            }
        }
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }

    public void setHeightTiles(int[][] heightTiles) {
        this.heightTiles = heightTiles;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public int[][] getHeightTiles() {
        return heightTiles;
    }
}
