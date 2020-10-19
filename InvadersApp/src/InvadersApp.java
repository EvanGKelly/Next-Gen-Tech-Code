import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;

public class InvadersApp extends JFrame implements Runnable, KeyListener {

    private static final Dimension WindowSize = new Dimension(800, 600);
    private static boolean isInitialised = false;
    private static final int NUMALIENS = 10;
    private aliens[] AliensArray = new aliens[NUMALIENS];
    private playership playerShip;
    private static String workingDirectory;
    private BufferStrategy strategy;
    private Graphics offScreenGraphics;
    private Image bulletImage;
    private ArrayList<Playerbullet> bulletsList = new ArrayList<>();
    private Playerbullet bullet;
    private Iterator iterator;
    private boolean isGameInPlay = false;
    private double score = 0;
    private double highScore = 0;
    public boolean startNewWave;
    private int wave = 0;
    private int alienSpeed = 5;

    public InvadersApp(){

        Dimension screensize =  java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width/2 - WindowSize.width/2;
        int y = screensize.height/2 - WindowSize.height/2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
        this.setTitle("Space Invaders, or something..");

        ImageIcon icon = new ImageIcon(workingDirectory + "\\alien_ship_1.png");
        Image alien = icon.getImage();

        ImageIcon icon2 = new ImageIcon(workingDirectory + "\\alien_ship_2.png");
        Image alien2 = icon2.getImage();

        for(int i = 0; i < NUMALIENS ; i++){
            //aliens alienImage = new aliens(alien, 740);
            AliensArray[i] = new aliens(alien, alien2, WindowSize.width);
            double xx = (i%5)*80 + 70;
            double yy = (i/5)*40 + 50;
            AliensArray[i].setPosition(xx, yy);
        }
        aliens.setFleetXSpeed(10);

        icon = new ImageIcon(workingDirectory + "\\player_ship.png");
        Image shipImage = icon.getImage();
        playerShip = new playership(shipImage, WindowSize.width);
        playerShip.setPosition(300, 530);

        Sprite2D.setWinWidth(WindowSize.width);

        Thread t = new Thread(this);
        t.start();
        addKeyListener(this);

        createBufferStrategy(2);
        strategy = getBufferStrategy();
        offScreenGraphics = strategy.getDrawGraphics();

        isInitialised = true;
    }

    public void run() {

        double x1;
        double x2;
        double w1;
        double w2;
        double h1;
        double h2;
        double y1;
        double y2;
        double px2;
        double pw2;
        double py2;
        double ph2;


        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            if(isGameInPlay){
                boolean aliensReverseNeeded = false;
                startNewWave = true;


                for (int i = 0; i < NUMALIENS; i++) {
                    if(AliensArray[i].isAlive) {
                        if (AliensArray[i].move()) {
                            aliensReverseNeeded = true;
                        }
                    }

                }
                for(aliens alien : AliensArray) {
                    if (alien.isAlive) {
                        startNewWave = false;
                    }
                }

                iterator = bulletsList.iterator();

                while (iterator.hasNext()) {

                    Playerbullet b = (Playerbullet) iterator.next();
                    b.move();

                    for (aliens alien : AliensArray) {

                        y2 = b.getY();
                        x2 = b.getX();
                        w1 = alien.myImage.getWidth(null);
                        h1 = alien.myImage.getHeight(null);
                        y1 = alien.getY();
                        x1 = alien.getX();
                        w2 = b.myImage.getWidth(null);
                        h2 = b.myImage.getHeight(null);




                        if (((x1 < x2 && x1 + w1 > x2) || (x2 < x1 && x2 + w2 > x1)) && ((y1 < y2 && y1 + h1 > y2) || (y2 < y1 && y2 + h2 > y1))) {
                            if(alien.isAlive) {
                                iterator.remove();
                                alien.isAlive = false;
                                score += 10;

                                if(score >= highScore){
                                        highScore = score;

                                }
                            }
                        }
                    }
                }

                for (aliens alien : AliensArray) {

                        px2 = playerShip.getX();
                        py2 = playerShip.getY();
                        ph2 = playerShip.myImage.getHeight(null);
                        pw2 = playerShip.myImage2.getWidth(null);

                        w1 = alien.myImage.getWidth(null);
                        h1 = alien.myImage.getHeight(null);
                        y1 = alien.getY();
                        x1 = alien.getX();

                    if (((x1 < px2 && x1 + w1 > px2) || (px2 < x1 && px2 + pw2 > x1)) && ((y1 < py2 && y1 + h1 > py2) || (py2 < y1 && py2 + ph2 > y1))) {
                        if(alien.isAlive) {
                            alienSpeed = 5;
                            aliens.setFleetXSpeed(alienSpeed);

                            wave = 0;
                            score = 0;
                            isGameInPlay = false;

                            for (int i = 0; i < NUMALIENS; i++) {
                                AliensArray[i].isAlive = true;
                                double xx = (i % 5) * 80 + 70;
                                double yy = (i / 5) * 40 + 50;
                                AliensArray[i].setPosition(xx, yy);
                            }
                        }

                    }
                    if(startNewWave){
                        startNewWave();
                    }
                }

                if (aliensReverseNeeded) {
                    aliens.reverseDirection();
                    for (int i = 0; i < NUMALIENS; i++) {
                        AliensArray[i].jumpDownwards();
                    }
                }
                playerShip.movePlayer();
            }
            this.repaint();
        }

    }

    public void startNewWave(){
        for (int i = 0; i < NUMALIENS; i++) {
            AliensArray[i].isAlive = true;
            double xx = (i % 5) * 80 + 70;
            double yy = (i / 5) * 40 + 50;
            AliensArray[i].setPosition(xx, yy);

        }
        wave = (wave + 1);
        alienSpeed = alienSpeed + 1;
        aliens.setFleetXSpeed(alienSpeed);


    }

    long lastShoot = System.currentTimeMillis();
    final long threshold = 500; // 500msec = half second

    public void keyPressed(KeyEvent e){

        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            playerShip.setXSpeed(-4);
        }

        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            playerShip.setXSpeed(4);
        }



        if(e.getKeyCode() == KeyEvent.VK_SPACE){

            long now = System.currentTimeMillis();
            if (now - lastShoot > threshold)
            {
                shootBullet();
                lastShoot = now;
            }
        }

        if(!isGameInPlay){
            isGameInPlay = true;
        }
    }

    public void keyReleased(KeyEvent e){
        playerShip.setXSpeed(0);
    }

    public void keyTyped(KeyEvent e){}

    public void shootBullet(){


        if(isGameInPlay){
            ImageIcon icon1 = new ImageIcon(workingDirectory + "\\bullet.png");
            bulletImage = icon1.getImage();
            bullet = new Playerbullet(bulletImage, WindowSize.width);

            bullet.setPosition(playerShip.getX() + 15, playerShip.getY());

            bulletsList.add(bullet);
        }

    }

    public void paint(Graphics g){

        if(!isInitialised){
            return;
        }

        g = offScreenGraphics;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WindowSize.width, WindowSize.height);

        if(isGameInPlay){
            Font font = new Font("Verdana", Font.BOLD, 12);
            g.setFont(font);
            g.setColor(Color.white);

            g.drawString("Score: " + score, 10, 50);
            g.drawString("High Score: " + highScore, 650, 50);
            g.drawString("Wave: " + wave, 400, 50);

            for (int i = 0; i < NUMALIENS; i++){
                AliensArray[i].paint(g);
            }
            iterator = bulletsList.iterator();
            while(iterator.hasNext()){
                Playerbullet b = (Playerbullet) iterator.next();
                b.paint(g);
            }
            playerShip.paint(g);
        }

        if(!isGameInPlay){
            g.setColor(Color.WHITE);
            Font font = new Font("Verdana", Font.BOLD, 80);
            g.setFont(font);
            g.drawString("Game Over", 175 , 150);
            Font font2 = new Font("Verdana", Font.BOLD, 40);
            g.setFont(font2);
            g.drawString("Press any key to play", 175, 250);
        }

        strategy.show();
    }

    public static void main(String[] args) {

        workingDirectory = System.getProperty("user.dir");
        System.out.println("Working Directory = " + workingDirectory);
        InvadersApp d = new InvadersApp();
    }
}
