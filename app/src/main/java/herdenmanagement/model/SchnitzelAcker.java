package herdenmanagement.model;

import java.util.List;

/**
 * Diese Klasse erweitert die Klasse {@link Acker} Um Rinder von dem Acker löschen zu können und
 * automatisch Gras hinzuzufügen.
 */
public class SchnitzelAcker extends Acker {
    /**
     * erzeugt einen neuen Acker.
     * @param spalten Spaltenanzahl die der Acker haben soll
     * @param zeilen Zeilenanzahl die der Acker haben soll
     */
    public SchnitzelAcker(int spalten, int zeilen) {
        super(spalten, zeilen);
    }

    /**
     * Diese Methode entfernt das angegebene objekt rindvieh der Klasse {@link Rindvieh} von dem Acker
     * @param rind objekt der Klasse {@link Rindvieh}
     */
    public void lassRindVerschwinden(Rindvieh rind) {
        List<Rindvieh> viecher = getViecher();
        int pos = viecher.indexOf(rind);
        if (pos != -1) {
            viecher.remove(pos);
        }
        informiereBeobachter(PROPERTY_VIECHER, rind, null);
    }

    /**
     * Diese Methode prüft ob sich auf dem Acker noch Gras objekte der Klasse {@link Gras} befindet
     * und verteilt 5 neue Gräser zufällig wenn keine Gräser mehr auf dem Acker sind.
     */
    public void pruefeGras() {
        if (this.getGraeser().size() == 0) {
            int i = 0;
            int zeilenMax = this.zaehleZeilen();
            int spaltenMax = this.zaehleSpalten();

            while (i < 5) {
                Position pos = new Position((int) (Math.random() * spaltenMax), (int) (Math.random() * zeilenMax));
                if (!this.istDaGras(pos) && !this.istDaEinEimer(pos)) {
                    this.lassGrasWachsen(pos);
                    i++;
                }
            }
        }
    }
}
