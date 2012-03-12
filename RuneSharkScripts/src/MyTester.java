import api.methods.*;
import api.util.ZipUtils;
import bot.script.Script;
import bot.script.ScriptManifest;
import util.Configuration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

@ScriptManifest(authors = { "Dwarfeh" }, category = "LOLOL", name = "LOL", description = "4 DUH LULZ", version = 1.0)
public class MyTester extends Script {
    public static final int WIDTH = 36;
    public static final int HEIGHT = 32;
    int startingXP;
    int gained = 0;
    Rectangle xpGained = new Rectangle(415, 5, 90, 35);
    boolean run = true;

    @Override
    public boolean onStart() {
        log("STARTED DA LULZ MACHINE");
        return true;
    }

    @Override
    public void onFinish() {
        run = false;
        log("WHY U STOP MEH");
    }


    @Override
    public int loop() {
        log("Color there: " +Inventory.getSlotAt(0).getCenterColor());
        return 150;
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setStroke(new BasicStroke(6));
        g.setColor(Color.white);
        g.draw3DRect(2, 438, 166, 22, true);
        return null;
    }
}
