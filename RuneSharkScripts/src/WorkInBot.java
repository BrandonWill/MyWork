

import api.methods.ColorUtil;
import api.methods.Inventory;
import api.methods.RSText;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/10/12
 * Time: 4:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class WorkInBot {
    /**
     * Classes are renamed so my scripts don't derp and
     * just think these are the classes they're looking for.
     */

}

class NOTREALCLASS_Game {
    public static final Rectangle ENERGY_TEXT_BOUNDS = new Rectangle(729, 92,
            32, 39);
    public static final Rectangle PRAYER_TEXT_BOUNDS = new Rectangle(733, 58,
            28, 26);
    public static final Rectangle HP_TEXT_BOUNDS = new Rectangle(719, 19, 27,
            25);

    /**
     * Gets the current hp amount
     *
     * @return The current amount
     */
    public static int getCurrentHP() {
        String s = RSText.findString(HP_TEXT_BOUNDS, null, null);
        if (s != null) {
            return Integer.parseInt(s);
        }
        return -1;
    }

    /**
     * Gets the current prayer amount
     *
     * @return The current prayer amount
     */
    public static int getCurrentPrayer() {
        String s = RSText.findString(PRAYER_TEXT_BOUNDS, null, null);
        if (s != null) {
            return Integer.parseInt(s);
        }
        return -1;
    }

    /**
     * Gets the current run energy
     *
     * @return The run energy
     */
    public static int getRunEnergy() {
        String s = RSText.findString(ENERGY_TEXT_BOUNDS, null, null);
        if (s != null) {
            return Integer.parseInt(s);
        }
        return -1;
    }
}

class NOTREALCLASS_Inventory {
    private static final int WIDTH = 36;
    private static final int HEIGHT = 32;

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
    }

    /**
     *
     * @param columnNumber Starts at 0 for columns
     * @return Correct X Value for the inventory column
     */
    private static int getXValue(int columnNumber) {
        return 561 + columnNumber * 42;
    }

    /**
     *
     * @param rowNumber Starts at 0 for Rows
     * @return correct Y value for inventory row
     */
    private static int getYValue(int rowNumber) {
        return 212 + rowNumber * 36;
    }

    /**
     * This allows more complex algorithms to be
     * developed in search and exclusion methods
     * @return All of the slot bounds
     */
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

    /**
     *
     * @param slotNumber Drops all except the given slot number. 0-27
     */
    public static void dropAllExcept(int slotNumber) {
        if (open()) {
            for (int i = 0; i < 28; i++) {
                if (i != slotNumber && !api.methods.Inventory.getSlotAt(i).isEmpty()) {
                    api.methods.Inventory.doAction(i, i <= 23 ? 2 : 1);
                }
            }
        }
    }

    /**
     *
     * @param slotNumber Drops all except the given slot numbers. 0-27
     */
    public static void dropAllExcept(int... slotNumber) {
        if (open()) {
            for (int i = 0; i < 28; i++) {
                for (int a : slotNumber) {
                    if (i != a && !api.methods.Inventory.getSlotAt(i).isEmpty()) {
                        api.methods.Inventory.doAction(i, i <= 23 ? 2 : 1);
                    }
                }
            }
        }
    }

    /**
     * Drops all inventory items.
     */
    public static void dropAll() {
        if (open()) {
            for (int i = 0; i < 28; i++) {
                if (!api.methods.Inventory.getSlotAt(i).isEmpty()) {
                    api.methods.Inventory.doAction(i, i <= 23 ? 2 : 1);
                }
            }
        }
    }

    /**
     *
     * @param centerColor Drops all except the given center color.
     * @param tolerance amount to choose between colors to drop
     */
    public static void dropAllExcept(Color centerColor, int tolerance) {
        if (open()) {
            for (int i = 0; i < 28; i++) {
                if (!api.methods.Inventory.getSlotAt(i).isEmpty() && ColorUtil.areColorsWithinTolerance(api.methods.Inventory.getSlotAt(i).getCenterColor(), centerColor, tolerance)) {
                    api.methods.Inventory.doAction(i, i <= 23 ? 2 : 1);
                }
            }
        }
    }

    /**
     *
     * @param tolerance amount to choose between colors to drop
     * @param colors Drops all except the given center colors.
     */
    public static void dropAllExcept(int tolerance, Color... colors) {
        if (open()) {
            for (int i = 0; i < 28; i++) {
                for (Color a : colors) {
                    if (!api.methods.Inventory.getSlotAt(i).isEmpty() && ColorUtil.areColorsWithinTolerance(api.methods.Inventory.getSlotAt(i).getCenterColor(), a, tolerance)) {
                        api.methods.Inventory.doAction(i, i <= 23 ? 2 : 1);
                    }
                }
            }
        }
    }

    /**
     *
     * @param color The center color of the item you would like to find the slot of
     * @return Returns the first slot that contains the desired color; if any.
     */
    public static Inventory.Slot getSlotWithCenterColor(Color color) {
        for (Inventory.Slot a : Inventory.Slot.values()) {
            if (a.getCenterColor().equals(color)) {
                return a;
            }
        }
        return null;
    }    

    /**
     *
     * @param columnNumber
     *                      Starts at 0 for column Number. 0-3
     * @param rowNumber
     *                      Starts at 0 for row Number. 0-6
     * @return Makes a rectangle from desired row and column number
     */
    private static Rectangle getInventorySlot(int columnNumber, int rowNumber) {
        return new Rectangle(getXValue(columnNumber), getYValue(rowNumber), WIDTH, HEIGHT);
    }


    /**
     *
     * @return Returns <tt>true</tt> if the tab is open, otherwise returns <tt>false</tt>
     */
    public static boolean isOpen() {
        return api.methods.Game.getOpenTab().equals(api.methods.Game.Tab.INVENTORY);
    }

    /**
     *
     * @return Returns <tt>true</tt> if the tab is open, if it is not open, it will attempt to open the tab
     */
    public static boolean open() {
        if (!isOpen()) {
            api.methods.Game.openTab(api.methods.Game.Tab.INVENTORY);
        }
        return isOpen();
    }

}

class LOOKATOTHERSTATS_Stats {

    //row 1
    public static final Rectangle ATTACK_RECTANGLE = new Rectangle(549, 211, 58, 24);
    public static final Rectangle STRENGTH_RECTANGLE = new Rectangle(549, 239, 58, 24);
    public static final Rectangle DEFENSE_RECTANGLE = new Rectangle(549, 267, 58, 24);
    public static final Rectangle RANGE_RECTANGLE = new Rectangle(549, 295, 58, 24);
    public static final Rectangle PRAYER_RECTANGLE = new Rectangle(549, 323, 58, 24);
    public static final Rectangle MAGIC_RECTANGLE = new Rectangle(549, 351, 58, 24);
    /*  Not Used:
    public static final Rectangle RUNECRAFTING_RECTANGLE = new Rectangle(549, 379, 58, 24);
    public static final Rectangle CONSTRUCTION_RECTANGLE = new Rectangle(549, 407, 58, 24);
    public static final Rectangle DUNGEONEERING_RECTANGLE = new Rectangle(549, 435, 58, 24);
     */
    //row 2
    public static final Rectangle CONSTITUTION_RECTANGLE = new Rectangle(611, 211, 58, 24);
    public static final Rectangle AGILITY_RECTANGLE = new Rectangle(611, 239, 58, 24);
    public static final Rectangle HERBLORE_RECTANGLE = new Rectangle(611, 267, 58, 24);
    public static final Rectangle THIEVING_RECTANGLE = new Rectangle(611, 295, 58, 24);
    public static final Rectangle CRAFTING_RECTANGLE = new Rectangle(611, 323, 58, 24);
    public static final Rectangle FLETCHING_RECTANGLE = new Rectangle(611, 351, 58, 24);
    public static final Rectangle SLAYER_RECTANGLE = new Rectangle(611, 379, 58, 24);
    /*  Not Used:
    public static final Rectangle HUNTING_RECTANGLE = new Rectangle(549, 407, 58, 24);
    */
    //row 3
    public static final Rectangle MINING_RECTANGLE = new Rectangle(673, 211, 58, 24);


    public enum stats {
        //row 1
        ATTACK(ATTACK_RECTANGLE,
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+75, 150, 23)),

        STRENGTH(STRENGTH_RECTANGLE,
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+75, 150, 23)),

        DEFENSE(DEFENSE_RECTANGLE,
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+75, 150, 23)),

        RANGE(RANGE_RECTANGLE,
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+75, 150, 23)),

        PRAYER(PRAYER_RECTANGLE,
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+75, 150, 23)),

        MAGIC(MAGIC_RECTANGLE,
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+75, 150, 23)),

        //row 2
        CONSTITUTION(CONSTITUTION_RECTANGLE,
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+75, 150, 23)),

        AGILITY(AGILITY_RECTANGLE,
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+75, 150, 23)),

        HERBLORE(HERBLORE_RECTANGLE,
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+75, 150, 23)),

        THIEVING(THIEVING_RECTANGLE,
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+75, 150, 23)),

        CRAFTING(CRAFTING_RECTANGLE,
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+75, 150, 23)),

        FLETCHING(FLETCHING_RECTANGLE,
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+75, 150, 23)),

        SLAYER(SLAYER_RECTANGLE,
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+75, 150, 23)),

        //row 3

        MINING(MINING_RECTANGLE,
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+75, 150, 23));

        public Rectangle statRect;
        public Rectangle levelRect;
        public Rectangle currentXP;
        public Rectangle nextLevel;
        public Rectangle remainder;

        stats(Rectangle statRec, Rectangle levelForSkill, Rectangle XP, Rectangle toLvl, Rectangle left) {
            statRect = statRec;
            levelRect = levelForSkill;
            currentXP = XP;
            nextLevel = toLvl;
            remainder = left;

        }

    }

}

class RANDOM_CODES {

    //Obtaining prices
    private static int getPrice(String name) {
        try {
            name = name.replace(' ', '+');
            if (!Character.isUpperCase(name.charAt(0))) {
                Character.toUpperCase(name.charAt(0));
            }
            URL url = new URL("http://rscript.org/lookup.php?type=ge&search=" + name);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains(name.replace('+', '_'))) {
                    String words[] = inputLine.split(" ");
                    return Integer.parseInt(words[4]);
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    private static int getPrice(int id) {
        try {
            URL url = new URL("http://rscript.org/lookup.php?type=ge&search=" + id);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("ITEM:")) {
                    String words[] = inputLine.split(" ");
                    return Integer.parseInt(words[4]);
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    //Obtaining ids
    public static int getID(String name) {
        try {
            name = name.replace(' ', '+');
            if (!Character.isUpperCase(name.charAt(0))) {
                name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }
            URL url = new URL("http://rscript.org/lookup.php?type=ge&search=" + name);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            int lineIndex = 0;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("IID:")) {
                    String words[] = inputLine.split(" ");
                    return Integer.parseInt(words[1]);
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }


    public int getAmount(Color colors) {
        int amount = 0;
        for (int i = 0; i < 28; i++) {
            if (areColorsClose(api.methods.Inventory.getSlotAt(i).getCenterColor(), colors, 10)) {
                amount += 1;
            }
        }
        return amount;
    }

    /*
    This was used to entirely revamp the bot to be a lot more stable.
    I didn't have time to rewrite it, but another developer did; I started the process.
     */
    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    //Code for obtaining xp gained. Too lazy to shorten:
    /*
import api.methods.ColorUtil;
import api.methods.Game;
import api.methods.Inventory;
import api.methods.Mouse;
import api.util.ZipUtils;
import bot.script.Script;
import bot.script.ScriptManifest;
import util.Configuration;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
@ScriptManifest(authors = { "Dwarfeh" }, category = "LOLOL", name = "LOL", description = "4 DUH LULZ", version = 1.0)
public class MyTester extends Script {
    public static final int WIDTH = 36;
    public static final int HEIGHT = 32;
    int startingXP;
    int gained = 0;
    Rectangle xpGained = new Rectangle(415, 5, 90, 35);
    boolean run = true;
    @Override
    public boolean onStart() {
            String text = RSText.findString(xpGained, null, null).replaceAll(" ", "");
            startingXP = text.length() >= 1 ? Integer.parseInt(text) : 0;
            log("XP: " +startingXP);
            log("STARTED DA LULZ MACHINE");
            return true;
    }
    @Override
    public void onFinish() {
            run = false;
            log("WHY U STOP MEH");
    }

    @Override
    public int loop() {
            gained = Integer.parseInt(RSText.findString(xpGained, null, null).replaceAll(" ", ""))-startingXP;
            log("IN LOOP");
            log("Text in rect: " +Integer.parseInt(RSText.findString(xpGained, null, null).replaceAll(" ", "")));
            log("XP GAIN: " +(Integer.parseInt(RSText.findString(xpGained, null, null).replaceAll(" ", ""))-startingXP));
            return 0;
    }
    @Override
    public Graphics doPaint(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;
            g.setStroke(new BasicStroke(6));
            g.setColor(Color.white);
            g.draw3DRect(1, 38, 140, 130, true);
            g.setStroke(new BasicStroke(3));
            g.setColor(new Color(0, 0, 0, 70));
            g.fill3DRect(1, 38, 140, 130, true);
            g.drawRect(xpGained.x, xpGained.y, xpGained.width, xpGained.height);
            g.setColor(Color.white);
            g.drawString("XP GAINED: " +gained, 10, 50);
            return null;
    }

}
}
     */


}



