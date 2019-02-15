package herdenmanagement.model;


import android.view.View;

import java.util.List;

import herdenmanagement.view.AckerView;

public class SchnitzelAcker extends Acker {
    public SchnitzelAcker(int spalten, int zeilen){
        super(spalten, zeilen);
    }
    public void lassRindVerschwinden(Rindvieh rind){
        List<Rindvieh> viecher = getViecher();
        int pos = viecher.indexOf(rind);
        if(pos != -1){
            viecher.remove(pos);
        }
        informiereBeobachter(PROPERTY_VIECHER, rind, null);
    }
    public void pruefeGras(){
        if(this.getGraeser().size() == 0){
            int i = 0;
            int zeilenMax = this.zaehleZeilen();
            int spaltenMax = this.zaehleSpalten();

            while (i < 5){
                int rdmY = (int) (Math.random()*zeilenMax);
                int rdmX = (int) (Math.random()*spaltenMax);
                if (!this.istDaGras(new Position(rdmX, rdmY)) && !this.istDaEinEimer(new Position(rdmX,rdmY))) {
                    this.lassGrasWachsen(new Position(rdmX,rdmY));
                    i++;
                }
            }
        }
    }
}
