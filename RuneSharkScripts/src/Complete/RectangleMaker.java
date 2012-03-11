package Complete;

import java.awt.*;
import java.awt.event.*;

import api.methods.ColorUtil;
import bot.Bot;
import bot.script.Script;
import bot.script.ScriptManifest;


@ScriptManifest(authors = { "Dwarfeh" }, category = "Developer Tool", name = "Rectangle Maker", description = "Easily draws rectangles", version = 1.0)
public class RectangleMaker extends Script implements MouseListener {
    int x1;
    int y1;
    int x2;
    int y2;
    int len;
    int wid = -1;

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        x1 = Bot.getCurrent().getCanvas().getMousePosition().x;
        y1 = Bot.getCurrent().getCanvas().getMousePosition().y;
    }

    public void mouseReleased(MouseEvent e) {
        x2 = Bot.getCurrent().getCanvas().getMousePosition().x;
        y2 = Bot.getCurrent().getCanvas().getMousePosition().y;

        len = y1 > y2 ? y1-y2 : y2 - y1;
        wid = x1 > x2 ? x1-x2 : x2 - x1;

        log("Rectangle made: (" +x1 +"," +y1 +"," +wid+"," +len +")");
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    @Override
    public boolean onStart() {
        Bot.getCurrent().getCanvas().addMouseListener(this);
        log("Rectangle Script has started!");
        return true;
    }

    @Override
    public void onFinish() {
        Bot.getCurrent().getCanvas().removeMouseListener(this);
        log("Rectangle Script has ended!");
        
    }

    @Override
    public int loop() {
        sleep(200);
        return 0;
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        if (wid != -1) {
            graphics.draw3DRect(x1, y1, wid, len, true);
        }
        return null;
    }

    public static double a(Color check, Color[] colors) {
        double lowest_dist = -1.0D;
        for (Color color : colors) {
            double dist = ColorUtil.getDistance(check, color);
            lowest_dist = (lowest_dist == -1.0D) || (dist < lowest_dist) ? dist : lowest_dist;
        }
        return lowest_dist;
    }
}

