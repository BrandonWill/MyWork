import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManager;
import bot.script.ScriptManifest;
import util.Configuration;

@ScriptManifest(authors = { "Dwarfeh" }, name = "Dwarfeh's Fisher", version = 0.1, description = "A fisher script for net, bait and fly powerfishing.",  category = "Fishing")

public class DwarfehFisher extends Script {
    boolean run = true;
    private long startTime = System.currentTimeMillis();

    private State state = State.FISHING;
    private Mode mode = Mode.FLY_FISHING;

    private int shrimpsDropped, anchoviesDropped, sardineDropped, herringDropped, pikeDropped, troutDropped, salmonDropped, lobsterDropped, tunaDropped, swordfishDropped;
    private int count = 1, countTo = 0, invIndex;

    private int fishingIntervalBase = 15, fishingIntervalRandom = 10;
    private int compareImages = 4, compareInterval = 600, compareThreshold = 300;
    private int fishingColor = 8556460, fishingThreshold = 10*10;
    private int mouseSpeed = 2;

    private volatile boolean isSelecting = true;

    private enum State {
        LOOKING_FOR_SPOT,
        FISHING,
        DROPPING
    }

    private enum Mode {
        FLY_FISHING,
        BAIT_FISHING,
        NET_FISHING,
        CAGE_FISHING,
        HARPOON_FISHING
    }

    public boolean onStart() {
        run = true;
        login.start();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final JFrame frame = new JFrame("DwarfehFisher");
                final JPanel panel = new JPanel();



                final JPanel behaviorPanel = new JPanel();

                behaviorPanel.setLayout(new GridLayout(5, 1));

                behaviorPanel.setBorder(BorderFactory.createTitledBorder("Behavior"));

                frame.setSize(450, 350);

                final JComboBox modeCombo = new JComboBox(new String[] { "Net fishing", "Bait Fishing", "Fly Fishing", "Cage fishing", "Harpoon fishing" });
                behaviorPanel.add(modeCombo);

                final JLabel fI = new JLabel("Fishing interval (s)");
                final JTextField fishingInterval = new JTextField(Integer.toString(fishingIntervalBase));
                behaviorPanel.add(fI);
                behaviorPanel.add(fishingInterval);

                final JLabel fI6 = new JLabel("Mouse speed");
                final JTextField mouseSpeed1 = new JTextField(Integer.toString(mouseSpeed));
                behaviorPanel.add(fI6);
                behaviorPanel.add(mouseSpeed1);

                //

                final JPanel parametersPanel = new JPanel();

                parametersPanel.setLayout(new GridLayout(10, 1));

                parametersPanel.setBorder(BorderFactory.createTitledBorder("Technical (advanced)"));

                final JLabel cI1 = new JLabel("Compare images (smaller is stricter)");
                final JTextField compareImages1 = new JTextField(Integer.toString(compareImages));
                parametersPanel.add(cI1);
                parametersPanel.add(compareImages1);

                final JLabel cI2 = new JLabel("Compare interval (ms, smaller is stricter)");
                final JTextField compareInterval1 = new JTextField(Integer.toString(compareInterval));
                parametersPanel.add(cI2);
                parametersPanel.add(compareInterval1);

                final JLabel cI3 = new JLabel("Color threshold 1 (bigger is stricter)");
                final JTextField compareThreshold1 = new JTextField(Integer.toString(compareThreshold));
                parametersPanel.add(cI3);
                parametersPanel.add(compareThreshold1);

                final JLabel cI4 = new JLabel("Color threshold 2 (smaller is stricter)");
                final JTextField compareThreshold2 = new JTextField(Integer.toString(fishingThreshold));
                parametersPanel.add(cI4);
                parametersPanel.add(compareThreshold2);

                final JLabel cI5 = new JLabel("Fishing spot color");
                final JTextField fishingSpotColor = new JTextField(Integer.toString(fishingColor));
                parametersPanel.add(cI5);
                parametersPanel.add(fishingSpotColor);

                //

                JPanel buttons = new JPanel();

                buttons.setLayout(new GridLayout(2, 1));

                //

                panel.add(behaviorPanel);
                panel.add(parametersPanel);
                panel.add(buttons);

                final JButton startButton = new JButton("Start");
                final JButton exitButton = new JButton("Exit");

                buttons.add(startButton);
                buttons.add(exitButton);

                frame.add(panel);

                frame.setVisible(true);

                exitButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        mode = null;
                        isSelecting = false;
                        frame.dispose();
                    }
                });

                startButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        isSelecting = false;

                        switch (modeCombo.getSelectedIndex()) {
                            case 0:
                                mode = Mode.NET_FISHING;
                                break;
                            case 1:
                                mode = Mode.BAIT_FISHING;
                                break;
                            case 2:
                                mode = Mode.FLY_FISHING;
                                break;
                            case 3:
                                mode = Mode.CAGE_FISHING;
                                break;
                            case 4:
                                mode = Mode.HARPOON_FISHING;
                                break;
                        }

                        try {
                            fishingIntervalBase = Integer.parseInt(fishingInterval.getText());
                            fishingIntervalRandom = fishingIntervalBase;
                        } catch (Exception e) {
                        }
                        try {
                            compareImages = Integer.parseInt(compareImages1.getText());
                            compareInterval = Integer.parseInt(compareInterval1.getText());
                            compareThreshold = Integer.parseInt(compareThreshold1.getText());
                        } catch (Exception e) {
                        }
                        try {
                            fishingThreshold = Integer.parseInt(compareThreshold2.getText());
                            fishingColor = Integer.parseInt(fishingSpotColor.getText());
                        } catch (Exception e) {
                        }
                        try {
                            mouseSpeed = Integer.parseInt(mouseSpeed1.getText());
                        } catch (Exception e) {
                        }

                        frame.dispose();
                    }

                });
            }
        });

        if (mode == null) {
            return false;
        }

        startTime = System.currentTimeMillis();
        return true;
    }


    @Override
    public void onFinish() {
        login.interrupt();
        run = false;
        log("Dwarfeh's Fisher Ended");
    }

    @Override
    public int loop() {
        try {

            while (isSelecting) {
                sleep(1000);
            }


            switch (state) {

                case LOOKING_FOR_SPOT:

                    count = 0;
                    countTo = fishingIntervalBase + (int)(Math.random() * fishingIntervalRandom);

                    if (checkAndClickSpot()) {
                        state = State.FISHING;
                        return 500;
                    }

                    BufferedImage[] bis = new BufferedImage[compareImages];

                    for (int i = 0; i < compareImages; i++) {
                        bis[i] = copyBufferedImage(Game.getImage());
                        if (i < compareImages - 1) {
                            sleep(compareInterval);
                        }
                    }

                    LinkedList<Point> fishingSpot = new LinkedList<Point>();

                    for (int x = Game.VIEWPORT.x; x < Game.VIEWPORT.x + Game.VIEWPORT.width; x++) {
                        for (int y = Game.VIEWPORT.y; y < Game.VIEWPORT.y + Game.VIEWPORT.height; y++) {
                            boolean add = false;

                            int lastCol = 0;
                            for (BufferedImage bi : bis) {
                                if (lastCol == 0) {
                                    lastCol = bi.getRGB(x, y) & 0xFFFFFF;
                                    continue;
                                }
                                int col = bi.getRGB(x, y) & 0xFFFFFF;
                                if (OCRUtil.getDistanceSquare(lastCol, col) > compareThreshold) {
                                    add = true;
                                    break;
                                }
                                lastCol = col;
                            }

                            if (!add) {
                                continue;
                            }

                            if (OCRUtil.getDistanceSquare(lastCol, fishingColor) < fishingThreshold) {
                                fishingSpot.add(new Point(x, y));
                            }
                        }
                    }

                    LinkedList<Point> visitedPoints = new LinkedList<Point>();

                    p:			for (Point p : fishingSpot) {
                        if (!Game.VIEWPORT.contains(p)) {
                            continue;
                        }

                        for (Point vp : visitedPoints) {
                            if (p.distance(vp) < 50) {
                                continue p;
                            }
                        }

                        Mouse.move(10,p);
                        sleep(250);

                        if (checkAndClickSpot()) {
                            state = State.FISHING;
                            return 500;
                        } else {
                            visitedPoints.add(p);
                        }
                    }

                    break;

                case FISHING:

                    if (ColorUtil.getDistance(Inventory.getSlotAt(27).getCenterColor(), new Color(61, 53, 43)) > 0.05
                            || ColorUtil.getDistance(Game.getColorAt(698, 445), new Color(67, 59, 49)) > 0.05) {
                        state = State.DROPPING;
                        invIndex = 0;
                        break;
                    }

                    if (count++ > countTo) {
                        state = State.LOOKING_FOR_SPOT;
                    }

                    break;

                case DROPPING:

                    if (invIndex >= 28) {
                        state = State.LOOKING_FOR_SPOT;
                        break;
                    }
                    //invIndex = 0 first go around
//                    Mouse.move(10,Inventory.getSlotAt(invIndex).getCenter());

//                    sleep(200);

                    String optionText = OCRUtil.getOptionsText();
//
//                    if (!optionText.contains("Raw")) {
//                        sleep(40 + (int)(Math.random() * 40));
//                        invIndex++;
//                        return 0;
//                    }
//
//                    if (invIndex <= 23) {
//                        Inventory.doAction(invIndex, 2);
//                    } else {
//                        Mouse.clickMouse(Inventory.getSlotAt(invIndex).getCenter().x, Inventory.getSlotAt(invIndex).getCenter().y, false);
//                        sleep(200);
//                        Mouse.clickMouse(Inventory.getSlotAt(invIndex).getCenter().x, Inventory.getSlotAt(invIndex).getCenter().y+20, true);
//                    }
                    //TODO FIND OCR FOR XP GAINED

                    dropAllExcept(0);
                    invIndex = 28;

                    switch (mode) {
                        case NET_FISHING:
                            if (optionText.contains("shrimps")) {
                                shrimpsDropped++;
                            } else if (optionText.contains("anchovies")) {
                                anchoviesDropped++;
                            }
                            break;
                        case BAIT_FISHING:
                            if (optionText.contains("sardine")) {
                                sardineDropped++;
                            } else if (optionText.contains("herring")) {
                                herringDropped++;
                            } else if (optionText.contains("pike")) {
                                pikeDropped++;
                            }
                            break;
                        case FLY_FISHING:
                            if (optionText.contains("trout")) {
                                troutDropped++;
                            } else if (optionText.contains("salmon")) {
                                salmonDropped++;
                            }
                            break;
                        case CAGE_FISHING:
                            if (optionText.contains("lobster")) {
                                lobsterDropped++;
                            }
                            break;
                        case HARPOON_FISHING:
                            if (optionText.contains("tuna")) {
                                tunaDropped++;
                            } else if (optionText.contains("swordfish")) {
                                swordfishDropped++;
                            }
                            break;
                    }

                    invIndex++;
                    if (invIndex < 28) {
                        return 200;
                    } else {
                        invIndex = 0;
                    }

                    state = State.LOOKING_FOR_SPOT;

                    break;
            }
        } catch (Exception e) {
        }

        return 1000;
    }

    private boolean checkAndClickSpot() {
        String optionsText = OCRUtil.getOptionsText();

        switch (mode) {
            case NET_FISHING:
                if (optionsText.contains("Net")) {
                    Mouse.click();
                    return true;
                } else {
                    return false;
                }
            case BAIT_FISHING:
                if (optionsText.contains("Net") || optionsText.contains("Lure")) {
                    Point currentLocation = Mouse.getLocation();
                    Mouse.click(currentLocation, false);
                    Mouse.move(currentLocation.x + (int)(Math.random() * 40) - 20, currentLocation.y + 45 + (int)(Math.random() * 6) - 3);
                    sleep(40 + (int)(Math.random() * 40));
                    Point cl = Mouse.getLocation();
                    Mouse.click(cl, true);
                    sleep(40 + (int)(Math.random() * 40));

                    return true;
                } else {
                    return false;
                }
            case FLY_FISHING:
                if (optionsText.contains("Lure")) {
                    Mouse.click();
                    return true;
                } else {
                    return false;
                }
            case CAGE_FISHING:
                if (optionsText.contains("Cage")) {
                    Mouse.click();
                    state = State.FISHING;
                    return true;
                } else {
                    return false;
                }
            case HARPOON_FISHING:
                if (optionsText.contains("Cage")) {
                    Point currentLocation = Mouse.getLocation();
                    Mouse.click(currentLocation, false);
                    Mouse.move(currentLocation.x + (int)(Math.random() * 40) - 20, currentLocation.y + 45 + (int)(Math.random() * 6) - 3);
                    sleep(40 + (int)(Math.random() * 40));
                    Point cl = Mouse.getLocation();
                    Mouse.click(cl, true);
                    sleep(40 + (int)(Math.random() * 40));

                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }

    private BufferedImage copyBufferedImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
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

    Thread login = new Thread(new Runnable() {
        public void run() {
            while (run) {
                if (findString(loggedOut, null, null).contains("You last logged in")) {
                    Mouse.click(379, 460);
                    sleep(random(600, 800));
                }
                try {
                    sleep(random(500, 800));
                } catch(Throwable e) { }
            }
        }
    });

    @Override
    public Graphics doPaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setStroke(new BasicStroke(6));
        g.setColor(Color.white);
        g.draw3DRect(1, 38, 140, 130, true);
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(0, 0, 0, 70));
        g.fill3DRect(1, 38, 140, 130, true);
        g.setColor(Color.white);

//        g.setFont(new Font("Arial", 0, 9));

        g.drawRect(Mouse.getLocation().x, Mouse.getLocation().y, 10, 10);

        g.drawString("Dwarfeh's Fisher", 10, 50);

        int timeSpan = (int)(System.currentTimeMillis() - startTime) / 1000;

        g.drawString("Running for " + getTime(timeSpan), 10, 70);

        g.drawString("Status: " + state, 10, 90);

        g.drawString("Mode: " + mode, 10, 110);

        int xp = shrimpsDropped * 10 + anchoviesDropped * 40
                + sardineDropped * 20 + herringDropped * 30 + pikeDropped * 60
                + troutDropped * 50 + salmonDropped * 70
                + lobsterDropped * 90
                + tunaDropped * 80 + swordfishDropped * 100;

        if (xp > 0) {
            switch (mode) {
                case NET_FISHING:
                    g.drawString("Fish dropped: " + shrimpsDropped + " shrimps, " + anchoviesDropped + " anchovies", 10, 130);
                    break;
                case BAIT_FISHING:
                    g.drawString("Fish dropped: " + sardineDropped + " sardine, " + herringDropped + " herring, " + pikeDropped + " pike", 10, 130);
                    break;
                case FLY_FISHING:
                    g.drawString("Fish dropped: " + troutDropped + " trout, " + salmonDropped + " salmon", 10, 130);
                    break;
                case CAGE_FISHING:
                    g.drawString("Fish dropped: " + lobsterDropped + (lobsterDropped == 1 ? " lobster" : " lobsters"), 10, 130);
                    break;
                case HARPOON_FISHING:
                    g.drawString("Fish dropped: " + tunaDropped + " tuna, " + swordfishDropped + " swordfish",10, 130);
                    break;
            }

            g.drawString("Experience: " + xp + " [" + (int)((double)xp / timeSpan * 3600) + "/h]", 10, 150);
        }
        return null;
    }

    Rectangle loggedOut = new Rectangle(145, 70, 116, 20);

    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    public boolean areColorsClose(Color color1, Color color2) {
        return areColorsClose(color1, color2, 10);
    }

    /**
     * NOTE THE MORE VARIABLES YOU DEFINE THE FASTER THIS WILL BE AND THE SMALLER THE RECTANGLE THE FASTER IT WILL BE
     * SO TRY NOT TO RUN THIS WITH NO FONT SPECIFIED AND NO COLOR OF THE TEXT SPECIFIED BECAUSE IT DRASTICALLY INCREASES THE AMOUNT
     * OF OPERATIONS IT MUST PERFORM. I AM WORKING ON A HELPER SCRIPT THAT WILL MAKE THIS A LOT EASIER
     */

    /**
     * Find text within box rectangle using specified font
     * @param fontC : color of the text inside of the rectangle, can be left null to search for any color
     * @param rec: rectangle to search for text
     * @param curfont: font type for the letters you will be searching for
     * @return : String of text that was found inside of rectangle
     */
    public String findString(Color fontC, Rectangle rec, FontTypes curfont) {
        return findString(rec, fontC, allfonts.allLetters[curfont.ordinal()]);
    }

    /**
     * Find text within box rectangle using all font types
     * @param rec: rectangle to search for text
     * @param fontC : color of the text inside of the rectangle, can be left null to search for any color
     * @return : String of text that was found inside of rectangle
     */
    private String findString(Rectangle rec, Color fontC) {
        return findString(rec, fontC, null);
    }

    /**
     * Find text within box rectangle
     * @param rec: rectangle to search for text
     * @param fontC : color of the text inside of the rectangle, can be left null to search for any color
     * @param font : font type for the letters you will be searching for, this can be left null to search for all font types
     * @return : String of text that was found inside of rectangle
     */
    private String findString(Rectangle rec, Color fontC, Font[] font) {
        ArrayList<Letter> nums = new ArrayList<Letter>();
        Font[] foundFont = font;

        for(int y = rec.y; y < rec.y + rec.height; y++) {
            M1: for(int x = rec.x; x < rec.x + rec.width; x++) {
                Color c = Game.getColorAt(x, y);
                if(fontC != null && !fontC.equals(c)) continue;
                for(int i = 0; i < allfonts.allLetters.length; i++) {
                    Font[] curFont = allfonts.allLetters[i];
                    if(foundFont != null) {
                        curFont = foundFont;
                        i = allfonts.allLetters.length;
                    }
                    M2:
                    for (Font aCurFont : curFont) {
                        final Rectangle loc = new Rectangle(x - aCurFont.goodpts[0].x, y - aCurFont.goodpts[0].y, aCurFont.letbox.width, aCurFont.letbox.height);
                        if (!rec.contains(loc)) continue;
                        for (int k = 0; k < aCurFont.goodpts.length; k++) {
                            if (!checkColor(Game.getColorAt(loc.x + aCurFont.goodpts[k].x, loc.y + aCurFont.goodpts[k].y), c, 40))
                                continue M2;
                        }
                        for (int k = 0; k < aCurFont.badpts.length; k++) {
                            if (checkColor(Game.getColorAt(loc.x + aCurFont.badpts[k].x, loc.y + aCurFont.badpts[k].y), c, 40))
                                continue M2;
                        }

                        nums.add(new Letter(aCurFont.letter, loc.x, loc.x + aCurFont.letbox.width));
                        foundFont = curFont;
                        fontC = c;
                        continue M1;
                    }
                }
            }
        }
        return sortLetters(nums);
    }

    /**
     * Class used to what letter was found and
     * its start location and ending location
     */
    public class Letter {
        private int x0, x1;
        private char letter;

        public Letter(char letter, int x0, int x1) {
            this.letter = letter;
            this.x0 = x0;
            this.x1 = x1;
        }
    }

    /**
     * Font class used to store information about the each font letter
     *
     */
    public class Font {
        private Point[] goodpts;
        private Point[] badpts;
        private  char letter;
        private Rectangle letbox;

        public Font(ArrayList<Point> goodpoints, ArrayList<Point> badpoints, char letter, Rectangle letbox) {
            goodpts = new Point[goodpoints.size()];
            goodpts = goodpoints.toArray(goodpts);
            badpts = new Point[badpoints.size()];
            badpts = badpoints.toArray(goodpts);
            this.letbox = letbox;
            this.letter = letter;
        }
    }

    /**
     * Enumerated types of all the fonts available to use
     *
     */
    public enum FontTypes {
        BigChars, CharsNPC, FriendChars, LoginChars, SmallChars, StatChars, UpChars, UpCharsEx
    }

    /**
     * Class that stores every single font typea and is used to load all font types
     */
    public class AllFonts {
        private String[] fontnames = {"BigChars", "CharsNPC", "FriendChars", "LoginChars", "SmallChars", "StatChars", "UpChars", "UpCharsEx"};
        private final Font[][] allLetters = new Font[fontnames.length][62];

        public AllFonts() {
            for(int i = 0; i < allLetters.length; i++) {
                allLetters[i] = grabfontset(allLetters[i], fontnames[i]);
            }
        }
    }

    /**
     * Load all images for the current font and store the font information to the Font array
     * @param fontset : font array that information will be stored
     * @param fontname : name of the current font
     * @return : a filled array of font information
     */
    public Font[] grabfontset(Font[] fontset, String fontname) {
        int cnt = 0;
        int i = 0;
        try {
            for(i = 48; i < 58; i++) {
                final BufferedImage img = ImageIO.read(new File(Configuration.Paths.getHomeDirectory() + "\\Cache\\Fonts\\Fonts\\" + fontname + "\\" + i + ".bmp"));
                fontset[cnt++] = grabPoints(img, (char)i);
            }
            for(i = 65; i < 91; i++) {
                final BufferedImage img = ImageIO.read(new File(Configuration.Paths.getHomeDirectory() +"\\Cache\\Fonts\\Fonts\\" + fontname + "\\" + i + ".bmp"));
                fontset[cnt++] = grabPoints(img, (char)i);
            }
            for(i = 97; i < 123; i++) {
                final BufferedImage img = ImageIO.read(new File(Configuration.Paths.getHomeDirectory() +"\\Cache\\Fonts\\Fonts\\" + fontname + "\\" + i + ".bmp"));
                fontset[cnt++] = grabPoints(img, (char)i);
            }
        } catch (IOException e) {
            log("Image reading failed on bitmap number " + i + " font was " + fontname);
        }
        return fontset;
    }

    /**
     * Grab all points in the image that match the given color
     * @param img : image to search through
     * @param letter : letter to search for
     * @return : an array of the points that matched color c
     */
    Font grabPoints(BufferedImage img, char letter) {
        ArrayList<Point> goodpts = new ArrayList<Point>();
        ArrayList<Point> badpts = new ArrayList<Point>();
        for(int y = 0; y < img.getHeight(); y++) {
            for(int x = 0; x < img.getWidth(); x++) {
                final Color c2 = new Color(img.getRGB(x, y));
                if(c2.equals(Color.WHITE)) goodpts.add(new Point(x, y));
                else badpts.add(new Point(x, y));
            }
        }
        return new Font(goodpts, badpts, letter, new Rectangle(img.getWidth(), img.getHeight()));
    }

    private boolean checkColor(final Color c, final Color c2, final int Tol) {
        return (checkColor(c.getRed(), c2.getRed()) + checkColor(c.getGreen(), c2.getGreen()) + checkColor(c.getBlue(), c2.getBlue())) < Tol;
    }

    private int checkColor(final int RGB, final int Val) {
        return Math.abs(RGB - Val);
    }

    AllFonts allfonts = new AllFonts();

    // WE COULD MAYBE REMOVE THIS IF WE ONLY SEARCH ALONG THE MIDDLE OF THE BOX FOR COLORS. THIS WOULD ALSO ALLOW US TO NOT HAVE TO DO ANYWHERE CLOSE
    // TO THE AMOUNT OF TRAVERSALS AND WOULD REMOVE A LOT OF EXTRA RUNTIME
    // THE ONLY DOWN SIDE WOULD BE THE PERSON MUST MAKE SURE HE HAS HIS BOX CENTERED IN A WAY THAT THE 1 Y LINE THEY SPECIFY HITS ALL THE LETTERS
    // THIS COULD ALSO BRING IN THE PROBLEM OF THE PERSON NOT KNOWING WHERE THE TEXT IS ON SCREEN AND WOULD MAKE HIS JOB A LOT MORE TROUBLESOME
    private String sortLetters(ArrayList<Letter> letters) {
        String text = "";
        Letter oldLetter = null;
        while(!letters.isEmpty()) {
            Letter curLetter = new Letter('X', 800, 800);
            for (Letter letter : letters) {
                if (letter.x0 < curLetter.x0) {
                    curLetter = letter;
                }
            }
            if(oldLetter != null && curLetter.x0 - oldLetter.x1 > 1) text += " ";
            oldLetter = curLetter;
            text += curLetter.letter;
            letters.remove(curLetter);
        }
        return text;
    }

    public int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max+1) - min) + min;
    }

    public static void dropAllExcept(int slotNumber) {
        for (int i = 0; i < 28; i++) {
            if (i != slotNumber && !Inventory.getSlotAt(i).isEmpty()) {
                Inventory.doAction(i, i <= 23 ? 2 : 1);
            }
        }
    }
}