import api.methods.Mouse;
import bot.Bot;
import bot.script.Script;
import bot.script.ScriptManager;
import bot.script.ScriptManifest;
import loader.RSLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;


@ScriptManifest(authors = { "Dwarfeh" }, category = "auto clicker", name = "Dwarfeh's Advanced Auto Clicker", description = "Clicks", version = 1.0)
public class DwarfehAdvancedAutoClicker extends Script implements KeyListener, MouseListener {
    private boolean Click = true;
    private boolean AssignedKey = false;
    private boolean AssignKey = false;
    private boolean AssignedMouse = false;
    private boolean AssignMouse = false;
    private boolean guiStart = false;
    private boolean Looping;
    private boolean Finished;
    String KeyPressed = "None";
    int MouseX = 0;
    int MouseY = 0;
    int row = 0;
    int Clicks;
    int ClicksLeft = 0;
    int DelayTime;
    int[] MouseX1;
    int[] MouseY1;
    int[] delayTime;
    int[] timesToClick;
    int valueAt = 0;
    int rowCount;
    //Set here instead of GUI class so they can modified
    //while the GUI is running and so that it's as if it's
    //being refreshed when it actually isn't
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;

    @Override
    public boolean onStart() {
        Bot.getCurrent().getCanvas().addMouseListener(this);
        //Bot.getCurrent().getCanvas().addKeyListener(this);
        RSLoader.getCurrent().getCanvas().addKeyListener(this);
        //Starts up my GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AutoClickerGUI gui = new AutoClickerGUI();
                gui.setVisible(true);

            }
        }
        );
        //Starts script
        return true;
    }

    @Override
    public void onFinish() {
        log("Auto Clicker ended");
    }

    @Override
    public int loop() {
        if (Finished) {
            log("Ending script");
            ScriptManager.getCurrent().stopScript();
        }
        //Script does "nothing" while GUI is running
        if (!guiStart) {
            //Script isn't stuck sleeping onRun if done like this
            //Allows MouseListener and KeyListener to be active while gui is
            return 200;
        }
        //Refer to jButton5ActionPerformed at the bottom to better understand this code
        if (guiStart) {
            if (Click) {
                if (!Looping) {
                    if (!Finished) {
                        //Goes through the arrays
                        for (int i = 0; i < rowCount;) {
                            Mouse.moveMouse(MouseX1[i], MouseY1[i]);
                            for (int t = 0; t < timesToClick[i] && MouseX1[i] > 1 && MouseY1[i] > 1;) {
                                Mouse.click(MouseX1[i], MouseY1[i], true);
                                sleep(delayTime[i]);
                                t++;
                            }
                            if (i + 1 == rowCount) {
                                Finished = true;
                            }
                            i++;
                        }
                    }
                }
                if (Looping) {
                    for (int i = 0; i < rowCount;) {
                        Mouse.moveMouse(MouseX1[i], MouseY1[i]);
                        for (int t = 0; t < timesToClick[i];) {
                            Mouse.click(MouseX1[i], MouseY1[i], true);
                            sleep(delayTime[i]);
                            t++;
                        }
                        if (i == rowCount) {
                            i = -1;
                        }
                        i++;
                    }
                }
            }
        }
        return 200;
    }

    @Override
    public Graphics doPaint(Graphics graphics) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        String z = "" +e;
        //Finds the text "KeyText=" in order to get the key name
        //Done because e.getKeyCode(); would return invalid for F1-F12
        z = z.substring(z.indexOf("keyText=") +8);
        //There's a comma between everything... String array splits it to get words
        String[] parts = z.split(",");
        //This is first. If it wasn't, AssignedKey would become conflicted and turn on as you set the hotkey
        //Parts[0] is the letters/numbers for what we're looking for in our hotkey        
        if (parts[0].equals(KeyPressed) && AssignedKey) {
            if (Click) {
                Click = false;
                log("Clicking turned off!");
            } else {
                Click = true;
                log("Clicking turned on!");
            }
        }
        //Sets hotkey
        if (!AssignedKey && AssignKey) {
            log("Hotkey: " + parts[0]);
            //Changes gui accordingly
            jTextField1.setText("" +parts[0]);
            //Lets us determine if the right key is pressed to turn on/off clicking above.
            KeyPressed = parts[0];
            AssignedKey = true;
            AssignKey = false;
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (!AssignedMouse && AssignMouse) {
            MouseX = e.getX();
            MouseY = e.getY();
            log("Mouse position: " + '(' + MouseX + ',' + MouseY + ')');
            AssignedMouse = true;
            AssignMouse = false;
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    //If you want to randomize, you'd do it here...
                    //ex. +MouseX +random(1,10)
                    jTable1.getModel().setValueAt("" +MouseX, row, 0);
                    jTable1.getModel().setValueAt("" +MouseY, row, 1);
                    row += 1;
                }
            });
            MouseX1[valueAt] = MouseX;
            MouseY1[valueAt] = MouseY;
            log("MouseX[" + valueAt + "]: " + MouseX1[valueAt] + " MouseY[" + valueAt + "]: " + MouseY1[valueAt]);
            valueAt += 1;
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
    public class rawr extends JFrame {
        
    }

    public class AutoClickerGUI extends JFrame {

        /** Creates new form AutoClickerGUI */
        public AutoClickerGUI() {
            initComponents();
        }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jPanel1 = new javax.swing.JPanel();
            jLabel1 = new javax.swing.JLabel();
            jPanel5 = new javax.swing.JPanel();
            jLabel14 = new javax.swing.JLabel();
            jScrollPane1 = new javax.swing.JScrollPane();
            jTable1 = new javax.swing.JTable();
            jLabel6 = new javax.swing.JLabel();
            jPanel6 = new javax.swing.JPanel();
            jLabel2 = new javax.swing.JLabel();
            jTextField1 = new javax.swing.JTextField();
            jButton1 = new javax.swing.JButton();
            jButton2 = new javax.swing.JButton();
            jPanel7 = new javax.swing.JPanel();
            jButton3 = new javax.swing.JButton();
            jButton4 = new javax.swing.JButton();
            jButton6 = new javax.swing.JButton();
            jButton7 = new javax.swing.JButton();
            jCheckBox1 = new javax.swing.JCheckBox();
            jCheckBox2 = new javax.swing.JCheckBox();
            jButton5 = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
            jPanel1.setForeground(new java.awt.Color(204, 204, 204));

            jLabel1.setText("Configure Auto Clicker Start/Stop Hotkey");

            jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true));
            jPanel5.setForeground(new java.awt.Color(204, 204, 204));

            jLabel14.setText("Mouse Location to click:");

            jTable1.setModel(new javax.swing.table.DefaultTableModel(
                    new Object [][] {
                            {null, null, new Integer(0), new Integer(0), new Integer(0), null},
                            {null, null, new Integer(0), new Integer(0), new Integer(0), null},
                            {null, null, new Integer(0), new Integer(0), new Integer(0), null},
                            {null, null, new Integer(0), new Integer(0), new Integer(0), null}
                    },
                    new String [] {
                            "Mouse X", "Mouse Y", "Minutes", "Seconds", "Milliseconds", "Clicks to do"
                    }
            ) {
                Class[] types = new Class [] {
                        java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }
            });
            jTable1.setColumnSelectionAllowed(true);
            jTable1.getTableHeader().setReorderingAllowed(false);
            jScrollPane1.setViewportView(jTable1);
            jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            jLabel6.setText("Time before clicks:");

            javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
            jPanel5.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(
                    jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                    .addGap(10, 10, 10)
                                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(360, 360, 360))
            );
            jPanel5Layout.setVerticalGroup(
                    jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel14)
                                            .addComponent(jLabel6))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true));
            jPanel6.setForeground(new java.awt.Color(204, 204, 204));

            jLabel2.setText("Start/Stop Auto Clicking: ");

            jTextField1.setText("None");

            jButton1.setText("Assign");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });

            jButton2.setText("Clear");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
            jPanel6.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(
                    jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(24, Short.MAX_VALUE))
            );
            jPanel6Layout.setVerticalGroup(
                    jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel2)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap())
            );

            jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true));
            jPanel7.setForeground(new java.awt.Color(204, 204, 204));

            jButton3.setText("Assign Mouse");
            jButton3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });

            jButton4.setText("Clear Mouse");
            jButton4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });

            jButton6.setText("Add Row");
            jButton6.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton6ActionPerformed(evt);
                }
            });

            jButton7.setText("Delete Row");
            jButton7.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton7ActionPerformed(evt);
                }
            });

            jCheckBox1.setText("Same delay with variance?");
            jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jCheckBox1ActionPerformed(evt);
                }
            });

            jCheckBox2.setText("Loop clicks?");

            javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
            jPanel7.setLayout(jPanel7Layout);
            jPanel7Layout.setHorizontalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(6, 6, 6)
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGap(406, 406, 406))
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                    .addComponent(jCheckBox2)
                                                    .addContainerGap())))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(565, Short.MAX_VALUE))
            );
            jPanel7Layout.setVerticalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                    .addComponent(jCheckBox1)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jCheckBox2)))
                                    .addContainerGap())
            );

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, 0, 482, Short.MAX_VALUE)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(26, Short.MAX_VALUE))
            );

            jButton5.setText("Start Clicking");
            jButton5.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                    .addGap(168, 168, 168)
                                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(13, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>

        //Assign Hotkey Value
        private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
            AssignedKey = false;
            AssignKey = true;
        }

        //Clear Hotkey Value
        private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
            jTextField1.setText("None");
        }

        //Assign Mouse Values
        private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
            AssignedMouse = false;
            AssignMouse = true;
            //Invoked because of the thread the GUI runs on. This will eliminate any errors
            //Should've been done on other one too, but I didn't.
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    MouseX1 = new int[jTable1.getModel().getRowCount()];
                    MouseY1 = new int[jTable1.getModel().getRowCount()];
                    delayTime = new int[jTable1.getRowCount()];
                    timesToClick = new int[jTable1.getRowCount()];
                    rowCount = jTable1.getRowCount();
                }
            });
        }

        //Clear Mouse Values
        private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    for (int i = 0; i < MouseX1.length;) {
                        jTable1.getModel().setValueAt(null, i, 0);
                        jTable1.getModel().setValueAt(null, i, 1);
                        i++;
                    }
                }
            });
            MouseX = 0;
            MouseY = 0;
            valueAt = 0;
            row = 0;
        }
        //Adds a row
        private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    model.addRow(new Object[]{null, null});
                    //Adds 0s to the rows so you don't have to
                    jTable1.getModel().setValueAt(0, jTable1.getRowCount() -1, 2);
                    jTable1.getModel().setValueAt(0, jTable1.getRowCount() -1, 3);
                    jTable1.getModel().setValueAt(0, jTable1.getRowCount() -1, 4);
                }
            });
        }

        private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    //Deletes last row
                    model.removeRow(jTable1.getRowCount() -1);
                }
            });
        }

        private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    //gets first number in the row
                    int firstNumb = Integer.parseInt(jTable1.getModel().getValueAt(0, 4).toString());
                    //goes through all but first number to randomize them
                    for (int i = 1; i < jTable1.getModel().getRowCount();) {
                        //randomizes first number
                        jTable1.getModel().setValueAt("" +(firstNumb + random(0, 500)), i, 4);
                        i++;
                    }
                }
            });
        }

        //randomizes the number
        public int random(int min, int max) {
            Random rand = new Random();
            return rand.nextInt(max - min + 1) + min;
        }

        //Start
        private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
            //Initalize variables...
            //I was owned by event dispatch thread here for 3 hours trying to figure this out...
            delayTime = new int[jTable1.getRowCount()];
            MouseX1 = new int[jTable1.getModel().getRowCount()];
            MouseY1 = new int[jTable1.getModel().getRowCount()];
            rowCount = jTable1.getRowCount();
            //Array for delay time determing on row
            for (int i = 0; i < jTable1.getModel().getRowCount();) {
                //Converts minutes and seconds into milliseconds
                int MinNumb = Integer.parseInt(jTable1.getModel().getValueAt(i, 2).toString()) *60000;
                int SecNumb = Integer.parseInt(jTable1.getModel().getValueAt(i, 3).toString()) *1000;
                int MilliNumb = Integer.parseInt(jTable1.getModel().getValueAt(i, 4).toString());
                delayTime[i] = MinNumb + SecNumb + MilliNumb;
                log("delayTime[" + i + "]" + delayTime[i]);
                i++;
            }
            //Array for times to click
            for (int i = 0; i < jTable1.getModel().getRowCount();) {
                timesToClick[i] = Integer.parseInt(jTable1.getModel().getValueAt(i, 5).toString());
                i++;
            }
            //Array for Mouse X
            for (int i = 0; i < jTable1.getModel().getRowCount();) {
                MouseX1[i] = Integer.parseInt(jTable1.getModel().getValueAt(i, 0).toString());
                i++;
            }
            //Array for Mouse Y
            for (int i = 0; i < jTable1.getModel().getRowCount();) {
                MouseY1[i] = Integer.parseInt(jTable1.getModel().getValueAt(i, 1).toString());
                i++;
            }
            //Randomizes values in millisecond area.
            if (jCheckBox2.isSelected()) {
                Looping = true;
            } else {
                Looping = false;
            }
            //Tells gui is now closed for the loop.
            guiStart = true;
            this.dispose();
        }
    }
}