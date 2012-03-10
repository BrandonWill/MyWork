import api.methods.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/4/12
 * Time: 6:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Examtest {

    static final int WIDTH = 36;
    static final int HEIGHT = 32;
//    static int[][][][] Slots = new int[4][6][][];


    /**
     *
     * @param columnNumber Starts at 0 for columns
     * @return Correct X Value for the inventory column
     */
    public static int getXValue(int columnNumber) {
        return 561 + columnNumber * 42;
    }

    /**
     *
     * @param rowNumber Starts at 0 for Rows
     * @return correct Y value for inventory row
     */
    public static int getYValue(int rowNumber) {
        return 212 + rowNumber * 36;
    }

    /**
     *
     * @param rowNumber Starts at 0 for row Number. 0-3
     * @param columnNumber Starts at 0 for column Number. 0-6
     * @return Makes a rectangle from desired row and column number
     */
    private static Rectangle getInventorySlot(int columnNumber, int rowNumber) {
        return new Rectangle(getXValue(columnNumber), getYValue(rowNumber), WIDTH, HEIGHT);
    }
    
    public static void main(String args[]) {
        Rectangle[] b = Slot.getAllSlotBounds();
//        System.out.println(b[27].getBounds());
//        System.out.println(Slot.SLOT_27.getBounds());
    }

    //561 = 0
    // 603 = 1
    //645 = 2
    // 687 = 3

    //212 = 0
    //248 = 1
    //284 = 2
    //320 = 3
    // 356 = 4
    // 392 = 5
    //428 = 6
    public static enum Slot {
        SLOT_0(0, getInventorySlot(0, 0)), SLOT_1(1,
                getInventorySlot(1, 0)), SLOT_2(2,
                getInventorySlot(2, 0)), SLOT_3(3,
                getInventorySlot(3, 0)), SLOT_4(4,
                getInventorySlot(0, 1)), SLOT_5(5,
                getInventorySlot(1, 1)), SLOT_6(6,
                getInventorySlot(2, 1)), SLOT_7(7,
                getInventorySlot(3, 1)), SLOT_8(8,
                getInventorySlot(0, 2)), SLOT_9(9,
                getInventorySlot(1, 2)), SLOT_10(10,
                getInventorySlot(2, 2)), SLOT_11(11,
                getInventorySlot(3, 2)), SLOT_12(12,
                getInventorySlot(0, 3)), SLOT_13(13,
                getInventorySlot(1, 3)), SLOT_14(14,
                getInventorySlot(2, 3)), SLOT_15(15,
                getInventorySlot(3, 3)), SLOT_16(16,
                getInventorySlot(0, 4)), SLOT_17(17,
                getInventorySlot(1, 4)), SLOT_18(18,
                getInventorySlot(2, 4)), SLOT_19(19,
                getInventorySlot(3, 4)), SLOT_20(20,
                getInventorySlot(0, 5)), SLOT_21(21,
                getInventorySlot(1, 5)), SLOT_22(22,
                getInventorySlot(2, 5)), SLOT_23(23,
                getInventorySlot(3, 5)), SLOT_24(24,
                getInventorySlot(0, 6)), SLOT_25(25,
                getInventorySlot(1, 6)), SLOT_26(26,
                getInventorySlot(2, 6)), SLOT_27(27,
                getInventorySlot(3, 6));

        private final int index;
        private final Rectangle bounds;

        public int getIndex() {
            return index;
        }

        private Slot(int index,Rectangle bounds) {
            this.index = index;
            this.bounds = bounds;
        }

        public static Slot getSlot(int index) {
            for (final Slot slot : Slot.values()) {
                if (slot.getIndex() == index) {
                    return slot;
                }
            }
            return null;
        }

        public static Rectangle[] getAllSlotBounds() {
            Rectangle[] a = new Rectangle[28];
            int i = 0;
            for (int column = 0; column < 4; column++) {
                for (int row = 0; row < 7; row++) {
                    a[i] = getInventorySlot(column, row);
                    i++;
                }
            }
            return a;
        }

        public static Slot getSlotOCR(String item) {
            for (final Slot slot : Slot.values()) {
                Mouse.move((int)slot.getCenter().getX(),(int)slot.getCenter().getY(),3,3);
                if (OCR.getUpText().contains(item)) {
                    return slot;
                }
            }
            return null;
        }

        /*
          public static int [] getSlotsWithColor(Color color) {
              ArrayList<Slot> list = new ArrayList<Slot>();
              for (final Slot slot : Slot.values()) {
                  for (final Color slot_color : slot.getColors()) {
                      if (slot_color.equals(color)) {
                          list.add(slot);
                      }
                  }
              }
              Slot s[] = new Slot[list.size()];
              for (int i = 0; i < s.length; i++) {
                  for(int j = 0; j < 18; i++){
                      if(list.get(i).getIndex() == j){
                          list.removeAll(i);
                      }
                  }
                  p[i] = list.get(i);
              }
              return p;
          }
          */

        public static Slot getSlotWithColor(Color color) {
            for (final Slot slot : Slot.values()) {
                for (final Color slot_color : slot.getColors()) {
                    if (slot_color.equals(color)) {
                        return slot;
                    }
                }
            }
            return null;
        }
        
        public static Slot getSlotWithinColorTolerance(Color color, Color tolerance) {
            for (final Slot slot : Slot.values()) {
                for (final Color slot_color : slot.getColors()) {
                    if (ColorUtil.areColorsWithinTolerance(slot_color, color, tolerance)) {
                        return slot;
                    }
                }
            }
            return null;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public Point getCenter() {
            return new Point(bounds.x + (int) (bounds.width / 2), bounds.y
                    + (int) (bounds.height / 2));
        }

        public Color getCenterColor() {
            final Point center = getCenter();
            return Game.isPointValid(center) ? Game.getColorAt(center) : null;
        }

        public Color[] getColors() {
            final List<Color> colors = new LinkedList<Color>();
            for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
                    final Point p = new Point(x, y);
                    if (Game.isPointValid(p)) {
                        final Color color = Game.getColorAt(p);
                        colors.add(color);
                    }
                }
            }
            return colors.toArray(new Color[colors.size()]);
        }

        public boolean isEmpty() {
            final Color center_color = getCenterColor();

            final int min_red = 63;
            final int max_red = 72;
            final int min_green = 53;
            final int max_green = 64;
            final int min_blue = 44;
            final int max_blue = 52;
            if (center_color != null) {
                final int center_red = center_color.getRed();
                final int center_green = center_color.getGreen();
                final int center_blue = center_color.getBlue();
                if (center_red >= min_red && center_red <= max_red) {
                    if (center_green >= min_green && center_green <= max_green) {
                        if (center_blue >= min_blue && center_blue <= max_blue) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public void click() {
            Mouse.click(getCenter());
        }

        public void click(final boolean left) {
            Mouse.click(getCenter(), left);
        }

    }


}
