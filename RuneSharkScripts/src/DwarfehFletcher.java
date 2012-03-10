import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManager;
import bot.script.ScriptManifest;

import java.awt.*;
import java.util.Random;

@ScriptManifest(authors = { "Dwarfeh" }, category = "Fletcher", name = "Dwarfeh's Fletcher", description = "Fletches with auto detection", version = 1.0)
public class DwarfehFletcher extends Script {
    Color col1 = null;
    Color col2 = null;
    Color col3 = null;
    boolean guiDone = true;
    boolean cutting;
    int withdrawCol1;
    int withdrawCol2;

    //TODO Finish banking and paint calculations then done :)

    @Override
    public boolean onStart() {
        log("Dwarfeh's Fletcher starting");
        return true;
    }

    @Override
    public int loop() {
        if (!guiDone) {
            log("GUI completed!");
            return 300;
        }
        if (col1 == null) {
            col1 = Inventory.getSlotAt(0).getCenterColor();
            log("Found 1st color: " +col1);
            withdrawCol1 = getAmount(col1);
            for (int i = 0; i < 28; i++) {
                if (col2 == null && Inventory.getSlotAt(i).getCenterColor().getRGB() != col1.getRGB()) {
                    col2 = Inventory.getSlotAt(i).getCenterColor();
                    log("Found a 2nd color: " +col2);
                    withdrawCol2 = getAmount(col2);
                }

            }
        }
        if (!isFletching() && getAmount(col2) > 0) {
            //use item 1
            Point[] a = ColorUtil.findAllColor(col2);
            for (Point anA : a) {
                if (Inventory.BOUNDS.contains(anA)) {
                    //using knife on log
                    Mouse.click(Inventory.getSlotAt(0).getCenter());
                    sleep(400);
                    Mouse.click(anA);
                    sleep(400);
                    //makes all
                    Mouse.click(new Point(259, 428), 3, 3);
                    sleep(400);
                    if (isFletching()) {
                        break;
                    }
                }
            }
        }

        if (getAmount(col2) == 0) {
            if (col3 == null) {
                for (int i = 0; i < 28; i++) {
                    if (col3 == null && Inventory.getSlotAt(i).getCenterColor().getRGB() != col1.getRGB() && Inventory.getSlotAt(i).getCenterColor().getRGB() != col2.getRGB()) {
                        col3 = Inventory.getSlotAt(i).getCenterColor();
                        log("Found a 3rd color: " +col3);
                        i = 28;
                    }
                }
            }

            bank();

        }
        return 0;
    }

//    public void useItem(Color color1, Color color2) {
//        //use item 2
//        Point a = Inventory.getSlotWithColor(color1).getCenter();
//        Point b = .Inventory.getSlotWithColor(color2).getCenter();
//        Mouse.click(a, 3, 3);
//        sleep(random(400, 600));
//        Mouse.click(b, 3, 3);
//        sleep(random(400, 600));
//        Mouse.click(new Point(259, 428), 3, 3);
//        sleep(random(500, 700));
//    }

    public boolean bankOpen() {
        if (Bank.isOpen()) {
            return true;
        }
//        Mouse.move();
        String text = RSText.getOptionsText().toLowerCase();
        if (text.contains("bank")) {
//            Mouse.click();
            sleep(random(500, 1000));
        }
        return Bank.isOpen();
        //TODO GET THIS DONE
    }

    public boolean bank() {
        if (!Bank.isOpen()) {
            return bankOpen();
        }
        if (withdrawCol1 == 1) {
            depositAllFromSlot(1);
        } else {
            depositAll();
        }
        if (getAmount(col1) != withdrawCol1) {
            withdrawItem1(withdrawCol1);
        }
        if (getAmount(col2) != withdrawCol2) {
            withdrawItem2(withdrawCol2);
        }
        if (getAmount(col1) == withdrawCol1 && getAmount(col2) == withdrawCol2) {
            Bank.close();
        }
        if (Inventory.isFull() && getAmount(col1) > withdrawCol1) {
            //TODO GET ACTION NUMBER
//            Inventory.doAction(Inventory.getSlotWithColor(col1), );
        }
        if (Inventory.isFull() && getAmount(col2) > withdrawCol2) {
            //TODO GET ACTION NUMBER
//            Inventory.doAction(Inventory.getSlotWithColor(col2), );
        }
        //TODO finish banking method
        return getAmount(col1) == withdrawCol1 && getAmount(col2) == withdrawCol2 && !Bank.isOpen();
    }

    public boolean depositAllFromSlot(int slotNumber) {
//        Mouse.click(Inventory.getSlotAt(slotNumber).getCenter(), false);
        //TODO GET ACTION NUMBER
//        Inventory.doAction(slotNumber, );
        sleep(random(800, 1000));
        return getAmount(Inventory.getSlotAt(slotNumber).getCenterColor()) == 0;

    }

    public void withdrawItem1(int amount) {
        Mouse.clickMouse(52, 105, false);
        sleep(random(1000, 1200));
        Mouse.clickMouse(52, 182, true);
        sleep(random(500, 800));
//        Mouse.clickMouse(52, 182, true);
//        sleep(200);
//        KeyBoard.sendString("" +amount, true);
//        sleep(300);
    }

    public void withdrawItem2(int amount) {
        Mouse.clickMouse(96, 106, false);
        sleep(random(1000, 1200));
        Mouse.clickMouse(90, 182, true);
        sleep(random(500, 800));
    }

    public void depositAll() {
        Rectangle a = new Rectangle(354, 298, 28, 19);
        Mouse.clickMouse((int) a.getCenterX(), (int) a.getCenterY(), true);
        sleep(random(1000, 1200));
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.yellow);
        if (col1 != null) {
            Point[] a = ColorUtil.findAllColor(col1);
            for (Point anA : a) {
                if (Inventory.BOUNDS.contains(anA)) {
                    g.drawRect(anA.x-5, anA.y-5, 20, 20);
                }
            }
        }
        g.setColor(Color.red);
        if (col2 != null) {
            Point[] a = ColorUtil.findAllColor(col2);
            for (Point anA : a) {
                if (Inventory.BOUNDS.contains(anA)) {
                    g.drawRect(anA.x-5, anA.y-5, 20, 20);
                }
            }
        }
        g.setColor(Color.green);
        if (col3 != null) {
            Point[] a = ColorUtil.findAllColor(col3);
            for (Point anA : a) {
                if (Inventory.BOUNDS.contains(anA)) {
                    g.drawRect(anA.x-5, anA.y-5, 20, 20);
                }
            }
        }
        return null;
    }

    public int getAmount(Color colors) {
        int amount = 0;
        for (int i = 0; i < 28; i++) {
            if (areColorsClose(Inventory.getSlotAt(i).getCenterColor(), colors, 10)) {
                amount += 1;
            }
        }
        return amount;
    }

    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    public boolean doneFletching() {
        Point[] a = ColorUtil.findAllColor(col2);
        for (Point anA : a) {
            if (Inventory.BOUNDS.contains(anA)) {
                return false;
            }
        }
        return true;
    }

    public boolean isFletching() {
        final int amount = getAmount(col2);
        if (getAmount(col2) == amount) {
            sleep(random(800, 1300));
            if (getAmount(col2) == amount) {
                sleep(random(700, 1000));
                if (getAmount(col2) == amount) {
                    sleep(random(500, 800));
                    if (getAmount(col2) == amount) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static int random(long min, long max) {
        Random rand = new Random();
        int a = (int) min;
        return (int) (rand.nextInt((int) ((max+1) - min)) + min);
    }

    @Override
    public void sleep(long a) {
        ScriptManager.getCurrent().sleep(random(a, a+200));
    }

}
