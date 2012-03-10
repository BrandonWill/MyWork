import api.methods.*;
import bot.script.Script;
import bot.script.ScriptManifest;

import java.awt.*;
import java.awt.image.BufferedImage;

@ScriptManifest(authors = { "Dwarfeh" }, category = "LOLOL", name = "LOL", description = "4 DUH LULZ", version = 1.0)
public class TestBankSquare extends Script {
    Color bankerColor = new Color(111, 107, 106);
    public boolean run = true;
    Point a[];
    boolean atBank;
    @Override
    public boolean onStart() {
//        if (!nearBanker()) {
//            bankerColor = new Color(114, 110, 109);
//        }
//        if (!nearBanker()) {
//            bankerColor = new Color(113, 109, 108);
//        }
//        if (!nearBanker()) {
//            bankerColor = new Color(112, 108, 107);
//        }
        if (!nearBanker()) {
            bankerColor = new Color(111, 107, 106);
        }
        if (!nearBanker()) {
            bankerColor = new Color(110, 106, 105);
        }
        if (!nearBanker()) {
            bankerColor = new Color(109, 105, 104);
        }
        if (!nearBanker()) {
            bankerColor = new Color(108, 104, 103);
        }
        if (!nearBanker()) {
            bankerColor = new Color(107, 103, 102);
        }
        if (!nearBanker()) {
            bankerColor = new Color(106, 102, 101);
        }
        if (!nearBanker()) {
            bankerColor = new Color(104, 101, 100);
        }

        if (nearBanker()) {
//            Point[] bc = ColorUtil.findAllColor(bankerColor);
//            Point toClick = null;
//            for (Point a: bc) {
//                if (a != null) {
//                 toClick = a;
//                }
//            }
//            Mouse.moveMouse(toClick.x, toClick.y);
//            String optionText = RSText.getOptionsText();
//            atBank = optionText.contains("Bank");
//            if (atBank)
            log("NEAR BANKER WITH COLOR: " +bankerColor);
        }
        log("Banking color: " +bankerColor);
        return true;
    }

    @Override
    public void onFinish() {

//        log("DONE LOLOL");
    }


    Rectangle bankers = new Rectangle(6, 92, 195, 231);

    public boolean closeToBankers() {
        Point c = ColorUtil.findColorWithinTolerance(bankerColor, 5, bankers);
        if (c != null) {
            Mouse.move(c.x, c.y);
            String optionText = RSText.getOptionsText();
            if (optionText.contains("Bank")) {
                Mouse.click(c);
                sleep(500);
            } else {
                c = null;
            }
        }
        if (c == null) {
            caseNumb++;
            lookForGrayBanker();
            sleep(500);
            closeToBankers();
        }
        return c != null;
    }


    @Override
    public int loop() {
        closeToBankers();
        log("close to bankers: " +closeToBankers());
        return 300;
    }

    int caseNumb = 0;
    void lookForGrayBanker() {
        switch (caseNumb) {
            case 0:
                bankerColor = new Color(114, 110, 109);
                break;

            case 1:
                bankerColor = new Color(113, 109, 108);
                break;

            case 2:
                bankerColor = new Color(112, 108, 107);
                break;

            case 3:
                bankerColor = new Color(111, 107, 106);
                break;

            case 4:
                bankerColor = new Color(110, 106, 105);
                break;

            case 5:
                bankerColor = new Color(109, 105, 104);
                break;

            case 6:
                bankerColor = new Color(108, 104, 103);
                break;

            case 7:
                bankerColor = new Color(107, 103, 102);
                break;

            case 8:
                bankerColor = new Color(106, 102, 101);
                break;

            case 9:
                bankerColor = new Color(104, 101, 100);
                caseNumb = 0;
                break;
                
        }
        log("On case: " +caseNumb);
        if (nearBanker()) {
            Point[] bc = ColorUtil.findAllColorWithinTolerance(bankerColor, bankers, 5);
            Point toClick = null;
            for (Point a: bc) {
                if (a != null) {
                    toClick = a;
                }
            }
            if (toClick != null)
                Mouse.move(toClick.x, toClick.y);
            String optionText = RSText.getOptionsText();
            atBank = optionText.contains("Bank");
            if (atBank)
                log("FOUND BANKER WITH COLOR: " +bankerColor);
        }
    }


    public Color RBGtoColor(int RBG) {
        int  red = (RBG & 0x00ff0000) >> 16;
        int  green = (RBG & 0x0000ff00) >> 8;
        int  blue = RBG & 0x000000ff;
        return new Color(red,green,blue);
    }

    public boolean areColorsClose(Color color1, Color color2, int toleranceAmount) {
        return (color1.getRed() - color2.getRed() < toleranceAmount && color1.getRed() - color2.getRed() > -toleranceAmount) && (color1.getBlue() - color2.getBlue() < toleranceAmount && color1.getBlue() - color2.getBlue() > -toleranceAmount) && (color1.getGreen() - color2.getGreen() < toleranceAmount && color1.getGreen() - color2.getGreen() > -toleranceAmount);
    }

    public boolean nearBanker() {
        try {
            return ColorUtil.findAllColor(bankerColor).length > 5;
        } catch (Exception e) {
            log("Error finding Banker!");
            return false;
        }
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        a = ColorUtil.findAllColor(bankerColor);
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.yellow);
        g.drawRect(2, 455, 57, 18);
        g.fillRect(2, 455, 57, 18);
        for (Point anA : a) {
            g.drawRect(anA.x-5, anA.y-5, 20, 20);
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
