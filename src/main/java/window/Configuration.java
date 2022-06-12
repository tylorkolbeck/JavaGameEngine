package window;

import org.joml.Vector4f;

public class Configuration {
    /**
     * Application title
     */
    private String title = "Window Title";
    /**
     * Application window width
     */
    private int width = 1920;
    /**
     * Application window height
     */
    private int height = 1080;
    /**
     * When true, application be maximized by default
     */
    private boolean fullScreen = true;


    /**
     *
     * Windows default background color
     */
    private Vector4f backgroundColor = new Vector4f(0.05f, 0.05f, 0.05f, 1.0f);


    public Vector4f getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Vector4f backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public boolean isFullScreen() {
        return this.fullScreen;
    }

    public void setFullScreen(final boolean fullScreen) {
        this.fullScreen = fullScreen;
    }
}
