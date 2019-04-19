import java.awt.*;

public class RectangleAndZombiePair
{
    private Zombie zombie;
    private Rectangle recZombie;

    public RectangleAndZombiePair(Zombie zombie, Rectangle recZombie)
    {
        this.zombie = zombie;
        this.recZombie = recZombie;
    }

    public Zombie getZombie()
    {
        return this.zombie;
    }

    public Rectangle getRecZombie()
    {
        return this.recZombie;
    }

    public void setRectangleBounds(int x, int y, int width, int height)
    {
        this.recZombie.setBounds(x, y, width, height);
    }
}
