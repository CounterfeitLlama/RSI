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

if (DeviceManager.getSpecificDevice(AbstractImageProvider.class, name) == null) {
	camera0 = new OpenCVImageProvider(0);
	DeviceManager.addConnection(camera0,name);
} else {
	camera0 = (AbstractImageProvider)DeviceManager.getSpecificDevice(AbstractImageProvider.class, name);
}

while (DeviceManager.getSpecificDevice(AbstractImageProvider.class, name) == null) {
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
while (!Thread.interrupted() && i < 200) {
	camera0.getLatestImage(inputImage, displayImage)               // capture image
	List<Detection> data = detector.getObjects(inputImage, displayImage)
	if (data.size() > 0) {
		println("Got: " + data.size() + 
		" x location = " + data.get(0).getX() +
		" y location " + data.get(0).getY() +
		" size = " + data.get(0).getSize())
	}
	i++;
}