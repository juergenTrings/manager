package herdenmanagement.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import herdenmanagement.model.Acker;
import herdenmanagement.model.Eimer;
import herdenmanagement.model.Gras;
import herdenmanagement.model.Position;
import herdenmanagement.model.PositionsElement;
import herdenmanagement.model.Rindvieh;

/**
 * Basisklasse für die Darstellung von Bundesländern wie Macklemburg-Vorpommern.
 *
 * Die AckerView ist Observer des Ackers. Werden auf letzterem Eimer, Gräser und Lebewesen
 * (insbesondere Kühe) eingefügt, informiert der Acker Objekte dieser Klasse AckerView über
 * die Änderungen. Es ist Aufgabe der AckerView für die neuen Elemente korrespondierend eine
 * {@link EimerView}, {@link GrasView} oder {@link RindviehView} zu erzeugen und als Child-Element
 * (siehe {@link #addView(View)}) anzuzeigen.
 */
public class AckerView extends FrameLayout implements PropertyChangeListener {

    private Animator animator;

    /**
     * Dargestellter Acker
     */
    private Acker acker;

    public AckerView(Context context) {
        super(context);
        setWillNotDraw(false);

        animator = new Animator(context);
    }

    /**
     * Erzeugt eine neue Ansicht für einen Acker
     *
     * @param context Context der App
     * @param attrs   Attribute zur grafischen Darstellung
     */
    public AckerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        animator = new Animator(context);
    }

    /**
     * Erzeugt eine neue Ansicht für einen Acker
     *
     * @param context      Context der App
     * @param attrs        Attribute zur grafischen Darstellung
     * @param defStyleAttr Attribute zum Stil
     */
    public AckerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        animator = new Animator(context);
    }

    public Animator.Threading getThreading() {
        return animator.getThreading();
    }

    public void setThreading(Animator.Threading threading) {
        animator.setThreading(threading);
    }

    /**
     * Beim Setzen des Ackers werden die momentan auf diesem vorhandene PositionsElement Objekte
     * angezeigt.
     *
     * @param acker Acker zur Ansicht
     */
    public void setAcker(Acker acker) {
        // Wenn die View bereits mit einem verknüpft ist,
        // wird diese Verknüpfiung jetzt aufgehoben
        if (this.acker != null) {
            this.acker.entferneBeobachter(this);

            // add initial objects from acker
            for (Rindvieh rindvieh : this.acker.getViecher()) {
                aktualisiereViecher(rindvieh, null);
            }

            for (Eimer eimer : this.acker.getEimer()) {
                aktualisiereEimer(eimer, null);
            }

            for (Gras gras : this.acker.getGraeser()) {
                aktualisiereGraeser(gras, null);
            }
        }

        // Der neue Acker wird gespeichert
        this.acker = acker;
        if (acker == null) {
            return;
        }

        // Die Verknüpfung mit dem neuen Acker herstellen
        this.acker.fuegeBeobachterHinzu(this);
        for (Rindvieh rindvieh : acker.getViecher()) {
            aktualisiereViecher(null, rindvieh);
        }

        for (Eimer eimer : acker.getEimer()) {
            aktualisiereEimer(null, eimer);
        }

        for (Gras gras : acker.getGraeser()) {
            aktualisiereGraeser(null, gras);
        }
    }

    /**
     * <p>
     * Measure the view and its content to determine the measured width and the
     * measured height. This method is invoked by {@link #measure(int, int)} and
     * should be overriden by subclasses to provide accurate and efficient
     * measurement of their contents.
     * </p>
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link android.view.View.MeasureSpec}.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link android.view.View.MeasureSpec}.
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // get the width from widthMeasureSpec
        float width = MeasureSpec.getSize(widthMeasureSpec);
        if (width < 0) {
            width = 0;
        }

        // get the height from widthMeasureSpec
        float height = MeasureSpec.getSize(heightMeasureSpec);
        if (height < 0) {
            height = 0;
        }

        int columns = acker == null ? 0 : acker.zaehleSpalten();
        float columnWidth = (int) (width / columns);

        int count = getChildCount();
        int rows = acker == null ? 0 : acker.zaehleZeilen();
        float rowHeight = (int) (height / rows);

        // set LayoutParams for all childs
        for (int i = 0; i < count; i++) {
            // check the class, avoid ClassCastExceptions for
            // custom child views
            if (!(getChildAt(i) instanceof PositionElementView)) {
                continue;
            }

            PositionElementView child = (PositionElementView) getChildAt(i);
            Position position = child.getPositionsElement().gibPosition();

            // set LayoutParams for child
            final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.width = (int) columnWidth;
            lp.height = (int) rowHeight;
            lp.leftMargin = (int) (position.x * columnWidth);
            lp.topMargin = (int) (position.y * rowHeight);

            // set exact size for child
            child.measure(lp.width | MeasureSpec.EXACTLY, lp.height | MeasureSpec.EXACTLY);
        }

        // set own size
        setMeasuredDimension((int) width, (int) height);
    }

    /**
     * Paint to draw a text. Reused in {@link #onDraw(Canvas)}
     */
    private static TextPaint TEXT_PAINT = new TextPaint();
    static {
        TEXT_PAINT.setTextSize(40);
        TEXT_PAINT.setColor(Color.LTGRAY);
    }

    /**
     * Paint to draw lines. Reused in {@link #onDraw(Canvas)}
     */
    private static Paint PAINT = new Paint();
    static {
        PAINT.setColor(Color.WHITE);
        PAINT.setStrokeWidth(6);
    }

    private static Rect TEXT_RECT = new Rect();

    protected void onDraw(Canvas canvas) {
        if (acker != null) {
            // Anzahl der Spalten und deren Breite ermitteln
            int columns = acker.zaehleSpalten();
            float columnWidth = getWidth() / columns;

            // Anzahl der Zeilen und deren Höhe ermitteln
            int rows = acker.zaehleZeilen();
            float rowHeight = getHeight() / rows;

            // Zeilenweise ....
            for (int y = 0; y < rows; y++) {

                // .... alle Spalten zeichnen
                for (int x = 0; x < columns; x++) {
                    // Hintergrund füllen
                    canvas.drawRect(
                            x * columnWidth + 4,
                            y * rowHeight + 4,
                            (x + 1) * columnWidth - 8,
                            (y + 1) * rowHeight - 8,
                            PAINT);

                    // textgröße berechnen und Text zentriert zeichnen
                    String text = x + ":" + y;
                    TEXT_PAINT.getTextBounds(text, 0, text.length(), TEXT_RECT);
                    canvas.drawText(
                            text,
                            (x + 0.5f) * columnWidth - TEXT_RECT.width() / 2,
                            (y + 0.5f) * rowHeight + TEXT_RECT.height() / 2,
                            TEXT_PAINT);
                }
            }
        }

        super.onDraw(canvas);
    }

    /**
     * Called from layout when this view should
     * assign a size and position to each of its children.
     * <p/>
     * Derived classes with children should override
     * this method and call layout on each of
     * their children.
     *
     * @param changed This is a new size or position for this view
     * @param left    Left position, relative to parent
     * @param top     Top position, relative to parent
     * @param right   Right position, relative to parent
     * @param bottom  Bottom position, relative to parent
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();

            child.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + lp.width, lp.topMargin + lp.height);
        }
    }

    /**
     * Legt die GUI Elemente an, wenn Eimer, Gräser etc. angelegt wurden
     *
     * @param evt Interessant sind z.B. die Nachrichten mit den Property-Name Acker.PROPERTY_EIMER
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (Acker.PROPERTY_EIMER.equals(evt.getPropertyName())) {
            aktualisiereEimer((Eimer) evt.getOldValue(), (Eimer) evt.getNewValue());
        }

        if (Acker.PROPERTY_VIECHER.equals(evt.getPropertyName())) {
            aktualisiereViecher((Rindvieh) evt.getOldValue(), (Rindvieh) evt.getNewValue());
        }

        if (Acker.PROPERTY_GRAESER.equals(evt.getPropertyName())) {
            aktualisiereGraeser((Gras) evt.getOldValue(), (Gras) evt.getNewValue());
        }

        // Bei Änderungen der Position, muss ein neues Layout berechnet werden
        if (PositionsElement.PROPERTY_POSITION.equals(evt.getPropertyName())) {
            animator.performAction(new Animator.Action() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(AckerView.this);
                    requestLayout();
                }
            });
        }
    }

    /**
     * Aktualisiert die Ansicht für die Gräser. Für neue Objekte
     * (newValue != null && oldValue == null) wird eine View erzeugt.
     * Bei zu entfernenden Objekte (newValue == null && oldValue != null)
     * wird auch die View entfernt.
     *
     * @param oldValue Neues Objekt auf dem Acker
     * @param newValue Bereits auf dem Acker vorhandenes Objekt
     */
    private void aktualisiereGraeser(final Gras oldValue, final Gras newValue) {
        if (newValue != null && oldValue == null) {
            newValue.fuegeBeobachterHinzu(this);
            addViewAmimated(new GrasView(getContext(), animator, newValue), false);
        } else if (newValue == null && oldValue != null) {
            oldValue.entferneBeobachter(this);
            removeViewAnimated(oldValue.gibId());
        }
    }

    /**
     * Aktualisiert die Ansicht für die Rinder. Für neue Objekte
     * (newValue != null && oldValue == null) wird eine View erzeugt.
     * Bei zu entfernenden Objekte (newValue == null && oldValue != null)
     * wird auch die View entfernt.
     *
     * @param oldValue Neues Objekt auf dem Acker
     * @param newValue Bereits auf dem Acker vorhandenes Objekt
     */
    private void aktualisiereViecher(final Rindvieh oldValue, final Rindvieh newValue) {
        if (newValue != null && oldValue == null) {
            newValue.fuegeBeobachterHinzu(this);
            addViewAmimated(new RindviehView(getContext(), animator, newValue), true);
        } else if (newValue == null && oldValue != null) {
            oldValue.entferneBeobachter(this);
            removeViewAnimated(oldValue.gibId());
        }
    }

    /**
     * Aktualisiert die Ansicht für die Eimer. Für neue Objekte
     * (newValue != null && oldValue == null) wird eine View erzeugt.
     * Bei zu entfernenden Objekte (newValue == null && oldValue != null)
     * wird auch die View entfernt.
     *
     * @param oldValue Neues Objekt auf dem Acker
     * @param newValue Bereits auf dem Acker vorhandenes Objekt
     */
    private void aktualisiereEimer(final Eimer oldValue, final Eimer newValue) {
        if (newValue != null && oldValue == null) {
            newValue.fuegeBeobachterHinzu(this);
            addViewAmimated(new EimerView(getContext(), animator, newValue), false);
        } else if (newValue == null && oldValue != null) {
            oldValue.entferneBeobachter(this);
            removeViewAnimated(oldValue.gibId());
        }
    }

    /**
     * @param id Ressourcen-ID der zu entfernenden View
     */
    private void removeViewAnimated(final int id) {
        animator.performAction(new Animator.Action() {
            @Override
            public void run() {
                // fade out the view
                View v = findViewById(id);

                // avoid NPE
                if (v == null) {
                    return;
                }

                // fade out
                TransitionManager.beginDelayedTransition(AckerView.this);
                v.setAlpha(0);

                // remove the view
                removeView(v);

                // layout anpassen?
                requestLayout();
                invalidate();
            }
        });
    }

    /**
     * Fügt dem Acker eine neue Darstellung für Eimer, Gras, etc. hinzu
     *
     * @param view Hinzufügende View
     * @param onTop true fürgt die View über alle anderen ein
     */
    private void addViewAmimated(final View view, final boolean onTop) {
        animator.performAction(new Animator.Action() {
            @Override
            public void run() {
                // langsam einblenden
                view.setAlpha(0);

                TransitionManager.beginDelayedTransition(AckerView.this);
                view.setAlpha(1);

                if (onTop) {
                    addView(view);
                } else {
                    addView(view, 0);
                }

                // layout anpassen?
                requestLayout();
                invalidate();
            }
        });
    }

    /**
     * @return Dargestellter Acker
     */
    public Acker getAcker() {
        return acker;
    }
}
