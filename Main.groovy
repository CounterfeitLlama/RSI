import java.awt.image.BufferedImage;
import com.neuronrobotics.imageprovider.Detection;
import java.util.List;
import com.neuronrobotics.bowlerstudio.av.ImagesToVideo
import javax.imageio.ImageIO;
import com.neuronrobotics.imageprovider.AbstractImageProvider;
import com.neuronrobotics.imageprovider.OpenCVImageProvider;
import com.neuronrobotics.imageprovider.StaticFileProvider;
import com.neuronrobotics.imageprovider.URLImageProvider;
import javafx.stage.FileChooser;
import javafx.application.Platform;
import com.neuronrobotics.sdk.addons.gamepad.IJInputEventListener;
import com.neuronrobotics.sdk.addons.gamepad.BowlerJInputDevice;
import net.java.games.input.Component;
import net.java.games.input.Event;

String name = "imageProvider"
AbstractImageProvider camera0 = null;

ServoChannel eyeY = new ServoChannel(dyio.getChannel(10))
ServoChannel eyeX = new ServoChannel(dyio.getChannel(5))

DyIOChannel button = dyio.getChannel(14)
boolean run = false

if (DeviceManager.getSpecificDevice(AbstractImageProvider.class, name) == null) {
	camera0 = new OpenCVImageProvider(0);
	DeviceManager.addConnection(camera0,name);
} else {
	camera0 = (AbstractImageProvider)DeviceManager.getSpecificDevice(AbstractImageProvider.class, name);
}

while (DeviceManager.getSpecificDevice(AbstractImageProvider.class,  name) == null) {
	ThreadUtil.wait(100)
}

// Starting with the connected camera from BowlerStudio
println(camera0)
File haarCascadeFile = ScriptingEngine.fileFromGit("https://github.com/madhephaestus/DefaultHaarCascade.git", "lbpcascade_frontalface.xml")
IObjectDetector detector = new HaarDetector(haarCascadeFile)

// Create the input and display images. The display is where the detector writes its detections overlay on the input image
BufferedImage inputImage = AbstractImageProvider.newBufferImage(640,480)
BufferedImage displayImage = AbstractImageProvider.newBufferImage(640,480)

ArrayList<File> inFiles = new ArrayList<File>();
String dir = ScriptingEngine.getWorkspace().getAbsolutePath() + "/imageCache/"
File dirFile = new File(dir)
if (!dirFile.exists()) {
	 dirFile.mkdir();
}

// Loop checking the camera for faces
int i = 0;
while (!Thread.interrupted()) {
	while (!Thread.interrupted() && run == true) {
		camera0.getLatestImage(inputImage, displayImage)               // capture image
		List<Detection> data = detector.getObjects(inputImage, displayImage)
		if (data.size() > 0) {
			println("Got: " + data.size() + 
			" x location = " + data.get(0).getX() +
			" y location " + data.get(0).getY() +
			" size = " + data.get(0).getSize())
			percentUp = data.get(0).getY() / inputImage.getHeight()
			eyeLocY = percentUp * (188-63) + 63
			eyeY.SetPosition((int)eyeLocY)
	
			percentRight = (inputImage.getWidth() - data.get(0).getX()) / inputImage.getWidth()
			eyeLocX = percentRight * (116-74) + 74
			eyeX.SetPosition((int)eyeLocX)
		}
	
		if (button.getValue() == 0) {
			run = false
			sleep(1000)
		}
	}

	if (button.getValue() == 0) {
		run = true
		sleep(1000)
	}
}

//Servo up and down limits:
/*Lower limit (pointing up): 63*/
/*Upper limit (pointing down): 188*/

//Servo side limits:
/*Lower limit (pointing right): 74*/
/*Upper limit (pointing left): 116*/