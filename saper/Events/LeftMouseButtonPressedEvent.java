package saper.Events;

/**
 * Klasa tworzaca obiekt przechowujacy informacje w ktorym miejscu zostal wcisniety lewy przycisk myszy
 */

public class LeftMouseButtonPressedEvent extends SaperEvent
{
    private int x;
    private int y;

    public LeftMouseButtonPressedEvent(final int x, final int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setX(final int x)
    {
        this.x = x;
    }

    public int getX()
    {
        return x;
    }

    public void setY(final int y)
    {
        this.y = y;
    }

    public int getY()
    {
        return y;
    }
}
