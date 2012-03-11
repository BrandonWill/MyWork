import api.methods.*;
import bot.Bot;
import bot.script.Script;
import bot.script.ScriptManifest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

@ScriptManifest(authors = { "Dwarfeh" }, name = "Dwarfeh's Firemaker", version = 0.1, description = "I MAKEITY THE FIRE",  category = "Firemaking")
public class DwarfehFiremaker extends Script implements MouseListener {

    private java.util.List<Point> TINDERBOXLOCS = new LinkedList<Point>();
    private java.util.List<Point> LOGLOCS = new LinkedList<Point>();

    private String STATE;

    private long STARTTIME = 0;

    private Point MIDSCREEN = new Point(259, 162);
    private Point MIDLEMINIMAP = new Point(627, 86);

    private Color BANKER = new Color(241, 230, 133);
    private Color TINDERBOX = new Color(92, 90, 87);
    private Color[] LOG = { new Color(131, 105, 58), new Color(154, 124, 87), new Color(89, 83, 59),
            new Color(76, 49, 10), new Color(116, 93, 52), new Color(48, 155, 143) }; //0= normal, 1= oak, 2= willow, 3= maple, 4= yew, 5= magic
    private Color BANKICON = new Color(102, 78, 13);

    //OLD MAPLE 120, 85, 38

    private double[] XPPERLOG = {40, 60, 90, 135, 202.5, 303.8};

    private int LOGCHOSEN;
    private int ANTIBANAMOUNT;
    private int MOUSESPEED = 0;
    private int LAGADJUST;
    private int LOGSLIT;
    private int FAILTOOPENBANK = 0;

    private Rectangle WALKRIGHT = new Rectangle(680, 67, 15, 30);
    private Rectangle LOGSINBANKSPOT = new Rectangle(40, 95, 25, 25);
    private Rectangle SIXTHROW = new Rectangle(560, 395, 165, 25);
    private Rectangle SEVENTHROW = new Rectangle(560, 430, 165, 25);
    private Rectangle ONOFFSWITCH = new Rectangle(10, 314, 66, 22);
    private Rectangle TILENORTH = new Rectangle(245, 135, 30, 25);
    private Rectangle TILESOUTH = new Rectangle(240, 200, 35, 35);

    private boolean SHOWPAINT = true;
    private boolean IGNOREYOUCAN;
    private boolean TINDERBOXLIGHT;
    private boolean GUIOPENED = true;

    private UserInterface ui;

    @Override
    public boolean onStart() {
        log("Starting up Dwarfeh's FireMaker");
        log("Please post feedback on the thread !");
        log("Remember it only works at Fist of Guthix !");
        log("Color for Maple log: " +Inventory.getSlotAt(27).getCenterColor());
        STATE = "Starting up Dwarfeh's FireMaker";
        STARTTIME = System.currentTimeMillis();
        //UI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ui = new UserInterface();
                ui.setVisible(true);
            }
        }
        );
        Bot.getCurrent().getCanvas().addMouseListener(this);
        //end UI
        STARTTIME = System.currentTimeMillis();
        return true;
    }
    

    @Override
    public void onFinish() {
        Bot.getCurrent().getCanvas().removeMouseListener(this);
        log("> Thanks for using Dwarfeh's FireMaker <");
        log("> Please post the screenshot on the thread! <");
    }

    boolean didDo = false;
    @Override
    public int loop() {
        try {
        if (MOUSESPEED == 0){
            if (!didDo) {
//                Game.clickCompass();
//                Camera.moveUp(random(2000, 2200));
                didDo = true;
            }
            return 300;
        }

        Mouse.setSpeed(random(MOUSESPEED - 1, MOUSESPEED + 1));
        if (InventoryContainsTinderbox()) {
            if (InventoryContainsLog()) {
                if (!StandsOnFire()) {
                    STATE = "Burning logs";
                    if (TINDERBOXLIGHT) {
                        BurnOneLogWithTinderbox();
                    } else {
                        BurnOneLogWithRightClick();
                    }
                } else {
                    STATE = "Can't light fire, moving";
                    WalkToOtherTile();
                }
            } else {
                if (NearBanker() || BankIsOpen()) {
                    if (BankIsOpen()) {
                        STATE = "Withdrawing logs";
                        WithdrawLogs();
                    } else {
                        STATE = "Opening bank";
                        OpenBank();
                    }
                } else {
                    STATE = "Walking to banker";
                    WalkToBank();
                }
            }
        } else {
            log("Can not spot a tinderbox in the inventory");
            STATE = "We don't have a tinderbox";
        }
        AntiBan();
        }    catch (Exception e) {}
        return 1;
    }

    private void WalkToOtherTile() {
        if (random(1, 3) == 2) {
            Mouse.click(RandomPointInRect(TILENORTH));
            sleep(1250 + LAGADJUST, 1500 + LAGADJUST);
        } else {
            Mouse.click(RandomPointInRect(TILESOUTH));
            sleep(1250 + LAGADJUST, (1500 + LAGADJUST));
        }
    }

    private void AntiBan() {
        switch(random(0, ANTIBANAMOUNT)){
            case 1:
                STATE = "Performing Anti-Ban";
                Mouse.moveOffScreen();
            case 2:
                STATE = "Performing Anti-Ban";
                Mouse.moveSlightly();
        }
    }

    private void WalkToRight() {
        sleep(random(500 + LAGADJUST, 750 + LAGADJUST));
        Mouse.click(RandomPointInRect(WALKRIGHT));
        sleep(5000 + LAGADJUST, 6500 + LAGADJUST);
    }

    private void WalkToBank() {
        Point BANKICONPOS = BankIconOnMap();
        if (BANKICONPOS != null) {
            sleep(random(500 + LAGADJUST, 750 + LAGADJUST));
            Mouse.click(BANKICONPOS);
            for (int i = 0; i < 10; i++) {
                boolean SPOTTEDBANKER = NearBanker();
                if (!SPOTTEDBANKER) {
                    sleep(random(500 + LAGADJUST, 600 + LAGADJUST));
                }
            }
        } else {
            WalkToRight();
        }
    }

    private Point BankIconOnMap() {
        return GetRandomPoint(BANKICON, 0.005D, 0, 75, MIDLEMINIMAP);
    }

    private boolean BankIsOpen() {
        return ColorIsInBounds(LOG[LOGCHOSEN], 0.03D, 20, new Point(52, 107));
    }

    private void OpenBank() {
        Point BANKERPOS = PointByColorInBounds(BANKER, 0.07D, 250, MIDSCREEN);
        if (BANKERPOS != null) {
            RightClick(BANKERPOS);
            sleep(random(600 + LAGADJUST, 900 + LAGADJUST));
            Mouse.click(BANKERPOS.x + random(-10, 10), BANKERPOS.y + random(40, 50));
            FAILTOOPENBANK++;
            sleep(random(2000 + LAGADJUST, 2350 + LAGADJUST));
            if (FAILTOOPENBANK >= 3) {
                FAILTOOPENBANK = 0;
                WalkToBank();
            }
        }
    }

    private void WithdrawLogs() {
        FAILTOOPENBANK = 0;
        Point RCLICK = RandomPointInRect(LOGSINBANKSPOT);
        RightClick(RCLICK);
        sleep(600 + LAGADJUST, 900 + LAGADJUST);
        Mouse.click(RCLICK.x + random(-10, 10), RCLICK.y + random(105, 115));
        sleep(600 + LAGADJUST, 900 + LAGADJUST);
        WalkToRight();
    }

    private boolean StandsOnFire() {
        Rectangle YOUBOUNDS = new Rectangle(8, 442, 50, 12);
        String OCR = findNumberString(YOUBOUNDS);
        if ("Yocuca".equals(OCR) && !IGNOREYOUCAN) {
            IGNOREYOUCAN = true;
            return true;
        }
        IGNOREYOUCAN = false;
        return false;
    }

    private boolean NearBanker() {
        return ColorIsInBounds(BANKER, 0.07D, 250);
    }

    private boolean ColorIsInBounds(Color COLOR, double TOLERANCE, double MAXDIST) {
        return ColorIsInBounds(COLOR, TOLERANCE, MAXDIST, MIDSCREEN);
    }
    private boolean ColorIsInBounds(Color COLOR, double TOLERANCE, double MAXDIST, Point MID) {
        Point POINT = PointByColorInBounds(COLOR, TOLERANCE, MAXDIST, MID);
        return POINT != null;
    }

    private Point PointByColorInBounds(Color COLOR, double TOLERANCE, double MAXDIST, Point MID) {
        Point NEAREST = null;
        double DIST = 0;
        java.util.List<Point> COLORLOCS = ImageUtil.getPointsWithColor(Game.getImage(), COLOR, TOLERANCE);
        for (Point POINT : COLORLOCS) {
            double distTmp = getDistanceBetween(POINT, MID);
            if (distTmp < MAXDIST) {
                if (NEAREST == null) {
                    DIST = distTmp;
                    NEAREST = POINT;
                } else if (distTmp < DIST) {
                    NEAREST = POINT;
                    DIST = distTmp;
                }
            }
        }
        return NEAREST;
    }

    private Point GetRandomPoint(Color COLOR, double TOLERANCE, int MINDIST,int MAXDIST, Point MID) {
        java.util.List<Point> LOCS = ImageUtil.getPointsWithColor(Game.getImage(), COLOR, TOLERANCE);
        Point RandomPoint = null;
        while (RandomPoint == null && LOCS != null) {
            try {
                Point RandomPointGuess = LOCS.get(random(0, LOCS.size()));
                double DIST = getDistanceBetween(RandomPointGuess, MID);
                if (DIST > MINDIST && DIST < MAXDIST) {
                    RandomPoint = RandomPointGuess;
                }
            } catch (IllegalArgumentException ex) {
                return null;
            } catch (IndexOutOfBoundsException ex) {
                return null;
            }
        }
        return RandomPoint;
    }

    private void BurnOneLogWithTinderbox() {
        Point TINDERBOXPOS = TinderboxPos();
        Point NEXTLOG = getSlotWithCenterColor(LOG[LOGCHOSEN], 5).getCenter();
        if (TINDERBOXPOS != null && NEXTLOG != null) {
            Mouse.click(TINDERBOXPOS);
            sleep(500 + LAGADJUST, 750 + LAGADJUST);
            Mouse.click(NEXTLOG);
            LOGSLIT++;
            sleep(1150 + LAGADJUST, 1400 + LAGADJUST);
        }
    }

    private void BurnOneLogWithRightClick() {
        Point NEXTLOG = getSlotWithCenterColor(LOG[LOGCHOSEN], 5).getCenter();
        if (NEXTLOG != null) {
            int YCHANGE;
            if (PointInRect(NEXTLOG, SIXTHROW)) {
                YCHANGE = 30;
            } else if (PointInRect(NEXTLOG, SEVENTHROW)) {
                YCHANGE = -15;
            } else {
                YCHANGE = 45;
            }
            RightClick(NEXTLOG);
            sleep(500 + LAGADJUST, 750 + LAGADJUST);
            Mouse.click(NEXTLOG.x + random(-10, 10), NEXTLOG.y + random(YCHANGE - 3, YCHANGE + 3));
            LOGSLIT++;
            sleep(1250 + LAGADJUST, 1500 + LAGADJUST);
        }
    }

//    private Point LogPos() {
//        LOGLOCS = ImageUtil.getPointsWithColor(Game.getImage(), LOG[LOGCHOSEN], 0.02D);
//        for (Point POINT : LOGLOCS) {
//            for (int CURRENT = 0; CURRENT < 28; CURRENT++) {
//                if (PointInRect(POINT, Inventory.getSlotAt(CURRENT).getBounds())) {
//                    return new Point(Inventory.getSlotAt(CURRENT).getCenter().x + random(-3, 3),
//                            Inventory.getSlotAt(CURRENT).getCenter().y + random(-3, 3));
//                }
//            }
//        }
//        return null;
//    }

    public Inventory.Slot getSlotWithCenterColor(Color color, int tolerance) {
        for (Inventory.Slot a : Inventory.Slot.values()) {
            if (areColorsClose(a.getCenterColor(), color, 5)) {
                return a;
            }
        }
        return null;
    }    
    
    private Point LogPos() {
        for (Inventory.Slot a : Inventory.Slot.values()) {
            if (areColorsClose(a.getCenterColor(), LOG[LOGCHOSEN], 3)) {
                return new Point(a.getCenter().x + random(-3, 3), a.getCenter().y + random(-3, 3));
            }
        }
        return null;
    }

    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    private Point TinderboxPos() {
        TINDERBOXLOCS = ImageUtil.getPointsWithColor(Game.getImage(), TINDERBOX, 0.015D);
        for (Point POINT : TINDERBOXLOCS) {
            for (int CURRENT = 0; CURRENT < 28; CURRENT++) {
                if (PointInRect(POINT, Inventory.getSlotAt(CURRENT).getBounds())) {
                    return new Point(Inventory.getSlotAt(CURRENT).getCenter().x + random(-5, 5),
                            Inventory.getSlotAt(CURRENT).getCenter().y + random(-5, 5));
                }
            }
        }
        return null;
    }

    private boolean InventoryContainsLog() {
        return ItemCountInInventory(LOG[LOGCHOSEN], 0.01D) != 0;
    }

    private boolean InventoryContainsTinderbox() {
        return ItemCountInInventory(TINDERBOX, 0.015D) != 0;
    }

    private int ItemCountInInventory(Color COLOR, double TOLERANCE) {
        boolean[] CLICKEDINVSPOT = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
                , false, false, false, false, false, false, false, false, false, false, false, false, false };
        int COUNT = 0;
        java.util.List<Point> PLANKLOCS = ImageUtil.getPointsWithColor(Game.getImage(), COLOR, TOLERANCE);
        for (Point POINT : PLANKLOCS) {
            for (int CURRENT = 0; CURRENT < 28; CURRENT++) {
                if (PointInRect(POINT, Inventory.getSlotAt(CURRENT).getBounds()) && !CLICKEDINVSPOT[CURRENT]) {
                    COUNT ++;
                    CLICKEDINVSPOT[CURRENT] = true;
                }
            }
        }
        return COUNT;
    }

    private boolean PointInRect(Point POINT, Rectangle RECT) {
        return POINT.x >= RECT.x && POINT.x <= (RECT.x + RECT.width) && POINT.y >= RECT.y && POINT.y <= (RECT.y + RECT.height);
    }

    private Point RandomPointInRect(Rectangle RECT) {
        return new Point(RECT.x + random(0, RECT.width), RECT.y + random(0, RECT.height));
    }

    public static void RightClick(Point POINT) {
        if (!Game.isPointValid(POINT.x, POINT.y)) {
            return;
        }
        if (!Mouse.getLocation().equals(new Point(POINT.x, POINT.y))) {
            Mouse.move(POINT.x, POINT.y);
        }
        Mouse.click(POINT.x, POINT.y, false);
    }

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    private final Color color1 = new Color(238, 227, 197);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(102, 102, 102);
    private final BasicStroke stroke1 = new BasicStroke(1);
    private final Font font1 = new Font("Calibri", 1, 24);
    private final Font font2 = new Font("Calibri", 1, 16);
    private final Image img1 = getImage("http://i.imgur.com/Pclgw.png");
    private final Image ON = getImage("http://i.imgur.com/SO5VA.png");
    private final Image OFF = getImage("http://i.imgur.com/uDXR6.png");


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

        g.setFont(new Font("Arial", 0, 9));

        g.drawRect(Mouse.getLocation().x, Mouse.getLocation().y, 10, 10);

        g.drawString("Dwarfeh's Firemaker", 10, 50);

        g.drawString("Running for " + SortTime(System.currentTimeMillis() - STARTTIME), 10, 70);

        g.drawString("Status: " + STATE, 10, 90);

        g.drawString("Logs Lit: " + LOGSLIT, 10, 110);

        g.drawString("XP Gained: " + RoundToK(LOGSLIT * XPPERLOG[LOGCHOSEN]) + "K", 10, 130);

        g.drawString("Firemaking XP per hour: " + RoundToK(XpPerHour(LOGSLIT * XPPERLOG[LOGCHOSEN], STARTTIME)) + "K", 10, 150);
////        Graphics2D g = (Graphics2D)g1;
//        int FIRSTY = 390;
//        int FIRSTX = 20;
//        if (SHOWPAINT) {
//            g.setColor(color1);
//            g.fillRoundRect(10, 348, 483, 123, 16, 16);
//            g.setColor(color2);
//            g.setStroke(stroke1);
//            g.drawRoundRect(10, 348, 483, 123, 16, 16);
//            g.setFont(font1);
//            g.setColor(color3);
//            g.drawString("Dwarfeh's FireMaker", 146, 372);
//            g.setColor(color2);
//            g.drawString("Dwarfeh's FireMaker", 145, 371);
//            g.drawLine(23, 361, 130, 361);
//            g.drawLine(400, 361, 481, 361);
//            g.setFont(font2);
//            g.drawString("Time running: " + SortTime(System.currentTimeMillis() - STARTTIME), FIRSTX, FIRSTY);
//            g.drawString("Current state: " + STATE, FIRSTX, FIRSTY + 18);
//            g.drawString("Logs lit: " + LOGSLIT , FIRSTX, FIRSTY + 36);
//            g.drawString("Firemaking XP gained: " + RoundToK(LOGSLIT * XPPERLOG[LOGCHOSEN]) + "K", FIRSTX, FIRSTY + 54);
//            g.drawString("Firemaking XP per hour: " + RoundToK(XpPerHour(LOGSLIT * XPPERLOG[LOGCHOSEN], STARTTIME)) + "K", FIRSTX, FIRSTY + 72);
//            g.drawImage(img1, 380, 280, null);
//            g.setColor(color3);
//            g.drawString("By Dwarfeh ", 328, 386);
//            g.setColor(color2);
//            g.drawString("By Dwarfeh ", 327, 385);
//            g.drawImage(ON, ONOFFSWITCH.x, ONOFFSWITCH.y, null);
//
//            for (Point POINT : TINDERBOXLOCS) {
//                for (int CURRENT = 0; CURRENT < 28; CURRENT++) {
//                    Rectangle INVBOUNDS = Inventory.getSlotAt(CURRENT).getBounds();
//                    if (PointInRect(POINT, INVBOUNDS)) {
//                        g.setColor(TINDERBOX);
//                        g.drawRect(INVBOUNDS.x, INVBOUNDS.y, INVBOUNDS.width, INVBOUNDS.height);
//                    }
//                }
//            }
//            for (Point POINT : LOGLOCS) {
//                for (int CURRENT = 0; CURRENT < 28; CURRENT++) {
//                    Rectangle INVBOUNDS = Inventory.getSlotAt(CURRENT).getBounds();
//                    if (PointInRect(POINT, INVBOUNDS)) {
//                        g.setColor(LOG[LOGCHOSEN]);
//                        g.drawRect(INVBOUNDS.x, INVBOUNDS.y, INVBOUNDS.width, INVBOUNDS.height);
//                    }
//                }
//            }
//        } else {
//            g.drawImage(OFF, ONOFFSWITCH.x, ONOFFSWITCH.y, null);
//        }
        return null;
    }

    private double RoundToK(Double ROUND) {
        return (Math.round(ROUND * 1000) / 1000) / 1000;
    }

    private double XpPerHour(double XP, long START) {
        if (XP == 0) {
            return 0;
        }
        return (int)(XP / ((System.currentTimeMillis() - START) / 1000L) * 3600.0D);
    }

    public static String SortTime(long millis){
        //Returns the current time
        long time = millis / 1000;
        String seconds = Integer.toString((int) (time % 60));
        String minutes = Integer.toString((int) ((time % 3600) / 60));
        String hours = Integer.toString((int) (time / 3600));
        for (int i = 0; i < 2; i++){
            if (seconds.length() < 2){
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2){
                minutes = "0" + minutes;
            }
            if (hours.length() < 2){
                hours = "0" + hours;
            }
        }
        return hours + "h " + minutes + "m " + seconds + "s";
    }

    public void mouseClicked(MouseEvent e) {
        if (PointInRect(e.getPoint(), ONOFFSWITCH)) {
            SHOWPAINT = !SHOWPAINT;
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    //OCR
    public class NUM {
        private Point[] points;
        private  String num;


        public NUM(Point[] points, String number) {
            this.points = points;
            this.num = number;
        }
    }

    public class NUMBERS {
        private int x;
        private String num;

        public NUMBERS(String num, int x) {
            this.x = x;
            this.num = num;
        }
    }

    ArrayList<NUMBERS> nums = new ArrayList<NUMBERS>();
    private Point[] Y = {new Point(0, 0), new Point(4, 0), new Point(0, 1), new Point(4, 1), new Point(0, 2), new Point(4, 2), new Point(1, 3), new Point(3, 3), new Point(2, 4),
            new Point(2, 4), new Point(2, 5), new Point(2, 6), new Point(2, 7), new Point(2, 8), new Point(2, 9)};

    private Point[] o = {new Point(1, 0), new Point(2, 0), new Point(3, 0), new Point(0, 1), new Point(4, 1), new Point(0, 2), new Point(4, 2), new Point(0, 3), new Point(4, 3),
            new Point(0, 4), new Point(4, 4), new Point(1, 5), new Point(2, 5), new Point(3, 5)};

    private Point[] u = {new Point(0, 0), new Point(4, 0), new Point(0, 1), new Point(4, 1), new Point(0, 2), new Point(4, 2), new Point(0, 3), new Point(4, 3), new Point(0, 4),
            new Point(4, 4), new Point(1, 5), new Point(2, 5), new Point(3, 5)};

    private Point[] c = {new Point(1, 0), new Point(2, 0), new Point(3, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3), new Point(0, 3), new Point(0, 4), new Point(1, 5),
            new Point(2, 5), new Point(3, 5)};

    private Point[] a = {new Point(1, 0), new Point(2, 0), new Point(3, 0), new Point(0, 1), new Point(4, 1), new Point(1, 2), new Point(2, 2), new Point(3, 2), new Point(4, 2),
            new Point(0, 3), new Point(4, 3), new Point(0, 4), new Point(4, 4), new Point(1, 5), new Point(2, 5), new Point(3, 5), new Point(4, 5)};

    private Point[] n = {new Point(0, 0), new Point(2, 0), new Point(3, 0), new Point(0, 1), new Point(1, 1), new Point(4, 1), new Point(0, 2), new Point(4, 2), new Point(0, 3),
            new Point(4, 3), new Point(0, 4), new Point(4, 4), new Point(4, 4), new Point(0, 5), new Point(5, 5)};
    private NUM[] allNumbers = {new NUM(Y, "Y"), new NUM(o, "o"), new NUM(u, "u"), new NUM(c, "c"), new NUM(a, "a"), new NUM(n, "n")};

    private final Color BLACK = new Color(0, 0, 0);

    void findNumber(NUM number, ArrayList<NUMBERS> numbF, Rectangle rec) {

        // TRAVERSE GAME SCREEN
        for(int y = rec.y; y < rec.y + rec.height; y++) {
            for(int x = rec.x; x < rec.x + rec.width; x++) {
                // FIND POINT WITH COLOR THAT MATCHES TEXT COLOR
                if(colorsMatch(Game.getColorAt(x, y), BLACK)) {
                    // MOVE POINT TO STARTING LOCATION OF NUMBER TO CHECK
                    final Point loc = new Point(x - number.points[0].x, y - number.points[0].y);
                    boolean found = true;
                    for(int i = 0; i < number.points.length; i++) {
                        if(!colorsMatch(Game.getColorAt(loc.x + number.points[i].x, loc.y + number.points[i].y), BLACK)) {
                            found = false;
                            break;
                        }
                    }
                    if(found) {
                        numbF.add(new NUMBERS(number.num, loc.x));
                    }
                }
            }
        }
    }
    String findNumberString(Rectangle rec) {
        nums.clear();
        for (NUM allNumber : allNumbers) {
            findNumber(allNumber, nums, rec);
        }
        return sortNumbers();
    }

    private String sortNumbers() {
        String num = "";
        while(!nums.isEmpty()) {
            NUMBERS curNum = new NUMBERS("X", 800);
            for (NUMBERS num1 : nums) {
                if (num1.x < curNum.x) {
                    curNum = num1;
                }
            }
            num += curNum.num;
            nums.remove(curNum);
        }
        return num;
    }

    boolean colorsMatch(final Color origC, final Color... comparC) {
        for (Color color : comparC) {
            if(origC.equals(color)) return true;
        }
        return false;
    }
    //END-OCR


    private class UserInterface extends JFrame {
        public UserInterface(){
            super("Dwarfeh's FireMaker By Dwarfeh");
            Tools();
        }
        private JLabel lblGENSET;
        private JLabel lblMOUSESPEED;
        private JComboBox<String> cmbMOUSESPEED;
        private JLabel lblLAGADJUST;
        private JComboBox<String> cmbLAGADJUST;
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
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            lblGENSET = new JLabel("General settings", JLabel.CENTER);
            lblGENSET.setForeground(Color.BLUE);
            lblGENSET.setBounds(5, 5, 300, 30);
            lblGENSET.setFont(new Font("Arial", Font.BOLD, 18));
            add(lblGENSET);

            lblMOUSESPEED = new JLabel("Mouse speed");
            lblMOUSESPEED.setForeground(Color.BLACK);
            lblMOUSESPEED.setBounds(5, 40, 300, 14);
            lblMOUSESPEED.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblMOUSESPEED);

            String[] MOUSESPEEDS = { "Slow", "Medium", "Fast", "Ultra fast" };
            cmbMOUSESPEED = new JComboBox<String>(MOUSESPEEDS);
            cmbMOUSESPEED.setForeground(Color.BLACK);
            cmbMOUSESPEED.setSelectedIndex(2);
            cmbMOUSESPEED.setBounds(125, 38, 180, 20);
            cmbMOUSESPEED.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbMOUSESPEED);

            lblLAGADJUST = new JLabel("Lag adjust");
            lblLAGADJUST.setForeground(Color.BLACK);
            lblLAGADJUST.setBounds(5, 62, 300, 14);
            lblLAGADJUST.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblLAGADJUST);

            String[] LAGADJUSTS = { "Nothing", "Minimal", "Little", "Medium", "Much" };
            cmbLAGADJUST = new JComboBox<String>(LAGADJUSTS);
            cmbLAGADJUST.setForeground(Color.BLACK);
            cmbLAGADJUST.setSelectedIndex(1);
            cmbLAGADJUST.setBounds(125, 60, 180, 20);
            cmbLAGADJUST.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbLAGADJUST);

            lblLOGS = new JLabel("Logs to light");
            lblLOGS.setForeground(Color.BLACK);
            lblLOGS.setBounds(5, 84, 300, 14);
            lblLOGS.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblLOGS);

            String[] LOGS = { "Normal", "Oak", "Willow", "Maple", "Yew", "Magic" };
            cmbLOGS = new JComboBox<String>(LOGS);
            cmbLOGS.setForeground(Color.BLACK);
            cmbLOGS.setSelectedIndex(3);
            cmbLOGS.setBounds(125, 82, 180, 20);
            cmbLOGS.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbLOGS);

            lblANTIBAN = new JLabel("Anti-Ban usage");
            lblANTIBAN.setForeground(Color.BLACK);
            lblANTIBAN.setBounds(5, 106, 300, 14);
            lblANTIBAN.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblANTIBAN);

            String[] ANTIBANAMOUNTS = { "Rarely", "Sometimes", "Medium", "Alot", "Almost always" };
            cmbANTIBAN = new JComboBox<String>(ANTIBANAMOUNTS);
            cmbANTIBAN.setForeground(Color.BLACK);
            cmbANTIBAN.setSelectedIndex(1);
            cmbANTIBAN.setBounds(125, 104, 180, 20);
            cmbANTIBAN.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbANTIBAN);

            lblLIGHTMETHOD = new JLabel("Lighting method");
            lblLIGHTMETHOD.setForeground(Color.BLACK);
            lblLIGHTMETHOD.setBounds(5, 128, 300, 14);
            lblLIGHTMETHOD.setFont(new Font("Arial", Font.BOLD, 14));
            add(lblLIGHTMETHOD);

            String[] LIGHTMETHODS = { "Tinderbox > Log", "Right click Log > Light" };
            cmbLIGHTMETHOD = new JComboBox<String>(LIGHTMETHODS);
            cmbLIGHTMETHOD.setForeground(Color.BLACK);
            cmbLIGHTMETHOD.setSelectedIndex(0);
            cmbLIGHTMETHOD.setBounds(125, 126, 180, 20);
            cmbLIGHTMETHOD.setFont(new Font("Arial", Font.PLAIN, 14));
            add(cmbLIGHTMETHOD);

            btnStart = new JButton("Start");
            btnStart.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnStartActionPerformed(evt);
                }
            });
            btnStart.setBounds(5, 152, 300, 20);
            add(btnStart);

            setVisible(true);
        }

        public void btnStartActionPerformed(ActionEvent e) {
            if (cmbMOUSESPEED.getSelectedIndex() == 0) {
                MOUSESPEED = 7;
            } else if (cmbMOUSESPEED.getSelectedIndex() == 1) {
                MOUSESPEED = 5;
            } else if (cmbMOUSESPEED.getSelectedIndex() == 2) {
                MOUSESPEED = 4;
            } else if (cmbMOUSESPEED.getSelectedIndex() == 3) {
                MOUSESPEED = 2;
            }

            if (cmbLAGADJUST.getSelectedIndex() == 0) {
                LAGADJUST = 0;
            } else if (cmbLAGADJUST.getSelectedIndex() == 1) {
                LAGADJUST = 75;
            } else if (cmbLAGADJUST.getSelectedIndex() == 2) {
                LAGADJUST = 150;
            } else if (cmbLAGADJUST.getSelectedIndex() == 3) {
                LAGADJUST = 275;
            } else if (cmbLAGADJUST.getSelectedIndex() == 4) {
                LAGADJUST = 450;
            }

            if (cmbANTIBAN.getSelectedIndex() == 0) {
                ANTIBANAMOUNT = 1000;
            } else if (cmbANTIBAN.getSelectedIndex() == 1) {
                ANTIBANAMOUNT = 750;
            } else if (cmbANTIBAN.getSelectedIndex() == 2) {
                ANTIBANAMOUNT = 500;
            } else if (cmbANTIBAN.getSelectedIndex() == 3) {
                ANTIBANAMOUNT = 250;
            } else if (cmbANTIBAN.getSelectedIndex() == 4) {
                ANTIBANAMOUNT = 150;
            }

            if (cmbLIGHTMETHOD.getSelectedIndex() == 0) {
                TINDERBOXLIGHT = true;
            } else if (cmbLIGHTMETHOD.getSelectedIndex() == 1) {
                TINDERBOXLIGHT = false;
            }
            LOGCHOSEN = cmbLOGS.getSelectedIndex();
            GUIOPENED = false;
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

    public static int getDistanceBetween(final Point p1, final Point p2) {
        if (p1 == null || p2 == null) {
            return -1;
        }
        final int xDiff = p2.x - p1.x;
        final int yDiff = p2.y - p1.y;
        return (int) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}