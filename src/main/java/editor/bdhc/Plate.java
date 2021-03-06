/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.bdhc;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class Plate {

    public static final int PLANE = 0;
    public static final int BRIDGE = 1;
    public static final int LEFT_STAIRS = 2;
    public static final int RIGHT_STAIRS = 3;
    public static final int UP_STAIRS = 4;
    public static final int DOWN_STAIRS = 5;
    public static final int OTHER = 6;

    public static final float SLOPE_UNIT = 4095.56247663f;

    public static final int[][] slopes = new int[][]{
        {0, 4096, 0},
        {0, 4096, 0},
        {2896, 2896, 0},
        {-2896, 2896, 0},
        {0, 2896, 2896},
        {0, 2896, -2896},};

    public static final Color[] colors = new Color[]{
        new Color(100, 100, 255, 100),
        new Color(100, 255, 100, 100),
        new Color(100, 255, 255, 100),
        new Color(255, 100, 255, 100),
        new Color(255, 255, 100, 100),
        new Color(255, 255, 255, 100),
        new Color(255, 100, 0, 100)
    };

    public int x, y;
    public int width, height;
    public float z;
    public int type;
    public int[] customSlope = new int[]{0, 4096, 0};
    //public int slopeX, slopeY, slopeZ;

    //private Color color;
    //private static final Color defaultColor = new Color(100, 100, 255, 100);
    public static final Color selectedColor = new Color(255, 100, 100, 100);

    public Plate(int x, int y, float z, int width, int height, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public Plate(int x, int y, float z, int width, int height, int type, int[] slopes) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.type = type;
        this.customSlope = slopes;
    }

    public Plate() {
        this.x = 0;
        this.y = 0;
        this.width = 2;
        this.height = 2;
        this.z = 0.0f;

        this.type = PLANE;

        //this.slopeX = 0;
        //this.slopeY = 0;
        //this.slopeZ = 4096;
        //this.color = defaultColor;
    }

    public Color getColor() {
        return applyZToColor(colors[type], 100);
    }

    public Color getBorderColor() {
        return applyZToColor(colors[type], 255);
    }

    private Color applyZToColor(Color c, int alpha) {
        int delta = ((int) z) * 10;

        int r = setInsideColorBounds(c.getRed() + delta);
        int g = setInsideColorBounds(c.getGreen() + delta);
        int b = setInsideColorBounds(c.getBlue() + delta);

        return new Color(r, g, b, alpha);
    }

    private static int setInsideColorBounds(int value) {
        return Math.max(0, Math.min(value, 255));
    }

    public int[] getSlope() {
        if (type == Plate.OTHER) {
            return customSlope;
        } else {
            return slopes[type];
        }
    }

    public void setAngleX(float angle) {
        type = angleXToType(angle);
        if (type == OTHER) {
            //System.out.println("x: " + (Math.sin(-angle) * SLOPE_UNIT) + " z: " + (Math.cos(-angle) * SLOPE_UNIT) + " y: " + (0.0f));
            customSlope[0] = (int) (Math.round(Math.sin(-angle) * SLOPE_UNIT));
            customSlope[1] = (int) (Math.round(Math.cos(-angle) * SLOPE_UNIT));
            customSlope[2] = (int) (0.0f);
            //System.out.println("x: " + customSlope[0] + " z: " + customSlope[1] + " y: " + customSlope[2]);
        }
    }

    public void setAngleY(float angle) {
        type = angleYToType(angle);
        if (type == OTHER) {
            customSlope[0] = (int) (0.0f);
            customSlope[1] = (int) (Math.round(Math.cos(-angle) * SLOPE_UNIT));
            customSlope[2] = (int) (Math.round(Math.sin(-angle) * SLOPE_UNIT));
        }
        //System.out.println("x: " + customSlope[0] + " z: " + customSlope[1] + " y: " + customSlope[2]);
    }

    private static int angleXToType(float angle) {
        float tol = 0.001f;
        if (areEqual(angle, 0.0f, tol)) {
            return PLANE;
        } else if (areEqual(angle, (float) (-45 * (Math.PI / 180f)), tol)) {
            return LEFT_STAIRS;
        } else if (areEqual(angle, (float) (45 * (Math.PI / 180f)), tol)) {
            return RIGHT_STAIRS;
        } else {
            return OTHER;
        }
    }

    private static int angleYToType(float angle) {
        float tol = 0.001f;
        if (areEqual(angle, 0.0f, tol)) {
            return PLANE;
        } else if (areEqual(angle, (float) (-45 * (Math.PI / 180f)), tol)) {
            return UP_STAIRS;
        } else if (areEqual(angle, (float) (45 * (Math.PI / 180f)), tol)) {
            return DOWN_STAIRS;
        } else {
            return OTHER;
        }
    }

    private static boolean areEqual(float value1, float value2, float tol) {
        return Math.abs(value1 - value2) < tol;
    }

    public float getAngleX() {
        int[] slope = getSlope();
        return (float) -Math.atan((float) slope[0] / slope[1]);
    }

    public float getAngleY() {
        int[] slope = getSlope();
        return (float) -Math.atan((float) slope[2] / slope[1]);
    }

    public float getAngleDegreesX() {
        return (float) ((getAngleX() * 180.0f) / Math.PI);
    }

    public float getAngleDegreesY() {
        return (float) ((getAngleY() * 180.0f) / Math.PI);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

}
