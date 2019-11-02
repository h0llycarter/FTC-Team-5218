package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import team25core.DeadmanMotorTask;
import team25core.GamepadTask;
import team25core.MechanumGearedDrivetrain;
import team25core.Robot;
import team25core.RobotEvent;
import team25core.TankMechanumControlScheme;
import team25core.TeleopDriveTask;

@TeleOp(name = "LM0 Telop")
public class MozartLM0Teleop extends Robot {
    // teleop with the mecanum drivetrain and linear lift
    // active wheel intake

    /* GAMEPAD 2
    // linear lift up (right bumper) down (right trigger)
    // active wheel intake in (A) out (B)
    // lazy susan --
        -- also change servo to != continuous, instead just standard
     */

    /* GAMEPAD 1
    // drivetrain
    // foundation arms stow (right bumper) and down (right trigger)
    // slow mode!
     */
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private MechanumGearedDrivetrain drivetrain;
    private TeleopDriveTask driveTask;
    private DcMotor leftIntake;
    private DcMotor rightIntake;
    private DcMotor lift;
    private Servo claw;
    private DcMotor leftArm;
    private DcMotor rightArm;
    private Servo susan;

    public void handleEvent (RobotEvent e) {

    }

    public void init()
    {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft"); //drivetrain
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        TankMechanumControlScheme scheme = new TankMechanumControlScheme(gamepad1);
        drivetrain = new MechanumGearedDrivetrain(60, frontLeft,frontRight, backLeft, backRight);
        driveTask = new TeleopDriveTask(this, scheme, frontLeft, frontRight, backLeft, backRight);
        leftIntake = hardwareMap.get(DcMotor.class, "leftIntake"); //wheel intake
        rightIntake = hardwareMap.get(DcMotor.class, "rightIntake");
        leftArm = hardwareMap.get(DcMotor.class, "leftArm"); //foundation mover
        rightArm = hardwareMap.get(DcMotor.class, "rightArm");
        lift = hardwareMap.dcMotor.get("lift"); //lift
        claw = hardwareMap.servo.get("claw"); //claw
        susan = hardwareMap.servo.get("susan"); //lazy susan
        // GamepadTask gamepad = new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_2);
        // addTask(gamepad);
    }

    @Override
    public void start()
    {
        this.addTask(driveTask);

        // GAMEPAD 2
        //lift
        DeadmanMotorTask liftUp = new DeadmanMotorTask(this, lift, HisaishiCalibration.LIFT_UP, GamepadTask.GamepadNumber.GAMEPAD_2, DeadmanMotorTask.DeadmanButton.RIGHT_BUMPER);
        addTask(liftUp);
        DeadmanMotorTask liftDown = new DeadmanMotorTask(this, lift, HisaishiCalibration.LIFT_DOWN, GamepadTask.GamepadNumber.GAMEPAD_2, DeadmanMotorTask.DeadmanButton.RIGHT_TRIGGER);
        addTask(liftDown);

        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_2) {
            public void handleEvent(RobotEvent e) {
                GamepadEvent event = (GamepadEvent) e;
                switch (event.kind){
                    case BUTTON_A_DOWN: //wheel intake
                        leftIntake.setPower(HisaishiCalibration.INTAKE_LEFT_COLLECT);
                        rightIntake.setPower(HisaishiCalibration.INTAKE_RIGHT_COLLECT);
                        break;
                    case BUTTON_B_DOWN:
                        leftIntake.setPower(HisaishiCalibration.INTAKE_LEFT_DISPENSE);
                        rightIntake.setPower(HisaishiCalibration.INTAKE_RIGHT_DISPENSE);
                        break;
                    case BUTTON_A_UP:
                    case BUTTON_B_UP:
                        leftIntake.setPower(0.0);
                        rightIntake.setPower(0.0);
                        break;
                    case LEFT_BUMPER_DOWN: //claw
                        claw.setPosition(HisaishiCalibration.CLAW_CLOSE);
                        break;
                    case LEFT_TRIGGER_DOWN:
                        claw.setPosition(HisaishiCalibration.CLAW_OPEN);
                        break;
                    case BUTTON_X_DOWN: //lazy susan
                        susan.setPosition(HisaishiCalibration.SUSAN_LEFT);
                        break;
                    case BUTTON_Y_DOWN:
                        susan.setPosition(HisaishiCalibration.SUSAN_RIGHT);
                        break;
                }
                }
        });
        this.addTask(new GamepadTask(this, GamepadTask.GamepadNumber.GAMEPAD_1){
            public void handleEvent(RobotEvent e){
                GamepadEvent event = (GamepadEvent) e;
                switch (event.kind) {
                    case RIGHT_BUMPER_DOWN: //foundation arms
                        leftArm.setPower(HisaishiCalibration.ARM_LEFT_STOW);
                        rightArm.setPower(HisaishiCalibration.ARM_RIGHT_STOW);
                        break;
                    case RIGHT_TRIGGER_DOWN:
                        leftArm.setPower(HisaishiCalibration.ARM_LEFT_DOWN);
                        rightArm.setPower(HisaishiCalibration.ARM_RIGHT_DOWN);
                        break;
                    case RIGHT_TRIGGER_UP:
                    case RIGHT_BUMPER_UP:
                        leftArm.setPower(0.0);
                        rightArm.setPower(0.0);
                        break;
                    case BUTTON_A_DOWN: //drivetrain
                        driveTask.slowDown(false);
                        break;
                    case BUTTON_B_DOWN:
                        driveTask.slowDown(true);
                        break;
                }
            }
        });
    }
}
