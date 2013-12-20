/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.Path.JoystickInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class LearnAutonomous extends IterativeRobot {

    Jaguar leftDrive = new Jaguar(1);
    Jaguar rightDrive = new Jaguar(2);
    RobotDrive drive = new RobotDrive(leftDrive, rightDrive);
    Joystick gamepad = new Joystick(1);
    Path path = new Path();
    int i = 0;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {

    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        if (i >= path.size()) {
            i = 0;  
        }
        JoystickInput ji = (JoystickInput) path.elementAt(i);
        drive.tankDrive(2 * ji.left / 100.0 - 1, 2 * ji.right / 100.0 - 1);
        i++;
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        SmartDashboard.putNumber("gamepad left Y: ", gamepad.getRawAxis(2));
        SmartDashboard.putNumber("gamepad right y: ", gamepad.getRawAxis(4));
        drive.tankDrive(gamepad.getRawAxis(2), gamepad.getRawAxis(4));
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {

    }

}
