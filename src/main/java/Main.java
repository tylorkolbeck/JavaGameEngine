import window.Configuration;
import window.Window;
import jade.scene.SceneManager;

public class Main {
    private static SceneManager sceneManager;
    public static Window window;

    public static void main(String[] args) {
        Configuration windowConfig = new Configuration();
        windowConfig.setTitle("Tylors Game Engine");

        window = Window.getInstance(windowConfig);
        window.run();
    }
}
