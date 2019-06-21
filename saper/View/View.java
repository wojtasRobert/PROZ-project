package saper.View;

import saper.Events.*;
import saper.AdditionalData.ExchangedData;
import saper.AdditionalData.FinalValues;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;

import static javax.swing.UIManager.setLookAndFeel;

/**
 * Klasa realizujaca Widok ze wzorca MVC
 * Odpowiada za przechwytywanie zdarzen i interakcje z uzytkownikiem
 */

public class View
{
    /**
     * Kolejka uzywana do komunikacji z kontrolerem
     */
    private final BlockingQueue<SaperEvent> blockingQueue;
    /**
     * Okienko gry
     */
    private SaperFrame frame;

    /**
     * Tworzy nowy Widok, tworzone jest nowe okno z ekranem gry
     * @param blockingQueue kolejka do komunikacji z Kontrolerem
     * @param data informacje na temat wartosci zawartych w polach, czasie, pozostalych minach
     */
    public View(final BlockingQueue<SaperEvent> blockingQueue, final ExchangedData data)
    {
        this.blockingQueue = blockingQueue;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new SaperFrame();
                refresh(data);
            }
        });
    }

    /**
     * Metoda odswiezajaca Widok na podstawie otrzymanych danych
     * @param data informacje o stanie pol, czasie, pozostalych minach
     */
    public void refresh(final ExchangedData data)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setSize(data.minefield.length * FinalValues.SQUARE_SIZE + FinalValues.HORIZONTAL_MARGIN,
                        data.minefield[0].length * FinalValues.SQUARE_SIZE + FinalValues.VERTICAL_MARGIN);
                frame.displayTime(data.time);
                frame.displayMinesLeft(data.minesLeft);
                frame.drawBoard(data.minefield);
            }
        });
    }

    /**
     * Metoda wyswietlajaca okienko dialogowe z bledem utworzenia planszy z powodu nieprawidlowych argumentow
     */
    public void boardCreationError()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, "Blad przy tworzeniu planszy!\nZle parametry!", "Blad utworzenia planszy", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Metoda informujaca o zwyciestwie
     */
    public void gameWon(final ExchangedData data)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,"Brawo byku!\nCzas: " + data.time, "VICTORY!", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Metoda informujaca o zwyciestwie
     */
    public void gameLost(final ExchangedData data)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,"Lipa byku!\nCzas: " + data.time, "DEAFEAT!", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Klasa tworzaca okno gry, dostep do pol i metod klasy VIEW
     */
    private class SaperFrame extends JFrame
    {
        private static final long serialVersionUID = 1L;
        private final SaperPanel panel;
        private final JLabel time;
        private final JLabel minesLeft;
        private final JLabel timeInfo;
        private final JLabel minesLeftInfo;

        /**
         * Konstruktor obiektu reprezentujacego okno gry
         * tworzone menu opcji, stopka z informacjami o czasie i pozostalych minach oraz panel gry
         */
        SaperFrame()
        {
            super("Saper PROZ 2018");
            try
            {
                setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            setLayout(new BorderLayout());
            setJMenuBar(createMenu());

            JPanel footprint = new JPanel();
            add(BorderLayout.SOUTH, footprint);
            minesLeftInfo = new JLabel("Miny do rozbrojenia: ");
            minesLeft = new JLabel("0");
            timeInfo = new JLabel("     Czas: ");
            time = new JLabel("0");

            footprint.add(minesLeftInfo);
            footprint.add(minesLeft);
            footprint.add(timeInfo);
            footprint.add(time);

            panel = new SaperPanel();

            JPanel central = new JPanel();

            central.add(panel);
            central.setBorder(new EtchedBorder());
            central.setLayout(null);

            add(BorderLayout.CENTER, central);
            panel.addMouseListener(new ClickListener());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            setVisible(true);
        }

        /**
         * Metoda ustawiajaca liczbe pozostalych do oznaczenia min do wyswietlenia
         * @param mines liczba min do oznaczenia
         */
        public void displayMinesLeft(final int mines)
        {
            minesLeft.setText(Integer.toString(mines));
        }

        /**
         * Metoda ustawiajaca czas do wyswietlenia
         * @param t liczba min do oznaczenia
         */
        public void displayTime(final int t)
        {
            time.setText(Integer.toString(t));
        }

        /**
         * Metoda rysujaca plansze na podstawie wprowadzonych danych
         * @param board tablica zawierajaca dane na temat planszy
         */
        public void drawBoard(byte board[][])
        {
            panel.drawPanel(board);
        }

        /**
         * Metoda tworzaca menu gry
         * @return panel z menu zawierajacym opcje gry
         */
        private JMenuBar createMenu()
        {
            JMenuItem newGame = new JMenuItem("Nowa gra");
            JMenuItem options = new JMenuItem("Opcje");
            JMenuItem finishGame = new JMenuItem("Zakoncz gre");

            newGame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try
                    {
                        blockingQueue.put(new StartGameEvent());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            });

            options.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showOptionsDialog();
                }
            });

            finishGame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try
                    {
                        blockingQueue.put(new FinishGameEvent());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            });

            JMenu game = new JMenu("Opcje gry");
            game.add(newGame);
            game.add(options);
            game.add(finishGame);

            JMenuBar menu = new JMenuBar();
            menu.add(game);
            return menu;
        }

        /**
         * Metoda tworzace okienko dialogowe w ktorym mozna dokonac wyboru poziomu gry
         * lub ustawic niestandardowe rozmiary
         * @return referencja na okno dialogowe
         */
        private JDialog showOptionsDialog()
        {
            JDialog dialog = new JDialog(SaperFrame.this, "Opcje gry", true);
            final ButtonGroup buttonGroup = new ButtonGroup();
            final JRadioButton beginner = new JRadioButton("Poczatkujacy");
            final JRadioButton advanced = new JRadioButton("Zaawansowany");
            final JRadioButton expert = new JRadioButton("Ekspert");
            final JRadioButton usersSettings = new JRadioButton("Niestandardowy");
            final JLabel labelWidth = new JLabel("Szerokosc (W) 8-30");
            final JLabel labelHeight = new JLabel("Wysokosc (H) 8-24");
            final JLabel labelMines = new JLabel("Liczba min >= 10 i <= (W * H) / 2");
            final JButton ok = new JButton("OK");


            dialog.setSize(FinalValues.OPTIONS_DIALOG_WIDTH, FinalValues.OPTIONS_DIALOG_HEIGHT);

            buttonGroup.add(beginner);
            buttonGroup.add(advanced);
            buttonGroup.add(expert);
            buttonGroup.add(usersSettings);

            dialog.setLayout(new GridLayout(5, 1));

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(1,3));
            panel.add(beginner);
            panel.add(advanced);
            panel.add(expert);

            dialog.add(panel);
            dialog.add(usersSettings);

            beginner.setSelected(true);     // domyslnie poziom latwy

            panel = new JPanel();
            panel.setLayout(new GridLayout(1,3));
            panel.add(labelWidth);
            panel.add(labelHeight);
            panel.add(labelMines);
            dialog.add(panel);

            panel = new JPanel();
            panel.setLayout(new GridLayout(1,3));
            final JTextComponent height = new JTextField();
            final JTextComponent width = new JTextField();
            final JTextComponent mines = new JTextField();

            JPanel secondPanel = new JPanel();
            secondPanel.setLayout(new BorderLayout());
            secondPanel.setBorder(new EtchedBorder());

            secondPanel.add(width);
            panel.add(secondPanel);

            secondPanel = new JPanel();
            secondPanel.setLayout(new BorderLayout());
            secondPanel.setBorder(new EtchedBorder());

            secondPanel.add(height);
            panel.add(secondPanel);

            secondPanel = new JPanel();
            secondPanel.setLayout(new BorderLayout());
            secondPanel.setBorder(new EtchedBorder());

            secondPanel.add(mines);
            panel.add(secondPanel);
            dialog.add(panel);

            panel = new JPanel();
            panel.setLayout(new FlowLayout());

            panel.setLayout(new FlowLayout());
            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    labelHeight.setEnabled(false);
                    labelWidth.setEnabled(false);
                    labelMines.setEnabled(false);
                    height.setEnabled(false);
                    width.setEnabled(false);
                    mines.setEnabled(false);
                }
            };

            actionListener.actionPerformed(null);
            beginner.addActionListener(actionListener);
            advanced.addActionListener(actionListener);
            expert.addActionListener(actionListener);
            usersSettings.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    labelHeight.setEnabled(true);
                    labelWidth.setEnabled(true);
                    labelMines.setEnabled(true);
                    height.setEnabled(true);
                    width.setEnabled(true);
                    mines.setEnabled(true);
                }
            });

           ok.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   Enumeration<AbstractButton> enumeration = buttonGroup.getElements();
                   if (enumeration.hasMoreElements())
                   {
                       try
                       {
                            if (beginner.isSelected())
                                blockingQueue.put(new SetMinefieldSizeEvent(FinalValues.BEGINNER_WIDTH, FinalValues.BEGINNER_HEIGHT, FinalValues.BEGINNER_MINES));
                            else if (advanced.isSelected())
                                blockingQueue.put(new SetMinefieldSizeEvent(FinalValues.ADVANCED_WIDTH, FinalValues.ADVANCED_HEIGHT, FinalValues.ADVANCED_MINES));
                            else if (expert.isSelected())
                                blockingQueue.put(new SetMinefieldSizeEvent(FinalValues.EXPERT_WIDTH, FinalValues.EXPERT_HEIGHT, FinalValues.EXPERT_MINES));
                            else if (usersSettings.isSelected())
                                blockingQueue.put(new SetMinefieldSizeEvent(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()), Integer.valueOf(mines.getText())));
                       }
                       catch (NumberFormatException ex)
                       {
                           boardCreationError();
                       }
                       catch (Exception ex)
                       {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                       }
                   }
                   dialog.dispose();
               }
           });
            panel.add(ok);
            dialog.add(panel);
            dialog.setVisible(true);
            return dialog;
        }


    }

    /**
     * Klasa sluzaca obsludze klikniec na panel okna gry
     */
    private class ClickListener extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent e)
        {
            try
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                    blockingQueue.put(new LeftMouseButtonPressedEvent(e.getX()/FinalValues.SQUARE_SIZE, e.getY()/FinalValues.SQUARE_SIZE));
                else
                    blockingQueue.put(new RightMouseButtonPressedEvent(e.getX()/FinalValues.SQUARE_SIZE, e.getY()/FinalValues.SQUARE_SIZE));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }
}
