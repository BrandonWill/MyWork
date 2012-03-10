import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManifest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

@ScriptManifest(authors = { "Dwarfeh" }, category = "Smelter", name = "Dwarfeh's Smelter", description = "Basic Smelter", version = 1.0)
public class DwarfehSmelter extends Script {

    private Color barColor;

    private boolean walkedToBank;
    private boolean walkedToFurnace = true;

    private Point pointForAtBank;
    private Color colorForAtBank;

    private Point pointToOpenBank;

    //ore Colors
    private Color coalColor = new Color(37, 37, 29);
    private Color ironColor = new Color(55, 33, 24);

    //Furnace
    private Point furnacePoint = new Point(102, 104);
    private Color furnaceColor = new Color(61, 58, 58);

    //Color to smelt
    private Point smeltSteelBarPoint = new Point(289, 419);
    private Color smeltSteelBarColor = new Color(130, 124, 124);

    //ore required
    private int ore1Required = 2;
    private int ore2Required = 1;

    private int ore1Amount = 18;
    private int ore2Amount = 10;
    
    //paint stuff
    private long startTime = 0, millis = 0, hours = 0, minutes = 0, seconds = 0, last = 0;
    private int amountSmithed;

    //Bank stuff
    Color bankerColor = new Color(109, 105, 104);
    boolean realBankerColorFound = false;

    
    String status = "None";
    String location = "None";

    public boolean atBank() {
        wasAtBank = true;
        if (!walkedToBank) {
            return false;
        }
        if (realBankerColorFound && nearBanker()) {
            return true;
        }
        Mouse.move(pointToOpenBank.x, pointToOpenBank.y);
        String optionText = OCRUtil.getOptionsText();

        if (optionText.contains("Bank")) {

        }

        return isOpen() || optionText.contains("Bank");
        //        if (!isOpen() && !optionText.contains("Bank")) {
//            return false;
//        }

//        return isOpen() || !atFurnace() && areColorsClose(colorForAtBank, ColorUtil.getColor(pointForAtBank));
    }


    public boolean nearBanker() {
        try {
            return ColorUtil.findAllColor(bankerColor).length >= 15;
        } catch (Exception e) {
            log("Error finding Banker!");
            return false;
        }
    }

    public boolean atFurnace() {
        if (!walkedToFurnace) {
            return false;
        }
        if (isSmithing()) {
            return true;
        }
        Mouse.move(furnacePoint.x, furnacePoint.y);
        String optionText = OCRUtil.getOptionsText();
        return optionText.contains("Smelt");
        //return !isOpen() && areColorsClose(furnaceColor, ColorUtil.getColor(furnacePoint), 25);
    }

    public int getOreAmount(Color colors) {
        int amount = 0;
        for (int i = 0; i < 28; i++) {
            if (areColorsClose(Inventory.getSlotAt(i).getCenterColor(), colors)) {
                amount += 1;
            }
        }
        return amount;
    }

    public void walkToBank(int incX, int incY) {
        Camera.setNorth();
        sleep(1000);
        walkedToFurnace = false;
        walkedToBank = true;
        Mouse.move(605 +incX, 156 +incY);
        Mouse.click();
    }

    public void walkToFurnace() {
        walkedToBank = false;
        walkedToFurnace = true;
        Mouse.move(648, 15);
        sleep(400);
        Mouse.click();
    }

    public void clickCompass() {
        Mouse.click(543, 23, true);
        sleep(300);
    }

    public boolean doneSmithing() {
        return areColorsClose(barColor, Inventory.getSlotAt(8).getCenterColor());
    }

    public void smeltSteel() {
        Mouse.click(291, 426, true);
        sleep(300);
    }

    public void openBank() {
        Mouse.move(pointToOpenBank.x, pointToOpenBank.y);
        String optionText = OCRUtil.getOptionsText();
        if (!optionText.contains("Bank")) {
            Mouse.move(pointToOpenBank.x-5, pointToOpenBank.y+20);
            optionText = OCRUtil.getOptionsText();
            if (!optionText.contains("Bank")) {
                return;
            }
            Mouse.click();
        }
        Mouse.click(pointToOpenBank, true);
        sleep(random(600, 800));
    }

    public boolean isOpen() {
        return areColorsClose(new Color(24, 21, 19), ColorUtil.getColor(487, 33));
    }

    public void depositAll() {
        Rectangle a = new Rectangle(354, 298, 28, 19);
        Mouse.clickMouse((int) a.getCenterX(), (int) a.getCenterY(), true);
        sleep(random(1000, 1200));
    }

    public void clickFurnace() {
        Mouse.clickMouse(furnacePoint.x, furnacePoint.y, true);
        sleep(1000);
        Mouse.move(291, 426);
        Mouse.click();
    }

    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    public boolean areColorsClose(Color color1, Color color2) {
        return areColorsClose(color1, color2, 10);
    }

    public void withdrawOre1(int amount) {
        Mouse.clickMouse(52, 105, false);
        sleep(random(1000, 1200));
        Mouse.clickMouse(52, 182, true);
        sleep(random(500, 800));
//        Mouse.clickMouse(52, 182, true);
//        sleep(200);
//        KeyBoard.sendString("" +amount, true);
//        sleep(300);
    }

    public void withdrawOre2(int amount) {
        Mouse.clickMouse(96, 106, false);
        sleep(random(1000, 1200));
        Mouse.clickMouse(90, 182, true);
        sleep(random(500, 800));
    }

    public boolean smeltingInterfaceIsUp() {
        return areColorsClose(smeltSteelBarColor, ColorUtil.getColor(smeltSteelBarPoint));
    }              //99, 259

    @Override
    public boolean onStart() {
        log("Smelter has begun");
        //Steel
        barColor = new Color(129, 124, 124);
        //al kahrid at Bank
        pointForAtBank = new Point(25, 132);
        colorForAtBank = new Color(79, 74, 69);

        //al kahrid open Bank
        pointToOpenBank = new Point(205, 154);

        startTime = System.currentTimeMillis();

//        println("Center Color for coal: " + Inventory.getSlotAt(0).getCenterColor());
//        println("Center color for iron: " +Inventory.getSlotAt(27).getCenterColor());
//        println("Coal amount: " +getOreAmount(coalColor));
//        println("iron amount: " +getOreAmount(ironColor));
//        println("Furnace Color: " +ColorUtil.getColor(102, 104));
//        println("At Furnace: " +atFurnace());
//        println("Done Smithing req: " +!doneSmithing());
//        println("is smithing req: " +!isSmithing());
//        println("ENUF 2 SMELT: " +new Boolean(getOreAmount(coalColor) >= ore1Required && getOreAmount(ironColor) >= ore2Required));
//        println("Steel bar Smelt Col: " +ColorUtil.getColor(289, 419));
//        println("Interface ^: " +smeltingInterfaceIsUp());
//        println("Color at 25, 132: " +ColorUtil.getColor(25, 132));
        return true;
    }

    @Override
    public void onFinish() {
        log("Smelter has ended");
    }

    boolean wasAtBank = false;
    @Override
    public int loop() {

        if (!Tabs.getCurrentTab().equals("Inventory") && !isOpen()) {
            Tabs.setTab("Inventory");
            sleep(500);
            return 300;
        }
        if (doneSmithing() && !walkedToBank && !atBank()) {
            walkToBank(0, 0);
            while (!atBank()) {
                status = "Walking to bank";
                sleep(2000);
            }
        }

        if ((atBank() || wasAtBank || walkedToBank) && (getOreAmount(coalColor) < ore1Required || getOreAmount(ironColor) < ore2Required)) {
            status = "needs to bank";
            if (!isOpen()) {
                openBank();
                status = "Opening Bank";
                sleep(400);
            }
            if (isOpen()) {
                if (doneSmithing()) {
                    amountSmithed += 9;
                    status = "Depositing all";
                    depositAll();
                    sleep(500);
                }
                status = "Withdrawing ores";
                withdrawOre1(ore1Amount);
                withdrawOre2(ore2Amount);
                sleep(300);
            }
        }
        if (getOreAmount(coalColor) >= ore1Required && getOreAmount(ironColor) >= ore2Required) {
            status = "Can smelt";
            if (!walkedToFurnace) {
                walkToFurnace();
                sleep(random(4500, 5000));
            }
            if (!atFurnace()) {
                String optionText = OCRUtil.getOptionsText();
                if (optionText.contains("Smelt")) {
                    Mouse.click();
                } else {
                    Mouse.move(Mouse.getLocation().x+30, Mouse.getLocation().y+50);
                    sleep(300);
                    optionText = OCRUtil.getOptionsText();
                    if (optionText.contains("Smelt")) {
                        Mouse.click();
                    }
                }
                status = "Walking to Furnace";
                return 300;
            }

            if (atFurnace() && !doneSmithing() && !isSmithing()) {
                if (!smeltingInterfaceIsUp()) {
                    status = "Clicking furnace";
                    clickFurnace();
                }
                if (smeltingInterfaceIsUp()) {
                    status = "Clicking interface";
                    smeltSteel();
                }
            }
        }
        return 400;
    }

    public boolean isSmithing() {
        final int amount = getOreAmount(barColor);
        if (getOreAmount(barColor) == amount) {
            sleep(random(800, 1300));
            if (getOreAmount(barColor) == amount) {
                sleep(random(700, 1000));
                if (getOreAmount(barColor) == amount) {
                    sleep(random(500, 800));
                    if (getOreAmount(barColor) == amount) {
                        return false;
                    }
                }
            }
        }
        status = "smelting";
        return true;
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        //Calculate time
        if(System.currentTimeMillis() - startTime > 0){
            millis = System.currentTimeMillis() - startTime;
            hours = millis / (1000 * 60 * 60);
            millis -= hours * (1000 * 60 * 60);
            minutes = millis / (1000 * 60);
            millis -= minutes * (1000 * 60);
            seconds = millis / 1000;
        }

        Graphics2D g = (Graphics2D) graphics;
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.gray);
        g.draw3DRect(1, 295, 132, 44, true);
        g.setColor(new Color(0, 0, 0, 70));
        g.fill3DRect(1, 295, 132, 44, true);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 0, 9));
        g.drawString("Status: " +status, 9, 302);
        g.drawString("RunTime: "+ hours +":"+ minutes + ":" + seconds, 9, 312);
//        g.drawString("Location: " +location, 9, 312);
        g.drawString("Bars smelted: " +amountSmithed, 9, 322);


        
        //draw mouse
        
        g.drawRect(Mouse.getLocation().x-5, Mouse.getLocation().y-5, 10, 10);

        return null;
    }

    public static class OCRUtil {

        private static class Character {
            private char character;
            private int[] fontPointsX, fontPointsY, shadowPointsX, shadowPointsY;
            private int height, width;

            private Character(char character, int[] x, int[] y) {
                this.character = character;
                this.fontPointsX = x;
                this.fontPointsY = y;

                ArrayList<Point> shadowPoints = new ArrayList<Point>();

                main:		for (int i = 0; i < fontPointsX.length; i++) {

                    for (int j = 0; j < fontPointsX.length; j++) {
                        if (fontPointsX[i] + 1 == fontPointsX[j] && fontPointsY[i] + 1 == fontPointsY[j]) {
                            continue main;
                        }
                    }

                    shadowPoints.add(new Point(fontPointsX[i] + 1, fontPointsY[i] + 1));

                    if (fontPointsX[i] + 1 > width) {
                        width = fontPointsX[i] + 1;
                    }
                    if (fontPointsY[i] + 1 > height) {
                        height = fontPointsY[i] + 1;
                    }
                }

                shadowPointsX = new int[shadowPoints.size()];
                shadowPointsY = new int[shadowPoints.size()];

                for (int i = 0; i < shadowPointsX.length; i++) {
                    shadowPointsX[i] = shadowPoints.get(i).x;
                    shadowPointsY[i] = shadowPoints.get(i).y;
                }
            }

            private char getCharacter() {
                return character;
            }

            private int getHeight() {
                return height;
            }

            private int getWidth() {
                return width;
            }

        }

        private static Character[] optionCharacters = {
                new Character('a',
                        new int[] {1, 2, 3, 4, 1, 2, 3, 4, 0, 4, 0, 4, 1, 2, 3, 4},
                        new int[] {0, 0, 0, 1, 2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5}),
                new Character('b',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 1, 2, 3, 4, 4, 4, 4},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 3, 3, 3, 8, 8, 8, 4, 5, 6, 7}),
                new Character('c',
                        new int[] {1, 2, 3, 0, 0, 0, 0, 4, 4, 1, 2, 3},
                        new int[] {0, 0, 0, 1, 2, 3, 4, 1, 4, 5, 5, 5}),
                new Character('d',
                        new int[] {4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 2, 3, 0, 0, 0, 0, 1, 2, 3},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 3, 3, 3, 4, 5, 6, 7, 8, 8, 8}),
                new Character('e',
                        new int[] {1, 2, 3, 0, 4, 0, 1, 2, 3, 4, 0, 0, 4, 1, 2, 3},
                        new int[] {0, 0, 0, 1, 1, 2, 2, 2, 2, 2, 3, 4, 4, 5, 5, 5}),
                new Character('f',
                        new int[] {2, 3, 1, 1, 0, 1, 2, 3, 1, 1, 1, 1, 1},
                        new int[] {0, 0, 1, 2, 3, 3, 3, 3, 4, 5, 6, 7, 8}),
                new Character('g',
                        new int[] {1, 2, 3, 4, 0, 0, 0, 0, 4, 4, 4, 4, 1, 2, 3, 4, 4, 3, 2, 1},
                        new int[] {0, 0, 0, 0, 1, 2, 3, 4, 1, 2, 3, 4, 5, 5, 5, 5, 6, 7, 7, 7}),
                new Character('h',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 4, 4, 4},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 3, 3, 3, 4, 5, 6, 7, 8}),
                new Character('i',
                        new int[] {0, 0, 0, 0, 0, 0, 0},
                        new int[] {0, 3, 4, 5, 6, 7, 8}),
                new Character('j',
                        new int[] {0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2},
                        new int[] {10, 3, 10, 0, 3, 4, 5, 6, 7, 8, 9}),
                new Character('k',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 3, 3, 4, 4},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 6, 5, 6, 4, 7, 3, 8}),
                new Character('l',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}),
                new Character('m',
                        new int[] {0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 4, 4, 4, 5, 6, 7, 8, 8, 8, 8, 8},
                        new int[] {0, 1, 2, 3, 4, 5, 0, 0, 0, 1, 2, 3, 4, 5, 0, 0, 0, 1, 2, 3, 4, 5}),
                new Character('n',
                        new int[] {0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 4, 4, 4, 4},
                        new int[] {0, 1, 2, 3, 4, 5, 0, 0, 0, 1, 2, 3, 4, 5}),
                new Character('o',
                        new int[] {1, 2, 3, 0, 4, 0, 4, 0, 4, 0, 4, 1, 2, 3},
                        new int[] {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5}),
                new Character('p',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 1, 2, 3, 4, 4, 4, 4},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 0, 0, 0, 5, 5, 5, 1, 2, 3, 4}),
                new Character('q',
                        new int[] {1, 2, 3, 4, 0, 4, 0, 4, 0, 4, 0, 4, 1, 2, 3, 4, 4, 4},
                        new int[] {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 6, 7}),
                new Character('r',
                        new int[] {0, 0, 0, 0, 0, 0, 1, 2, 3},
                        new int[] {0, 1, 2, 3, 4, 5, 1, 0, 0}),
                new Character('s',
                        new int[] {1, 2, 3, 0, 0, 1, 2, 3, 3, 0, 1, 2},
                        new int[] {0, 0, 0, 1, 2, 2, 3, 3, 4, 5, 5, 5}),
                new Character('t',
                        new int[] {1, 1, 0, 1, 2, 3, 1, 1, 1, 1, 2, 3},
                        new int[] {0, 1, 2, 2, 2, 2, 3, 4, 5, 6, 7, 7}),
                new Character('u',
                        new int[] {0, 0, 0, 0, 0, 4, 4, 4, 4, 4, 1, 2, 3, 4},
                        new int[] {0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 5, 5, 5, 5}),
                new Character('v',
                        new int[] {0, 4, 0, 4, 1, 3, 1, 3, 2, 2},
                        new int[] {0, 0, 1, 1, 2, 2, 3, 3, 4, 4}),
                new Character('w',
                        new int[] {0, 3, 6, 0, 3, 6, 0, 2, 4, 6, 0, 2, 4, 6, 1, 5, 1, 5},
                        new int[] {0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5}),
                new Character('x',
                        new int[] {0, 4, 1, 3, 2, 2, 1, 3, 0, 4},
                        new int[] {0, 0, 1, 1, 2, 3, 4, 4, 5, 5}),
                new Character('y',
                        new int[] {0, 4, 1, 1, 1, 3, 3, 3, 2, 2, 2, 1},
                        new int[] {0, 0, 1, 2, 3, 1, 2, 3, 4, 5, 6, 7}),
                new Character('z',
                        new int[] {0, 1, 2, 3, 3, 2, 1, 0, 0, 1, 2, 3},
                        new int[] {0, 0, 0, 0, 1, 2, 3, 4, 5, 5, 5, 5}),

                new Character('A',
                        new int[] {2, 3, 2, 3, 1, 4, 1, 4, 1, 4, 0, 1, 2, 3, 4, 5, 0, 5, 0, 5},
                        new int[] {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 7, 7}),
                new Character('B',
                        new int[] {0, 1, 2, 3, 0, 4, 0, 4, 0, 1, 2, 3, 4, 0, 5, 0, 5, 0, 5, 0, 1, 2, 3, 4},
                        new int[] {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7, 7, 7}),
                new Character('C',
                        new int[] {2, 3, 4, 5, 1, 6, 0, 0, 0, 0, 1, 6, 2, 3, 4, 5},
                        new int[] {0, 0, 0, 0, 1, 1, 2, 3, 4, 5, 6, 6, 7, 7, 7, 7}),
                new Character('D',
                        new int[] {0, 1, 2, 3, 4, 0, 5, 0, 6, 0, 6, 0, 6, 0, 6, 0, 5, 0, 1, 2, 3, 4},
                        new int[] {0, 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7, 7, 7}),
                new Character('E',
                        new int[] {0, 1, 2, 3, 4, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0, 1, 2, 3, 4},
                        new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 4, 5, 6, 7, 7, 7, 7, 7}),
                new Character('F',
                        new int[] {0, 1, 2, 3, 4, 0, 0, 0, 1, 2, 3, 0, 0, 0, 0},
                        new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 5, 6, 7}),
                new Character('G',
                        new int[] {2, 3, 4, 5, 1, 6, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 6, 6, 5, 4},
                        new int[] {0, 0, 0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 7, 7, 6, 5, 4, 4, 4}),
                new Character('H',
                        new int[] {0, 5, 0, 5, 0, 5, 0, 1, 2, 3, 4, 5, 0, 5, 0, 5, 0, 5, 0, 5},
                        new int[] {0, 0, 1, 1, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7}),
                new Character('I',
                        new int[] {0, 1, 2, 1, 1, 1, 1, 1, 1, 0, 1, 2},
                        new int[] {0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 7, 7}),
                new Character('J',
                        new int[] {1, 2, 3, 3, 3, 3, 3, 3, 3, 0, 1, 2},
                        new int[] {0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 7, 7}),
                new Character('K',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 3, 3, 4, 4, 5, 5},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 4, 3, 4, 2, 5, 1, 6, 0, 7}),
                new Character('L',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 7, 7, 7, 7}),
                new Character('M',
                        new int[] {0, 1, 5, 6, 0, 1, 5, 6, 0, 2, 4, 6, 0, 2, 4, 6, 0, 3, 6, 0, 3, 6, 0, 6, 0, 6},
                        new int[] {0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7}),
                new Character('N',
                        new int[] {0, 1, 5, 0, 1, 5, 0, 2, 5, 0, 2, 5, 0, 3, 5, 0, 3, 5, 0, 4, 5, 0, 4, 5},
                        new int[] {0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7}),
                new Character('O',
                        new int[] {2, 3, 4, 1, 5, 0, 6, 0, 6, 0, 6, 0, 6, 1, 5, 2, 3, 4},
                        new int[] {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7}),
                new Character('P',
                        new int[] {0, 1, 2, 3, 0, 4, 0, 4, 0, 4, 0, 1, 2, 3, 0, 0, 0},
                        new int[] {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 4, 5, 6, 7}),
                new Character('Q',
                        new int[] {2, 3, 4, 1, 5, 0, 6, 0, 6, 0, 6, 0, 6, 1, 5, 2, 3, 4, 4, 5, 6},
                        new int[] {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7, 8, 9, 9}),
                new Character('R',
                        new int[] {0, 1, 2, 3, 0, 4, 0, 4, 0, 4, 0, 1, 2, 3, 0, 0, 0, 3, 4, 5},
                        new int[] {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 4, 5, 6, 7, 5, 6, 7}),
                new Character('S',
                        new int[] {1, 2, 3, 4, 0, 5, 0, 1, 2, 3, 4, 5, 5, 0, 1, 2, 3, 4},
                        new int[] {0, 0, 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 6, 6, 7, 7, 7, 7}),
                new Character('T',
                        new int[] {0, 1, 2, 3, 4, 5, 6, 3, 3, 3, 3, 3, 3, 3},
                        new int[] {0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7}),
                new Character('U',
                        new int[] {0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 5},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 7, 7, 7, 6, 5, 4, 3, 2, 1, 0}),
                new Character('V',
                        new int[] {0, 5, 0, 5, 0, 5, 1, 4, 1, 4, 1, 4, 2, 3, 2, 3},
                        new int[] {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7}),
                new Character('W',
                        new int[] {0, 4, 8, 0, 4, 8, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 1, 3, 5, 7, 2, 6, 2, 6},
                        new int[] {0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 7, 7}),
                new Character('X',
                        new int[] {0, 5, 0, 5, 1, 4, 2, 3, 2, 3, 1, 4, 0, 5, 0, 5},
                        new int[] {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7}),
                new Character('Y',
                        new int[] {0, 6, 1, 5, 2, 4, 3, 3, 3, 3, 3},
                        new int[] {0, 0, 1, 1, 2, 2, 3, 4, 5, 6, 7}),
                new Character('Z',
                        new int[] {0, 1, 2, 3, 4, 5, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5},
                        new int[] {0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 7, 7, 7, 7, 7}),

                new Character('0',
                        new int[] {1, 2, 3, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 0, 4, 1, 2, 3},
                        new int[] {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7}),
                new Character('1',
                        new int[] {2, 0, 1, 2, 2, 2, 2, 2, 2, 0, 1, 2, 3, 4},
                        new int[] {0, 1, 1, 1, 2, 3, 4, 5, 6, 7, 7, 7, 7, 7}),
                new Character('2',
                        new int[] {1, 2, 3, 0, 4, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4},
                        new int[] {0, 0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 7, 7, 7}),
                new Character('3',
                        new int[] {1, 2, 3, 0, 4, 4, 3, 2, 4, 4, 4, 0, 1, 2, 3},
                        new int[] {0, 0, 0, 1, 1, 2, 3, 3, 4, 5, 6, 6, 7, 7, 7}),
                new Character('4',
                        new int[] {4, 4, 3, 4, 2, 4, 1, 4, 0, 0, 1, 2, 3, 4, 5, 4, 4},
                        new int[] {0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 5, 5, 5, 6, 7}),
                new Character('5',
                        new int[] {0, 1, 2, 3, 4, 0, 0, 0, 1, 2, 3, 4, 4, 0, 4, 1, 2, 3},
                        new int[] {0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 5, 6, 6, 7, 7, 7}),
                new Character('6',
                        new int[] {2, 3, 1, 0, 0, 1, 2, 3, 0, 4, 0, 4, 0, 4, 1, 2, 3},
                        new int[] {0, 0, 1, 2, 3, 3, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7}),
                new Character('7',
                        new int[] {0, 1, 2, 3, 4, 4, 3, 3, 2, 2, 1, 1},
                        new int[] {0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7}),
                new Character('8',
                        new int[] {1, 2, 3, 0, 4, 0, 4, 1, 2, 3, 0, 4, 0, 4, 0, 4, 1, 2, 3},
                        new int[] {0, 0, 0, 1, 1, 2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 7}),
                new Character('9',
                        new int[] {1, 2, 3, 0, 4, 0, 4, 0, 4, 1, 2, 3, 4, 4, 3, 2, 1},
                        new int[] {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 4, 5, 6, 7, 7}),

                new Character('-',
                        new int[] {0, 1, 2},
                        new int[] {0, 0, 0}),
                new Character('/',
                        new int[] {0, 0, 1, 1, 2, 2, 3, 3, 4, 4},
                        new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}),
                new Character('(',
                        new int[] {2, 1, 1, 0, 0, 0, 0, 0, 1, 1, 2},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}),
                new Character(')',
                        new int[] {0, 1, 1, 2, 2, 2, 2, 2, 1, 1, 0},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
        };

        static {
            Arrays.sort(optionCharacters, new Comparator<Character>() {
                public int compare(Character arg0, Character arg1) {
                    if (arg0.width < arg1.width) {
                        return 1;
                    }
                    if (arg0.width > arg1.width) {
                        return -1;
                    }

                    if (arg0.fontPointsX.length < arg1.fontPointsX.length) {
                        return 1;
                    }
                    if (arg0.fontPointsX.length > arg1.fontPointsX.length) {
                        return -1;
                    }

                    return 0;
                }
            });
        }

        public static String getOptionsText() {
            StringBuilder builder = new StringBuilder();

            BufferedImage gameImage = Game.getImage();

            int leftUpperX = 5, leftUpperY = 5, width = Game.VIEWPORT.width - 10, height = 20;

            boolean[][] ocrImage = new boolean[width][height];

            for (int x = leftUpperX; x < leftUpperX + width; x++) {
                for (int y = leftUpperY; y < leftUpperY + height; y++) {
                    int color = gameImage.getRGB(x, y) & 0xFFFFFF;

                    ocrImage[x - leftUpperX][y - leftUpperY] = getDistanceSquare(color, 14474460) < 12500 // WHITE
                            || getDistanceSquare(color, 56540) < 12500  // CYAN
                            || getDistanceSquare(color, 14474240) < 12500  // YELLOW
                            || getDistanceSquare(color, 15106620) < 12500;

                }
            }

            // First, find a capital letter in the area ((0,0),(50,height))

            int posX = 0;

            x:		for (int x = 0; x < 50; x++) {
                for (int y = 0; y < height; y++) {

                    c:				for (Character c : optionCharacters) {

                        if (c.getCharacter() < 'A' || c.getCharacter() > 'Z') {
                            continue;
                        }

                        if (y + c.getHeight() >= 20) {
                            continue;
                        }

                        if (x + c.getWidth() >= 25) {
                            continue;
                        }

                        for (int j = 0; j < c.fontPointsX.length; j++) {
                            if (!ocrImage[x + c.fontPointsX[j]][y + c.fontPointsY[j]]) {
                                continue c;
                            }
                        }

                        for (int j = 0; j < c.shadowPointsX.length; j++) {
                            if (ocrImage[x + c.shadowPointsX[j]][y + c.shadowPointsY[j]]) {
                                continue c;
                            }
                        }

                        builder.append(c.getCharacter());
                        posX = x + c.getWidth();

                        break x;

                    }

                }
            }

            // now read the rest of the characters

            int lastPosX = 0;

            for (; posX < Game.VIEWPORT.width - 10; posX++) {
                y:			for (int y = 0; y < height; y++) {

                    c:				for (Character c : optionCharacters) {

                        if (y + c.getHeight() >= height) {
                            continue;
                        }

                        if (posX + c.getWidth() >= Game.VIEWPORT.width - 10) {
                            continue;
                        }

                        for (int j = 0; j < c.fontPointsX.length; j++) {
                            if (!ocrImage[posX + c.fontPointsX[j]][y + c.fontPointsY[j]]) {
                                continue c;
                            }
                        }

                        for (int j = 0; j < c.shadowPointsX.length; j++) {
                            if (ocrImage[posX + c.shadowPointsX[j]][y + c.shadowPointsY[j]]) {
                                continue c;
                            }
                        }

                        if (lastPosX != 0 && posX - lastPosX > 5) {
                            builder.append(' ');
                        }

                        builder.append(c.getCharacter());
                        posX += c.getWidth() - 1;
                        lastPosX = posX + 1;

                        break y;
                    }
                }
            }

            return builder.toString();
        }

        public static int getDistanceSquare(int c1, int c2) {
            int rd = ((c1 >> 16) & 0xFF) - ((c2 >> 16) & 0xFF);
            int gd = ((c1 >> 8) & 0xFF) - ((c2 >> 8) & 0xFF);
            int bd = (c1 & 0xFF) - (c2 & 0xFF);
            return rd*rd + gd*gd + bd*bd;
        }

    }
    
    public int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max+1) - min) + min;
    }

}
