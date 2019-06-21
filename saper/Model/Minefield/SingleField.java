package saper.Model.Minefield;

import saper.AdditionalData.Enums.CheckField;

/**
 * Obiekt reprezentujacy pojedyncze pole na planszy.
 */

public class SingleField
{
    /**
     * Wartosc informujaca o zaminowaniu pola.
     */
    private boolean isMined;

    /**
     * Wartosc informujaca o odkryciu pola.
     */
    private boolean isShown;

    /**
     * Wartosc reprezentujaca aktualny stan pola.
     */
    CheckField checkState;

    /**
     * Konstruktor, wszystkie wartosci domyslnie ustawione na FALSE i NOT_CHECKED
     */
    SingleField()
    {
        this.isMined = false;
        this.isShown = false;
        this.checkState = CheckField.NOT_CHECKED;
    }

    /**
     * Metoda ustawiajaca wartosc isMined
     * @param mined ustawione na TRUE oznacza zaminowanie pola
     */
    public void setMined(final boolean mined)
    {
        this.isMined = mined;
    }

    /**
     * Metoda zwracajaca wartosc isMined pola
     */
    public boolean isMined()
    {
        return isMined;
    }

    /**
     * Metoda ustawiajajaca wartosc isShown
     * @param shown ustawiony na TRUE oznacza odkrycie pola
     */
    public void setShown(boolean shown)
    {
        this.isShown = shown;
    }

    /**
     * Metoda zwracajaca wartosc isShown pola
     */
    public boolean isShown()
    {
        return isShown;
    }

    /**
     * Metoda ustawiajaca nowa wartosc check
     * @param state o wartosci odpowiedniej wartosci definiuje oznaczenie pola
     */
    public void setCheckState(CheckField state)
    {
        this.checkState = state;
    }

    /**
     * Metoda zwracajaca stan oznaczenia pola
     */
    public CheckField getCheckState()
    {
        return checkState;
    }
}

