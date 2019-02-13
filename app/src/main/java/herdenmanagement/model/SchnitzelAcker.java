package herdenmanagement.model;


import java.util.List;

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

    }
}
