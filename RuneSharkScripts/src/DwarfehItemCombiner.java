import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManager;
import bot.script.ScriptManifest;

import java.awt.*;
import java.util.Random;

@ScriptManifest(authors = { "Dwarfeh" }, category = "Item Combiner", name = "Dwarfeh's Item Combiner", description = "Combines items with auto detection", version = 1.0)
public class DwarfehItemCombiner extends Script {
    Color col1 = null;
    Color col2 = null;
    Color col3 = null;
    boolean guiDone = true;
    boolean cutting;
    int withdrawCol1;
    int withdrawCol2;
    boolean opened = false;
    int numberToUse = 2;

    Rectangle xpGained = new Rectangle(415, 5, 90, 35);
    long startTime;
    int startingXP;
    int gained;
    int banked = 0;

    @Override
    public boolean onStart() {
        log("Dwarfeh's Item Combiner starting");
        toggleXPDisplay();
        String text = RSText.findString(xpGained, null, null).replaceAll(" ", "");
        if (text.contains("+")) {
            text = text.substring(4);
        }
        startingXP = text.length() >= 1 ? Integer.parseInt(text) : 0;
        startTime = System.currentTimeMillis();
        return true;
    }

    @Override
    public int loop() {
        try {
            String text = RSText.findString(xpGained, null, null).replaceAll(" ", "");
            if (text.contains("+")) {
                text = text.substring(4);
            }
            gained = Integer.parseInt(text)-startingXP;
            if (!Tabs.getCurrentTab().equals("Inventory")) {
                log("Inventory tab not open. Possibly stuck in random DERP!!!!");
                ScriptManager.getCurrent().stopScript();
            }
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
            if (!isFletching() && getAmount(col1) > 0) {
                log("Using items on each other");
                useItem(col1, col2);
            }

            if (getAmount(col1) == 0) {
                if (col3 == null) {
                    for (int i = 0; i < 28; i++) {
                        if (col3 == null && Inventory.getSlotAt(i).getCenterColor().getRGB() != col1.getRGB() && Inventory.getSlotAt(i).getCenterColor().getRGB() != col2.getRGB()) {
                            col3 = Inventory.getSlotAt(i).getCenterColor();
                            log("Found a 3rd color: " +col3);
                            i = 28;
                        }
                    }
                }
                log("Attempting to bank");
                bank();

            }
        } catch (Throwable ignored) { }
        return 0;
    }

    public void useItem(Color color1, Color color2) {
        Point a = getSlotWithCenterColor(color1).getCenter();
        Point b = getSlotWithCenterColor(color2).getCenter();
        if (b != null && Inventory.BOUNDS.contains(b)) {
            Inventory.doAction(getSlotWithCenterColor(color1), numberToUse);
            sleep(random(400, 600));
            Mouse.click(b, 3, 3);
            sleep(random(400, 600));
            Mouse.click(new Point(259, 428), 3, 3);
            sleep(random(500, 700));
        }
    }

    public boolean bankOpen() {
        if (opened) {
            return opened;
        }
        Mouse.move(220, 159);
        String text = RSText.getOptionsText().toLowerCase();
        if (text.contains("bank")) {
            Mouse.click(220, 159);
            sleep(random(500, 1000));
            opened = true;
        }
        return opened;
    }

    public boolean bank() {
        if (bankOpen())  {

            banked += (getAmount(col3));
            if (withdrawCol1 == 1) {
                depositAllExcept(1);
            } else {
                depositAll();
            }
            if (withdrawCol1 > 1) {
                withdrawItem1();
                sleep(random(1500, 2000));
            }
            withdrawItem2();
            sleep(random(1500, 2000));
            closeBank();

        }
        return true;
    }

    public boolean depositAllExcept(int slotNumber) {
        Inventory.doAction(slotNumber < 27 ? slotNumber+1: slotNumber-1, 6);
        sleep(random(800, 1000));
        return getAmount(Inventory.getSlotAt(slotNumber).getCenterColor()) == 0;

    }

    void closeBank() {
        log("Closing bank");
        Mouse.move(random(487, 489), random(33, 35));
        sleep(random(1300, 1700));
        Mouse.click();
        sleep(random(1500, 2000));
        opened = false;
    }

    public Inventory.Slot getSlotWithCenterColor(Color color) {
        for (Inventory.Slot a : Inventory.Slot.values()) {
            if (a.getCenterColor().getRGB() == color.getRGB()) {
                return a;
            }
        }
        return null;
    }

    public void withdrawItem1() {
        Mouse.clickMouse(52, 105, false);
        sleep(random(1000, 1200));
        Mouse.clickMouse(52, 182, true);
        sleep(random(500, 800));
//        Mouse.clickMouse(52, 182, true);
//        sleep(200);
//        KeyBoard.sendString("" +amount, true);
//        sleep(300);
    }

    public void withdrawItem2() {
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
            for (Inventory.Slot anA : Inventory.Slot.values()) {
                if (anA.getCenterColor().getRGB() == col1.getRGB()) {
                    g.drawRect(anA.getBounds().x, anA.getBounds().y, anA.getBounds().width, anA.getBounds().height);
                }
            }
        }
        g.setColor(Color.red);
        if (col2 != null) {
            for (Inventory.Slot anA : Inventory.Slot.values()) {
                if (anA.getCenterColor().getRGB() == col2.getRGB()) {
                    g.drawRect(anA.getBounds().x, anA.getBounds().y, anA.getBounds().width, anA.getBounds().height);
                }
            }
        }
        g.setColor(Color.green);
        if (col3 != null) {
            for (Inventory.Slot anA : Inventory.Slot.values()) {
                if (anA.getCenterColor().getRGB() == col3.getRGB()) {
                    g.drawRect(anA.getBounds().x, anA.getBounds().y, anA.getBounds().width, anA.getBounds().height);
                }
            }
        }

        g.setStroke(new BasicStroke(6));
        g.setColor(Color.white);
        g.draw3DRect(1, 38, 140, 85, true);
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(0, 0, 0, 70));
        g.fill3DRect(1, 38, 140, 85, true);
        g.setColor(Color.white);

        g.setFont(new Font("Arial", 0, 9));

        g.drawRect(Mouse.getLocation().x, Mouse.getLocation().y, 10, 10);

        g.drawString("Dwarfeh's Fisher", 10, 50);

        int timeSpan = (int)(System.currentTimeMillis() - startTime) / 1000;

        g.drawString("Running for " + getTime(timeSpan), 10, 70);


        if (timeSpan > 0) {
            g.drawString("Experience: " + gained + " [" + (int)((double)gained / timeSpan * 3600) + "/h]", 10, 90);
//        }
        }
        
        g.drawString("Banked items: " +banked, 10, 110);
        return null;
    }

    private String getTime(int seconds) {
        StringBuilder sb = new StringBuilder();

        if (seconds > 3600) {
            sb.append(seconds / 3600);
            seconds %= 3600;
            sb.append(':');
        }

        int mins = seconds / 60;

        if (mins == 0) {
            sb.append("00");
        } else {
            if (mins < 10) {
                sb.append('0');
            }
            sb.append(mins);
        }

        sb.append(':');

        int secs = seconds % 60;

        if (secs == 0) {
            sb.append("00");
        } else {
            if (secs < 10) {
                sb.append('0');
            }
            sb.append(secs);
        }

        return sb.toString();
    }

    public int getAmount(Color colors) {
        int amount = 0;
        for (int i = 0; i < 28; i++) {
            if (areColorsClose(Inventory.getSlotAt(i).getCenterColor(), colors, 2)) {
                amount += 1;
            }
        }
        return amount;
    }

    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    public boolean isFletching() {
        final int amount = getAmount(col1);
        if (getAmount(col1) == amount) {
            sleep(random(800, 1300));
            if (getAmount(col1) == amount) {
                sleep(random(700, 1000));
                if (getAmount(col1) == amount) {
                    sleep(random(500, 800));
                    if (getAmount(col1) == amount) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static int random(long min, long max) {
        Random rand = new Random();
        return (int) (rand.nextInt((int) ((max+1) - min)) + min);
    }

    @Override
    public void sleep(long a) {
        ScriptManager.getCurrent().sleep(random(a, a+200));
    }

    public void toggleXPDisplay() {
        try {
            String text = RSText.findString(xpGained, null, null).replaceAll(" ", "");
            if (text.contains("+")) {
                text = text.substring(4);
            }
            gained = Integer.parseInt(text);
        }  catch(Throwable e) {
            Mouse.move(random(532, 534), random(60, 62));
            sleep(random(600, 800));
            Mouse.click(random(532, 534), random(60, 62));
            sleep(random(600, 800));
        }
    }
}

