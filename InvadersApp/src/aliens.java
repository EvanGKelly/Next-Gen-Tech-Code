import java.awt.*;

public class aliens extends Sprite2D {

    protected static double xSpeed = 0;
    protected boolean isAlive = true;


    public aliens(Image i,Image i2, int windowWidth){
        super(i, i2);
        winWidth = windowWidth;
    }

    public void paint(Graphics g){
        if(isAlive){
            super.paint(g);
        }
    }

    public boolean move(){

            x += xSpeed;
            if (x<=0 || x>= winWidth-myImage.getWidth(null)){
                return true;
            }
            else {
                return false;
            }

    }

    public static void setFleetXSpeed(double speed){
        xSpeed = speed;
    }

    public static void reverseDirection(){
            xSpeed = -xSpeed;
    }

    public void jumpDownwards(){
        y += 10;
    }








}
