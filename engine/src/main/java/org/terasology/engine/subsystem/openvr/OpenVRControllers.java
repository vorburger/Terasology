/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.engine.subsystem.openvr;

import jopenvr.VRControllerState_t;
import org.joml.Matrix4f;
import org.terasology.input.ButtonState;
import org.terasology.input.ControllerDevice;
import org.terasology.input.InputType;
import org.terasology.input.device.ControllerAction;
import org.terasology.rendering.openvrprovider.ControllerListener;
import org.terasology.rendering.openvrprovider.OpenVRUtil;
import org.terasology.input.ControllerId;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * This class acts as an interface between OpenVR controllers and Terasology controllers. By implementing
 * ControllerListener.buttonStateChanged(), OpenVR controller events make it into this class and are stored. Then,
 * by implementing getInputQueue(), an overridden method from ControllerDevice, the Terasology controller system
 * frequently retrieves and handles these events.
 */
class OpenVRControllers implements ControllerDevice, ControllerListener {

    private Queue<ControllerAction> queuedActions = new ArrayDeque<>();
    private VRControllerState_t cachedStateBefore;
    private VRControllerState_t cachedStateAfter;

    /**
     * Get the controller names provided by this ControllerDevice.
     * @return the list of controllers names.
     */
    @Override
    public List<String> getControllers() {
        List<String> ids = new ArrayList<>();
        ids.add("OpenVR");
        return ids;
    }

    /**
     * Get all queued actions registered since this method was last called.
     * @return a queue of actions.
     */
    @Override
    public Queue<ControllerAction> getInputQueue() {
        Queue<ControllerAction> result = new ArrayDeque<>(queuedActions);
        queuedActions.clear();
        return result;
    }

    private boolean switchedUp(long buttonIndex) {
        return OpenVRUtil.switchedUp(buttonIndex, cachedStateBefore.ulButtonPressed, cachedStateAfter.ulButtonPressed);
    }

    private boolean switchedDown(long buttonIndex) {
        return OpenVRUtil.switchedDown(buttonIndex, cachedStateBefore.ulButtonPressed, cachedStateAfter.ulButtonPressed);
    }

    private void addAction(int controllerButton, ButtonState buttonState, float axisValue) {
        queuedActions.add(new ControllerAction(InputType.CONTROLLER_BUTTON.getInput(controllerButton),
                    "OpenVR", buttonState, axisValue));
    }

    private void addAction(int controllerButton, ButtonState buttonState) {
        addAction(controllerButton, buttonState, 1.0f);
    }

    private void handleController0() {
        if (switchedUp(ControllerListener.BUTTON_TRIGGER)) {
            addAction(ControllerId.ZERO, ButtonState.UP);
        } else if (switchedDown(ControllerListener.BUTTON_TRIGGER)) {
            addAction(ControllerId.ZERO, ButtonState.DOWN);
        } else if (switchedUp(ControllerListener.BUTTON_GRIP)) {
            addAction(ControllerId.ONE, ButtonState.UP);
        } else if (switchedDown(ControllerListener.BUTTON_GRIP)) {
            addAction(ControllerId.ONE, ButtonState.DOWN);
        } else if (switchedUp(ControllerListener.BUTTON_APP_MENU)) {
            addAction(ControllerId.TWO, ButtonState.UP);
        } else if (switchedDown(ControllerListener.BUTTON_APP_MENU)) {
            addAction(ControllerId.TWO, ButtonState.DOWN);
        } else if (switchedDown(ControllerListener.BUTTON_TOUCHPAD)) {
            addAction(ControllerId.X_AXIS, ButtonState.DOWN, -cachedStateAfter.rAxis[0].x);
            addAction(ControllerId.Y_AXIS, ButtonState.DOWN, cachedStateAfter.rAxis[0].y);
        } else if (switchedUp(ControllerListener.BUTTON_TOUCHPAD)) {
            addAction(ControllerId.X_AXIS, ButtonState.UP, 0);
            addAction(ControllerId.Y_AXIS, ButtonState.UP, 0);
        }
    }

    private void handleController1() {
        if (switchedUp(ControllerListener.BUTTON_TRIGGER)) {
            addAction(ControllerId.THREE, ButtonState.UP);
        } else if (switchedDown(ControllerListener.BUTTON_TRIGGER)) {
            addAction(ControllerId.THREE, ButtonState.DOWN);
        } else if (switchedUp(ControllerListener.BUTTON_GRIP)) {
            addAction(ControllerId.FOUR, ButtonState.UP);
        } else if (switchedDown(ControllerListener.BUTTON_GRIP)) {
            addAction(ControllerId.FOUR, ButtonState.DOWN);
        } else if (switchedUp(ControllerListener.BUTTON_APP_MENU)) {
            addAction(ControllerId.FIVE, ButtonState.UP);
        } else if (switchedDown(ControllerListener.BUTTON_APP_MENU)) {
            addAction(ControllerId.FIVE, ButtonState.DOWN);
        } else if (switchedDown(ControllerListener.BUTTON_TOUCHPAD)) {
            if (cachedStateAfter.rAxis[0].x < 0 && cachedStateAfter.rAxis[0].y < 0) {
                addAction(ControllerId.SIX, ButtonState.DOWN);
            } else if (cachedStateAfter.rAxis[0].x > 0 && cachedStateAfter.rAxis[0].y < 0) {
                addAction(ControllerId.SEVEN, ButtonState.DOWN);
            } else if (cachedStateAfter.rAxis[0].x < 0 && cachedStateAfter.rAxis[0].y < 0) {
                addAction(ControllerId.EIGHT, ButtonState.DOWN);
            } else if (cachedStateAfter.rAxis[0].x > 0 && cachedStateAfter.rAxis[0].y > 0) {
                addAction(ControllerId.NINE, ButtonState.DOWN);
            }
        } else if (switchedUp(ControllerListener.BUTTON_TOUCHPAD)) {
            if (cachedStateAfter.rAxis[0].x < 0 && cachedStateAfter.rAxis[0].y < 0) {
                addAction(ControllerId.SIX, ButtonState.UP);
            } else if (cachedStateAfter.rAxis[0].x > 0 && cachedStateAfter.rAxis[0].y < 0) {
                addAction(ControllerId.SEVEN, ButtonState.UP);
            } else if (cachedStateAfter.rAxis[0].x < 0 && cachedStateAfter.rAxis[0].y < 0) {
                addAction(ControllerId.EIGHT, ButtonState.UP);
            } else if (cachedStateAfter.rAxis[0].x > 0 && cachedStateAfter.rAxis[0].y > 0) {
                addAction(ControllerId.NINE, ButtonState.UP);
            }
        }
    }

    /**
     * Called whenever the OpenVR controller button state changes for a given controller (left or right).
     * @param stateBefore - the state before the last change.
     * @param stateAfter - the state after the last change.
     * @param handIndex - the hand index, an integer - 0 for left, 1 for right.
     */
    @Override
    public void buttonStateChanged(VRControllerState_t stateBefore, VRControllerState_t stateAfter, int handIndex) {
        cachedStateBefore = stateBefore;
        cachedStateAfter = stateAfter;
        if (handIndex == 0) {
            handleController0();
        } else {
            handleController1();
        }
    }

    /**
     * Called whenever the OpenVR controller pose changes for a given controller (left or right). This particular
     * listener just ignores pose updates.
     * @param pose - the pose of the controller (a 4x4 matrix).
     * @param handIndex - the hand index, an integer - 0 for left, 1 for right.
     */
    @Override
    public void poseChanged(Matrix4f pose, int handIndex) {
        // currently no actions are sensitive to controller movement
    }

}