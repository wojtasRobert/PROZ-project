package saper.Model.Minefield;

import saper.AdditionalData.Enums.CheckField;
import java.util.*;

/**
 * Klasa tworzaca obiekt planszy do gry, ktora sklada sie z tablicy pol typu SingleField
 */
public class Minefield
{
      /**
     * Tablica pol
     */
    private final SingleField[][] gameBoard;

    /**
     * Liczba min na planszy zalezna od wybranego trybu gry.
     */
    private final int numberOfMines;

    /**
     * Liczba odkrytych pol na planszy.
     */
    private int shownFields = 0;

    /**
     * Liczba oznaczonych pol na planszy.
     */
    private int checkedFields = 0;

    /**
     * Konstruktor obiektu planszy do gry.
     * @param width oznacza szerokosc planszy w danym trybie
     * @param height oznacza wysokosc planszy w danym trybie
     * @param mines oznacza liczbe min na planszy w danym trybie
     */
    public Minefield(final int width, final int height, final int mines)
    {
        this.numberOfMines = mines;
        this.gameBoard = new SingleField[width][height];
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                gameBoard[i][j] = new SingleField();
            }
        }
    }

    /**
     * Metoda rozmieszczajaca miny na planszy po pierwszym kliknieciu,
     * co za tym idzie, nie ma mozliwosci przegrania gry w pierwszym ruchu.
     * @param x wspolrzedna X pola kliknietego jako pierwsze
     * @param y wspolrzedna Y pola kliknietego jako pierwsze
     */
    public void plantMines(final int x, final int y)
    {
        Random rand = new Random();
        int i = numberOfMines;
        while(i > 0)
        {
            int rand_x = rand.nextInt(getWidth());
            int rand_y = rand.nextInt(getHeight());
            if((rand_x != x && rand_y != y) && checkField(rand_x, rand_y))
            {
                gameBoard[rand_x][rand_y].setMined(true);
                i--;
            }
        }
    }

    /**
     * Metoda sprawdzajaca mozliwosc polozenia miny na danym polu
     * @param x - wspolrzedna X
     * @param y - wspolrzedna Y
     * @return true jesli mozna polozyc mine
     */
    private boolean checkField(int x, int y)
    {
        if(gameBoard[x][y].isMined()) return false;
        if(!checkFieldsSurrounding(x, y)) return false;
        for (int i = x-1; i <= x+1; i++)
        {
            if (i >= 0 && i < getWidth())
            {
                for (int j = y - 1; j <= y + 1; j++)
                {
                    if (j >= 0 && j <= getHeight())
                    {
                        if (i != x || j != y)
                        {
                            gameBoard[x][y].setMined(true);
                            if (!checkFieldsSurrounding(i,j))
                            {
                                gameBoard[x][y].setMined(false);
                                return false;
                            }
                            gameBoard[x][y].setMined(false);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Metoda sprawdzajaca otoczenie pojedynczego pola w celu znalezienia wolnego miejsca na mine.
     * @param x - wspolrzedna X
     * @param y - wspolrzedna Y
     * @return true jesli w otoczeniu pola mozna polozyc mine
     */
    private boolean checkFieldsSurrounding(int x, int y)
    {
        if(countMines(x,y) == 8) return false;
        else if((x == 0 || x == getWidth()-1) && (y == 0 || y == getHeight()-1)) // przypadek w rogu
        {
            if (countMines(x, y) == 3) return false;
        }
        else if (x == 0 || x == getWidth()-1 || y == 0 || y == getHeight()-1) // przypadek przy granicy planszy
        {
            if (countMines(x, y) == 5) return false;
        }
        return true;
    }

    /**
     * Metoda zliczajaca miny wokol danego pola
     * @param x - wsporzedna X
     * @param y - wspolrzedna Y
     * @return liczba min w otoczeniu pola
     */
    public int countMines(int x, int y)
    {
        int mines = 0;
        for (int i = x-1; i <= x+1; i++)
        {
            if (i >= 0 && i < getWidth())
            {
                for (int j = y-1; j <= y+1; j++)
                {
                    if (j >= 0 && j < getHeight())
                    {
                        if (i != x || j != y)
                        {
                            if (gameBoard[i][j].isMined()) mines++;
                        }
                    }
                }
            }
        }
        return mines;
    }

    /**
     * Metoda odkrywajaca pole
     * @param x - wspolrzedna X
     * @param y - wspolrzedna Y
     * @return true jesli nastapily zmiany
     */
    public boolean showField(final int x, final int y)
    {
        if(gameBoard[x][y].isMined() && gameBoard[x][y].getCheckState() != CheckField.FLAG)
        {
            throw(new RuntimeException("PROBA ODKRYCIA POLA Z MINA"));
        }
        if (gameBoard[x][y].isShown() || gameBoard[x][y].getCheckState() == CheckField.FLAG) return false;

        gameBoard[x][y].setCheckState(CheckField.NOT_CHECKED);
        gameBoard[x][y].setShown(true);

        boolean minesAround = false;

        back:
        for (int i = x-1; i <= x+1; ++i)
        {
            for (int j = y-1; j <= y+1; ++j)
            {
                if (i >= 0 && j >= 0 && i < getWidth() && j < getHeight())
                {
                    if (gameBoard[i][j].isMined())
                    {
                        minesAround = true;
                        break back;
                    }
                }
            }
        }

        if (!minesAround)
        {
            for (int i = x - 1; i <= x + 1; ++i)
            {
                for (int j = y - 1; j <= y + 1; ++j)
                {
                    if (i >= 0 && j >= 0 && i < getWidth() && j < getHeight())
                    {
                        showField(i, j);
                    }
                }
            }
        }
        shownFields++;
        return true;
    }

    /**
     * Metoda sluzaca do oznaczania pol prawym przyciskiem myszy w przypadku gdy nie sa odkryte.
     * Zmiany nastepuja cyklicznie
     * @param x wspolrzedna X pola
     * @param y wspolrzedna Y pola
     * @return true jesli zaszly zmiany
     */
    public boolean checkSingleField(final int x, final int y)
    {
        if(gameBoard[x][y].isShown()) return false;

        switch(gameBoard[x][y].getCheckState())
        {
            case NOT_CHECKED:
                gameBoard[x][y].setCheckState(CheckField.FLAG);
                checkedFields++;
                break;
            case FLAG:
                gameBoard[x][y].setCheckState(CheckField.QUESTIONMARK);
                checkedFields--;
                break;
            case QUESTIONMARK:
                gameBoard[x][y].setCheckState(CheckField.NOT_CHECKED);
                break;
        }
        return true;
    }

    /**
     * Metoda zwracajaca pojedyncze pole o podanych wspolrzednych
     * @param x wspolrzedna X pola
     * @param y wspolrzedna Y pola
     */
    public SingleField getSingleField(final int x, final int y)
    {
        return gameBoard[x][y];
    }

    /**
     * @return Liczba oznaczonych pol
     */
    public int getCheckedFields()
    {
        return checkedFields;
    }

    /**
     * @return Liczba odkrytych pol
     */
    public int getShownFields()
    {
        return shownFields;
    }

    /**
     * Liczba min na planszy
     */
    public int getNumberOfMines()
    {
        return numberOfMines;
    }

    /**
     * @return Liczba min do odkrycia
     */
    public int getMinesLeft()
    {
        return numberOfMines - checkedFields;
    }

    /**
     * @return wysokosc planszy
     */
    public int getHeight()
    {
        return gameBoard[0].length;
    }

    /**
     * @return szerokosc planszy
     */
    public int getWidth()
    {
        return gameBoard.length;
    }

    /**
     * @return true jesli gracz prawidlowo rozbroi wszystkie miny
     */
    public boolean minefieldCleared()
    {
        return (numberOfMines + shownFields == getWidth() * getHeight()
                && numberOfMines == checkedFields);
    }

    /**
     * Metoda zwracajaca true jesli pole jest odkryte
     * @param x wspolrzedna X pola
     * @param y wspolrzedna Y pola
     */
    public boolean isFieldShown(final int x, final int y)
    {
        return gameBoard[x][y].isShown();
    }

    /**
     * Metoda zwracajaca true jesli pole jest zaminowane
     * @param x wspolrzedna X pola
     * @param y wspolrzedna Y pola
     */
    public boolean isFieldMined(final int x, final int y)
    {
        return gameBoard[x][y].isMined();
    }

    /**
     * Metoda zwracajaca true jesli pole jest nieoznaczone
     * @param x wspolrzedna X pola
     * @param y wspolrzedna Y pola
     */
    public boolean isFieldNotChecked(final int x, final int y)
    {
        return (gameBoard[x][y].getCheckState() == CheckField.NOT_CHECKED);
    }


    /**
     * Metoda zwracajaca true jesli pole jest oznaczone flaga
     * @param x wspolrzedna X pola
     * @param y wspolrzedna Y pola
     */
    public boolean isFieldAFlag(final int x, final int y)
    {
        return (gameBoard[x][y].getCheckState() == CheckField.FLAG);
    }

    /**
     * Metoda zwracajaca true jesli pole jest oznaczone znakiem zapytania
     * @param x wspolrzedna X pola
     * @param y wspolrzedna Y pola
     */
    public boolean isFieldAQuestionmark(final int x, final int y)
    {
        return (gameBoard[x][y].getCheckState() == CheckField.QUESTIONMARK);
    }
}
