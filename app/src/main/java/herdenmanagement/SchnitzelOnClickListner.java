package herdenmanagement;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import de.ba.herdenmanagement.R;
import herdenmanagement.model.Acker;
import herdenmanagement.model.Rindvieh;
import herdenmanagement.model.SchnitzelAcker;
import herdenmanagement.view.AckerView;

public class SchnitzelOnClickListner implements View.OnClickListener {
    private MainActivity mainActivity;
    private Rindvieh vera;

    public SchnitzelOnClickListner(MainActivity mainActivity, Rindvieh vera) {
        this.mainActivity = mainActivity;
        this.vera = vera;
    }

    @Override
    public void onClick(View view) {
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
