package jinputjoysticktestv2;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 *
 * Joystick Test with JInput
 *
 *
 * @author TheUzo007 http://theuzo007.wordpress.com
 *
 * Created 22 Oct 2013
 *
 */
public class JoystickTest {

    public static void main(String args[]) throws IOException {
        //JInputJoystickTest jinputJoystickTest = new JInputJoystickTest();
        // Writes (into console) informations of all controllers that are found.
        //jinputJoystickTest.getAllControllersInfo();
        // In loop writes (into console) all joystick components and its current values.
        //jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.STICK);
        //jinputJoystickTest.pollControllerAndItsComponents(Controller.Type.GAMEPAD);

        new JoystickTest();
    }

    final JFrameWindow window;
    private ArrayList<Controller> foundControllers;
    PrintWriter outFile;

    public JoystickTest() throws IOException {
        this.outFile = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Peter\\Dropbox\\Workspace\\Java\\2014Programming\\LearnAutonomous\\src\\edu\\wpi\\first\\wpilibj\\templates\\Path.java")));
        //System.out.println("TEAM 293 SPIKE AUTONOMOUS PATH");
        //System.out.println("Path.java is open....");
        outFile.println("package edu.wpi.first.wpilibj.templates;import java.util.Vector;public class Path extends Vector {Path(){super();");
        window = new JFrameWindow();

        foundControllers = new ArrayList<>();
        searchForControllers();

        // If at least one controller was found we start showing controller data on window.
        if (!foundControllers.isEmpty()) {
            startShowingControllerData();
        } else {
            window.addControllerName("No controller found!");
        }
    }

    /**
     * Search (and save) for controllers of type Controller.Type.STICK,
     * Controller.Type.GAMEPAD, Controller.Type.WHEEL and
     * Controller.Type.FINGERSTICK.
     */
    private void searchForControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for (int i = 0; i < controllers.length; i++) {
            Controller controller = controllers[i];

            if (controller.getType() == Controller.Type.STICK
                    || controller.getType() == Controller.Type.GAMEPAD
                    || controller.getType() == Controller.Type.WHEEL
                    || controller.getType() == Controller.Type.FINGERSTICK) {
                // Add new controller to the list of all controllers.
                foundControllers.add(controller);

                // Add new controller to the list on the window.
                window.addControllerName(controller.getName() + " - " + controller.getType().toString() + " type");
            }
        }
    }

    /**
     * Starts showing controller data on the window.
     */
    private void startShowingControllerData() {
        while (true) {
            
            //SOME HOW THIS IS REALLY IMPORTANT
            
            System.out.print("");
            if (window.isRecording) {
                // Currently selected controller.
                int selectedControllerIndex = window.getSelectedControllerName();
                Controller controller = foundControllers.get(selectedControllerIndex);

                // Pull controller for current data, and break while loop if controller is disconnected.
                if (!controller.poll()) {
                    window.showControllerDisconnected();
                    break;
                }

                // X axis and Y axis
                int xAxisPercentage = 0;
                int yAxisPercentage = 0;
                // JPanel for other axes.
                JPanel axesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 2));
                axesPanel.setBounds(0, 0, 200, 190);

                // JPanel for controller buttons
                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
                buttonsPanel.setBounds(6, 19, 246, 110);

                // Go trough all components of the controller.
                Component[] components = controller.getComponents();
                for (int i = components.length-1; i >= 0; i--) {
                    Component component = components[i];
                    Identifier componentIdentifier = component.getIdentifier();

                    // Buttons
                    if (component.getName().contains("Button")) {
                        // Is button pressed?
                        boolean isItPressed = true;
                        if (component.getPollData() == 0.0f) {
                            isItPressed = false;
                        }

                        // Button index
                        String buttonIndex;
                        buttonIndex = component.getIdentifier().toString();

                        // Create and add new button to panel.
                        JToggleButton aToggleButton = new JToggleButton(buttonIndex, isItPressed);
                        aToggleButton.setPreferredSize(new Dimension(48, 25));
                        aToggleButton.setEnabled(false);
                        buttonsPanel.add(aToggleButton);

                        // We know that this component was button so we can skip to next component.
                        continue;
                    }

                    // Hat switch
                    if (componentIdentifier == Component.Identifier.Axis.POV) {
                        float hatSwitchPosition = component.getPollData();
                        window.setHatSwitch(hatSwitchPosition);

                        // We know that this component was hat switch so we can skip to next component.
                        continue;
                    }

                    // Axes
                    if (component.isAnalog()) {
                        float axisValue = component.getPollData();
                        int axisValueInPercentage = getAxisValueInPercentage(axisValue);

                        // X axis
                        if (componentIdentifier == Component.Identifier.Axis.X) {
                            xAxisPercentage = axisValueInPercentage;
                            continue; // Go to next component.
                        }
                        // Y axis

                        if (componentIdentifier == Component.Identifier.Axis.Y) {
                            yAxisPercentage = axisValueInPercentage;
                            outFile.print("this.addElement(new JoystickInput(" + axisValueInPercentage);
                            //System.out.print("this.addElement(new JoystickInput(" + axisValueInPercentage);
                            continue; // Go to next component.
                        }

                        // Other axis
                        JLabel progressBarLabel = new JLabel(component.getName());
                        JProgressBar progressBar = new JProgressBar(0, 100);
                        progressBar.setValue(axisValueInPercentage);
                        if (component.getName().equals("Z Rotation")) {
                            outFile.println("," + axisValueInPercentage + "));");
                            //System.out.println("," + axisValueInPercentage + "));");
                        }

                        axesPanel.add(progressBarLabel);
                        axesPanel.add(progressBar);

                    }
                }
                // Now that we go trough all controller components,
                // we add butons panel to window,
                window.setControllerButtons(buttonsPanel);
                // set x and y axes,
                window.setXYAxis(xAxisPercentage, yAxisPercentage);

                // add other axes panel to window.
                window.addAxisPanel(axesPanel);

                // We have to give processor some rest.
                try {
                    Thread.sleep(12);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JoystickTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (window.close) {
                    outFile.println("}public class JoystickInput {double left = 0.0;double right = 0.0;JoystickInput(double left, double right) {this.left = left;this.right = right;}}}");
                    outFile.close();
                    //System.out.println("Path.java is closed....");
                    window.close=false;
                } else {
                }
            }
        }
    }

    /**
     * Given value of axis in percentage. Percentages increases from left/top to
     * right/bottom. If idle (in center) returns 50, if joystick axis is pushed
     * to the left/top edge returns 0 and if it's pushed to the right/bottom
     * returns 100.
     *
     * @return value of axis in percentage.
     */
    public int getAxisValueInPercentage(float axisValue) {
        return (int) (((2 - (1 - axisValue)) * 100) / 2);
    }
}
