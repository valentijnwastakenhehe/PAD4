// Dit is een project gemaakt door: Ahmed El Ela en Valentijn Bruggeman
// Dit project heeft als eind doel om een NAO-robot te laten bewegen, dansen en muziek af te laten spelen. Daarnaast controleert de NAO of zijn bewegingen correct nagedaan worden
// Project groep naam: PAD-04
// Klas: IT101

package src;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;
import src.configuration.ConfigureNao;
import src.core.BehaviourController;
import src.leds.OogController;
import src.motion.MotionController;
import src.motion.PostureController;
import src.motion.TrackerController;
import src.memory.Memory;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALAutonomousBlinking;
import src.motion.*;
import src.vision.RedBallDetection;
import src.speech.TextToSpeech;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Nao {
    private Application application;
    private TextToSpeech tts;
    private OogController ogen;
    private PostureController posture;
    private MotionController motion;
    private RedBallDetection redBallDetection;
    private Memory memory;
    private ALMemory newALMemory;
    private static TrackerController redBallTracker;
    // can be used in later code maybe??
    private long redBallid;
	private BehaviourController behaviour;
    public static float x;
    public static float y;
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
		newALMemory = new ALMemory(application.session());
		ALbackgroundmovement = new BackgroundMovement(application.session());
        autonomousLife = new ALAutonomousLife(application.session());


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
        memory.subscribeToEvent("redBallDetected", o -> {
            System.out.println("red ball detected");
            List<Object> data = (List<Object>) o;
            List<Float> BallInfo = (List<Float>) data.get(1);
            x = BallInfo.get(0);
            y = BallInfo.get(1);
            System.out.println("Red ball position: x=" + x + ", y=" + y);
        });
    }
// rode bal tracken (bekijk de TrackerController voor comments)
    public void track(String pMode, Float pMaxDistance, String pTarget, Object pParams, String pEffector) throws CallError, InterruptedException {
        redBallTracker.startTracker(pMode, pMaxDistance, pTarget, pParams, pEffector);
    }
// niets meer tracken
    public void stopTracker() throws CallError, InterruptedException {
        redBallTracker.stopTracker();
    }
// do while loop om tijdelijk events te controllen
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
    public float[] returnPosition(int index) throws CallError, InterruptedException {

        return redBallTracker.getPosition(1);
    }
    public void checkBallonBoven() throws Exception {
        System.out.println("ik check de ballon hoogte");

        while (true) {
            if (x >= -2 && x <= 2 && y >= -2 && y <= -0.2) {
                bepaalOogKleur("green", 1);
                praten("Goed gedaan");
                break;
            } else {
                bepaalOogKleur("red", 1);
                praten("Beweeg nu je armen omhoog!");
            }
        }
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
    public void checkBallonLinks() throws Exception {
        System.out.println("ik check de ballon hoogte");

        while (true) {
            if (x >= -2 && x <= 0 && y >= -0.2 && y <= 0.2) {
                bepaalOogKleur("green", 1);
                praten("Goed gedaan");
                break;
            } else {
                bepaalOogKleur("red", 1);
                praten("Beweeg nu je armen naar links!");
            }
        }
    }
    public void checkBallonRechts() throws Exception {
        System.out.println("ik check de ballon hoogte");

        while (true) {
            if (x >= 0 && x <= 2 && y >= -0.2 && y <= 0.2) {
                bepaalOogKleur("green", 1);
                praten("Goed gedaan");
                break;
            } else {
                bepaalOogKleur("red", 1);
                praten("Beweeg nu je armen naar rechts!");
            }
        }
    }
    public void checkBallonLaag() throws Exception {
        System.out.println("ik check de ballon hoogte");

        while (true) {
            if (x >= -2 && x <= 2 && y >= 0.1 && y <= 2) {
                bepaalOogKleur("green", 1);
                praten("Goed gedaan");
                break;
            } else {
                bepaalOogKleur("red", 1);
                praten("Beweeg nu je armen naar beneden!");
            }
        }
    }
    public void ballonVastHouden() throws Exception {
        motion.fingerControl();
        motion.wristControl(0.8, -0.8);
    }
    public void armenOmhoog() throws Exception {
        motion.shoulderRollControl(0.0872665, -0.0872665);
        motion.shoulderPitchControl(-1.5, -1.5);
    }
    // armen links en rechts moet veranderd worden zodat de elleboog een beetje gebogen is
    public void armenRechts() throws Exception {
        motion.shoulderPitchControl(0, 0);
        motion.shoulderRollControl(0.314159, 0.314159); // if LRoll is too short try a higher digit max 76
    }
    // armen links en rechts moet veranderd worden zodat de elleboog een beetje gebogen is
    public void armenLinks() throws Exception {
        motion.shoulderRollControl(-0.314159, -0.314159);
    }
    public void armenOnder() throws Exception {
        motion.shoulderRollControl(0.0872665, -0.0872665);
        motion.shoulderPitchControl(1.09956, 1.09956);
    }
}
