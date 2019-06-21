package saper.Events;

public class SetMinefieldSizeEvent extends SaperEvent
{
    private int width;
    private int height;
    private int mines;

    public SetMinefieldSizeEvent(final int width, final int height, final int mines)
    {
        super();
        this.width = width;
        this.height = height;
        this.mines = mines;
    }

    public void setHeight(final int height)
    {
        this.height = height;
    }

    public int getHeight()
    {
        return height;
    }

    public void setWidth(final int width)
    {
        this.width = width;
    }

    public int getWidth()
    {
        return width;
    }

    public void setMines(final int mines)
    {
        this.mines = mines;
    }

    public int getMines()
    {
        return mines;
    }
}
