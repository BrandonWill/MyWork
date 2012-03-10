import api.methods.Bank;
import api.methods.Game;
import api.methods.Inventory;
import api.methods.Mouse;
import bot.script.Script;
import bot.script.ScriptManifest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 3/8/12
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
@ScriptManifest(authors = { "Dwarfeh" }, category = "Money Making", name = "Dwarfeh's Vial Filler", description = "Fills Vials", version = 1.0)
public class VialFiller extends Script {
    boolean deposited = false;

    Color vialColor;
    @Override
    public boolean onStart() {
        vialColor = Inventory.getSlotAt(0).getCenterColor();
        return true;
    }

    @Override
    public int loop() {
        if (needsToBank()) {
            bank();
        }
        return 0;
    }

    public boolean needsToBank() {
        return getAmount(vialColor) == 0;
    }

    public boolean bank() {
        if (openBank()) {
            if (!deposited) {
                depositAll();
                sleep(random(1500, 1700));
                deposited = true;
            }
            withdrawVials();
        }
        return withdrawVials();
    }

    public boolean openBank() {
        if (!Bank.isOpen()) {
            Mouse.move(230, 128);
            String a = OCRUtil.getOptionsText();
            if (a.contains("Banker")) {
                Mouse.click(new Point(random(230, 232), random(128, 130)), false);
                sleep(random(600, 800));
                Mouse.click(random(Mouse.getLocation().x, Mouse.getLocation().x+2), Mouse.getLocation().y+random(40, 42));
                sleep(random(600, 800));
            }
        }
        return Bank.isOpen();
    }

    public boolean atFountain() {
        return true;
    }

    public boolean withdrawVials() {
        if (Bank.isOpen() && getAmount(vialColor) != 28) {
            Mouse.move(random(52, 54), random(105, 107));
            sleep(random(1200, 1700));
            String a = OCRUtil.getOptionsText();
            if (a.contains("Vial")) {
                Mouse.clickMouse(random(52, 54), random(105, 107), false);
                sleep(random(1000, 1200));
                Mouse.clickMouse(random(52, 54), random(182, 184), true);
                sleep(random(500, 800));
            }
        }
        return getAmount(vialColor) == 28;
    }

    public void depositAll() {
        Rectangle a = new Rectangle(354, 298, 28, 19);
        Mouse.clickMouse((int) a.getCenterX(), (int) a.getCenterY(), true);
        sleep(random(1000, 1200));
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

    @Override
    public Graphics doPaint(Graphics graphics) {
        graphics.drawRect(Mouse.getLocation().x-5, Mouse.getLocation().y-5, 10, 10);
        return null;
    }

    public int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max+1) - min) + min;
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
}
