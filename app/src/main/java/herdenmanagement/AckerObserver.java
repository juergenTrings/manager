package herdenmanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import de.ba.herdenmanagement.R;
import herdenmanagement.model.Position;


import herdenmanagement.model.Acker;
import herdenmanagement.model.Rindvieh;
import herdenmanagement.model.SchnitzelRind;
import herdenmanagement.view.AckerView;

public class AckerObserver implements View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    public AckerObserver() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ackerPressed(v, event);
        }
        return true;
    }

    private void ackerPressed(View v, MotionEvent event) {
        if(v instanceof AckerView){
            AckerView ackerView = (AckerView) v;
            Acker acker = ackerView.getAcker();
            if(acker.getViecher().size() == 1){
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

        return new Position(spalte, zeile);
    }

    private void buttonStatus(AckerView v) {
        View rootView = v.getRootView();
        Activity c = (Activity) v.getContext();
        Acker acker = v.getAcker();
        Rindvieh kuh = acker.getViecher().get(0);
        if(acker.getViecher().size() == 1){
            final Button buttonMilch = rootView.findViewById(R.id.milchBtn);
            final Button buttonGras = rootView.findViewById(R.id.grasBtn);
            final Button buttonRauch = rootView.findViewById(R.id.rauchBtn);

            // Rauchbutton
            if (acker.istDaGras(kuh.gibPosition())) {
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonGras.setVisibility(View.VISIBLE);
                        buttonRauch.setVisibility(View.VISIBLE);
                        buttonMilch.setVisibility(View.INVISIBLE);
                    }
                });
            } else if (acker.istDaEinEimer(kuh.gibPosition())) {
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonMilch.setVisibility(View.VISIBLE);
                        buttonGras.setVisibility(View.INVISIBLE);
                        buttonRauch.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonMilch.setVisibility(View.INVISIBLE);
                        buttonGras.setVisibility(View.INVISIBLE);
                        buttonRauch.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }

    }
}
