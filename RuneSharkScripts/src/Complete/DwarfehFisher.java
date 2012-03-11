package Complete;

import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManifest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.Random;

@ScriptManifest(authors = { "Dwarfeh" }, name = "Dwarfeh's Fisher", version = 0.1, description = "A fisher script for net, bait and fly powerfishing.",  category = "Fishing")

public class DwarfehFisher extends Script {
    boolean run = true;
    private long startTime = System.currentTimeMillis();

    private State state = State.FISHING;
    private Mode mode = Mode.FLY_FISHING;

    private int count = 1;
    private int countTo = 0;

    private int fishingIntervalBase = 15, fishingIntervalRandom = 10;
    private int compareImages = 4, compareInterval = 600, compareThreshold = 300;
    private int fishingColor = 8556460, fishingThreshold = 10*10;
    private int mouseSpeed = 2;
    Rectangle loggedOut = new Rectangle(145, 70, 116, 20);

    private volatile boolean isSelecting = true;


    Rectangle xpGained = new Rectangle(415, 5, 90, 35);
    int startingXP;
    int gained;

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
        toggleXPDisplay();
        String text = RSText.findString(xpGained, null, null).replaceAll(" ", "");
        if (text.contains("+")) {
            text = text.substring(4);
        }
        startingXP = text.length() >= 1 ? Integer.parseInt(text) : 0;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final JFrame frame = new JFrame("DwarfehFisher");
                final JPanel panel = new JPanel();



                final JPanel behaviorPanel = new JPanel();

                behaviorPanel.setLayout(new GridLayout(5, 1));

                behaviorPanel.setBorder(BorderFactory.createTitledBorder("Behavior"));

                frame.setSize(450, 350);

                final JComboBox<String> modeCombo = new JComboBox<String>(new String[] { "Net fishing", "Bait Fishing", "Fly Fishing", "Cage fishing", "Harpoon fishing" });
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
                            compareImages = Integer.parseInt(compareImages1.getText());
                            compareInterval = Integer.parseInt(compareInterval1.getText());
                            compareThreshold = Integer.parseInt(compareThreshold1.getText());
                            fishingThreshold = Integer.parseInt(compareThreshold2.getText());
                            fishingColor = Integer.parseInt(fishingSpotColor.getText());
                            mouseSpeed = Integer.parseInt(mouseSpeed1.getText());
                        } catch (Exception ignored) {
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

            gained = Integer.parseInt(RSText.findString(xpGained, null, null).replaceAll(" ", ""))-startingXP;
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
                                if (RSText.getDistanceSquare(lastCol, col) > compareThreshold) {
                                    add = true;
                                    break;
                                }
                                lastCol = col;
                            }

                            if (!add) {
                                continue;
                            }

                            if (RSText.getDistanceSquare(lastCol, fishingColor) < fishingThreshold) {
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

                    if (ColorUtil.getDistance(Inventory.getSlotAt(27).getCenterColor(), new Color(61, 53, 43)) > 0.05 || ColorUtil.getDistance(Game.getColorAt(698, 445), new Color(67, 59, 49)) > 0.05) {
                        state = State.DROPPING;
                        break;
                    }

                    if (count++ > countTo) {
                        state = State.LOOKING_FOR_SPOT;
                    }

                    break;

                case DROPPING:

                    dropAllExcept(0);

                    state = State.LOOKING_FOR_SPOT;

                    break;
            }
        } catch (Exception ignored) {
        }

        return 1000;
    }

    private boolean checkAndClickSpot() {
        String optionsText = RSText.getOptionsText();

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

    Thread login = new Thread(new Runnable() {
        public void run() {
            while (run) {
                if (RSText.findString(loggedOut, null, null).contains("You last logged in")) {
                    Mouse.click(379, 460);
                    sleep(random(600, 800));
                }
                try {
                    sleep(random(500, 800));
                } catch(Throwable ignored) { }
            }
        }
    });

    public void toggleXPDisplay() {
        Mouse.move(random(532, 534), random(60, 62));
        sleep(random(600, 800));
        Mouse.click(random(532, 534), random(60, 62));
        sleep(random(600, 800));
    }

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

        if (timeSpan > 0) {
            g.drawString("Experience: " + gained + " [" + (int)((double)gained / timeSpan * 3600) + "/h]", 10, 150);
//        }
        }
        return null;
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