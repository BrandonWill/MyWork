import api.methods.Game;
import api.methods.Mouse;
import api.methods.RSText;
import api.methods.Tabs;
import api.util.ZipUtils;
import bot.script.Script;
import bot.script.ScriptManifest;
import util.Configuration;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryIteratorException;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: 2/23/12
 * Time: 5:32 PM
 * To change this template use File | Settings | File Templates.
 */


@ScriptManifest(authors = { "Dwarfeh" }, category = "Finding Stats", name = "Stat Finder", description = "", version = 1.0)
public class Stats extends Script {
    
    //row 1
    public static final Rectangle ATTACK_RECTANGLE = new Rectangle(549, 211, 58, 24);
    public static final Rectangle STRENGTH_RECTANGLE = new Rectangle(549, 239, 58, 24);
    public static final Rectangle DEFENSE_RECTANGLE = new Rectangle(549, 267, 58, 24);
    public static final Rectangle RANGE_RECTANGLE = new Rectangle(549, 295, 58, 24);
    public static final Rectangle PRAYER_RECTANGLE = new Rectangle(549, 323, 58, 24);
    public static final Rectangle MAGIC_RECTANGLE = new Rectangle(549, 351, 58, 24);
    /*  Not Used:
    public static final Rectangle RUNECRAFTING_RECTANGLE = new Rectangle(549, 379, 58, 24);
    public static final Rectangle CONSTRUCTION_RECTANGLE = new Rectangle(549, 407, 58, 24);
    public static final Rectangle DUNGEONEERING_RECTANGLE = new Rectangle(549, 435, 58, 24);
     */
    //row 2
    public static final Rectangle CONSTITUTION_RECTANGLE = new Rectangle(611, 211, 58, 24);
    public static final Rectangle AGILITY_RECTANGLE = new Rectangle(611, 239, 58, 24);
    public static final Rectangle HERBLORE_RECTANGLE = new Rectangle(611, 267, 58, 24);
    public static final Rectangle THIEVING_RECTANGLE = new Rectangle(611, 295, 58, 24);
    public static final Rectangle CRAFTING_RECTANGLE = new Rectangle(611, 323, 58, 24);
    public static final Rectangle FLETCHING_RECTANGLE = new Rectangle(611, 351, 58, 24);
    public static final Rectangle SLAYER_RECTANGLE = new Rectangle(611, 379, 58, 24);
    /*  Not Used:
    public static final Rectangle HUNTING_RECTANGLE = new Rectangle(549, 407, 58, 24);
    */
    //row 3
    public static final Rectangle MINING_RECTANGLE = new Rectangle(673, 211, 58, 24);




    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public int loop() {
        Mouse.moveMouse((int) stats.MINING.statRect.getCenterX(), (int) stats.MINING.statRect.getCenterY());
        log("Found level: " + RSText.findString(stats.MINING.levelRect,null, null));
//        log("Found Current xp: " +RSText.findString(stats.CONSTITUTION.currentXP ,null, null));
//        log("Found xp to lvl: " +RSText.findString(stats.CONSTITUTION.nextLevel, null, null));
//        log("Found remainder: " +RSText.findString(stats.CONSTITUTION.remainder, null, null));
        return 4000;
    }

    public enum stats {
        //row 1
        ATTACK(ATTACK_RECTANGLE,
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)ATTACK_RECTANGLE.getCenterX() +11, (int)ATTACK_RECTANGLE.getCenterY()+75, 150, 23)),

        STRENGTH(STRENGTH_RECTANGLE,
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)STRENGTH_RECTANGLE.getCenterX() +11, (int)STRENGTH_RECTANGLE.getCenterY()+75, 150, 23)),

        DEFENSE(DEFENSE_RECTANGLE,
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)DEFENSE_RECTANGLE.getCenterX() + 11, (int)DEFENSE_RECTANGLE.getCenterY()+75, 150, 23)),

        RANGE(RANGE_RECTANGLE,
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)RANGE_RECTANGLE.getCenterX() + 11, (int)RANGE_RECTANGLE.getCenterY()+75, 150, 23)),

        PRAYER(PRAYER_RECTANGLE,
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)PRAYER_RECTANGLE.getCenterX() + 11, (int)PRAYER_RECTANGLE.getCenterY()+75, 150, 23)),

        MAGIC(MAGIC_RECTANGLE,
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)MAGIC_RECTANGLE.getCenterX() + 11, (int)MAGIC_RECTANGLE.getCenterY()+75, 150, 23)),

        //row 2
        CONSTITUTION(CONSTITUTION_RECTANGLE,
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)CONSTITUTION_RECTANGLE.x - 7, (int)CONSTITUTION_RECTANGLE.getCenterY()+75, 150, 23)),

        AGILITY(AGILITY_RECTANGLE,
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)AGILITY_RECTANGLE.x - 7, (int)AGILITY_RECTANGLE.getCenterY()+75, 150, 23)),

        HERBLORE(HERBLORE_RECTANGLE,
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)HERBLORE_RECTANGLE.x - 7, (int)HERBLORE_RECTANGLE.getCenterY()+75, 150, 23)),

        THIEVING(THIEVING_RECTANGLE,
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)THIEVING_RECTANGLE.x - 7, (int)THIEVING_RECTANGLE.getCenterY()+75, 150, 23)),

        CRAFTING(CRAFTING_RECTANGLE,
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)CRAFTING_RECTANGLE.x - 7, (int)CRAFTING_RECTANGLE.getCenterY()+75, 150, 23)),

        FLETCHING(FLETCHING_RECTANGLE,
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)FLETCHING_RECTANGLE.x - 7, (int)FLETCHING_RECTANGLE.getCenterY()+75, 150, 23)),

        SLAYER(SLAYER_RECTANGLE,
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 7, (int)SLAYER_RECTANGLE.getCenterY()+75, 150, 23)),

        //row 3

        MINING(MINING_RECTANGLE,
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+38, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+48, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+65, 150, 23),
                new Rectangle((int)SLAYER_RECTANGLE.x - 110, (int)SLAYER_RECTANGLE.getCenterY()+75, 150, 23));

        public Rectangle statRect;
        public Rectangle levelRect;
        public Rectangle currentXP;
        public Rectangle nextLevel;
        public Rectangle remainder;

        stats(Rectangle statRec, Rectangle levelForSkill, Rectangle XP, Rectangle toLvl, Rectangle left) {
            statRect = statRec;
            levelRect = levelForSkill;
            currentXP = XP;
            nextLevel = toLvl;
            remainder = left;

        }

    }

    @Override
    public Graphics doPaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.yellow);
        if (Mouse.getLocation() != null) {
            Rectangle text = new Rectangle(Mouse.getLocation().x +10, Mouse.getLocation().y+20, 150, 50);
            g.drawRect(MINING_RECTANGLE.x, MINING_RECTANGLE.y, MINING_RECTANGLE.width, MINING_RECTANGLE.height);
//            g.drawRect(Mouse.getLocation().x +11, Mouse.getLocation().y+40, 150, 18);
//            g.drawRect(stats.CONSTITUTION.levelRect.x, stats.CONSTITUTION.levelRect.y, stats.CONSTITUTION.levelRect.width, stats.CONSTITUTION.levelRect.height);
//            g.drawRect(stats.CONSTITUTION.currentXP.x, stats.CONSTITUTION.currentXP.y, stats.CONSTITUTION.currentXP.width, stats.CONSTITUTION.currentXP.height);
//            g.drawRect(stats.CONSTITUTION.nextLevel.x, stats.CONSTITUTION.nextLevel.y, stats.CONSTITUTION.nextLevel.width, stats.CONSTITUTION.nextLevel.height);
//            g.drawRect(stats.CONSTITUTION.remainder.x, stats.CONSTITUTION.remainder.y, stats.CONSTITUTION.remainder.width, stats.CONSTITUTION.remainder.height);
//            g.setColor(Color.pink);
//            g.drawRect(Mouse.getLocation().x +11, Mouse.getLocation().y+40, 150, 20);
//          
//      RSText.findString(text, null, null);
            sleep(500);
        }
        return null;
    }

}
