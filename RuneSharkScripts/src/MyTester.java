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
        String text = findString(xpGained, null, null).replaceAll(" ", "");
        startingXP = text.length() >= 1 ? Integer.parseInt(text) : 0;
        log("XP: " +startingXP);
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
        gained = Integer.parseInt(findString(xpGained, null, null).replaceAll(" ", ""))-startingXP;
        log("IN LOOP");
        log("Text in rect: " +Integer.parseInt(findString(xpGained, null, null).replaceAll(" ", "")));
        log("XP GAIN: " +(Integer.parseInt(findString(xpGained, null, null).replaceAll(" ", ""))-startingXP));
        return 0;
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setStroke(new BasicStroke(6));
        g.setColor(Color.white);
        g.draw3DRect(1, 38, 140, 130, true);
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(0, 0, 0, 70));
        g.fill3DRect(1, 38, 140, 130, true);
        g.drawRect(xpGained.x, xpGained.y, xpGained.width, xpGained.height);
        g.setColor(Color.white);
        g.drawString("XP GAINED: " +gained, 10, 50);
        return null;
    }



    /**
     * NOTE THE MORE VARIABLES YOU DEFINE THE FASTER THIS WILL BE AND THE SMALLER THE RECTANGLE THE FASTER IT WILL BE
     * SO TRY NOT TO RUN THIS WITH NO FONT SPECIFIED AND NO COLOR OF THE TEXT SPECIFIED BECAUSE IT DRASTICALLY INCREASES THE AMOUNT
     * OF OPERATIONS IT MUST PERFORM. I AM WORKING ON A HELPER SCRIPT THAT WILL MAKE THIS A LOT EASIER
     */

    /**
     * Find text within box rectangle using specified font
     * @param fontC : color of the text inside of the rectangle, can be left null to search for any color
     * @param rec: rectangle to search for text
     * @param curfont: font type for the letters you will be searching for
     * @return : String of text that was found inside of rectangle
     */
    public String findString(Color fontC, Rectangle rec, FontTypes curfont) {
        return findString(rec, fontC, allfonts.allLetters[curfont.ordinal()]);
    }

    /**
     * Find text within box rectangle using all font types
     * @param rec: rectangle to search for text
     * @param fontC : color of the text inside of the rectangle, can be left null to search for any color
     * @return : String of text that was found inside of rectangle
     */
    private String findString(Rectangle rec, Color fontC) {
        return findString(rec, fontC, null);
    }

    /**
     * Find text within box rectangle
     * @param rec: rectangle to search for text
     * @param fontC : color of the text inside of the rectangle, can be left null to search for any color
     * @param font : font type for the letters you will be searching for, this can be left null to search for all font types
     * @return : String of text that was found inside of rectangle
     */
    private String findString(Rectangle rec, Color fontC, Font[] font) {
        ArrayList<Letter> nums = new ArrayList<Letter>();
        Font[] foundFont = font;

        for(int y = rec.y; y < rec.y + rec.height; y++) {
            M1: for(int x = rec.x; x < rec.x + rec.width; x++) {
                Color c = Game.getColorAt(x, y);
                if(fontC != null && !fontC.equals(c)) continue;
                for(int i = 0; i < allfonts.allLetters.length; i++) {
                    Font[] curFont = allfonts.allLetters[i];
                    if(foundFont != null) {
                        curFont = foundFont;
                        i = allfonts.allLetters.length;
                    }
                    M2:
                    for (Font aCurFont : curFont) {
                        final Rectangle loc = new Rectangle(x - aCurFont.goodpts[0].x, y - aCurFont.goodpts[0].y, aCurFont.letbox.width, aCurFont.letbox.height);
                        if (!rec.contains(loc)) continue;
                        for (int k = 0; k < aCurFont.goodpts.length; k++) {
                            if (!checkColor(Game.getColorAt(loc.x + aCurFont.goodpts[k].x, loc.y + aCurFont.goodpts[k].y), c, 40))
                                continue M2;
                        }
                        for (int k = 0; k < aCurFont.badpts.length; k++) {
                            if (checkColor(Game.getColorAt(loc.x + aCurFont.badpts[k].x, loc.y + aCurFont.badpts[k].y), c, 40))
                                continue M2;
                        }

                        nums.add(new Letter(aCurFont.letter, loc.x, loc.x + aCurFont.letbox.width));
                        foundFont = curFont;
                        fontC = c;
                        continue M1;
                    }
                }
            }
        }
        return sortLetters(nums);
    }

    /**
     * Class used to what letter was found and
     * its start location and ending location
     */
    public class Letter {
        private int x0, x1;
        private char letter;

        public Letter(char letter, int x0, int x1) {
            this.letter = letter;
            this.x0 = x0;
            this.x1 = x1;
        }
    }

    /**
     * Font class used to store information about the each font letter
     *
     */
    public class Font {
        private Point[] goodpts;
        private Point[] badpts;
        private  char letter;
        private Rectangle letbox;

        public Font(ArrayList<Point> goodpoints, ArrayList<Point> badpoints, char letter, Rectangle letbox) {
            goodpts = new Point[goodpoints.size()];
            goodpts = goodpoints.toArray(goodpts);
            badpts = new Point[badpoints.size()];
            badpts = badpoints.toArray(goodpts);
            this.letbox = letbox;
            this.letter = letter;
        }
    }

    /**
     * Enumerated types of all the fonts available to use
     *
     */
    public enum FontTypes {
        BigChars, CharsNPC, FriendChars, LoginChars, SmallChars, StatChars, UpChars, UpCharsEx
    }

    /**
     * Class that stores every single font typea and is used to load all font types
     */
    public class AllFonts {
        private String[] fontnames = {"BigChars", "CharsNPC", "FriendChars", "LoginChars", "SmallChars", "StatChars", "UpChars", "UpCharsEx"};
        private final Font[][] allLetters = new Font[fontnames.length][62];

        public AllFonts() {
            for(int i = 0; i < allLetters.length; i++) {
                allLetters[i] = grabfontset(allLetters[i], fontnames[i]);
            }
        }
    }

    /**
     * Load all images for the current font and store the font information to the Font array
     * @param fontset : font array that information will be stored
     * @param fontname : name of the current font
     * @return : a filled array of font information
     */
    public Font[] grabfontset(Font[] fontset, String fontname) {
        int cnt = 0;
        int i = 0;
        try {
            for(i = 48; i < 58; i++) {
                final BufferedImage img = ImageIO.read(new File(Configuration.Paths.getHomeDirectory() + "\\Cache\\Fonts\\Fonts\\" + fontname + "\\" + i + ".bmp"));
                fontset[cnt++] = grabPoints(img, (char)i);
            }
            for(i = 65; i < 91; i++) {
                final BufferedImage img = ImageIO.read(new File(Configuration.Paths.getHomeDirectory() +"\\Cache\\Fonts\\Fonts\\" + fontname + "\\" + i + ".bmp"));
                fontset[cnt++] = grabPoints(img, (char)i);
            }
            for(i = 97; i < 123; i++) {
                final BufferedImage img = ImageIO.read(new File(Configuration.Paths.getHomeDirectory() +"\\Cache\\Fonts\\Fonts\\" + fontname + "\\" + i + ".bmp"));
                fontset[cnt++] = grabPoints(img, (char)i);
            }
        } catch (IOException e) {
            log("Image reading failed on bitmap number " + i + " font was " + fontname);
        }
        return fontset;
    }

    /**
     * Grab all points in the image that match the given color
     * @param img : image to search through
     * @param letter : letter to search for
     * @return : an array of the points that matched color c
     */
    Font grabPoints(BufferedImage img, char letter) {
        ArrayList<Point> goodpts = new ArrayList<Point>();
        ArrayList<Point> badpts = new ArrayList<Point>();
        for(int y = 0; y < img.getHeight(); y++) {
            for(int x = 0; x < img.getWidth(); x++) {
                final Color c2 = new Color(img.getRGB(x, y));
                if(c2.equals(Color.WHITE)) goodpts.add(new Point(x, y));
                else badpts.add(new Point(x, y));
            }
        }
        return new Font(goodpts, badpts, letter, new Rectangle(img.getWidth(), img.getHeight()));
    }

    private boolean checkColor(final Color c, final Color c2, final int Tol) {
        return (checkColor(c.getRed(), c2.getRed()) + checkColor(c.getGreen(), c2.getGreen()) + checkColor(c.getBlue(), c2.getBlue())) < Tol;
    }

    private int checkColor(final int RGB, final int Val) {
        return Math.abs(RGB - Val);
    }

    AllFonts allfonts = new AllFonts();

    // WE COULD MAYBE REMOVE THIS IF WE ONLY SEARCH ALONG THE MIDDLE OF THE BOX FOR COLORS. THIS WOULD ALSO ALLOW US TO NOT HAVE TO DO ANYWHERE CLOSE
    // TO THE AMOUNT OF TRAVERSALS AND WOULD REMOVE A LOT OF EXTRA RUNTIME
    // THE ONLY DOWN SIDE WOULD BE THE PERSON MUST MAKE SURE HE HAS HIS BOX CENTERED IN A WAY THAT THE 1 Y LINE THEY SPECIFY HITS ALL THE LETTERS
    // THIS COULD ALSO BRING IN THE PROBLEM OF THE PERSON NOT KNOWING WHERE THE TEXT IS ON SCREEN AND WOULD MAKE HIS JOB A LOT MORE TROUBLESOME
    private String sortLetters(ArrayList<Letter> letters) {
        String text = "";
        Letter oldLetter = null;
        while(!letters.isEmpty()) {
            Letter curLetter = new Letter('X', 800, 800);
            for (Letter letter : letters) {
                if (letter.x0 < curLetter.x0) {
                    curLetter = letter;
                }
            }
            if(oldLetter != null && curLetter.x0 - oldLetter.x1 > 1) text += " ";
            oldLetter = curLetter;
            text += curLetter.letter;
            letters.remove(curLetter);
        }
        return text;
    }
}
