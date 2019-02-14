package herdenmanagement;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import herdenmanagement.model.SchnitzelAcker;
import herdenmanagement.model.Rindvieh;
import herdenmanagement.AckerObserver;

public class SchnitzelAckerUnitTest {

    private PropertyChangeEvent evt;
    private PropertyChangeListener listener;

    @Before
    public void setUp() {
        listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SchnitzelAckerUnitTest.this.evt = evt;
            }
        };
    }

    @Test
    public void SchnitzelAcker() {
        SchnitzelAcker acker = new SchnitzelAcker(10, 10);
        acker.fuegeBeobachterHinzu(listener);

        Rindvieh rindvieh = new Rindvieh("Rindvieh");
        acker.lassRindWeiden(rindvieh);
        assertEquals(SchnitzelAcker.PROPERTY_VIECHER, evt.getPropertyName());
        assertTrue(evt.getNewValue() instanceof Rindvieh);
        acker.lassRindVerschwinden(rindvieh);
        assertEquals(0, acker.getViecher().size());
    }
}
