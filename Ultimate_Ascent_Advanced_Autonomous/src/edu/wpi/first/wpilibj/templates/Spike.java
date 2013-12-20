package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.Path.JoystickInput;
import edu.wpi.first.wpilibj.templates.subsystems.Angle;
import edu.wpi.first.wpilibj.templates.subsystems.AutoCenter;
import edu.wpi.first.wpilibj.templates.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.templates.subsystems.OI;
import edu.wpi.first.wpilibj.templates.subsystems.Shooter;
import edu.wpi.first.wpilibj.templates.subsystems.Vision;

/**
 * It is important to read the comments here. Some things only work if
 * everything is set up correctly. They will need to be disabled or there might
 * be PWM timeout errors.
 */
public class Spike extends IterativeRobot {

    private static boolean readyToFire = false;
    private static final DriverStationLCD LCD = DriverStationLCD.getInstance();
    private static final Timer timer = new Timer();
    //Angles for shooting in autonomous.  Auto aiming does not work very well through the pyramid.  
    private static double sideShotAngle = 8.214,
            centerShotAngle = 7.35;
    public static DigitalInput autonomousSwitch = new DigitalInput(RobotMap.autonomousSwitch);
    public static boolean initEmergencyConstantValue = true;
    Path path = new Path();
    int i = 0;

    /**
     * Code here runs once when the robot starts. While loops should only be
     * used here.
     */
    public void robotInit() {
        //Initialize angle and shooter
        Angle.angleInit();
        Shooter.shooterInit();
//        //start and reset the timer
        timer.start();
        timer.reset();
//        //Clear the LCD
        LCD.updateLCD();
    }

    /**
     * Code here loops every 20 milliseconds during the autonomous period. While
     * loops should not be used.
     */
    public void autonomousPeriodic() {
        if (i >= path.size()) {
            i = 0;
        }
        JoystickInput ji = (JoystickInput) path.elementAt(i);
        DriveTrain.tankDrive(2 * ji.left / 100.0 - 1, 2 * ji.right / 100.0 - 1);
        i++;
    }

    /**
     * Code here loops every 20 milliseconds during the autonomous period. While
     * loops should not be used. Everything is nice and tidy compared to last
     * year... Pieces of the robot are written in separate classes for ease of
     * reading and troubleshooting.
     */
    public void teleopPeriodic() {
        OI.driveRobot(); //Drive control
        OI.controlClimb(); //Initiate climb button
        Shooter.calculateRPM();  //Constantly calculate the RPM
        //These things can only move before climbing has begun.  Once you hit the climbing button, there's no going back.  
        if (OI.beginClimb == false) {
            if (!OI.toggleFeed) {
                Shooter.runShooter(); //Adjusts the shooter PWM value accordly depending on the RPM
            } else {
                Shooter.stopShooter();
            }
            OI.controlAutoAim(); //Button that starts autoAiming
            OI.controlFeed(); //Button that toggles feed
            OI.controlTrigger(); //Button that controls the trigger
            OI.controlWinch(); //Button that controls the winch.  Only used to initialize the robot before a match.  
        }

        runSmartDashboard(); //Constantly sends diagnostic information from the robot to the DriverStation
        LCD.updateLCD(); //Updates LCD so that we have feedback on what is happening.  Only one is needed per periodic loop.  
    }

    /**
     * SmartDashboard is used to send diagnostic information back to the
     * DriverStation here.
     */
    private static void runSmartDashboard() {
        SmartDashboard.putNumber("Shooter Angle: ", Angle.angleEncoder.getDistance()); //Should be very accurate.  
        SmartDashboard.putNumber("Shooter RPM: ", Shooter.currentRPM); //line plot :D
        SmartDashboard.putBoolean("RPM Status: ", Shooter.shooterStatus()); //Big green/red square on the smartdashboard. 
        SmartDashboard.putNumber("Shooter PWM Value: ", Shooter.shooterPWM1.getSpeed()); //Diagnostic information.  Not really important to the driver
        SmartDashboard.putNumber("Target Angle", Vision.calculateAngle());
        SmartDashboard.putNumber("Gyro Angle", AutoCenter.getGyro());
        if (initEmergencyConstantValue) {
            SmartDashboard.putNumber("EMERGENCY CORRECTION VALUE", Vision.emergencyCorrectionValue);
            initEmergencyConstantValue = false;
        }

    }
}