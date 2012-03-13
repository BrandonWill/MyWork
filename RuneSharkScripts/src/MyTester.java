import api.methods.*;
import api.util.ZipUtils;
import bot.script.Script;
import bot.script.ScriptManifest;
import ui.BotFrame;
import ui.LogTextArea;
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
//        String text = RSText.findString(new Rectangle(350, 5, Game.VIEWPORT.width - 200, 20), null, null).replaceAll(" ", "");
//        log("Text before modification: " +text);
//        if (text.contains("xp")) {
//            text = text.substring(text.indexOf("xp")+2);
//            log("Setting text as: " +text.substring(text.indexOf("xp")+2));
//        }
//
//        log("Options text: " +text);
        String text = RSText.getOptionsText();
        if (text.contains("xp")) {
            text = text.substring(text.indexOf("xp")+2, text.length()-4).replaceAll(" ", "");
            log("Setting text as: " +text);
        }
        return 150;                                         
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
//        g.drawRect(350, 5, Game.VIEWPORT.width - 200, 20);
        return null;
    }
}
