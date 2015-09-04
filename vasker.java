import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;


public class vasker {
	public static void main(String[] args)  throws Exception{
		try {
			Brick brick = BrickFinder.getDefault(); // finner roboten

			Port s1 = brick.getPort("S1"); // fargesensor
 			Port s2 = brick.getPort("S2"); // trykksensor
 			Port s3 = brick.getPort("S3"); // lydsensor

			EV3ColorSensor fargeSensor = new EV3ColorSensor(s1); // fargesensor
			SampleProvider fargeLeser = fargeSensor.getMode("RGB");  // svart = 0.01..
			float[] fargeSample = new float[fargeLeser.sampleSize()];  // avlest verdi

			EV3TouchSensor trykkSensor = new EV3TouchSensor(s2); // trykksensor
			SampleProvider trykkLeser = trykkSensor.getTouchMode(); // setter i rett modus
			float[] trykkSample = new float[trykkLeser.sampleSize()]; // 0 = ikke trykk, 1 = trykk

			NXTSoundSensor lydSensor = new NXTSoundSensor(s3); // lydsensor
			SampleProvider lydLeser = lydSensor.getDBMode(); // setter i rett modus (kan bruke hørselstilnærming også)
			float[] lydSample = new float[lydLeser.sampleSize()]; // avlest verdi

			Motor.A.setSpeed(200);
			Motor.B.setSpeed(200);
			Motor.C.setSpeed(200);

			int veiFarge = 0;
			for (int i = 0; i<100; i++){
				fargeLeser.fetchSample(fargeSample, 0);
				veiFarge += fargeSample[0]* 100;
			}
			veiFarge = veiFarge / 100 + 5;

			lydLeser.fetchSample(lydSample, 0);
			float stille = lydSample[0];
			float lydMax = 40;
			boolean fortsett = true;
			trykkLeser.fetchSample(trykkSample, 0);

			while (fortsett) {
				if (lydSample[0] > lydMax) {
					Motor.A.stop();
					Motor.B.stop();
					Motor.C.stop();
					Thread.sleep(4000);
				} else if (fargeSample[0] * 100 < veiFarge) {
					Motor.B.stop();
					Motor.A.forward();
					Thread.sleep(200);
				} else if (trykkSample[0] > 0) {
					fortsett = false;
				} else {
					Motor.A.forward();
					Motor.B.forward();
					Motor.C.forward();
				}

				trykkLeser.fetchSample(trykkSample, 0);
				lydLeser.fetchSample(lydSample, 0);
				fargeLeser.fetchSample(fargeSample, 0);

				Motor.A.stop();
				Motor.B.stop();
				Motor.C.stop();
			}

			/*
			forslag:

			kalibrer hvit/svart
			definer stille lyd og max lyd før stopp
			while (fortsett) {
				if (svart)
					roter venstre
				else if (trykket)
					avslutt?
				else if (lyd > max)
					vent 4 sek
				else
					skru på viftearm
					kjør forover

				les av lyd
				les av farge
			}

			Motor.A.setSpeed(200);
			Motor.B.setSpeed(200);
			Motor.C.setSpeed(200);
			Motor.X.rotate(vinkel);

			float lyd = 0;
			lydLeser.fetchSample(lydSample, 0);
			lyd = lydSample[0];

			float trykk = 0;
			trykkLeser.fetchSample(trykkSample, 0);
			trykk = trykkSample[0];
			if (trykk > 0) {
				// knappen er trykket inn
			} else {
				// knappen er ikke trykket på
			}

		 	Grethes metode for å beregne lysintensitet for en svart linje
			int svart = 0;
			for (int i = 0; i<100; i++){
				fargeLeser.fetchSample(fargeSample, 0);
				svart += fargeSample[0]* 100;
			}
			svart = svart / 100 + 5;
			System.out.println("Svart: " + svart);
			*/
		}catch(Exception e) {
			System.out.println("Error: " + e);
		}
	}

}

