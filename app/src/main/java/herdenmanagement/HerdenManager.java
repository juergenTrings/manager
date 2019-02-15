package herdenmanagement;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import de.ba.herdenmanagement.R;
import herdenmanagement.model.Acker;
import herdenmanagement.model.Position;
import herdenmanagement.model.Rindvieh;
import herdenmanagement.model.SchnitzelAcker;
import herdenmanagement.view.AckerView;

/**
 * Die Klasse dient der Orginisation von Rinderherden. Hierzu werden auf einem {@link Acker}
 * Objekte der Klase {@link herdenmanagement.model.Eimer} und {@link herdenmanagement.model.Gras}
 * positioniert. Objekte der Klasse {@link Rindvieh} könen sich auf einem Acker bewegen
 * und das Gras fressen oder rauchen. Steht auf der aktuellen Position einer kuh ein Eimer,
 * kann diese auch gemolken werden.
 * <p>
 * Mit einer {@link AckerView} wird ein erzeugter Acker auch grafisch angezeigt.
 * Auf diesem können Instanzen von {@link Rindvieh}, {@link herdenmanagement.model.Eimer} und
 * {@link herdenmanagement.model.Gras} eingefügt werden.
 * <p>
 * Im Muster Model View Controller (MVC) entsprechen Objekte dieser Klasse dem Controller.
 * {@link Acker}, {@link Rindvieh}, {@link herdenmanagement.model.Eimer} und
 * {@link herdenmanagement.model.Gras} bilden im MVC Muster das Model. Im Muster Observer
 * stellen sie die beobachtbaren Objekte dar. Die eigentliche grafische Darstellung des Models
 * erfolgt in den View-Klassen des MVC Musters (also zum Beispiel in der Klasse
 * {@link herdenmanagement.view.RindviehView}. Diese View-Klassen sind gleichzeit Beobachter
 * gemäß des Observer Muster. Wenn man also Veränderungen an einer kuh vornimmt, wird diese
 * Ihre Beaobachter informieren und diese passen ihre grafische Darstellung an.
 * <p>
 * Die Klasse verknüpft im Wesentlichen einen {@link Acker} (= Model im MVC Muster) mit seiner
 * {@link AckerView} (= View im MVC Muster). Da sie diese und andere Vorgänge
 * (insbesondere Änderungen auf Acker) organisiert, ist sie der Controller im MVC Muster.
 */
public class HerdenManager {

    /**
     * Acker, auf dem nach Herzenslust geackert werden kasnn.
     */
    private SchnitzelAcker acker;

    /**
     * Rindvieh, dass wirklich gern auf dem Acker steht und lustige Geräusche macht.
     */
    private Rindvieh vera;

    /**
     *  Der Switch um das Rindvieh zu erstellen bzw. zu vernichten
     */
    private Switch buttonKuh;

    /**
     * Der Button um Gras zu fressen
     */
    private Button buttonGras;
    /**
     * Der Button um Gras zu rauchen
     */
    private Button buttonRauchen;

    /**
     * Der Button um Milch in den Eimer zu geben.
     */
    private Button buttonMilch;


    /**
     * Aufruf zur Erzeugung eines HerdenManagers.
     * Diese Methode lässt zum Beispiel Gras wachsen und stellt Eimer auf.
     * Die Einrichtung des Ackers wird nicht animiert dargestellt.
     *
     * @param mainActivity Hauptaktivität der App
     */
    @SuppressLint("ClickableViewAccessibility")
    public void richteAckerEin(MainActivity mainActivity) {
        // Die View, die die Acker Elememnte anzeigen kann, wird in der Datei
        // res/activity_main.xml optisch ausgerichtet und in der App platziert
        AckerView ackerView = mainActivity.findViewById(R.id.acker_view);

        // Acker erzeugen
        acker = new SchnitzelAcker(5, 7);

        // AckerView mit Acker verknüpfen
        ackerView.setAcker(acker);

        // Acker befüllen
        acker.lassGrasWachsen(new Position(1, 1));
        acker.stelleEimerAuf(new Position(2, 2));
        acker.lassGrasWachsen(new Position(2, 4));
        acker.lassGrasWachsen(new Position(2, 5));
        acker.lassGrasWachsen(new Position(3, 2));
        acker.lassGrasWachsen(new Position(3, 4));

        // Acker Bewegung erstellen
        AckerObserver ackerObserver = new AckerObserver();
        ackerView.setOnTouchListener(ackerObserver);
    }

    /**
     * Hier wird eine Herde gemanagt. Und zwar professionell. Das bedeutet vor allem,
     * dass Rinder bewegt und zum Fressen angehalten werden. Natürlich können Sie danach
     * Milch geben!
     * <p>
     * Die Aktionen dieser Methoden werden animiert und nacheinander dargestellt. Man kann in der
     * App also die Reihenfolge der Aktionen sehen (anders als die Aktionen in
     * {@link #richteAckerEin(MainActivity)}.
     *
     * @param mainActivity Hauptaktivität
     */
    public void manageHerde(final MainActivity mainActivity) {

        vera = new Rindvieh("Schmois");
        buttonKuh = mainActivity.findViewById(R.id.kuhBtn);
        buttonMilch = mainActivity.findViewById(R.id.milchBtn);
        buttonGras = mainActivity.findViewById(R.id.grasBtn);
        buttonRauchen = mainActivity.findViewById(R.id.rauchBtn);

        buttonKuh.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (buttonKuh.isChecked()) {
                            acker.lassRindWeiden(vera);
                            buttonKuh.setText(R.string.kuhBtn_on);
                        } else {
                            acker.lassRindVerschwinden(vera);
                            buttonKuh.setText(R.string.kuhBtn_off);
                        }
                    }
                }
        );

        buttonGras.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vera.frissGras();
                        v.setEnabled(false);
                        buttonRauchen.setEnabled(false);
                        acker.pruefeGras();
                    }
                }
        );

        buttonRauchen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vera.raucheGras();
                        v.setEnabled(false);
                        buttonGras.setEnabled(false);
                        acker.pruefeGras();
                    }
                }
        );
        buttonMilch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vera.gibMilch();
                        v.setEnabled(false);
                    }
                }
        );
    }
}
