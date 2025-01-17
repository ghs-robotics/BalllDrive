package org.firstinspires.ftc.teamcode.vision;

import static android.os.SystemClock.sleep;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.cv.ColorDetectionPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class Camera {

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

//    private final int cameraWidth = 1920; //320
//    private final int cameraHeight = 1080; // 240

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    private final double fx = 578.272;
    private final double fy = 578.272;
    private final double cx = 402.145;
    private final double cy = 221.506;
    // UNITS ARE METERS
    private final double tagsize = 0.166; //This is 30% of original pdf size

    private int LEFT;
    private int MIDDLE;
    private int RIGHT;

    private int colorDetection;
    private ColorDetectionPipeline colorDetectionPipeline;

    public Camera(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }

    public void setupColorDetection() {

        OpenCvCamera camera;

        //Tag IDs for 3 different park locations
        LEFT = 0; //Lime
        MIDDLE = 1; //Magenta
        RIGHT = 2; //Cyan
        colorDetection = -1;

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);
        colorDetectionPipeline = new ColorDetectionPipeline(telemetry);

        camera.setPipeline(colorDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(1920, 1080, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addLine("Error: Camera could not open");
                telemetry.update();
            }
        });

        telemetry.setMsTransmissionInterval(50);
    }

    public int runColorDetection() {
        int color = -1;
        ArrayList<Integer> currentDetections = colorDetectionPipeline.getColorDetections();

        if (currentDetections.size() != 0) {
            boolean colorFound = false;

            for (Integer detection : currentDetections) {
                if (detection == LEFT || detection == MIDDLE || detection == RIGHT) {
                    color = currentDetections.get(currentDetections.size()-1);
                    colorFound = true;
                    break;
                }
            }


//        if (colorFound) {
//            telemetry.addLine("Color of interest is in sight!");
//            telemetry.addLine("Spotted color #" + color);
//
//        } else {
//            telemetry.addLine("Don't see color of interest :(");
//
//            if (color == -1) {
//                telemetry.addLine("(The color has never been seen)");
//            } else {
//                telemetry.addLine("\nBut we HAVE seen the color before");
//            }
//        }
//
//        } else {
//            telemetry.addLine("Don't see color of interest :(");
//
//            if (color == -1) {
//                telemetry.addLine("(The color has never been seen)");
//            } else {
//                telemetry.addLine("\nBut we HAVE seen the color before");
//            }
//
        }

        for(int detection : currentDetections) {
            telemetry.addLine("Detection: " + detection);
        }

        telemetry.update();
        sleep(20);


        return color;
    }

}
