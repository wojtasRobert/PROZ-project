package saper.Model;

/**
 * Klasa odpowiedzialna za zlicznie i resetowanie czasu gry
 * liczonego w sekundach.
 */
public class CountTime
{
    private int time;

    /**
     * Metoda zwracajaca aktualny czas gry wyrazony w sekundach
     * @return time
     */
    public int getTime()
    {
        return time;
    }

    /**
     * Metoda resetujaca czas
     */
    public void resetTime()
    {
        time = 0;
    }

    /**
     * Metoda zliczajaca czas
     */
    public void timeCounter()
    {
        time++;
    }
}
