package herdenmanagement;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import de.ba.herdenmanagement.R;
import herdenmanagement.model.Position;

import herdenmanagement.model.Acker;
import herdenmanagement.model.Rindvieh;
import herdenmanagement.view.AckerView;

/**
 * Die Klasse überwacht den Acker. Sie bewegt ein Rindvieh wenn auf den Acker geklickt wird und
 * verändert den Status der Buttons.
 * Sie Implementiert das Interface {@link View.OnTouchListener} um einem Acker zugeordnet zu werden.
 */

public class AckerObserver implements View.OnTouchListener {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ackerPressed(v, event);
        }
        return true;
    }

    /**
     * Diese Methode wandelt positioniert ein rindvieh auf das angeklickte Ackerfeld und lässt die
     * Buttons aktualisierten.
     * Sie überprüft ob die {@link View}
     *
     * @param v     View objekt die Angeklickt wurde (in diesem Fall eine AckerView)
     * @param event MotionEvent objekt von dem onTouch.
     */
    private void ackerPressed(View v, MotionEvent event) {
        if (v instanceof AckerView) {
            AckerView ackerView = (AckerView) v;
            Acker acker = ackerView.getAcker();
            if (acker.getViecher().size() == 1) {
                Position FeldPos = feldSuchen(event.getX(), event.getY(), ackerView);
                acker.getViecher().get(0).setzePosition(FeldPos);
                buttonStatus(ackerView);
            }
        }
    }

    private Position feldSuchen(float x, float y, AckerView v) {
        Acker acker = v.getAcker();
        int spalte = (int) (x / (v.getWidth() / (float) acker.zaehleSpalten()));
        int zeile = (int) (y / (v.getHeight() / (float) acker.zaehleZeilen()));
        Position pos = new Position(spalte, zeile);
        return pos;
    }

    private void buttonStatus(AckerView v) {
        View rootView = v.getRootView();
        Acker acker = v.getAcker();
        Rindvieh kuh = acker.getViecher().get(0);
        if (acker.getViecher().size() == 1) {
            final Button buttonMilch = rootView.findViewById(R.id.milchBtn);
            final Button buttonGras = rootView.findViewById(R.id.grasBtn);
            final Button buttonRauch = rootView.findViewById(R.id.rauchBtn);

            buttonMilch.setEnabled(false);
            buttonGras.setEnabled(false);
            buttonRauch.setEnabled(false);

            // Rauchbutton
            if (acker.istDaGras(kuh.gibPosition())) {
                buttonGras.setEnabled(true);
                buttonRauch.setEnabled(true);
            } else if (acker.istDaEinEimer(kuh.gibPosition())) {
                buttonMilch.setEnabled(true);
            }
        }

    }
}
