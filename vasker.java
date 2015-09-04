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
 			Port s4 = brick.getPort("S4"); // distansesensor

			EV3ColorSensor fargeSensor = new EV3ColorSensor(s1); // fargesensor
			SampleProvider fargeLeser = fargeSensor.getMode("RGB");  // svart = 0.01..
			float[] fargeSample = new float[fargeLeser.sampleSize()];  // avlest verdi

			EV3TouchSensor trykkSensor = new EV3TouchSensor(s2); // trykksensor
			SampleProvider trykkLeser = trykkSensor.getTouchMode(); // setter i rett modus
			float[] trykkSample = new float[trykkLeser.sampleSize()]; // 0 = ikke trykk, 1 = trykk

			NXTSoundSensor lydSensor = new NXTSoundSensor(s3); // lydsensor
			SampleProvider lydLeser = lydSensor.getDBMode(); // setter i rett modus (kan bruke hørselstilnærming også)
			float[] lydSample = new float[lydLeser.sampleSize()]; // avlest verdi

			EV3UltrasonicSensor distSensor = new EV3UltrasonicSensor(s4);
			SampleProvider distLeser = distSensor.getDistanceMode();
			float[] distSample = new float[distLeser.sampleSize()];

			Motor.B.setSpeed(25);
			Motor.C.setSpeed(25);
			Motor.A.setSpeed(15); //styring

			int veiFarge = 0;
			for (int i = 0; i<100; i++){
				fargeLeser.fetchSample(fargeSample, 0);
				veiFarge += fargeSample[0]* 100;
			}
			veiFarge = veiFarge / 100 - 5;

			lydLeser.fetchSample(lydSample, 0);
			float stille = lydSample[0];
			float lydMax = 30;
			boolean fortsett = true;
			//trykkLeser.fetchSample(trykkSample, 0);
			int retning = 1;
			distLeser.fetchSample(distSample, 0);
			float distStart = distSample[0];
			System.out.println("Distanse er " + distStart);
			float maxDist = 0.003f;


			System.out.println("Starter loop");

			while (fortsett) {

				//trykkLeser.fetchSample(trykkSample, 0);
				distLeser.fetchSample(distSample, 0);
				//System.out.println("Distanse " + distSample[0]);
				lydLeser.fetchSample(lydSample, 0);
				fargeLeser.fetchSample(fargeSample, 0);
				//System.out.println("Sampler");
				float distTest = distSample[0];
				//System.out.println((distStart - maxDist) + "-" + (distStart + maxDist) + ": " + distTest);

				if (lydSample[0] > lydMax) {
					Motor.B.stop();
					Motor.C.stop();
					System.out.println("Hog lyd!");
					Thread.sleep(4000);
				} else if (fargeSample[0] * 100 < veiFarge) {
					System.out.println("Snur");
					Motor.B.setSpeed(30);
					Motor.C.setSpeed(30);
					if (retning == 1) {
						retning = 0;
						Motor.B.forward();
						Motor.C.forward();
						Thread.sleep(1000);
					} else {
						retning = 1;
						Motor.B.backward();
						Motor.C.backward();
						Thread.sleep(1000);
					}
					Motor.B.setSpeed(25);
					Motor.C.setSpeed(25);
				} else if (trykkSample != null && trykkSample[0] > 0) {
					System.out.println("Trykket");
					fortsett = false;
				} else if (retning == 1) {
					//System.out.println("Kjorer fremover");
					Motor.B.backward();
					Motor.C.backward();
					if (distTest < (distStart - maxDist)) {
						Motor.A.forward();
						System.out.println("Svinger venstre");
					} else if (distTest > (distStart + maxDist) ) {
						Motor.A.backward();
						System.out.println("Svinger hoyre");
					}
					Thread.sleep(200);
				} else {
					//System.out.println("Kjorer bakover");
					Motor.B.forward();
					Motor.C.forward();
					if (distSample[0] < (distStart - maxDist) ) {
						Motor.A.backward();
					} else if (distSample[0] > (distStart + maxDist) ) {
						Motor.A.forward();
					}
					Thread.sleep(200);
				}

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

