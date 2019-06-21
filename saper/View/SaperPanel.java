package saper.View;

import saper.AdditionalData.FinalValues;

import javax.swing.*;
import java.awt.*;

public class SaperPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * Tablica zawierajaca obrazki ktore mozna umiescic na planszy
     * Indeksy obrazkow odpowiadajace definicjom umieszczonymi w klasie Posibilities
     */
    private static Image images[] = new Image[15];
    {
        images[0] = new ImageIcon(this.getClass().getResource("images/0.jpg")).getImage();
        images[1] = new ImageIcon(this.getClass().getResource("images/1.jpg")).getImage();
        images[2] = new ImageIcon(this.getClass().getResource("images/2.jpg")).getImage();
        images[3] = new ImageIcon(this.getClass().getResource("images/3.jpg")).getImage();
        images[4] = new ImageIcon(this.getClass().getResource("images/4.jpg")).getImage();
        images[5] = new ImageIcon(this.getClass().getResource("images/5.jpg")).getImage();
        images[6] = new ImageIcon(this.getClass().getResource("images/6.jpg")).getImage();
        images[7] = new ImageIcon(this.getClass().getResource("images/7.jpg")).getImage();
        images[8] = new ImageIcon(this.getClass().getResource("images/8.jpg")).getImage();
        images[9] = new ImageIcon(this.getClass().getResource("images/covered.jpg")).getImage();
        images[10] = new ImageIcon(this.getClass().getResource("images/question_mark.jpg")).getImage();
        images[11] = new ImageIcon(this.getClass().getResource("images/bomb.jpg")).getImage();
        images[12] = new ImageIcon(this.getClass().getResource("images/red_bomb.jpg")).getImage();
        images[13] = new ImageIcon(this.getClass().getResource("images/bad_bomb.jpg")).getImage();
        images[14] = new ImageIcon(this.getClass().getResource("images/flag.jpg")).getImage();
    }

    /**
     * Tablica przechowujaca referencje do tablic z danymi do odswiezania pola gry
     */
    private byte tab[][];

    /**
     * Konstruktor wywoluje setBounds poniewaz rodzic nie powinien stosowac menadzera rozkladu
     */
    SaperPanel()
    {
        setBounds(0,0,1,1);
    };

    /**
     * Przesloniona metoda rysujaca komponent
     */
    public void paintComponent(Graphics graphic){
        super.paintComponent(graphic);
        Component component = getParent();
        setBounds((component.getWidth() - FinalValues.SQUARE_SIZE * tab.length)/2,(component.getHeight()- FinalValues.SQUARE_SIZE * tab[0].length)/2,FinalValues.SQUARE_SIZE * tab.length,FinalValues.SQUARE_SIZE * tab[0].length);
        for(int i = 0;i < tab.length;++i)
            for(int j = 0;j < tab[i].length;++j)
                graphic.drawImage(images[tab[i][j]],i * FinalValues.SQUARE_SIZE,j *FinalValues.SQUARE_SIZE,null);
    }

    /**
     * Metoda ktora na podstawie zadanych danych rysuje plansze na ktorej rozgrywa sie rozgrywka
     * @param minefield zawiera wiadomosci co trzeba narysowac w danym miejscu planszy
     */
    public void drawPanel(byte minefield[][])
    {
        tab = minefield;
        repaint();
    }
}
