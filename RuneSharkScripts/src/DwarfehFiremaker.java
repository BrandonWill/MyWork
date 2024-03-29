import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManager;
import bot.script.ScriptManifest;
import util.Configuration;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;

@ScriptManifest(authors = { "Dwarfeh" }, name = "Dwarfeh's Firemaker", version = 1.3, description = "I MAKEITY THE FIRE",  category = "Firemaking")
public class DwarfehFiremaker extends Script {
    private String STATE;

    private long startTime = 0;

    private Point midScreen = new Point(259, 162);
    private Point middleMiniMap = new Point(627, 86);

    private Color banker = new Color(241, 230, 133);
    private Color tinderbox = new Color(56, 48, 8);
    private Color[] log = { new Color(78, 58, 34), new Color(111, 84, 49), new Color(54, 48, 17),
            new Color(76, 49, 10), new Color(62, 45, 9)}; //0= normal, 1= oak, 2= willow, 3= maple, 4= yew, 5= magic
    //TODO Get a members acc for Magic color
    private Color bankIcon = new Color(238, 210, 36);

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
    private volatile boolean run = true;
    private int startingXP;
    private volatile int gained;
    private boolean checked = false;
    private int missing;
    Rectangle loggedOut = new Rectangle(145, 70, 116, 20);

    //TODO Make this a GUI Option
    private boolean ensureLogBurning;

    @Override
    public boolean onStart() {
        log("Starting up Dwarfeh's FireMaker");
        log("Please post feedback on the thread !");
        log("Remember it only works at Fist of Guthix !");
        STATE = "Starting up...";
        startTime = System.currentTimeMillis();
        toggleXPDisplay();
        findXP.start();
        login.start();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ui = new UserInterface();
                ui.setVisible(true);
            }
        }
        );
        log("Center Color at where maple log should be at slot 2 is: " + Inventory.getSlotAt(1).getCenterColor());
        log("Is it determined your log: " +areColorsClose(Inventory.getSlotAt(1).getCenterColor(), log[logChosen], 5));
        log("Is it a tinderbox: " +areColorsClose(Inventory.getSlotAt(1).getCenterColor(), tinderbox, 5));
        return true;
    }


    @Override
    public void onFinish() {
        findXP.interrupt();
        login.interrupt();
        run = false;
        log("Thanks for using Dwarfeh's FireMaker");
    }

    @Override
    public int loop() {
        try {
            if (mouseSpeed == 0) {
                return 300;
            }
            if (mouseSpeed > 0 && !checked) {
                final boolean[] optionWasChosen = {false};
                if (!areColorsClose(Inventory.getSlotAt(1).getCenterColor(), log[logChosen], 5) && getAmount(log[logChosen]) == 0) {
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JFrame frame = new JFrame();
                            String message = "The log chosen does not match. \n" +
                                    "The color that's supposed to be there is now drawn \n" +
                                    "Would you like to keep this color?";
                            int answer = JOptionPane.showConfirmDialog(frame, message);
                            if (answer == JOptionPane.YES_OPTION) {
                                optionWasChosen[0] = true;
                                log[logChosen] = Inventory.getSlotAt(1).getCenterColor();
                            } else if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CANCEL_OPTION) {
                                optionWasChosen[0] = true;
                                log("No idea what the fuck I'm supposed to do. Stopping");
                                ScriptManager.getCurrent().stopScript();
                            }
                        }
                    }
                    );
                    while (!optionWasChosen[0]) {
                        STATE = "Waiting on option to be chosen";
                        sleep(400);
                    }
                }
                checked = true;
            }

            if (!xpIsThere()) {
                toggleXPDisplay();
            }
            Mouse.setSpeed(random(mouseSpeed - 1, mouseSpeed + 1));
            if (inventoryContainsTinderbox() || !tinderboxLight) {
                if (inventoryContainsLog()) {
                    if (!standingOnFire()) {
                        STATE = "Burning logs";
                        if (logsLit > 0 && logWasLit() || logsLit == 0) {
                            counter = 0;
                            burnLog();
                        }
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
                STATE = "Can't find tinderbox";
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

    private void walkToRight() {
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
            walkToRight();
        }
    }

    private Point BankIconOnMap() {
        return getRandomPoint(bankIcon, 0.005D, 0, 75, middleMiniMap);
    }

    private void openBank() {
        Point bankerPos = pointByColorInBounds(banker, 0.07D, 250, midScreen);
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
        walkToRight();
    }

    private boolean standingOnFire() {
        String OCR = RSText.findString(new Rectangle(2, 438, 166, 22), null, null).replaceAll(" ", "");
        if ("Youcantlihtairehere".equals(OCR) && !ignoreYouCan) {
            ignoreYouCan = true;
            return true;
        }
        ignoreYouCan = false;
        return false;
    }

    private boolean nearBank() {
        return colorIsInBounds(banker, 0.04D, 250);
    }

    private boolean colorIsInBounds(Color color, double tolerance, double maxDist) {
        return colorIsInBounds(color, tolerance, maxDist, midScreen);
    }

    private boolean colorIsInBounds(Color color, double tolerance, double maxDist, Point mid) {
        return pointByColorInBounds(color, tolerance, maxDist, mid) != null;
    }

    private Point pointByColorInBounds(Color color, double tolerance, double maxDist, Point mid) {
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
                } else {
                    break;
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
                    sleep(1750 + lagAdjust, 2500 + lagAdjust);
                    logsLit++;
                }
                break;

            case 1:
                Point tinderboxPos = getSlotWithCenterColor(tinderbox, 5).getCenter();
                if (tinderboxPos != null && nextLog != null) {
                    click(tinderboxPos, random(0, 10), random(0, 10));
                    sleep(500 + lagAdjust, 750 + lagAdjust);
                    click(nextLog, random(0, 10), random(0, 10));
                    sleep(1150 + lagAdjust, 1400 + lagAdjust);
                    logsLit++;
                }
                break;
        }
    }

    int counter = 0;
    public boolean logWasLit() {
        if (logsLit*xpPerLog[logChosen] != gained) {
            STATE = "Looking for a missing log...";
            sleep(1000, 1500);
            if (logsLit*xpPerLog[logChosen] != gained) {
                STATE = "Missing log not found!";
                missing++;
                logsLit =  gained/ (int) xpPerLog[logChosen];
                counter++;
            }
        }
        return counter > 5 || logsLit * xpPerLog[logChosen] == gained;
    }

    Thread findXP = new Thread(new Runnable() {
        public void run() {
            try {
                while (run) {
                    if (startingXP == 0) {
                        String text = RSText.getOptionsText();
                        if (text.contains("xp") && isInteger(text.substring(text.indexOf("xp")+2, text.length()-4).replaceAll(" ", ""))) {
                            log("Start xp would be this: " +(text.substring(text.indexOf("xp")+2, text.length()-4).replaceAll(" ", "")) + "-" +text.substring(text.indexOf("xp")-6, text.indexOf("xp")).replaceAll(" ", "").replaceAll("\\+", "").replaceAll("\\-", ""));
                            startingXP= Integer.parseInt(text.substring(text.indexOf("xp")+2, text.length()-4).replaceAll(" ", "")) - Integer.parseInt(text.substring(text.indexOf("xp")-6, text.indexOf("xp")).replaceAll(" ", "").replaceAll("\\+", "").replaceAll("\\-", ""));
                            log("Setting starting xp as: " +startingXP);
                        }
                    }
                    if (startingXP > 0) {
                        String text = RSText.getOptionsText();
                        if (text.contains("xp") && isInteger(text.substring(text.indexOf("xp")+2, text.length()-4).replaceAll(" ", ""))) {
                            if (Integer.parseInt(text.substring(text.indexOf("xp")+2, text.length()-4).replaceAll(" ", ""))-startingXP > 0) {
                            gained = Integer.parseInt(text.substring(text.indexOf("xp")+2, text.length()-4).replaceAll(" ", ""))-startingXP;
                            }
                        }
                    }
                    try {
                        sleep(random(500, 800));
                    } catch(Throwable ignored) { }
                }
            } catch (Throwable e) {
                String[] a = Arrays.toString(e.getStackTrace()).split(",");
                for (String z : a) {
                    log(z);
                }
            }
        }
    });

    private boolean isInteger(String a) {
        try {
            Integer.parseInt(a);
            return true;
        }  catch(Throwable e) {
            return false;
        }
    }

    public void click(final Point p, final int randX, final int randY) {
        final Point a = new Point(random(p.x - randX, p.x + randX), random(p.y - randY, p.y + randY));
        while (Game.isPointValid(a) && !Mouse.getLocation().equals(a)) {
            Mouse.move(a);
        }
        Mouse.click();
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

    private void toggleXPDisplay() {
        try {
            String text = RSText.getOptionsText();
            if (!text.substring(text.length()-4).contains("-l-i")) {
                throw new Exception();
            }
        } catch (Exception e)  {
            Mouse.move(random(532, 534), random(60, 62));
            sleep(random(600, 800));
            Mouse.click(random(532, 534), random(60, 62));
            sleep(random(600, 800));
        }
    }

    private boolean xpIsThere() {
        String text = RSText.getOptionsText();
        return text.substring(text.length()-4).contains("-l-i");
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

        g.drawString("Dwarfeh's Firemaker version " +DwarfehFiremaker.class.getAnnotation(ScriptManifest.class).version(), 10, 50);

        g.drawString("Running for " + hours +":"+ minutes + ":" + seconds, 10, 70);

        g.drawString("Status: " + STATE, 10, 90);

        g.drawString("Logs Lit: " + logsLit, 10, 110);

        g.drawString("Unaccounted for logs: " +missing, 10, 130);

        g.drawString("xp Gained: " + gained, 10, 150);  //(logsLit * xpPerLog[logChosen])

        g.drawString("Firemaking xp per hour: " + (int) (gained * 3600000D / (System.currentTimeMillis() - startTime)), 10, 170);

        if (mouseSpeed != 0) {
            for (Inventory.Slot a : Inventory.Slot.values()) {
                if (areColorsClose(a.getCenterColor(), tinderbox, 5)) {
                    g.setColor(Color.BLUE);
                    g.drawRect(a.getBounds().x, a.getBounds().y, a.getBounds().width, a.getBounds().height);
                }
                if (areColorsClose(a.getCenterColor(), log[logChosen], 5)) {
                    g.setColor(Color.PINK);
                    g.drawRect(a.getBounds().x, a.getBounds().y, a.getBounds().width, a.getBounds().height);
                }
            }
        }
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

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    setVisible(false);
                    dispose();
                    log("Frame closed. Stopping script");
                    ScriptManager.getCurrent().stopScript();
                }
            });

            lblGENSET = new JLabel("Settings", JLabel.CENTER);
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

            String[] LOGS = { "Normal", "Oak", "Willow", "Maple", "Yew"}; //"Magic"
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

            String[] LIGHTMETHODS = { "Use Tinderbox", "Tinderbox in toolbelt" };
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
            try {
                String filename = Configuration.Paths.getCacheDirectory() + "\\DwarfehFireMaker.txt";
                Scanner in = new Scanner(new BufferedReader(new FileReader(filename)));
                String line;
                String[] opts = {};
                while (in.hasNext()) {
                    line = in.next();
                    if (line.contains(":")) {
                        opts = line.split(":");
                    }
                }
                in.close();
                if (opts.length > 1) {
                    cmbmouseSpeed.setSelectedIndex(Integer.parseInt(opts[0]));
                    cmblagAdjust.setSelectedIndex(Integer.parseInt(opts[1]));
                    cmbLOGS.setSelectedIndex(Integer.parseInt(opts[2]));
                    cmbANTIBAN.setSelectedIndex(Integer.parseInt(opts[3]));
                    cmbLIGHTMETHOD.setSelectedIndex(Integer.parseInt(opts[4]));
                }
            } catch (final Exception e2) {
                log("Error loading settings.  If this is first time running script, ignore.");
            }
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
            saveFile();

            ui.dispose();
        }

        void saveFile() {
            try {
                final BufferedWriter out = new BufferedWriter(new FileWriter(Configuration.Paths.getCacheDirectory() + "\\DwarfehFireMaker.txt"));
                out.write((cmbmouseSpeed.getSelectedIndex())
                        + ":" // 0
                        + (cmblagAdjust.getSelectedIndex())
                        + ":" // 1
                        + (cmbLOGS.getSelectedIndex())
                        + ":" // 2
                        + (cmbANTIBAN.getSelectedIndex())
                        + ":" // 3
                        + (cmbLIGHTMETHOD.getSelectedIndex())); //22
                out.close();
            } catch (final Exception e1) {
                log("Error saving setting.");
            }
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