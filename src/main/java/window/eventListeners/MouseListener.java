package window.eventListeners;

import org.joml.Vector4f;
import window.Window;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;
    private boolean mouseButtonState[] = new boolean[9];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    public static void mousePosCb(long window, double xPos, double yPos) {
        // set current positions to last positions
        get().lastX = get().xPos;
        get().lastY = get().yPos;

        // set current positions
        get().xPos = xPos;
        get().yPos = yPos;

        get().isDragging = get().mouseButtonState[0] || get().mouseButtonState[1] || get().mouseButtonState[2];
    }

    public static void mouseBtnCb(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonState.length) {
                get().mouseButtonState[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if (button < get().mouseButtonState.length) {
                get().mouseButtonState[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseBtnScrollCb(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getMouseX() {
        return (float)get().xPos;
    }

    public static float getMouseY() {
        return (float)get().yPos;
    }

    public static float getOrhoX() {
        float currentX = getMouseX();
        currentX = (currentX / (float)Window.getWidth()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
        tmp.mul(Window.getScene().camera().getInverseProjection()).mul(Window.getScene().camera().getInverseView());
        currentX = tmp.x;

        return currentX;
    }

    public static float getOrthoY() {
        float currentY = Window.getHeight() - getMouseY();
        currentY = (currentY / (float)Window.getHeight()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);
        tmp.mul(Window.getScene().camera().getInverseProjection()).mul(Window.getScene().camera().getInverseView());
        currentY = tmp.y;

        return currentY;
    }

    public static float getMouseDx() {
        return (float)(get().lastX - get().xPos);
    }

    public static float getMouseDy() {
        return (float)(get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isMouseDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonState.length) {
            return get().mouseButtonState[button];
        }

        return false;
    }

}
