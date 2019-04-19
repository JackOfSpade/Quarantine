public class Zombie
{
    private int intAwareness;
    private int intSpeed;

    public Zombie()
    {
        this.intAwareness = (int)Math.round(Math.random() * 75.0D) + 25;
    }

    public void setAwareness(int intNum)
    {
        this.intAwareness = intNum;
    }

    public int getAwareness()
    {
        return this.intAwareness;
    }

    public void setSpeed(int intSpeed)
    {
        this.intSpeed = intSpeed;
    }

    public int getSpeed()
    {
        return this.intSpeed;
    }

    public void maxAwareness()
    {
        this.setAwareness(9999);
    }

    public void regularAwareness()
    {
        this.setAwareness((int)Math.round(Math.random() * 75.0D) + 25);
    }
}
