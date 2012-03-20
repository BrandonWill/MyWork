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

    @Override
    public boolean onStart() {
        log("STARTED DA LULZ MACHINE");
        return true;
    }

    @Override
    public void onFinish() {
        log("WHY U STOP MEH");
    }


    @Override
    public int loop() {
    return 150;
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        return null;
    }
}
