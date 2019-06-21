package saper;

import saper.Controller.Controller;
import saper.Events.SaperEvent;
import saper.Events.TimeEvent;
import saper.Model.Model;
import saper.View.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Robert Wojtaś
 * Klasa główna, tworzaca nowa sesje gry
 * tworzy obiekty Model, View, Controller, uruchamia timer
 */

public class Saper
{
    public static void main(String[] args)
    {
        try
        {
            /**
             * Model ze wzorca MVC
             */
            final Model model = new Model();
            /**
             * Kolejka blokujaca na zdarzenia
             */
            final BlockingQueue<SaperEvent> blockingQueue = new LinkedBlockingDeque<>();
            /**
             * Widok ze wzorca MVC
             */
            final View view = new View(blockingQueue, model.getData());
            /**
             * Kontroler ze wzorca MVC
             * @param model obiekt modelu
             * @param view obiekt widoku
             * @param blockingQueue kolejka sluzaca do komunikacji miedzymodulowej
             */
            final Controller controller = new  Controller(model, view, blockingQueue);

            final Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try
                    {
                        blockingQueue.put(new TimeEvent());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            });
            timer.start();
            controller.saperCommunication();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }
}
