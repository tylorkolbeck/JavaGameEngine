package jade.scene;

import jade.scene.scenes.LevelEditorScene;
import jade.scene.scenes.LevelScene;

public class SceneManager {
    private static Scene currentScene;

    public SceneManager() {}

    public static void setScene(int scene) {
        switch (scene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene" + scene;
        }

        if (currentScene != null) {
            currentScene.load();
            currentScene.init();
            currentScene.start();
        }
    }

    public static void setScene(Scene scene) {
        currentScene = scene;
    }

    public static Scene getScene() {
        return currentScene;
    }
}
