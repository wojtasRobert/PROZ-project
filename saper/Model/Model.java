package saper.Model;


import saper.AdditionalData.*;
import saper.AdditionalData.Enums.GameState;
import saper.Model.Minefield.Minefield;

import static saper.AdditionalData.Possibilities.*;

/**
 * Klasa tworzaca obiekty Modelu ze wzorca MVC
 */

public class Model
{
    /**
     * Plansza gry
     */
    Minefield gameBoard;

    /**
     * Zmienna przechowujaca czas gry
     */
    private CountTime gameTime;

    /**
     * Stan gry
     */
    private GameState gameState;

    /**
     * Konstruktor domyslny Modelu, poczatkowy rozmiar pola taki jak dla poziomu Poczatkujacego
     */
    public Model()
    {
        gameTime = new CountTime();
        gameBoardInit(FinalValues.BEGINNER_WIDTH, FinalValues.BEGINNER_HEIGHT, FinalValues.BEGINNER_MINES);
    }

    /**
     * Metoda inicjalizujaca plansze o zadanych parametrach
     * @param width szerokosc planszy
     * @param height wysokosc planszy
     * @param mines liczba min na planszy
     * @return true jesli wprowadzono zmiany
     */
    public boolean gameBoardInit(final int width, final int height, final int mines)
    {
        this.gameBoard = new Minefield(width, height, mines);
        this.gameState = GameState.START;
        this.gameTime.resetTime();
        return true;
    }

    /**
     * Metoda inkrementujaca czas
     */
    public boolean countTime()
    {
        switch (gameState)
        {
            case PLAYING:
                gameTime.timeCounter();
                return true;
            default:
                return false;
        }
    }

    /**
     * Metoda sprawdzajaca mozliwosc utworzenia planszy podanej przez uzytkownika
     * @param width szerokosc od 8 do 36 pol
     * @param height wysokosc od 8 do 24 pol
     * @param mines liczba min od 10 do 668 pol
     * @return true jesli pole spelnia warunki
     */
    public boolean checkUsersField(final int width, final int height, final int mines)
    {
        if (width < 8 || width > 30 || height < 8 || height > 24) return false;
        if (mines < 10 || mines > (int)(0.5*(width*height))) return false;
        return true;
    }

    /**
     * Metoda tworzaca nowa gre
     * @return true informuje o poprawnej operacji utworzenia gry
     */
    public boolean newGame()
    {
        gameBoardInit(this.gameBoard.getWidth(), this.gameBoard.getHeight(), this.gameBoard.getNumberOfMines());
        return true;
    }

    /**
     * Metoda rozmieszczajaca miny na planszy w momencie pierwszego klikniecia oraz
     * odkrywajaca pole i sprawdzajaca warunek zwyciestwa
     * @param x wspolrzedna x nacisnietego pola
     * @param y wspolrzedna y nacisnietego pola
     * @return true jesli wykonano poprawnie
     */
    public boolean showClickedField(final int x, final int y)
    {
        if (gameState == GameState.START)
        {
            gameBoard.plantMines(x, y);
            gameBoard.showField(x, y);
            gameState = GameState.PLAYING;
            return true;
        }
        if (gameState == GameState.PLAYING)
        {
            if (gameBoard.isFieldMined(x, y) && !gameBoard.isFieldAFlag(x, y))
            {
                gameState = GameState.LOST;
                gameBoard.getSingleField(x, y).setShown(true);
                return true;
            }
            boolean tmp = gameBoard.showField(x, y);
            if (gameBoard.minefieldCleared()) gameState = GameState.WON;
            return tmp;
        }
        return false;
    }

    /**
     * Metoda oznaczajaca pole
     * @param x wspolrzedna x nacisnietego pola
     * @param y wspolrzedna y nacisnietego pola
     * @return true jesli wykonano poprawnie
     */
    public boolean checkField(final int x, final int y)
    {
        if (gameState == GameState.PLAYING)
        {
            boolean tmp = gameBoard.checkSingleField(x, y);
            if (gameBoard.minefieldCleared()) gameState = GameState.WON;
            return true;
        }
        return false;
    }

    /**
     * @return true jesli gracz wygral
     */
    public boolean isWon()
    {
        return gameState == GameState.WON;
    }

    /**
     * @return true jesli gracz przegral
     */
    public boolean isLost()
    {
        return gameState == GameState.LOST;
    }

    /**
     * Metoda ustawiajaca stan gry na koniec
     */
    public void finishGame()
    {
        gameState = GameState.FINISHED;;
    }

    public ExchangedData getData()
    {
        switch (gameState)
        {
            case START:
                return startGameData();
            case LOST:
                return lostGameData();
            default:
                return actualData();
        }
    }

    /**
     * Metoda tworzaca startowe dane gry
     * @return startData
     */
    public ExchangedData startGameData()
    {
        ExchangedData startData = new ExchangedData();
        startData.minesLeft = gameBoard.getMinesLeft();
        startData.time = gameTime.getTime();
        startData.minefield = new byte[gameBoard.getWidth()][gameBoard.getHeight()];
        for (int i = 0; i < gameBoard.getWidth(); i++)
            for (int j = 0; j < gameBoard.getHeight(); j++)
                startData.minefield[i][j] = COVERED;
        return startData;
    }

    /**
     * Metoda tworzaca dane do wyslania podczas rozgrywki
     * @return data
     */
    public ExchangedData actualData()
    {
        ExchangedData data = new ExchangedData();
        data.minesLeft = gameBoard.getMinesLeft();
        data.time = gameTime.getTime();
        data.minefield = new byte[gameBoard.getWidth()][gameBoard.getHeight()];
        for (int i = 0; i < gameBoard.getWidth(); i++)
        {
            for (int j = 0; j < gameBoard.getHeight(); j++)
            {
                data.minefield[i][j] = COVERED;         // SPRAWDZIC CZY TO POTRZEBNE
                if (gameBoard.isFieldAQuestionmark(i, j)) data.minefield[i][j] = QUESTION;
                else if (gameBoard.isFieldAFlag(i, j)) data.minefield[i][j] = FLAG;
                else if (gameBoard.isFieldShown(i, j))
                {
                    int tmp = gameBoard.countMines(i, j);
                    data.minefield[i][j] = (byte)tmp;
                    if (tmp == 0) data = showZeros(i, j, data);
                }
            }
        }
        return data;
    }

    /**
     * Metoda generujaca dane w przypadku porazki
     * @return stan gry po porazce
     */
    public ExchangedData lostGameData()
    {
        ExchangedData lostData = new ExchangedData();
        lostData.minesLeft = gameBoard.getMinesLeft();
        lostData.time = gameTime.getTime();
        lostData.minefield = new byte[gameBoard.getWidth()][gameBoard.getHeight()];
        for(int i = 0;i < gameBoard.getWidth(); i++)
        {
            for (int j = 0; j < gameBoard.getHeight(); j++)
            {
                if(gameBoard.isFieldShown(i, j))
                {
                    if (gameBoard.isFieldMined(i, j)) lostData.minefield[i][j] = DETONATE;
                    else
                    {
                        int tmp = gameBoard.countMines(i, j);
                        lostData.minefield[i][j] = (byte)tmp;
                    }
                }
                else if (gameBoard.isFieldAFlag(i, j))
                    lostData.minefield[i][j] = (byte)(gameBoard.isFieldMined(i, j) ? FLAG : BAD_CALL);
                else if (gameBoard.isFieldAQuestionmark(i, j))
                    lostData.minefield[i][j] = (byte)(gameBoard.isFieldMined(i ,j) ? HAS_MINE : QUESTION);
                else if (gameBoard.isFieldNotChecked(i, j))
                    lostData.minefield[i][j] = (byte)(gameBoard.isFieldMined(i, j) ? HAS_MINE : ZERO);
            }
        }
        return lostData;
    }

    /**
     * Metoda rekurencyjna, odkrywajaca pola nie sasiadujace z zadna mina
     * @param x wspolrzedna x pola
     * @param y wspolrzedna y pola
     * @param data aktualny stan gry
     * @return data stan gry po odslonieciu pustych pol
     */
    private ExchangedData showZeros(final int x, final int y, ExchangedData data)
    {
        for (int i = x - 1; i <= x + 1;++i)
            if (i >= 0 && i < gameBoard.getWidth())
                for (int j = y - 1; j <= y + 1; ++j)
                    if (j >= 0 && j < gameBoard.getHeight())
                        if (i != x || j != y)
                        {
                            if (!gameBoard.isFieldShown(i, j))
                            {
                                data.minefield[i][j] = (byte)gameBoard.countMines(i, j);
                                gameBoard.showField(i, j);
                                if (data.minefield[i][j] == ZERO) data = showZeros(i, j, data);
                            }
                        }
        return data;
    }
}
