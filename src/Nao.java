// Dit is een project gemaakt door: Ahmed El Ela en Valentijn Bruggeman
// Dit project heeft als eind doel om een NAO-robot te laten bewegen, dansen en muziek af te laten spelen. Daarnaast controleert de NAO of zijn bewegingen correct nagedaan worden
// Project groep naam: PAD-04
// Klas: IT101

package src;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALAutonomousBlinking;
import src.configuration.ConfigureNao;
import src.core.BehaviourController;
import src.leds.OogController;
import src.memory.Memory;
import src.motion.BackgroundMovement;
import src.motion.MotionController;
import src.motion.PostureController;
import src.motion.TrackerController;
import src.vision.RedBallDetection;
import src.speech.TextToSpeech;

public class Nao {
    private Application application;
    private TextToSpeech tts;
    private OogController ogen;
    private PostureController posture;
    private MotionController motion;
    private RedBallDetection redBallDetection;
    private Memory memory;
    private TrackerController redBallTracker;

    // can be used in later code maybe??
    private long redBallid;
	private BehaviourController behaviour;

    private BackgroundMovement ALbackgroundmovement;


// Verbind met robot
    public void verbind() throws Exception {
        String robotUrl = "tcp://" + ConfigureNao.HOSTNAME + ":" + ConfigureNao.PORT;
        // Create a new application
        this.application = new Application(new String[]{}, robotUrl);
        // Start your application
        application.start();

        tts = new TextToSpeech(application.session());
        ogen = new OogController(application.session());
        posture = new PostureController(application.session());
        motion = new MotionController(application.session());
        redBallDetection = new RedBallDetection(application.session());
        memory = new Memory(application.session());
        redBallTracker = new TrackerController(application.session());
		behaviour = new BehaviourController(application.session());
        ALbackgroundmovement = new BackgroundMovement(application.session());

    }
// Praten
    public void praten(String tekst) throws Exception {
        this.tts.praten(tekst);
    }
// Oog leds bedienen
    public void bepaalOogKleur(String color, float duration) throws Exception {
        ogen.bepaalOogKleur(color, duration);
    }
// Armen bewegen
    // Posture (stand, crouch & sit)
    public void postureInput(String postureName, float maxSpeedFraction) throws Exception {
        posture.postureInput(postureName, maxSpeedFraction);
    }
    public void bepaalMotion(String names, double angleLists, float timeLists, boolean isAbsolute) throws Exception {
        motion.bepaalMotion(names, angleLists, timeLists, isAbsolute);
    }
// rood herkennen (is nog niet helemaal netjes)
    public void detectRedBall() throws Exception {
        redBallDetection.subscribe();
        memory.subscribeToEvent("redBallDetected", data ->
                System.out.println("Red ball detected"));
    }
// rode bal tracken (bekijk de TrackerController voor comments)
    public void track(String pMode, Float pMaxDistance, String pTarget, Object pParams, String pEffector) throws CallError, InterruptedException {
        redBallTracker.startTracker(pMode, pMaxDistance, pTarget, pParams, pEffector);
    }
// niets meer tracken
    public void stopTracker() throws CallError, InterruptedException {
        redBallTracker.stopTracker();
    }
// loop om tijdelijk events te controllen
    public void doWhile(int millis, int time) throws InterruptedException {
        int counter = 0;
        do {
            Thread.sleep(millis);
            counter++;
        } while (counter<time);
    }
	public void bepaalBehaviour(String behavior) throws CallError, InterruptedException{
        behaviour.bepaalBehaviour(behavior);
    }

    public void setBackgroundmovement(boolean enabled) throws CallError, InterruptedException {
        ALbackgroundmovement.moveInBackground(enabled);
    }

    public void touchButton(String sensorName, EventCallback touchEventCallback) throws Exception {
        switch (sensorName) {
            case "Front":
                memory.subscribeToEvent("FrontTactilTouched", touchEventCallback);
                break;

            case "Middle":
                memory.subscribeToEvent("MiddleTactilTouched", touchEventCallback);
                break;

            case "Rear":
                memory.subscribeToEvent("RearTactilTouched", touchEventCallback);
                break;

            default:
                throw new IllegalArgumentException("Invalid sensor name: " + sensorName);
        }
    }


}
