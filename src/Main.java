import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.awt.Color.*;

public class Main extends JApplet implements Runnable, KeyListener {
    private int intFrame;
    private int intFrame2;
    private Thread tdAnimator;
    private Dimension offDimension;
    private Image offImage;
    private Graphics offGraphics;
    private RectangleAndZombiePair[] rectangleAndZombiePairArray = new RectangleAndZombiePair[50];
    private static final Map<Integer, int[]> zombieStartingLocationsMap = new TreeMap<Integer, int[]>();
    private int intX;
    private int intY;
    private int[] intRandom = new int[50];
    private boolean bolUpDown = true;
    private boolean bolLeftRight = true;
    private boolean[] bolInvisibility = new boolean[50];
    private Rectangle[] recBuilding = new Rectangle[73];
    private Rectangle[] recAccess = new Rectangle[58];
    private Rectangle recPlayer;
    private Rectangle recHelicopter;
    private boolean bolDead = false;
    private boolean bolWin = false;
    private boolean bolOnce = false;
    private boolean bolOnce2 = false;
    private boolean bolOnce3 = false;
    private boolean bolOnce4 = false;
    private Rectangle recFood;
    private int intFood = 100;
    private Rectangle recWater;
    private int intWater = 100;
    private Rectangle recBed;
    private int intBed = 100;
    private Rectangle[] recLight = new Rectangle[4];
    private JFrame JListFrame = new JFrame("");
    private JPanel JListPanel = new JPanel();
    private boolean bolLeft = false;
    private boolean bolRight = false;
    private boolean bolUp = false;
    private boolean bolDown = false;
    private int intIntroCounter = 0;
    private Clip audioClip;
    private Clip audioClip2;
    private Timer[] timerArray = new Timer[50];
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int currentZombieIndex;
    private Boolean[] timerRunningArray = new Boolean[50];
    private long startingMilliseconds;
    private int currentPhase;
    private static final Map<Integer, Integer> phaseTimeMap = new TreeMap<Integer, Integer>();
    private ImageIcon instructions = new ImageIcon(Main.class.getResource("Instructions.jpeg"));
    private Image helicopterGif = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("Helicopter.gif"));
    private Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private int xCoordinate = (int) (((dimension.getWidth() - this.getWidth()) / 2) - ((dimension.getWidth() - this.getWidth()) / 3));
    private int yCoordinate = (int) (((dimension.getHeight() - this.getHeight()) / 2) - ((dimension.getWidth() - this.getWidth()) / 4));
    private Window window = null;

    @Override
    public void init() {
        Container c = this.getParent();
        while (c.getParent()!=null) {
            c = c.getParent();
        }
        if (c instanceof Window) {
            window = (Window)c;
        } else {
            System.out.println(c);
        }

        window.setLocation(this.xCoordinate, this.yCoordinate);

        new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VLCJPlayer("https://dl.dropboxusercontent.com/s/uf5eyrpffbu3knp/IntroVideo.mp4", xCoordinate, yCoordinate, 0);
            }
        });

        try
        {
            Thread.sleep(53565);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        int input = JOptionPane.showOptionDialog(null, "", "   Instructions", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, this.instructions, null, null);

        String str = this.getParameter("fps");
        int fps = str != null?Integer.parseInt(str):100;
        this.intFrame = fps > 0?1000 / fps:50;
        Container pane = this.getContentPane();
        pane.setLayout((LayoutManager)null);
        pane.setBackground(Color.black);
        Arrays.fill(intRandom, 1);
        Arrays.fill(timerRunningArray, false);

        for(int x = 0; x < this.recLight.length; ++x) {
            this.recLight[x] = new Rectangle();
        }

        for(int x = 0; x < this.rectangleAndZombiePairArray.length; x++)
        {
            if(x % 20 == 0)
            {
                rectangleAndZombiePairArray[x] = new RectangleAndZombiePair(new FastZombie(), new Rectangle());
            }
            else
            {
                rectangleAndZombiePairArray[x] = new RectangleAndZombiePair(new SlowZombie(), new Rectangle());
            }
        }

        for(int x = 0; x < this.recBuilding.length; ++x) {
            this.recBuilding[x] = new Rectangle();
        }

        for(int x = 0; x < this.recAccess.length; ++x) {
            this.recAccess[x] = new Rectangle();
        }

        this.recPlayer = new Rectangle();

        this.recHelicopter = new Rectangle();

        this.recWater = new Rectangle();

        this.recFood = new Rectangle();

        this.recBed = new Rectangle();

        zombieStartingLocationsMap.put(0, new int[]{50, 25, 10, 10});
        zombieStartingLocationsMap.put(1, new int[]{200, 25, 10, 10});
        zombieStartingLocationsMap.put(2, new int[]{300, 50, 10, 10});
        zombieStartingLocationsMap.put(3, new int[]{350, 50, 10, 10});
        zombieStartingLocationsMap.put(4, new int[]{550, 25, 10, 10});
        zombieStartingLocationsMap.put(5, new int[]{650, 50, 10, 10});
        zombieStartingLocationsMap.put(6, new int[]{875, 25, 10, 10});
        zombieStartingLocationsMap.put(7, new int[]{1100, 50, 10, 10});
        zombieStartingLocationsMap.put(8, new int[]{1150, 50, 10, 10});
        zombieStartingLocationsMap.put(9, new int[]{25, 150, 10, 10});
        zombieStartingLocationsMap.put(10, new int[]{925, 150, 10, 10});
        zombieStartingLocationsMap.put(11, new int[]{1075, 150, 10, 10});
        zombieStartingLocationsMap.put(12, new int[]{200, 200, 10, 10});
        zombieStartingLocationsMap.put(13, new int[]{1075, 250, 10, 10});
        zombieStartingLocationsMap.put(14, new int[]{25, 300, 10, 10});
        zombieStartingLocationsMap.put(15, new int[]{350, 300, 10, 10});
        zombieStartingLocationsMap.put(16, new int[]{600, 300, 10, 10});
        zombieStartingLocationsMap.put(17, new int[]{700, 300, 10, 10});
        zombieStartingLocationsMap.put(18, new int[]{950, 300, 10, 10});
        zombieStartingLocationsMap.put(19, new int[]{1050, 300, 10, 10});
        zombieStartingLocationsMap.put(20, new int[]{850, 325, 10, 10});
        zombieStartingLocationsMap.put(21, new int[]{1150, 350, 10, 10});
        zombieStartingLocationsMap.put(22, new int[]{400, 400, 10, 10});
        zombieStartingLocationsMap.put(23, new int[]{650, 350, 10, 10});
        zombieStartingLocationsMap.put(24, new int[]{925, 400, 10, 10});
        zombieStartingLocationsMap.put(25, new int[]{1100, 400, 10, 10});
        zombieStartingLocationsMap.put(26, new int[]{175, 450, 10, 10});
        zombieStartingLocationsMap.put(27, new int[]{450, 450, 10, 10});
        zombieStartingLocationsMap.put(28, new int[]{550, 450, 10, 10});
        zombieStartingLocationsMap.put(29, new int[]{25, 500, 10, 10});
        zombieStartingLocationsMap.put(30, new int[]{850, 475, 10, 10});
        zombieStartingLocationsMap.put(31, new int[]{1175, 500, 10, 10});
        zombieStartingLocationsMap.put(32, new int[]{250, 550, 10, 10});
        zombieStartingLocationsMap.put(33, new int[]{900, 550, 10, 10});
        zombieStartingLocationsMap.put(34, new int[]{50, 600, 10, 10});
        zombieStartingLocationsMap.put(35, new int[]{200, 575, 10, 10});
        zombieStartingLocationsMap.put(36, new int[]{575, 600, 10, 10});
        zombieStartingLocationsMap.put(37, new int[]{1050, 600, 10, 10});
        zombieStartingLocationsMap.put(38, new int[]{150, 650, 10, 10});
        zombieStartingLocationsMap.put(39, new int[]{400, 670, 10, 10});
        zombieStartingLocationsMap.put(40, new int[]{550, 660, 10, 10});
        zombieStartingLocationsMap.put(41, new int[]{650, 670, 10, 10});
        zombieStartingLocationsMap.put(42, new int[]{1175, 680, 10, 10});
        zombieStartingLocationsMap.put(43, new int[]{125, 680, 10, 10});
        zombieStartingLocationsMap.put(44, new int[]{875, 680, 10, 10});
        zombieStartingLocationsMap.put(45, new int[]{375, 680, 10, 10});
        zombieStartingLocationsMap.put(46, new int[]{700, 680, 10, 10});
        zombieStartingLocationsMap.put(47, new int[]{200, 680, 10, 10});
        zombieStartingLocationsMap.put(48, new int[]{875, 600, 10, 10});
        zombieStartingLocationsMap.put(49, new int[]{1050, 630, 10, 10});

        this.requestFocus();
        this.addKeyListener(this);
        this.setSize(1200, 750);
        this.setBackground(Color.black);

        this.phaseTimeMap.put(0, 0);
        this.phaseTimeMap.put(1, 90000);
        this.phaseTimeMap.put(2, 60000);
        this.phaseTimeMap.put(3, 9999999);
        start();
    }

    @Override
    public void start() {
        setUpGameObjectLocations();
        this.requestFocus();
        this.tdAnimator = new Thread(this);
        this.tdAnimator.start();
    }

    @Override
    public void stop() {
        this.tdAnimator = null;
    }

    @Override
    public void destroy()
    {
        System.exit(0);
    }

    public void setUpGameObjectLocations()
    {
        this.recLight[0].setBounds(225, 550, 5, 5);
        this.recLight[1].setBounds(400, 50, 5, 5);
        this.recLight[2].setBounds(725, 675, 5, 5);
        this.recLight[3].setBounds(1100, 350, 5, 5);

        for(int x = 0; x<rectangleAndZombiePairArray.length;x++)
        {
            rectangleAndZombiePairArray[x].setRectangleBounds(zombieStartingLocationsMap.get(x)[0], zombieStartingLocationsMap.get(x)[1], zombieStartingLocationsMap.get(x)[2], zombieStartingLocationsMap.get(x)[3]);
        }

        this.recBuilding[0].setBounds(50, 50, 25, 50);
        this.recBuilding[1].setBounds(100, 50, 75, 25);
        this.recBuilding[2].setBounds(175, 50, 25, 100);
        this.recBuilding[3].setBounds(50, 125, 100, 25);
        this.recBuilding[4].setBounds(50, 200, 75, 25);
        this.recBuilding[5].setBounds(125, 200, 25, 125);
        this.recBuilding[6].setBounds(50, 275, 25, 125);
        this.recBuilding[7].setBounds(75, 375, 75, 25);
        this.recBuilding[8].setBounds(50, 450, 25, 25);
        this.recBuilding[9].setBounds(100, 450, 50, 25);
        this.recBuilding[10].setBounds(50, 500, 25, 50);
        this.recBuilding[11].setBounds(100, 500, 50, 50);
        this.recBuilding[12].setBounds(0, 650, 25, 100);
        this.recBuilding[13].setBounds(25, 695, 75, 50);
        this.recBuilding[14].setBounds(45, 650, 55, 25);
        this.recBuilding[15].setBounds(250, 100, 25, 100);
        this.recBuilding[16].setBounds(300, 100, 100, 25);
        this.recBuilding[17].setBounds(250, 225, 100, 25);
        this.recBuilding[18].setBounds(375, 150, 25, 100);
        this.recBuilding[19].setBounds(200, 300, 25, 100);
        this.recBuilding[20].setBounds(225, 300, 75, 25);
        this.recBuilding[21].setBounds(200, 425, 75, 25);
        this.recBuilding[22].setBounds(275, 375, 25, 75);
        this.recBuilding[23].setBounds(450, 0, 50, 25);
        this.recBuilding[24].setBounds(450, 50, 50, 150);
        this.recBuilding[25].setBounds(300, 500, 25, 150);
        this.recBuilding[26].setBounds(375, 500, 100, 25);
        this.recBuilding[27].setBounds(400, 525, 25, 50);
        this.recBuilding[28].setBounds(475, 500, 50, 75);
        this.recBuilding[29].setBounds(525, 500, 25, 150);
        this.recBuilding[30].setBounds(400, 600, 75, 25);
        this.recBuilding[31].setBounds(325, 625, 175, 25);
        this.recBuilding[32].setBounds(550, 100, 125, 25);
        this.recBuilding[33].setBounds(550, 150, 25, 100);
        this.recBuilding[34].setBounds(575, 225, 125, 25);
        this.recBuilding[35].setBounds(675, 100, 25, 75);
        this.recBuilding[36].setBounds(600, 400, 100, 125);
        this.recBuilding[37].setBounds(675, 525, 25, 50);
        this.recBuilding[38].setBounds(600, 575, 25, 75);
        this.recBuilding[39].setBounds(625, 625, 75, 25);
        this.recBuilding[40].setBounds(750, 50, 25, 200);
        this.recBuilding[41].setBounds(800, 50, 100, 25);
        this.recBuilding[42].setBounds(775, 125, 75, 50);
        this.recBuilding[43].setBounds(875, 100, 25, 200);
        this.recBuilding[44].setBounds(800, 200, 75, 25);
        this.recBuilding[45].setBounds(750, 275, 125, 25);
        this.recBuilding[46].setBounds(750, 350, 25, 100);
        this.recBuilding[47].setBounds(825, 350, 50, 25);
        this.recBuilding[48].setBounds(875, 350, 25, 100);
        this.recBuilding[49].setBounds(775, 425, 100, 25);
        this.recBuilding[50].setBounds(750, 500, 25, 150);
        this.recBuilding[52].setBounds(825, 500, 25, 150);
        this.recBuilding[53].setBounds(750, 675, 100, 25);
        this.recBuilding[54].setBounds(950, 50, 25, 150);
        this.recBuilding[55].setBounds(975, 50, 75, 25);
        this.recBuilding[56].setBounds(1025, 125, 25, 125);
        this.recBuilding[57].setBounds(950, 225, 75, 25);
        this.recBuilding[58].setBounds(950, 350, 50, 50);
        this.recBuilding[59].setBounds(1025, 350, 25, 50);
        this.recBuilding[60].setBounds(950, 450, 25, 50);
        this.recBuilding[61].setBounds(975, 450, 100, 25);
        this.recBuilding[62].setBounds(1100, 450, 25, 25);
        this.recBuilding[63].setBounds(1125, 450, 25, 100);
        this.recBuilding[64].setBounds(950, 525, 150, 25);
        this.recBuilding[65].setBounds(900, 650, 175, 25);
        this.recBuilding[66].setBounds(900, 696, 250, 25);
        this.recBuilding[68].setBounds(1125, 650, 25, 25);
        this.recBuilding[69].setBounds(1100, 100, 25, 200);
        this.recBuilding[70].setBounds(1125, 100, 25, 25);
        this.recBuilding[71].setBounds(1175, 100, 25, 200);
        this.recBuilding[72].setBounds(1150, 275, 25, 25);

        this.recAccess[0].setBounds(75, 50, 25, 25);
        this.recAccess[1].setBounds(50, 100, 25, 25);
        this.recAccess[2].setBounds(150, 125, 25, 25);
        this.recAccess[3].setBounds(75, 75, 100, 50);
        this.recAccess[4].setBounds(50, 225, 25, 50);
        this.recAccess[5].setBounds(75, 225, 50, 150);
        this.recAccess[6].setBounds(125, 325, 25, 50);
        this.recAccess[7].setBounds(75, 450, 25, 100);
        this.recAccess[8].setBounds(50, 475, 100, 25);
        this.recAccess[9].setBounds(25, 650, 20, 45);
        this.recAccess[10].setBounds(45, 675, 55, 20);
        this.recAccess[11].setBounds(275, 100, 25, 25);
        this.recAccess[12].setBounds(275, 125, 100, 100);
        this.recAccess[13].setBounds(250, 200, 25, 25);
        this.recAccess[14].setBounds(375, 125, 25, 25);
        this.recAccess[15].setBounds(350, 225, 25, 25);
        this.recAccess[16].setBounds(225, 325, 50, 100);
        this.recAccess[17].setBounds(200, 400, 25, 25);
        this.recAccess[18].setBounds(275, 325, 25, 50);
        this.recAccess[19].setBounds(450, 25, 50, 25);
        this.recAccess[20].setBounds(325, 500, 50, 25);
        this.recAccess[21].setBounds(325, 525, 75, 100);
        this.recAccess[22].setBounds(500, 625, 25, 25);
        this.recAccess[29].setBounds(400, 575, 25, 25);
        this.recAccess[30].setBounds(425, 525, 50, 75);
        this.recAccess[31].setBounds(475, 575, 50, 50);
        this.recAccess[32].setBounds(600, 425, 25, 25);
        this.recAccess[33].setBounds(625, 425, 50, 25);
        this.recAccess[34].setBounds(650, 450, 25, 50);
        this.recAccess[35].setBounds(675, 475, 25, 25);
        this.recAccess[23].setBounds(550, 125, 25, 25);
        this.recAccess[24].setBounds(575, 125, 100, 100);
        this.recAccess[25].setBounds(675, 175, 25, 50);
        this.recAccess[26].setBounds(600, 525, 25, 50);
        this.recAccess[27].setBounds(625, 525, 50, 100);
        this.recAccess[28].setBounds(675, 575, 25, 50);
        this.recAccess[32].setBounds(775, 50, 25, 25);
        this.recAccess[33].setBounds(775, 75, 100, 50);
        this.recAccess[34].setBounds(875, 75, 25, 25);
        this.recAccess[35].setBounds(850, 125, 25, 50);
        this.recAccess[36].setBounds(800, 175, 75, 25);
        this.recAccess[37].setBounds(775, 175, 25, 50);
        this.recAccess[38].setBounds(750, 250, 25, 25);
        this.recAccess[39].setBounds(775, 225, 100, 50);
        this.recAccess[40].setBounds(775, 350, 50, 25);
        this.recAccess[41].setBounds(775, 375, 100, 50);
        this.recAccess[42].setBounds(775, 500, 50, 175);
        this.recAccess[43].setBounds(750, 650, 25, 25);
        this.recAccess[44].setBounds(825, 650, 25, 25);
        this.recAccess[45].setBounds(950, 200, 25, 25);
        this.recAccess[46].setBounds(975, 75, 50, 150);
        this.recAccess[47].setBounds(1025, 75, 25, 50);
        this.recAccess[48].setBounds(1000, 350, 25, 50);
        this.recAccess[49].setBounds(950, 500, 25, 25);
        this.recAccess[50].setBounds(975, 475, 150, 50);
        this.recAccess[51].setBounds(1075, 450, 25, 25);
        this.recAccess[52].setBounds(1100, 525, 25, 25);
        this.recAccess[53].setBounds(900, 675, 250, 20);
        this.recAccess[55].setBounds(1075, 650, 50, 25);
        this.recAccess[54].setBounds(1150, 100, 25, 25);
        this.recAccess[56].setBounds(1125, 125, 50, 150);
        this.recAccess[57].setBounds(1125, 275, 25, 25);

        this.recPlayer.setBounds(450, 300, 10, 10);

        this.recHelicopter.setBounds(460, 370,8,30);

        this.recWater.setBounds(350, 550, 8, 8);

        this.recFood.setBounds(1000, 578, 8, 8);

        this.recBed.setBounds(350, 150, 15, 30);
    }

    public void run() {
        long tm = System.currentTimeMillis();

        while(Thread.currentThread() == this.tdAnimator) {
            this.repaint();

            try {
                tm += (long)this.intFrame;
                Thread.sleep(50L);
            } catch (InterruptedException var4) {
                break;
            }

            this.intFrame++;

            this.intFrame++;
            if(!this.bolDead) {
                this.intFrame2 = this.intFrame;
            }
        }

    }

    public void paint(Graphics g) {
        if(this.offImage != null) {
            g.drawImage(this.offImage, 0, 0, (ImageObserver)null);
        }

        update(g);
    }

    public void update(Graphics g) {
        Dimension d = this.size();
        if(this.offGraphics == null || d.width != this.offDimension.width || d.height != this.offDimension.height) {
            this.offDimension = d;
            this.offImage = this.createImage(d.width, d.height);
            this.offGraphics = this.offImage.getGraphics();
        }

        this.offGraphics.setColor(this.getBackground());
        this.offGraphics.fillRect(0, 0, d.width, d.height);
        try {
            this.paintFrame(this.offGraphics);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        g.drawImage(this.offImage, 0, 0, (ImageObserver)null);
    }

    public void paintFrame(Graphics g) throws URISyntaxException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        g.setColor(Color.yellow);
        this.UpdateVisibilityCircle(g, this.recPlayer, 120);
        g.setColor(Color.yellow);

        int x;
        for(x = 0; x < this.recLight.length; ++x) {
            this.UpdateVisibilityCircle(g, this.recLight[x], 75);
        }

        g.setColor(new Color(156, 93, 82));
        this.UpdateLights(g);

        g.setColor(Color.gray);
        this.UpdateBuildings(g);

        if(this.IntersectionCheck(this.recPlayer, this.recHelicopter) && this.currentPhase == 3 && !this.bolOnce)
        {
            this.bolWin = true;
            this.bolOnce = true;
            this.recPlayer.setLocation(500, -2000);

            this.setVisible(false);

            new NativeDiscovery().discover();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new VLCJPlayer("https://dl.dropboxusercontent.com/s/cnww1rxlny9tz4j/EndingVideo.mp4", xCoordinate, yCoordinate, 1);
                }
            });
        }

        if(this.IntersectionCheck(this.recPlayer, this.recWater)) {
            if(this.intWater <= 50) {
                this.intWater += 50;
            } else {
                this.intWater = 100;
            }

            do
            {
                this.recWater.setLocation((int)Math.round(Math.random() * 1180.0D), (int)Math.round(Math.random()) * 650);
            }
            while(this.ArrayIntersectionCheck(this.recWater, this.recBuilding));
        }

        if(this.IntersectionCheck(this.recPlayer, this.recFood)) {
            if(this.intFood <= 50) {
                this.intFood += 50;
            } else {
                this.intFood = 100;
            }

            do {
                this.recFood.setLocation((int)Math.round(Math.random() * 1180.0D), (int)Math.round(Math.random()) * 650);
            } while(this.ArrayIntersectionCheck(this.recFood, this.recBuilding));
        }

        if(this.IntersectionCheck(this.recPlayer, this.recBed)) {
            if(this.intBed <= 50) {
                this.intBed += 50;
            } else {
                this.intBed = 100;
            }

            do {
                this.recBed.setLocation((int)Math.round(Math.random() * 1180.0D), (int)Math.round(Math.random()) * 650);
            } while(this.ArrayIntersectionCheck(this.recBed, this.recBuilding));
        }

        g.setColor(Color.lightGray);
        this.UpdateAccess(g);
        g.setColor(Color.green);
        this.UpdatePlayer(g);

        if(this.bolDead!=true)
        {
            for(x = 0; x < rectangleAndZombiePairArray.length; ++x)
            {
                Rectangle currentRecZombie = rectangleAndZombiePairArray[x].getRecZombie();
                this.intX = (int)currentRecZombie.getX();
                this.intY = (int)currentRecZombie.getY();
                Zombie currentZombie = rectangleAndZombiePairArray[x].getZombie();
                int currentAwareness = currentZombie.getAwareness();
                int currentSpeed = currentZombie.getSpeed();

                if(ChaseCheck(this.recPlayer, currentRecZombie, currentAwareness))
                {
                    currentSpeed = Math.abs(currentSpeed);

                    if(currentRecZombie.getX() < this.recPlayer.getX() && currentRecZombie.getY() < this.recPlayer.getY())
                    {
                        currentRecZombie.setLocation(this.intX + currentSpeed, this.intY + currentSpeed);
                    }
                    else if(currentRecZombie.getX() > this.recPlayer.getX() && currentRecZombie.getY() > this.recPlayer.getY())
                    {
                        currentRecZombie.setLocation(this.intX - currentSpeed, this.intY - currentSpeed);
                    }
                    else if(currentRecZombie.getX() < this.recPlayer.getX() && currentRecZombie.getY() > this.recPlayer.getY())
                    {
                        currentRecZombie.setLocation(this.intX + currentSpeed, this.intY - currentSpeed);
                    }
                    else if(currentRecZombie.getX() > this.recPlayer.getX() && currentRecZombie.getY() < this.recPlayer.getY())
                    {
                        currentRecZombie.setLocation(this.intX - currentSpeed, this.intY + currentSpeed);
                    }
                    else if(currentRecZombie.getX() < this.recPlayer.getX())
                    {
                        currentRecZombie.setLocation(this.intX + currentSpeed, this.intY);
                    }
                    else if(currentRecZombie.getX() > this.recPlayer.getX())
                    {
                        currentRecZombie.setLocation(this.intX - currentSpeed, this.intY);
                    }
                    else if(currentRecZombie.getY() < this.recPlayer.getY())
                    {
                        currentRecZombie.setLocation(this.intX, this.intY + currentSpeed);
                    }
                    else if(currentRecZombie.getY() > this.recPlayer.getY())
                    {
                        currentRecZombie.setLocation(this.intX, this.intY - currentSpeed);
                    }
                }
                else if(this.intRandom[x] == 0)
                {
                    currentRecZombie.setLocation(this.intX, this.intY + currentSpeed);
                }
                else if(this.intRandom[x] == 1)
                {
                    currentRecZombie.setLocation(this.intX + currentSpeed, this.intY);
                }
                else if(this.intRandom[x] == 2)
                {
                    currentRecZombie.setLocation(this.intX + currentSpeed, this.intY + currentSpeed);
                }
                else if(this.intRandom[x] == 3)
                {
                    currentRecZombie.setLocation(this.intX + currentSpeed, this.intY - currentSpeed);
                }

                for(int y = 0; y < this.recBuilding.length; ++y)
                {
                    if(this.IntersectionCheck(currentRecZombie, this.recBuilding[y]) || currentRecZombie.getX() >= 1191.0D || currentRecZombie.getX() <= 0.0D || currentRecZombie.getY() <= 0.0D || currentRecZombie.getY() >= 741.0D)
                    {
                        currentRecZombie.setLocation(this.intX, this.intY);

                        if(!this.ChaseCheck(this.recPlayer, currentRecZombie, currentAwareness))
                        {
                            rectangleAndZombiePairArray[x].getZombie().setSpeed(currentSpeed * -1);
                            this.intRandom[x] = (int)Math.round(Math.random() * 3.0D);
                            this.bolUpDown = true;
                            this.bolLeftRight = true;
                        }
                        else
                        {
                            if(currentRecZombie.getX() < this.recPlayer.getX() && this.bolLeftRight)
                            {
                                currentRecZombie.setLocation(this.intX + currentSpeed, this.intY);

                                for(int z = 0; z < this.recBuilding.length; z++)
                                {
                                    if(this.IntersectionCheck(currentRecZombie, this.recBuilding[y]) || currentRecZombie.getX() >= 1191.0D || currentRecZombie.getX() <= 0.0D || currentRecZombie.getY() <= 0.0D || currentRecZombie.getY() >= 741.0D)
                                    {
                                        currentRecZombie.setLocation(this.intX, this.intY);
                                        this.bolLeftRight = false;
                                    }
                                }
                            }
                            else if(currentRecZombie.getX() > this.recPlayer.getX() && this.bolLeftRight)
                            {
                                currentRecZombie.setLocation(this.intX - currentSpeed, this.intY);

                                for(int z = 0; z < this.recBuilding.length; z++)
                                {
                                    if(this.IntersectionCheck(currentRecZombie, this.recBuilding[y]) || currentRecZombie.getX() >= 1191.0D || currentRecZombie.getX() <= 0.0D || currentRecZombie.getY() <= 0.0D || currentRecZombie.getY() >= 741.0D)
                                    {
                                        currentRecZombie.setLocation(this.intX, this.intY);
                                        this.bolLeftRight = false;
                                    }
                                }
                            }

                            if(currentRecZombie.getY() < this.recPlayer.getY() && this.bolUpDown)
                            {
                                currentRecZombie.setLocation(this.intX, this.intY + currentSpeed);

                                for(int z = 0; z < this.recBuilding.length; z++)
                                {
                                    if(this.IntersectionCheck(currentRecZombie, this.recBuilding[y]) || currentRecZombie.getX() >= 1191.0D || currentRecZombie.getX() <= 0.0D || currentRecZombie.getY() <= 0.0D || currentRecZombie.getY() >= 741.0D)
                                    {
                                        currentRecZombie.setLocation(this.intX, this.intY);
                                        this.bolUpDown = false;
                                    }
                                }
                            }
                            else if(currentRecZombie.getY() > this.recPlayer.getY() && this.bolUpDown)
                            {
                                currentRecZombie.setLocation(this.intX, this.intY - currentSpeed);

                                for(int z = 0; z < this.recBuilding.length; z++)
                                {
                                    if(this.IntersectionCheck(currentRecZombie, this.recBuilding[y]) || currentRecZombie.getX() >= 1191.0D || currentRecZombie.getX() <= 0.0D || currentRecZombie.getY() <= 0.0D || currentRecZombie.getY() >= 741.0D)
                                    {
                                        currentRecZombie.setLocation(this.intX, this.intY);
                                        this.bolUpDown = false;
                                    }
                                }
                            }
                        }
                    }
                }

                if(this.IntersectionCheck(currentRecZombie, this.recPlayer) && !this.bolOnce)
                {
                    this.bolDead = true;
                    this.bolOnce = true;
                    int retry = JOptionPane.showConfirmDialog((Component)null, "   A zombie has chewed off your head!\n\n   Survive again?", "  Oh no!", 0, JOptionPane.PLAIN_MESSAGE, null);

                    if(retry == 0)
                    {
                        try
                        {
                            this.JListPanel.setVisible(false);
                            this.JListFrame.setVisible(false);
                            this.Reset();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        destroy();
                    }
                }

                for(int y = 0; y < this.rectangleAndZombiePairArray.length; ++y)
                {
                    if(x != y && this.IntersectionCheck(currentRecZombie, this.rectangleAndZombiePairArray[y].getRecZombie()))
                    {
                        currentRecZombie.setLocation(this.intX, this.intY);
                        rectangleAndZombiePairArray[x].getZombie().setSpeed(currentSpeed * -1);
                        this.intRandom[x] = (int)Math.round(Math.random() * 3.0D);
                    }
                }

                if(!this.PlayerVisibilityCheck(this.recPlayer, currentRecZombie) && !this.LightVisibilityCheck(this.recLight, currentRecZombie)) {
                    this.bolInvisibility[x] = true;
                } else {
                    this.bolInvisibility[x] = false;
                }
            }
        }



        this.UpdateZombies(g);
        g.setColor(cyan);
        g.drawRect((int)this.recWater.getX(), (int)this.recWater.getY(), (int)this.recWater.getWidth(), (int)this.recWater.getHeight());
        g.fillRect((int)this.recWater.getX(), (int)this.recWater.getY(), (int)this.recWater.getWidth(), (int)this.recWater.getHeight());
        g.setColor(Color.orange);
        g.drawRect((int)this.recFood.getX(), (int)this.recFood.getY(), (int)this.recFood.getWidth(), (int)this.recFood.getHeight());
        g.fillRect((int)this.recFood.getX(), (int)this.recFood.getY(), (int)this.recFood.getWidth(), (int)this.recFood.getHeight());
        g.setColor(Color.white);
        g.drawRect((int)this.recBed.getX(), (int)this.recBed.getY(), (int)this.recBed.getWidth(), (int)this.recBed.getHeight());
        g.fillRect((int)this.recBed.getX(), (int)this.recBed.getY(), (int)this.recBed.getWidth(), (int)this.recBed.getHeight());
        g.setColor(Color.white);
        if(this.intFrame2 % 60 == 0 && !this.bolDead) {
            this.intWater--;
            this.intFood--;
            this.intBed--;
        }

        if(!this.bolDead)
        {
            g.setFont(new Font("TimesRoman", Font.BOLD, 18));
            int minute = (int)Math.floor((phaseTimeMap.get(this.currentPhase) - (System.currentTimeMillis()-startingMilliseconds))/60000);
            int second = (int)Math.round((phaseTimeMap.get(this.currentPhase) - (System.currentTimeMillis()-startingMilliseconds))%60000/1000);

            if(this.currentPhase == 1 && (minute > 0 || second > 0))
            {
                if(second>=10)
                {
                    g.drawString("Survive for " + minute + ":" + second, 550, 22);
                }
                else
                {
                    g.drawString("Survive for " + minute + ":0" + second, 550, 22);
                }

                g.setColor(white);
                g.drawString("Water: ", 550, 44);
                g.setColor(cyan);
                g.fillRect(620, 34, this.intWater, 10);

                g.setColor(white);
                g.drawString("Food: ", 550, 66);
                g.setColor(orange);
                g.fillRect(620, 56, this.intFood, 10);

                g.setColor(white);
                g.drawString("Sleep: ", 550, 88);
                g.fillRect(620, 78, this.intBed, 10);
            }
            else if(this.currentPhase == 1 && (minute <= 0 || second <= 0))
            {
                this.currentPhase++;
                startingMilliseconds = System.currentTimeMillis();
            }

            minute = (int)Math.floor((phaseTimeMap.get(this.currentPhase) - (System.currentTimeMillis()-startingMilliseconds))/60000);
            second = (int)Math.round((phaseTimeMap.get(this.currentPhase) - (System.currentTimeMillis()-startingMilliseconds))%60000/1000);

            if(this.currentPhase == 2 && (minute > 0 || second > 0))
            {
                if(!this.bolOnce4)
                {
                    this.bolOnce4 = true;
                    File soundFile = new File(Main.class.getResource("Evac.wav").toURI());
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    // Get a sound clip resource.
                    this.audioClip2 = AudioSystem.getClip();
                    // Open audio clip and load samples from the audio input stream.
                    this.audioClip2.open(audioIn);
                    this.audioClip2.start();
                }

                minute = (int)Math.floor((60000-(System.currentTimeMillis()-startingMilliseconds))/60000);
                second = (int)Math.round((60000-(System.currentTimeMillis()-startingMilliseconds))%60000/1000);

                if(second>=10)
                {
                    g.drawString("Evac arrives in " + minute + ":" + second, 550, 22);
                }
                else
                {
                    g.drawString("Evac arrives in " + minute + ":0" + second, 550, 22);
                }

                g.setColor(white);
                g.drawString("Water: ", 550, 44);
                g.setColor(cyan);
                g.fillRect(620, 34, this.intWater, 10);

                g.setColor(white);
                g.drawString("Food: ", 550, 66);
                g.setColor(orange);
                g.fillRect(620, 56, this.intFood, 10);

                g.setColor(white);
                g.drawString("Sleep: ", 550, 88);
                g.fillRect(620, 78, this.intBed, 10);
            }
            else if(this.currentPhase == 2 && (minute <= 0 || second <= 0))
            {
                this.currentPhase++;
                startingMilliseconds = System.currentTimeMillis();
            }

            if(this.currentPhase == 3)
            {
                g.setFont(new Font("TimesRoman", Font.BOLD, 36));
                g.drawString("GET TO THE CHOPPER!", 520, 40);

                if(!this.bolOnce3)
                {
                    this.bolOnce3 = true;
                    File soundFile = new File(Main.class.getResource("GetToDaChoppa.wav").toURI());
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    // Get a sound clip resource.
                    this.audioClip2 = AudioSystem.getClip();
                    // Open audio clip and load samples from the audio input stream.
                    this.audioClip2.open(audioIn);
                    this.audioClip2.start();
                }

                this.intWater = 100;
                this.intFood = 100;
                this.intBed = 100;
            }
        }

        if(this.currentPhase == 3 && this.bolWin==false)
        {
            g.drawImage(this.helicopterGif,400, 300,null);
        }


        if(this.intWater == 0 && !this.bolOnce) {
            this.bolOnce = true;
            this.bolDead = true;
            x = JOptionPane.showConfirmDialog((Component)null, "   You died of thirst!\n\n   Survive again?", "  Oh no!", 0, JOptionPane.PLAIN_MESSAGE, null);
            if(x == 0) {
                try {
                    this.JListPanel.setVisible(false);
                    this.JListFrame.setVisible(false);
                    this.Reset();
                } catch (IOException var7) {
                    ;
                }
            } else {
                destroy();
            }
        } else if(this.intFood == 0 && !this.bolOnce) {
            this.bolOnce = true;
            this.bolDead = true;
            x = JOptionPane.showConfirmDialog((Component)null, "   You died of hunger!\n\n   Survive again?", "  Oh no!", 0, JOptionPane.PLAIN_MESSAGE, null);
            if(x == 0) {
                try {
                    this.JListPanel.setVisible(false);
                    this.JListFrame.setVisible(false);
                    this.Reset();
                } catch (IOException var6) {
                    ;
                }
            } else {
                destroy();
            }
        } else if(this.intBed == 0 && !this.bolOnce) {
            this.bolOnce = true;
            this.bolDead = true;
            x = JOptionPane.showConfirmDialog((Component)null, "   You died of sleep depreciation!\n\n   Survive again?", "  Oh no!", 0, JOptionPane.PLAIN_MESSAGE, null);
            if(x == 0) {
                try {
                    this.JListPanel.setVisible(false);
                    this.JListFrame.setVisible(false);
                    this.Reset();
                } catch (IOException var5) {
                    ;
                }
            } else {
                destroy();
            }
        }

        if(!this.bolDead) {
            this.intX = (int)this.recPlayer.getX();
            this.intY = (int)this.recPlayer.getY();
            if(this.bolUp) {
                this.recPlayer.setLocation((int)this.recPlayer.getX(), (int)this.recPlayer.getY() - 3);
            } else if(this.bolDown) {
                this.recPlayer.setLocation((int)this.recPlayer.getX(), (int)this.recPlayer.getY() + 3);
            } else if(this.bolLeft) {
                this.recPlayer.setLocation((int)this.recPlayer.getX() - 3, (int)this.recPlayer.getY());
            } else if(this.bolRight) {
                this.recPlayer.setLocation((int)this.recPlayer.getX() + 3, (int)this.recPlayer.getY());
            }

            this.PlayerIntersectionCheck();
        }

        if(!this.bolOnce2) {
            this.stop();
            this.bolOnce2 = true;
            if(this.intIntroCounter == 0)
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
                {

                }

                this.intIntroCounter = 1;
                this.startingMilliseconds = System.currentTimeMillis();
                this.currentPhase = 1;

                try {
                    // Open an audio input stream.
                    File soundFile = new File(Main.class.getResource("ZombieAmbience.wav").toURI());
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    // Get a sound clip resource.
                    this.audioClip = AudioSystem.getClip();
                    // Open audio clip and load samples from the audio input stream.
                    this.audioClip.open(audioIn);
                    this.audioClip.loop(Clip.LOOP_CONTINUOUSLY);
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            start();
        }

    }

    public void UpdatePlayer(Graphics g) {
        g.drawRect((int)this.recPlayer.getX(), (int)this.recPlayer.getY(), this.recPlayer.width, (int)this.recPlayer.getHeight());
        g.fillRect((int)this.recPlayer.getX(), (int)this.recPlayer.getY(), this.recPlayer.width, (int)this.recPlayer.getHeight());
    }

    public void UpdateVisibilityCircle(Graphics g, Rectangle rec, int intRadius) {
        g.drawOval((int)rec.getX() - intRadius, (int)rec.getY() - intRadius, intRadius * 2, intRadius * 2);
        g.fillOval((int)rec.getX() - intRadius, (int)rec.getY() - intRadius, intRadius * 2, intRadius * 2);
    }

    public void UpdateLights(Graphics g) {
        for(int x = 0; x < this.recLight.length; ++x) {
            g.drawRect((int)this.recLight[x].getX(), (int)this.recLight[x].getY(), this.recLight[x].width, (int)this.recLight[x].getHeight());
            g.fillRect((int)this.recLight[x].getX(), (int)this.recLight[x].getY(), this.recLight[x].width, (int)this.recLight[x].getHeight());
        }
    }

    public void UpdateZombies(Graphics g)
    {
        for(int x = 0; x < this.rectangleAndZombiePairArray.length; x++)
        {
            Rectangle currentRecZombie = this.rectangleAndZombiePairArray[x].getRecZombie();
            Rectangle resetRecZombie = new Rectangle();
            resetRecZombie.setBounds(zombieStartingLocationsMap.get(x)[0], zombieStartingLocationsMap.get(x)[1], zombieStartingLocationsMap.get(x)[2], zombieStartingLocationsMap.get(x)[3]);
            currentZombieIndex = x;

            if(!this.bolInvisibility[x])
            {
                if(rectangleAndZombiePairArray[currentZombieIndex].getZombie().getSpeed() == 2 || rectangleAndZombiePairArray[currentZombieIndex].getZombie().getSpeed() == -2)
                {
                    g.setColor(Color.magenta);
                }
                else
                {
                    g.setColor(Color.red);
                }

                if(this.timerRunningArray[x]== true && this.timerArray[x]!=null)
                {

                    this.timerArray[x].cancel();
                    this.timerArray[x].purge();
                    regularAwareness();
                }
            }
            //invisible inside buildings when no vision
            else if(this.bolInvisibility[x] && this.ArrayIntersectionCheck(currentRecZombie, this.recAccess))
            {
                g.setColor(Color.lightGray);
            }
            else
            {
                g.setColor(Color.black);

                if(this.timerRunningArray[x] == false)
                {
                    this.timerArray[x] = new Timer();
                    this.timerArray[x].schedule(new TimerTask()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            maxAwareness();
                                                        }

                                                    }, (int)(Math.random()*60000+10000));
                }
            }

            g.drawRect((int)currentRecZombie.getX(), (int)currentRecZombie.getY(), currentRecZombie.width, (int)currentRecZombie.getHeight());
            g.fillRect((int)currentRecZombie.getX(), (int)currentRecZombie.getY(), currentRecZombie.width, (int)currentRecZombie.getHeight());
        }

    }

    public void UpdateBuildings(Graphics g) {
        for(int x = 0; x < this.recBuilding.length; ++x) {
            g.drawRect((int)this.recBuilding[x].getX(), (int)this.recBuilding[x].getY(), this.recBuilding[x].width, (int)this.recBuilding[x].getHeight());
            g.fillRect((int)this.recBuilding[x].getX(), (int)this.recBuilding[x].getY(), this.recBuilding[x].width, (int)this.recBuilding[x].getHeight());
        }

    }

    public void UpdateAccess(Graphics g) {
        for(int x = 0; x < this.recAccess.length; ++x) {
            g.drawRect((int)this.recAccess[x].getX(), (int)this.recAccess[x].getY(), this.recAccess[x].width, (int)this.recAccess[x].getHeight());
            g.fillRect((int)this.recAccess[x].getX(), (int)this.recAccess[x].getY(), this.recAccess[x].width, (int)this.recAccess[x].getHeight());
        }

    }

    public void keyPressed(KeyEvent event) {
        if(!this.bolDead) {
            if(event.getKeyCode() == 40) {
                this.bolDown = true;
            } else if(event.getKeyCode() == 38) {
                this.bolUp = true;
            } else if(event.getKeyCode() == 37) {
                this.bolLeft = true;
            } else if(event.getKeyCode() == 39) {
                this.bolRight = true;
            }
        }

    }

    public void keyTyped(KeyEvent event)
    {
    }

    public void keyReleased(KeyEvent event) {
        if(event.getKeyCode() == 40) {
            this.bolDown = false;
        } else if(event.getKeyCode() == 38) {
            this.bolUp = false;
        } else if(event.getKeyCode() == 37) {
            this.bolLeft = false;
        } else if(event.getKeyCode() == 39) {
            this.bolRight = false;
        }

    }

    public void maxAwareness()
    {
        rectangleAndZombiePairArray[currentZombieIndex].getZombie().maxAwareness();
        timerRunningArray[currentZombieIndex] = true;
    }

    public void regularAwareness()
    {
        rectangleAndZombiePairArray[currentZombieIndex].getZombie().regularAwareness();
        timerRunningArray[currentZombieIndex] = false;
    }

    public void PlayerIntersectionCheck() {
        for(int y = 0; y < this.recBuilding.length; ++y) {
            if(this.IntersectionCheck(this.recPlayer, this.recBuilding[y]) || this.recPlayer.getX() >= 1191.0D || this.recPlayer.getX() <= 0.0D || this.recPlayer.getY() <= 0.0D || this.recPlayer.getY() >= 741.0D) {
                this.recPlayer.setLocation(this.intX, this.intY);
            }
        }

    }

    public boolean IntersectionCheck(Rectangle rec1, Rectangle rec2) {
        double[] dblCoordinate1 = new double[]{rec1.getX() + rec1.getWidth() / 2.0D, rec1.getY() + rec1.getHeight() / 2.0D};
        double[] dblCoordinate2 = new double[]{rec2.getX() + rec2.getWidth() / 2.0D, rec2.getY() + rec2.getHeight() / 2.0D};
        return Math.abs(dblCoordinate1[0] - dblCoordinate2[0]) <= rec1.getWidth() / 2.0D + rec2.getWidth() / 2.0D && Math.abs(dblCoordinate1[1] - dblCoordinate2[1]) <= rec1.getHeight() / 2.0D + rec2.getHeight() / 2.0D;
    }

    public boolean ChaseCheck(Rectangle rec1, Rectangle rec2, int intAwareness) {
        double[] dblCoordinate1 = new double[]{rec1.getX() + rec1.getWidth() / 2.0D, rec1.getY() + rec1.getHeight() / 2.0D};
        double[] dblCoordinate2 = new double[]{rec2.getX() + rec2.getWidth() / 2.0D, rec2.getY() + rec2.getHeight() / 2.0D};
        return Math.sqrt(Math.pow(dblCoordinate1[0] - dblCoordinate2[0], 2.0D) + Math.pow(dblCoordinate1[1] - dblCoordinate2[1], 2.0D)) <= (double)intAwareness;
    }

    public boolean ArrayIntersectionCheck(Rectangle rec, Rectangle[] recArray) {
        double[] dblCoordinate1 = new double[]{rec.getX() + rec.getWidth() / 2.0D, rec.getY() + rec.getHeight() / 2.0D};

        for(int x = 0; x < recArray.length; ++x) {
            double[] dblCoordinate2 = new double[]{recArray[x].getX() + recArray[x].getWidth() / 2.0D, recArray[x].getY() + recArray[x].getHeight() / 2.0D};
            if(Math.abs(dblCoordinate1[0] - dblCoordinate2[0]) <= rec.getWidth() / 2.0D + recArray[x].getWidth() / 2.0D && Math.abs(dblCoordinate1[1] - dblCoordinate2[1]) <= rec.getHeight() / 2.0D + recArray[x].getHeight() / 2.0D) {
                return true;
            }
        }

        return false;
    }

    public boolean PlayerVisibilityCheck(Rectangle rec1, Rectangle rec2) {
        double[] dblCoordinate1 = new double[]{rec1.getX() + rec1.getWidth() / 2.0D, rec1.getY() + rec1.getHeight() / 2.0D};
        double[] dblCoordinate2 = new double[]{rec2.getX() + rec2.getWidth() / 2.0D, rec2.getY() + rec2.getHeight() / 2.0D};
        return Math.sqrt(Math.pow(dblCoordinate1[0] - dblCoordinate2[0], 2.0D) + Math.pow(dblCoordinate1[1] - dblCoordinate2[1], 2.0D)) <= 120.0D;
    }

    public boolean LightVisibilityCheck(Rectangle[] rec1, Rectangle rec2) {
        double[] dblCoordinate2 = new double[]{rec2.getX() + rec2.getWidth() / 2.0D, rec2.getY() + rec2.getHeight() / 2.0D};

        for(int x = 0; x < rec1.length; ++x) {
            double[] dblCoordinate1 = new double[]{rec1[x].getX() + rec1[x].getWidth() / 2.0D, rec1[x].getY() + rec1[x].getHeight() / 2.0D};
            if(Math.sqrt(Math.pow(dblCoordinate1[0] - dblCoordinate2[0], 2.0D) + Math.pow(dblCoordinate1[1] - dblCoordinate2[1], 2.0D)) <= 75.0D) {
                return true;
            }
        }

        return false;
    }

    public void Reset() throws IOException {
        this.intX = 0;
        this.intY = 0;
        this.bolUpDown = true;
        this.bolLeftRight = true;
        this.bolDead = false;
        this.bolWin = false;
        this.bolOnce = false;
        this.bolOnce3 = false;
        this.bolOnce4 = false;
        this.intFood = 100;
        this.intWater = 100;
        this.intBed = 100;
        this.bolLeft = false;
        this.bolRight = false;
        this.bolUp = false;
        this.bolDown = false;
        this.startingMilliseconds = System.currentTimeMillis();
        this.currentPhase=1;

        start();
    }
}
