package saper.Controller;
import saper.Events.*;

/**
 * Interfejs odpowiedzialny za odpowiedz Controllera na zapytanie plynace z Model
 * Konieczna do prawidlowego jego dzialania jest definicja funkcji pass()
 */
public interface SaperGameAction
{
    /**
     * Metoda podajaca instrukcje do modelu i widoku
     * @param event zdarzenie
     */
    abstract public void pass(SaperEvent event);
}