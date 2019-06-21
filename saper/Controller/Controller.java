package saper.Controller;

import saper.Events.*;
import saper.Model.*;
import saper.View.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Klasa Controller, realizujaca komunikacje miedzy modelem a widokiem
 * zgodnie ze wzorcem MVC
 */

public class Controller
{
    private final Model model;
    private final View view;

    /**
     * Kolejka dla obiektow zdarzen sapera
     */
    private final BlockingQueue<SaperEvent> blockingQueue;

    /**
     * Odwzorowanie obiektow SaperEvent na obiekty SaperAction
     */
    private final Map<Class<? extends SaperEvent>,SaperGameAction> eventActionMap;

    /**
     * Konstruktor obiektow komunikujacych ze soba Model i View
     * @param model przesylany model
     * @param view przesylany widok
     * @param blockingQueue kolejka na zdarzenia z gry
     */
    public Controller(final Model model, final View view, final BlockingQueue<SaperEvent> blockingQueue)
    {
        this.model = model;
        this.view = view;
        this.blockingQueue = blockingQueue;
        eventActionMap = new HashMap<>();
        fillEventContainer();
    }

    /**
     * Metoda zapelniajaca kontener zdarzen
     */
    private void fillEventContainer()
    {
        /**
         * Rozpoczecie gry
         */
        eventActionMap.put(StartGameEvent.class, new SaperGameAction() {
            @Override
            public void pass(SaperEvent event) {
                model.newGame();
                view.refresh(model.getData());
            }
        });

        /**
         * Ustawienie rozmiaru planszy
         */
        eventActionMap.put(SetMinefieldSizeEvent.class, new SaperGameAction() {
            @Override
            public void pass(SaperEvent event) {
                SetMinefieldSizeEvent minefieldSizeEvent = (SetMinefieldSizeEvent) event;
                if (model.checkUsersField(minefieldSizeEvent.getWidth(), minefieldSizeEvent.getHeight(), minefieldSizeEvent.getMines())) {
                    model.gameBoardInit(minefieldSizeEvent.getWidth(), minefieldSizeEvent.getHeight(), minefieldSizeEvent.getMines());
                    view.refresh(model.getData());
                } else
                    view.boardCreationError();
            }
        });

        /**
         * Wcisniecie lewego klawisza
         */
        eventActionMap.put(LeftMouseButtonPressedEvent.class, new SaperGameAction() {
            @Override
            public void pass(SaperEvent event) {
                LeftMouseButtonPressedEvent leftButtonEvent = (LeftMouseButtonPressedEvent) event;
                if (model.showClickedField(leftButtonEvent.getX(), leftButtonEvent.getY()))
                    view.refresh(model.getData());

                if (model.isWon()) {
                    view.gameWon(model.getData());
                    model.finishGame();
                }

                if (model.isLost()) {
                    view.gameLost(model.getData());
                    model.finishGame();
                }
            }
        });

        eventActionMap.put(RightMouseButtonPressedEvent.class, new SaperGameAction() {
            @Override
            public void pass(SaperEvent event) {
                RightMouseButtonPressedEvent rightButtonEvent = (RightMouseButtonPressedEvent) event;
                if (model.checkField(rightButtonEvent.getX(), rightButtonEvent.getY()))
                    view.refresh(model.getData());
                if (model.isWon()) {
                    view.gameWon(model.getData());
                    model.finishGame();
                }
            }
        });

        /**
         * Uplyniecie kolejnej sekundy
         */
        eventActionMap.put(TimeEvent.class, new SaperGameAction() {
            @Override
            public void pass(SaperEvent event) {
                if (model.countTime())
                    view.refresh(model.getData());
            }
        });

        /**
         * Porazka
         */
        eventActionMap.put(FinishGameEvent.class, new SaperGameAction() {
            @Override
            public void pass(SaperEvent event) {
                System.exit(0);
            }
        });
    }

        /**
         * Metoda, ktora zbiera informacje z Widoku i na podstawie ktorych realizowane sa
         * odpowiednie zadania z kontenera zdarzen
         */
        public void saperCommunication()
        {
            while (true)
            {
                try
                {
                    SaperEvent event = blockingQueue.take();
                    SaperGameAction action = eventActionMap.get(event.getClass());
                    action.pass(event);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

    }




