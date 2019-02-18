package herdenmanagement;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import de.ba.herdenmanagement.R;
import herdenmanagement.model.Acker;
import herdenmanagement.model.Rindvieh;
import herdenmanagement.model.SchnitzelAcker;
import herdenmanagement.view.AckerView;


/**
 * Die Klasse die Als Onclicklistner für die Buttons fungiert.
 */
public class SchnitzelOnClickListener implements View.OnClickListener {
    private Rindvieh vera;

    /**
     * Constructor für die Klasse. Zuweisung eines Rindviehs.
     * @param vera Rindvieh welches auf den Acker gesetzt werden soll.
     */
    public SchnitzelOnClickListener(Rindvieh vera) {
        this.vera = vera;
    }

    /**
     * Verwalten alle Buttons. Wenn der
     * @param view angeklickte View.
     */
    @Override
    public void onClick(View view) {
        View mainActivity = view.getRootView();
        AckerView ackerView = mainActivity.findViewById(R.id.acker_view);
        Acker acker = ackerView.getAcker();
        SchnitzelAcker schnitzelAcker = (SchnitzelAcker) acker;

        switch (view.getId()) {
            case R.id.kuhBtn:
                Switch buttonKuh = (Switch) view;
                if (buttonKuh.isChecked()) {
                    acker.lassRindWeiden(vera);
                    buttonKuh.setText(R.string.kuhBtn_on);
                } else {
                    schnitzelAcker.lassRindVerschwinden(vera);
                    buttonKuh.setText(R.string.kuhBtn_off);
                }
                break;
            case R.id.grasBtn:
                Button buttonGras = (Button) view;
                vera.frissGras();
                buttonGras.setEnabled(false);
                mainActivity.findViewById(R.id.rauchBtn).setEnabled(false);
                schnitzelAcker.pruefeGras();
                break;
            case R.id.rauchBtn:
                Button buttonRauch = (Button) view;
                vera.raucheGras();
                buttonRauch.setEnabled(false);
                mainActivity.findViewById(R.id.grasBtn).setEnabled(false);
                schnitzelAcker.pruefeGras();
                break;
            case R.id.milchBtn:
                Button buttonMilch = (Button) view;
                vera.gibMilch();
                buttonMilch.setEnabled(false);
                break;
        }
    }
}
