package window;

import imgui.ImGuiLayer;
import jade.scene.Scene;
import jade.scene.SceneManager;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL32;
import renderer.debug.DebugDraw;
import util.Time;
import window.eventListeners.KeyListener;
import window.eventListeners.MouseListener;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width;
    private int height;
    private static Window window = null;
    public Vector4f backgroundColor;
    private Configuration config;

    /**
     * Pointer to the native window
     */
    private static long glfwWindow;
    private String glslVersion = null;
    private ImGuiLayer guiLayer;
    public static SceneManager sceneManager = null;

    public Window(Configuration config) {
        this.setBackgroundColor(config.getBackgroundColor());
        this.width = config.getWidth();
        this.height = config.getHeight();
        this.config = config;
    }

    public static Window getInstance() {
        return getInstance(new Configuration());
    }

    public static Window getInstance(Configuration config) {
        if (Window.window == null) {
            Window.window = new Window(config);
        }

        return Window.window;
    }

    public static Scene getScene() {
        return SceneManager.getScene();
    }

    // Initialize the window and start the loop
    public void run() {
        System.out.println("LWJGL running " + Version.getVersion());

        initWindow();
        guiLayer = new ImGuiLayer(glfwWindow);

        setScene(0);
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initWindow() {
        setSceneManager(new SceneManager());

        // Error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }


        decideGlGlsVersions();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindow = glfwCreateWindow(width, height, config.getTitle(), NULL, NULL);

        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        registerEventCallbacks(glfwWindow);


//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            final IntBuffer pWidth = stack.mallocInt(1); // int*
//            final IntBuffer pHeight = stack.mallocInt(1); // int*
//
//            GLFW.glfwGetWindowSize(glfwWindow, pWidth, pHeight);
//            final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
//            GLFW.glfwSetWindowPos(glfwWindow, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
//        }
        glfwMakeContextCurrent(glfwWindow);

        createCapabilities();
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glfwSwapInterval(GLFW_TRUE);


        if (config.isFullScreen()) {
            glfwMaximizeWindow(glfwWindow);
        } else {
            glfwShowWindow(glfwWindow);
        }

        clearBuffer();
        renderBuffer();
    }

    protected void runFrame() {
        System.out.println("Running Frame");
    }


    private void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();


            DebugDraw.beginFrame();
            // Clear the buffer
            glClearColor(this.backgroundColor.x,this.backgroundColor.y, this.backgroundColor.z, this.backgroundColor.w );
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Call the update function on the current scene
            if (dt >= 0) {
                // Drawing lines first will make them behind everything else
                DebugDraw.draw();
                if (Window.sceneManager.getScene() != null) {
                    Window.sceneManager.getScene().update(dt);
                }
            }


            this.guiLayer.update(dt, sceneManager.getScene());
            glfwSwapBuffers(glfwWindow); // swap color buffer

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

        getScene().saveExit();
    }

    private void registerEventCallbacks(long glfwWindow) {
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCb);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseBtnCb);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseBtnScrollCb);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
            glViewport(0,0, newWidth, newHeight);
        });
    }

    public void setWindowDimensions(int width, int height) {
        this.height = height;
        this.width = width;

    }

    public void setSceneManager(SceneManager sceneManager) {
        Window.sceneManager = sceneManager;
    }

    public void setScene(int scene) {
        sceneManager.setScene(scene);
    }

    public static int getWidth() {
        return getInstance().width;
    }

    public static int getHeight() {
        return getInstance().height;
    }

    public static void setHeight(int height) {
        getInstance().height = height;
    }

    public static void setWidth(int width) {
        getInstance().width = width;
    }

    public void decideGlGlsVersions() {
        final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        if (isMac) {
            glslVersion = "#version 150";
            org.lwjgl.glfw.GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);          // Required on Mac
        } else {
            glslVersion = "#version 130";
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
        }
    }

    private void clearBuffer() {
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
    }

    private void renderBuffer() {
        glfwSwapBuffers(glfwWindow);
        glfwPollEvents();
    }

    public void setBackgroundColor(Vector4f color) {
        this.backgroundColor = color;
    }
}
