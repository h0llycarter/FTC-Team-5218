package opmodes.LM0;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import team25core.DeadReckonPath;
import team25core.DeadReckonTask;
import team25core.DeadmanMotorTask;
import team25core.FourWheelDirectDrivetrain;
import team25core.GamepadTask;
import team25core.MecanumWheelDriveTask;
import team25core.MechanumGearedDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.TankMechanumControlScheme;

/**
 * Created by Lizzie on 11/2/2019.
 */
@Autonomous(name = "5218 LM0 Autonomous")
public class MozartLM0Autonomous extends Robot {
    // drivetrain and mechanisms declaration
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor lift;
    private Servo susan;
    private Servo claw;
    private Servo leftArm;
    private Servo rightArm;
    private MechanumGearedDrivetrain drivetrain;

    // gamepad and telemetry declaration
    private GamepadTask gamepad;
    private Telemetry.Item alliance;
    private Telemetry.Item startPos;
    private Telemetry.Item path;

    // skybridge constant declaration
    private DeadReckonPath initialPath;
    private MozartSkybridgePath skybridgePath;
    private MozartSkybridgePath.AllianceColor allianceColor;
    private MozartSkybridgePath.StartingPosition startingPosition;

    @Override
    public void init() {
        // drivetrain and mechanisms initialization
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        lift = hardwareMap.dcMotor.get("lift");
        susan = hardwareMap.servo.get("susan");
        claw = hardwareMap.servo.get("claw");
        leftArm = hardwareMap.servo.get("leftArm");
        rightArm = hardwareMap.servo.get("rightArm");

        TankMechanumControlScheme scheme = new TankMechanumControlScheme(gamepad1);
        drivetrain = new MechanumGearedDrivetrain(60, frontRight, backRight, frontLeft, backLeft);
        drivetrain.encodersOn();
        drivetrain.resetEncoders();

        // gamepad and telemtry initialization
        gamepad = new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1);
        addTask(gamepad);

        alliance = telemetry.addData("Alliance: ", "NOT SELECTED");
        startPos = telemetry.addData("Starting Position: ", "NOT SELECTED");
        path = telemetry.addData("Path: ", "NOT SELECTED");

        skybridgePath = new MozartSkybridgePath();
        initialPath = new DeadReckonPath();
        allianceColor = allianceColor.DEFAULT;
        startingPosition = startingPosition.DEFAULT;
    }

    @Override
    public void handleEvent(RobotEvent e) {
        if (e instanceof GamepadTask.GamepadEvent) {
            GamepadTask.GamepadEvent event = (GamepadTask.GamepadEvent) e;
            handleGamePadSelection(event);
        }
    }

    @Override
    public void start() {
        initialPath = skybridgePath.getPath(allianceColor, startingPosition);
        initialMove(initialPath);
    }

    public void handleGamePadSelection (GamepadTask.GamepadEvent event) {
        switch (event.kind){
            case BUTTON_X_DOWN:
                allianceColor = allianceColor.BLUE;
                alliance.setValue("BLUE");
                break;
            case BUTTON_B_DOWN:
                allianceColor = allianceColor.RED;
                alliance.setValue("RED");
                break;
            case BUTTON_Y_DOWN:
                startingPosition = startingPosition.BUILDING;
                startPos.setValue("BUILDING SIDE");
                break;
            case BUTTON_A_DOWN:
                startingPosition = startingPosition.LOADING;
                startPos.setValue("LOADING SIDE");
                break;
        }
    }

    public void initialMove(final DeadReckonPath path) {
        addTask(new DeadReckonTask(this, path, drivetrain) {
            public void handleEvent(RobotEvent e) {
                DeadReckonTask.DeadReckonEvent event = (DeadReckonTask.DeadReckonEvent) e;
                switch(event.kind) {
                    case PATH_DONE:
                        RobotLog.i("163: PATH DONE");
                }
            }
        });
    }



}
