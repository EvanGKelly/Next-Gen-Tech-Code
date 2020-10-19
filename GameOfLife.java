import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.*;


public class GameOfLife extends JFrame implements Runnable, MouseListener, MouseMotionListener {

    private static final Dimension WindowSize = new Dimension(1000, 700);
    private static String workingDirectory;
    private BufferStrategy strategy;
    private Graphics offScreenGraphics;
    private static boolean isInitialised = false;

    static int numCols = 30;
    static int numRows = 30;
    private static boolean[][][] cellArray = new boolean[numRows][numCols][2];
    private boolean showCell = false;
    int i;
    int j;
    private boolean isPlaying = false;
    int currBuffer = 0;

    String line = null;
    String filename = "lifegame.txt";



    public GameOfLife(){

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screensize =  java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width/2 - WindowSize.width/2;
        int y = screensize.height/2 - WindowSize.height/2;

        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);

        this.setTitle("Conway's game of life, or something..");

        Thread t = new Thread(this);
        t.start();
        addMouseListener(this);
        addMouseMotionListener(this);

        createBufferStrategy(2);
        strategy = getBufferStrategy();

        offScreenGraphics = strategy.getDrawGraphics();

        for(i = 0; i < numRows; i++){

            for (j = 0; j < numCols; j++){
                cellArray[i][j][currBuffer] = showCell;
            }
        }

        isInitialised = true;
    }


    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(isPlaying){
                for (int x=0;x<numRows;x++) {
                    for (int y=0;y<numCols;y++) {
                        // count the live neighbours of cell [x][y][0]

                        int neighbours = 0;

                        for (int xx=-1;xx<=1;xx++) {
                            for (int yy=-1;yy<=1;yy++) {

                                if (xx!=0 || yy!=0) {
                                    // check cell [x+xx][y+yy][0]
                                    // but.. what if x+xx==-1, etc. ?

                                    int x2 = (x + xx + numRows) % numRows;
                                    int y2 = (y + yy + numCols) % numCols;

                                    if(cellArray[x2][y2][currBuffer]){
                                        neighbours += 1;

                                    }

                                }
                            }
                        }

                        if(!cellArray[x][y][currBuffer] && neighbours == 3){

                            cellArray[x][y][(currBuffer+1)%2] = true;
                        }
                        else if(cellArray[x][y][currBuffer] && (neighbours < 2 || neighbours > 3)){
                            cellArray[x][y][(currBuffer+1)%2] = false;
                        }
                        else{
                            cellArray[x][y][(currBuffer+1)%2] = cellArray[x][y][currBuffer];
                        }
                    }
                }
                 currBuffer = ++currBuffer%2;

            }


            this.repaint();
        }
    }

    public void paint(Graphics g){
        if(!isInitialised){
            return;
        }

        g = offScreenGraphics;

            g.setColor(Color.white);
            int x = 0;
            int y = 0;

            for (i = 0; i < numRows; i++) {

                for (j = 0; j < numCols; j++) {
                    //cellArray[i][j] = false;
                    if (cellArray[i][j][currBuffer]) {
                        g.setColor(Color.black);
                    }
                    g.fillRect(x, y, 20, 20);
                    g.setColor(Color.white);

                    y += 20;
                }
                x += 20;
                y = 0;

            }

        if(!isPlaying) {


            g.setColor(Color.green);
            g.fillRect(10, 40, 80, 20);
            g.fillRect(100, 40, 80, 20);
            g.fillRect(285, 40, 60, 20);
            g.fillRect(360, 40, 60, 20);
            g.setColor(Color.black);
            Font font = new Font("Courier", Font.BOLD,12);
            g.setFont(font);
            g.drawString("Start", 35, 55);
            g.drawString("Random", 115, 55);
            g.drawString("Save", 300, 55);
            g.drawString("Load", 375, 55);
        }
        strategy.show();
    }

    public void hideCell(int x, int y){
        int x1 = x;
        int y1 = y;

        cellArray[x1][y1][currBuffer] = !cellArray[x1][y1][currBuffer];
    }

    public void randCell(){
        for (i = 0; i < numRows; i++) {

            for (j = 0; j < numCols; j++) {
                double num = Math.random();

                if(num >= 0.75){
                    cellArray[i][j][currBuffer] = true;
                }
                else{
                    cellArray[i][j][currBuffer] = false;
                }
            }
        }

    }


    // mouse events which must be implemented for MouseListener
    public void mousePressed(MouseEvent e) {
        createBoard(e);
    }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void mouseClicked(MouseEvent e) { }

    public void mouseMoved(MouseEvent e){
        createBoard(e);
    }

    public void mouseDragged(MouseEvent e){
        createBoard(e);
    }
    public void createBoard(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        hideCell(x/20, y/20);

        // Start Game Button
        if(x>=10 && x<=90 && y>=40 && y<=60){
            isPlaying = true;
        }
        // Random Game Button
        if(x>=100 && x<=180 && y>=40 && y<=60){
            randCell();
        }
        // Save Game Button
        if(x >= 285 && x <= 345 && y >=40 && y<=60){
            saveGame();
        }
        //Load Game Button
        if(x>=360 && x<=420 && y >= 40 && y<=60){
            loadGame();
        }

        System.out.println(e.getPoint());
    }

    public void saveGame(){
        try {
            FileWriter writer = new FileWriter(filename);
            System.out.println("Saving Game");

            for(int x = 0; x<numCols; x++){
                for(int y = 0; y<numRows; y++){
                    if(cellArray[x][y][currBuffer]){
                        writer.write("1");

                    }
                    else{
                        writer.write("0");
                    }
                    writer.write("\n");
                }
            }
            writer.close();

        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public void loadGame(){
        int i = 0;
        int j = 0;

        try{
            File file = new File(filename);
            FileReader read = new FileReader(file);
            BufferedReader br = new BufferedReader(read);
            String c;

            c = br.readLine();

            while(c != null){
                if(c.compareTo("1") == 0){
                    cellArray[i][j][currBuffer] = true;
                }
                else if(c.compareTo("0") == 0){
                    cellArray[i][j][currBuffer] = false;
                }
                j++;
                if(j == 30){
                    i++;
                    j = 0;
                }
                c = br.readLine();
            }
            br.close();
            System.out.println("completed now!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        workingDirectory = System.getProperty("user.dir");
        System.out.println("Working Directory = " + workingDirectory);
        GameOfLife game = new GameOfLife();
    }

}
