import java.awt.*;

public class playership extends Sprite2D {

    protected static double xSpeed = 0;

    public playership(Image i, int windowWidth){
        super(i, i);
    }

    public void setXSpeed(double speed){
        xSpeed = speed;
    }

    public boolean movePlayer(){
        x += xSpeed;

        if(x <= 0 || x > winWidth-myImage.getWidth(null)){
            return true;
        }
        else {
            return false;
        }
    }

}
