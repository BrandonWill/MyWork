import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManifest;

import java.awt.*;
import java.util.*;
import javax.swing.*;

@ScriptManifest(authors = { "Dwarfeh" }, name = "Dwarfeh's Firemaker", version = 0.1, description = "I MAKEITY THE FIRE",  category = "Firemaking")
public class DwarfehFiremaker extends Script {
    private String STATE;

    private long startTime = 0;

    private Point midScreen = new Point(259, 162);
    private Point middleMiniMap = new Point(627, 86);

    private Color banker = new Color(241, 230, 133);
    private Color tinderbox = new Color(56, 48, 8);
    private Color[] log = { new Color(131, 105, 58), new Color(154, 124, 87), new Color(89, 83, 59),
            new Color(76, 49, 10), new Color(116, 93, 52), new Color(48, 155, 143) }; //0= normal, 1= oak, 2= willow, 3= maple, 4= yew, 5= magic
    private Color bankIcon = new Color(102, 78, 13);

    //OLD MAPLE 120, 85, 38

    private double[] xpPerLog = {40, 60, 90, 135, 202.5, 303.8};

    private int logChosen;
    private int antibanAmount;
    private int mouseSpeed = 0;
    private int lagAdjust;
    private int logsLit;
    private int failToOpenBank = 0;

    private static final Rectangle WALKRIGHT = new Rectangle(680, 67, 15, 30);
    private static final Rectangle LOGSINBANKSPOT = new Rectangle(40, 95, 25, 25);
    private static final Rectangle TILENORTH = new Rectangle(245, 135, 30, 25);
    private static final Rectangle TILESOUTH = new Rectangle(240, 200, 35, 35);


    private boolean ignoreYouCan;
    private boolean tinderboxLight;

    private UserInterface ui;

    @Override
    public boolean onStart() {
        log("Starting up Dwarfeh's FireMaker");
        log("Please post feedback on the thread !");
        log("Remember it only works at Fist of Guthix !");
        STATE = "Starting up...";
        startTime = System.currentTimeMillis();
        //UI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ui = new UserInterface();
                ui.setVisible(true);
            }
        }
        );
        //end UI
        startTime = System.currentTimeMillis();
        log("tinderbox color: " +Inventory.getSlotAt(0).getCenterColor());
        return true;
    }


    @Override
    public void onFinish() {
        log("Thanks for using Dwarfeh's FireMaker");
    }

    @Override
    public int loop() {
        try {
            if (mouseSpeed == 0){
                return 300;
            }
            Mouse.setSpeed(random(mouseSpeed - 1, mouseSpeed + 1));
            if (inventoryContainsTinderbox() || !tinderboxLight) {
                if (inventoryContainsLog()) {
                    if (!standingOnFire()) {
                        STATE = "Burning logs";
                        burnLog();
                    } else {
                        STATE = "Can't light fire, moving";
                        walkToCloseTile();
                    }
                } else {
                    if (nearBank() || Bank.isOpen()) {
                        if (Bank.isOpen()) {
                            STATE = "Withdrawing logs";
                            withdrawLogs();
                        } else {
                            STATE = "Opening bank";
                            openBank();
                        }
                    } else {
                        STATE = "Walking to banker";
                        walkToBank();
                    }
                }
            } else {
                log("Can not spot a tinderbox in the inventory");
                STATE = "We don't have a tinderbox";
            }
            antiBan();
        }    catch (Exception ignored) {}
        return 1;
    }

    private void walkToCloseTile() {
        switch(random(1, 3)) {
            case 2:
                Mouse.click(randomPointInRect(TILENORTH));
                sleep(1250 + lagAdjust, 1500 + lagAdjust);
                break;

            default:
                Mouse.click(randomPointInRect(TILESOUTH));
                sleep(1250 + lagAdjust, (1500 + lagAdjust));
                break;
        }
    }

    private void antiBan() {
        switch(random(0, antibanAmount)){
            case 1:
                STATE = "Performing Anti-Ban";
                Mouse.moveOffScreen();
            case 2:
                STATE = "Performing Anti-Ban";
                Mouse.moveSlightly();
        }
    }

    private void WalkToRight() {
        sleep(random(500 + lagAdjust, 750 + lagAdjust));
        Mouse.click(randomPointInRect(WALKRIGHT));
        sleep(5000 + lagAdjust, 6500 + lagAdjust);
    }

    private void walkToBank() {
        Point bankIconPos = BankIconOnMap();
        if (bankIconPos != null) {
            sleep(random(500 + lagAdjust, 750 + lagAdjust));
            Mouse.click(bankIconPos);
            for (int i = 0; i < 10; i++) {
                boolean spottedBanker = nearBank();
                if (!spottedBanker) {
                    sleep(random(500 + lagAdjust, 600 + lagAdjust));
                }
            }
        } else {
            WalkToRight();
        }
    }

    private Point BankIconOnMap() {
        return getRandomPoint(bankIcon, 0.005D, 0, 75, middleMiniMap);
    }

    private void openBank() {
        Point bankerPos = PointByColorInBounds(banker, 0.07D, 250, midScreen);
        if (bankerPos != null) {
            Mouse.click(bankerPos, false);
            sleep(random(600 + lagAdjust, 900 + lagAdjust));
            Mouse.click(bankerPos.x + random(-10, 10), bankerPos.y + random(40, 50));
            failToOpenBank++;
            sleep(random(2000 + lagAdjust, 2350 + lagAdjust));
            if (failToOpenBank >= 3) {
                failToOpenBank = 0;
                walkToBank();
            }
        }
    }

    private void withdrawLogs() {
        failToOpenBank = 0;
        Point rClick = randomPointInRect(LOGSINBANKSPOT);
        Mouse.click(rClick, false);
        sleep(600 + lagAdjust, 900 + lagAdjust);
        Mouse.click(rClick.x + random(-10, 10), rClick.y + random(105, 115));
        sleep(600 + lagAdjust, 900 + lagAdjust);
        WalkToRight();
    }

    private boolean standingOnFire() {
        Rectangle yourBounds = new Rectangle(2, 438, 166, 22);
        String OCR = RSText.findString(new Rectangle(2, 438, 166, 22), null, null).replaceAll(" ", "");
        if ("Youcantlihtairehere".equals(OCR) && !ignoreYouCan) {
            ignoreYouCan = true;
            return true;
        }
        ignoreYouCan = false;
        return false;
    }

    private boolean nearBank() {
        return colorIsInBounds(banker, 0.07D, 250);
    }

    private boolean colorIsInBounds(Color color, double tolerance, double maxDist) {
        return ColorIsInBounds(color, tolerance, maxDist, midScreen);
    }
    private boolean ColorIsInBounds(Color color, double tolerance, double maxDist, Point mid) {
        Point point = PointByColorInBounds(color, tolerance, maxDist, mid);
        return point != null;
    }

    private Point PointByColorInBounds(Color color, double tolerance, double maxDist, Point mid) {
        Point closest = null;
        double dist = 0;
        java.util.List<Point> colorLoc = ImageUtil.getPointsWithColor(Game.getImage(), color, tolerance);
        for (Point point : colorLoc) {
            double distTmp = Calc.getDistanceBetween(point, mid);
            if (distTmp < maxDist) {
                if (closest == null) {
                    dist = distTmp;
                    closest = point;
                } else if (distTmp < dist) {
                    closest = point;
                    dist = distTmp;
                }
            }
        }
        return closest;
    }

    private Point getRandomPoint(Color color, double tolerance, int minDist,int maxDist, Point mid) {
        java.util.List<Point> loc = ImageUtil.getPointsWithColor(Game.getImage(), color, tolerance);
        Point randomPoint = null;
        while (randomPoint == null && loc != null) {
            try {
                Point randomPointGuess = loc.get(random(0, loc.size()));
                double dist = Calc.getDistanceBetween(randomPointGuess, mid);
                if (dist > minDist && dist < maxDist) {
                    randomPoint = randomPointGuess;
                }
            } catch (Throwable ignored) { }
        }
        return randomPoint;
    }

    private void burnLog() {
        Point nextLog = getSlotWithCenterColor(log[logChosen], 5).getCenter();
        switch (Boolean.toString(tinderboxLight).equals("true") ? 1 : 0) {
            case 0:
                if (nextLog != null) {
                    Inventory.doAction(getSlotWithCenterColor(log[logChosen], 5), getSlotWithCenterColor(log[logChosen], 5).getIndex() <= 19 ? 2 : getSlotWithCenterColor(log[logChosen], 5).getIndex() <= 23 ? 1 : -1);
                    logsLit++;
                    sleep(1750 + lagAdjust, 2500 + lagAdjust);
                }
                break;

            case 1:
                Point tinderboxPos = getSlotWithCenterColor(tinderbox, 5).getCenter();
                if (tinderboxPos != null && nextLog != null) {
                    Mouse.click(tinderboxPos);
                    sleep(500 + lagAdjust, 750 + lagAdjust);
                    Mouse.click(nextLog);
                    logsLit++;
                    sleep(1150 + lagAdjust, 1400 + lagAdjust);
                }
                break;
        }
    }

    public Inventory.Slot getSlotWithCenterColor(Color color, int tolerance) {
        for (Inventory.Slot a : Inventory.Slot.values()) {
            if (areColorsClose(a.getCenterColor(), color, tolerance)) {
                return a;
            }
        }
        return null;
    }

    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    private boolean inventoryContainsLog() {
        return getAmount(log[logChosen]) != 0;
    }

    private boolean inventoryContainsTinderbox() {
        return getAmount(tinderbox) != 0;
    }

    public int getAmount(Color colors) {
        int amount = 0;
        for (int i = 0; i < 28; i++) {
            if (areColorsClose(Inventory.getSlotAt(i).getCenterColor(), colors, 5)) {
                amount += 1;
            }
        }
        return amount;
    }

    private Point randomPointInRect(Rectangle rect) {
        return new Point(rect.x + random(0, rect.width), rect.y + random(0, rect.height));
    }

    @Override
    public Graphics doPaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setStroke(new BasicStroke(6));
        g.setColor(Color.white);
        g.draw3DRect(1, 38, 140, 150, true);
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(0, 0, 0, 70));
        g.fill3DRect(1, 38, 140, 150, true);
        g.setColor(Color.white);

        Long millis = System.currentTimeMillis() - startTime;
        Long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        Long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        Long seconds = millis / 1000;

        g.setFont(new Font("Arial", 0, 9));

        g.drawRect(Mouse.getLocation().x, Mouse.getLocation().y, 10, 10);

        g.drawString("Dwarfeh's Firemaker", 10, 50);

        g.drawString("Running for " + hours +":"+ minutes + ":" + seconds, 10, 70);

        g.drawString("Status: " + STATE, 10, 90);

        g.drawString("Logs Lit: " + logsLit, 10, 110);

        g.drawString("xp Gained: " + (logsLit * xpPerLog[logChosen]), 10, 130);

        g.drawString("Firemaking xp per hour: " + (int) ((logsLit * xpPerLog[logChosen]) * 3600000D / (System.currentTimeMillis() - startTime)), 10, 150);
        return null;
    }

    private class UserInterface extends JFrame {
        public UserInterface(){
            super("Dwarfeh's FireMaker By Dwarfeh");
            Tools();
        }
        private JLabel lblGENSET;
        private JLabel lblmouseSpeed;
        private JComboBox<String> cmbmouseSpeed;
        private JLabel lbllagAdjust;
        private JComboBox<String> cmblagAdjust;
        private JLabel lblLOGS;
        private JComboBox<String> cmbLOGS;
        private JLabel lblANTIBAN;
        private JComboBox<String> cmbANTIBAN;
        private JLabel lblLIGHTMETHOD;
        private JComboBox<String> cmbLIGHTMETHOD;
        private JButton btnStart;

        public final void Tools(){
            setLocationRelativeTo(null);
            setLayout(null);
            setSize(320, 212);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setResizable(false);

            lblGENSET = new JLabel("General settings", JLabel.CENTER);
            lblGENSET.setForeground(Color.BLUE);
            lblGENSET.setBounds(5, 5, 300, 30);
            lblGENSET.setFont(new Font("Arial", Font.BOLD, 18));
            add(lblGENSET);

            lblmouseSpeed = new JLabel("Mouse speed");
            lblmouseSpeed.setForeground(Color.LIGHT_GRAY);
            lblmouseSpeed.setBounds(5, 40, 300, 14);
            lblmouseSpeed.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblmouseSpeed);

            String[] mouseSpeedS = { "Slow", "Medium", "Fast", "Ultra fast" };
            cmbmouseSpeed = new JComboBox<String>(mouseSpeedS);
            cmbmouseSpeed.setForeground(Color.LIGHT_GRAY);
            cmbmouseSpeed.setSelectedIndex(2);
            cmbmouseSpeed.setBounds(125, 38, 180, 20);
            cmbmouseSpeed.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbmouseSpeed);

            lbllagAdjust = new JLabel("Lag adjust");
            lbllagAdjust.setForeground(Color.LIGHT_GRAY);
            lbllagAdjust.setBounds(5, 62, 300, 14);
            lbllagAdjust.setFont(new Font("Arial", Font.BOLD, 14));
            add(lbllagAdjust);

            String[] lagAdjustS = { "Nothing", "Minimal", "Little", "Medium", "Much" };
            cmblagAdjust = new JComboBox<String>(lagAdjustS);
            cmblagAdjust.setForeground(Color.LIGHT_GRAY);
            cmblagAdjust.setSelectedIndex(1);
            cmblagAdjust.setBounds(125, 60, 180, 20);
            cmblagAdjust.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmblagAdjust);

            lblLOGS = new JLabel("Logs to light");
            lblLOGS.setForeground(Color.LIGHT_GRAY);
            lblLOGS.setBounds(5, 84, 300, 14);
            lblLOGS.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblLOGS);

            String[] LOGS = { "Normal", "Oak", "Willow", "Maple", "Yew", "Magic" };
            cmbLOGS = new JComboBox<String>(LOGS);
            cmbLOGS.setForeground(Color.LIGHT_GRAY);
            cmbLOGS.setSelectedIndex(3);
            cmbLOGS.setBounds(125, 82, 180, 20);
            cmbLOGS.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbLOGS);

            lblANTIBAN = new JLabel("Anti-Ban usage");
            lblANTIBAN.setForeground(Color.LIGHT_GRAY);
            lblANTIBAN.setBounds(5, 106, 300, 14);
            lblANTIBAN.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblANTIBAN);

            String[] antibanAmountS = { "Rarely", "Sometimes", "Medium", "Alot", "Almost always" };
            cmbANTIBAN = new JComboBox<String>(antibanAmountS);
            cmbANTIBAN.setForeground(Color.LIGHT_GRAY);
            cmbANTIBAN.setSelectedIndex(1);
            cmbANTIBAN.setBounds(125, 104, 180, 20);
            cmbANTIBAN.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbANTIBAN);

            lblLIGHTMETHOD = new JLabel("Lighting method");
            lblLIGHTMETHOD.setForeground(Color.LIGHT_GRAY);
            lblLIGHTMETHOD.setBounds(5, 128, 300, 14);
            lblLIGHTMETHOD.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblLIGHTMETHOD);

            String[] LIGHTMETHODS = { "Tinderbox > Log", "Right click Log > Light" };
            cmbLIGHTMETHOD = new JComboBox<String>(LIGHTMETHODS);
            cmbLIGHTMETHOD.setForeground(Color.LIGHT_GRAY);
            cmbLIGHTMETHOD.setSelectedIndex(0);
            cmbLIGHTMETHOD.setBounds(125, 126, 180, 20);
            cmbLIGHTMETHOD.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbLIGHTMETHOD);

            btnStart = new JButton("Start");
            btnStart.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnStartActionPerformed();
                }
            });
            btnStart.setBounds(5, 152, 300, 20);
            add(btnStart);

            setVisible(true);
        }

        public void btnStartActionPerformed() {
            switch(cmbmouseSpeed.getSelectedIndex()) {
                case 0:
                    mouseSpeed =7;
                    break;

                case 1:
                    mouseSpeed = 5;
                    break;

                case 2:
                    mouseSpeed = 4;
                    break;

                case 3:
                    mouseSpeed = 2;
                    break;
            }

            switch(cmblagAdjust.getSelectedIndex()) {
                case 0:
                    lagAdjust = 0;
                    break;

                case 1:
                    lagAdjust = 75;
                    break;

                case 2:
                    lagAdjust = 150;
                    break;

                case 3:
                    lagAdjust = 275;
                    break;

                case 4:
                    lagAdjust = 450;
                    break;
            }

            switch(cmbANTIBAN.getSelectedIndex()) {
                case 0:
                    antibanAmount = 1000;
                    break;

                case 1:
                    antibanAmount = 750;
                    break;

                case 2:
                    antibanAmount = 500;
                    break;

                case 3:
                    antibanAmount = 250;
                    break;

                case 4:
                    antibanAmount = 150;
                    break;
            }

            tinderboxLight = cmbLIGHTMETHOD.getSelectedIndex() == 0;

            logChosen = cmbLOGS.getSelectedIndex();
            ui.dispose();
        }
    }

    public int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max+1) - min) + min;
    }

    public void sleep(int a, int b) {
        sleep(random(a, b));
    }
    
}