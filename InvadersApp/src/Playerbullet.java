import java.awt.*;

public class Playerbullet extends Sprite2D {

    private double ySpeed = -5.5;

    public Playerbullet(Image i,int windowWidth){
        super(i, i);
    }

    public boolean move(){

        y += ySpeed;

        return true;
    }

}
