package herdenmanagement.view;

import android.app.Activity;
import android.content.Context;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Der Animator versieht die Statusänderung von Objekten mit einer Animation. Ändert eine
 * View zum Beispiel Ihre Position, sorgt der Animator für einen sanften Übergang. Der Nutzer
 * erhält den Eindruck, dass die View sich tatsächlich langsam zu den neuen Koordianten bewegt.
 * <P>
 * Ein Animator wird folgerichtig den View Klassen (@see {@link PositionElementView} im
 * HerdenManagement zugewiesen und von der {@link AckerView} erzeugt und verwaltet.
 * <P>
 * Die Methoden des Klassen aus dem Paket herdenmanagement.model laufen in einem Thread,
 * der von den Methoden der Klassen aus dem Paket herdenmanagement.view losgelöst ist.
 * Der Grund liegt in der Tatsache begründet, dass Änderungen am Layout nur im
 * Main Thread einer Android App vorgenommen werden können.
 */
public class Animator {

    /**
     * Wartezeit für Bewegungen in ms
     */
    public static int WARTEZEIT = 500;

    /**
     * Werden PositionsElemente mit Buttons bewegt, sollte der Animator im Modus
     * ASYNCHRONOUS betrieben werden. Dies stellt die Sichtbarkeit aller Aktionen sicher.
     */
    public enum Threading {
        SYNCHRONOUS,
        ASYNCHRONOUS,
        ASYNCHRONOUS_NO_WAIT
    }

    private Threading threading = Threading.SYNCHRONOUS;

    /**
     * true, wenn im Modus ASYNCHRONOUS regelmäßig Aktionen ausgeführt werden sollen
     */
    private boolean running = false;

    /**
     * Context der App. Dient der Ermittlung des UI-Thread.
     */
    private Activity context;

    /**
     * Liste von Actions, die nacheinander abgearbeitet werden
     */
    private Queue<Action> actions = new ArrayBlockingQueue<>(1024);

    /**
     * Erzeugt einen Animator im gegeben Context (= In der Ragel die MainActivity)
     *
     * @param context Context der aktuellen App
     */
    public Animator(Context context) {
        if (context instanceof Activity) {
            this.context = (Activity) context;
        }
    }

    /**
     * @return SYNCHRONOUS oder ASYNCHRONOUS
     */
    public Threading getThreading() {
        return threading;
    }

    /**
     * @param threading siehe {@link #threading}
     */
    public void setThreading(Threading threading) {
        if (this.threading == Threading.SYNCHRONOUS && (threading == Threading.ASYNCHRONOUS || threading == Threading.ASYNCHRONOUS_NO_WAIT)) {
            this.threading = threading;
            running = true;
            start();
        } else if ((this.threading == Threading.ASYNCHRONOUS || this.threading == Threading.ASYNCHRONOUS_NO_WAIT) && threading == Threading.SYNCHRONOUS) {

            // alle Aktionen abarbeiten
            if (running) {
                while (!actions.isEmpty()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            this.threading = threading;
            running = false;
        }
    }

    /**
     * Action, die vom Animatopr ausgeführt werden kann.
     */
    public static abstract class Action implements Runnable {

        private int waitingTime;

        /**
         * Erzeugt eine Action. Die Wartezeit nach Ausführung der Action
         * ist {@link Animator#WARTEZEIT}
         */
        public Action() {
            this(Animator.WARTEZEIT);
        }

        /**
         * Erzeugt eine Action
         *
         * @param waitingTime Wartezeit nach Ausführung der Action
         */
        public Action(int waitingTime) {
            this.waitingTime = waitingTime;
        }

        /**
         * Schläft die in waitingTime eingestellte Zahl Millisekunden
         */
        public void sleep() {
            if (waitingTime < 0) {
                return;
            }

            try {
                Thread.sleep(waitingTime);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Fügt eine Action hinzu
     *
     * @param action Auszuführende Aktion
     */
    public void performAction(Action action) {
        if (threading == Threading.ASYNCHRONOUS || threading == Threading.ASYNCHRONOUS_NO_WAIT) {
            actions.add(action);
        } else {
            // perform the action
            context.runOnUiThread(action);

            // wait the predefined waiting time
            action.sleep();
        }
    }

    /**
     * Startet den Animator. Dieser Thread arbeitet alle Actions in actions ab und führt
     * sie im UI-Thread des Context aus.
     */
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {

                    if (!actions.isEmpty()) {
                        Action action = actions.poll();

                        if (action == null) {
                            continue;
                        }

                        // perform the action
                        context.runOnUiThread(action);

                        // wait the predefined waiting time
                        if (threading != Threading.ASYNCHRONOUS_NO_WAIT) {
                            action.sleep();
                        }
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }).start();
    }
}
