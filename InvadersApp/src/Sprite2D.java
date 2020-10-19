import java.awt.*;

public class Sprite2D {

    protected double x, y;
    protected Image myImage;
    protected Image myImage2;
    protected static int winWidth;
    private int framesDrawn;


    public Sprite2D(Image i, Image i2){

    //  load image from disk. Make sure you have the path right!
        myImage = i;
        myImage2 = i2;
    }

    public void setPosition(double xx, double yy){
        x = xx;
        y = yy;
    }

    public void paint(Graphics g){
        // display the image (final argument is an ‘ImageObserver’ object)
        framesDrawn++;
        if ( framesDrawn % 100 < 50 )
            g.drawImage(myImage, (int)x, (int)y, null);
        else
            g.drawImage(myImage2, (int)x, (int)y, null);
    }

    public static void setWinWidth(int w) {
        winWidth = w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
