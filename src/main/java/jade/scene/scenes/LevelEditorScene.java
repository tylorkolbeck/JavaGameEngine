package jade.scene.scenes;

import assetPool.AssetPool;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Prefabs;
import jade.camera.Camera;
import jade.component.*;
import jade.gameObject.GameObject;
import jade.scene.Scene;
import jade.transform.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import window.eventListeners.MouseListener;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private Spritesheet sprites;

    MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();
        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");

        // Setup camera
        this.camera = new Camera(new Vector2f(-250, 0));

        if (levelLoaded) {
            this.activeGameObject = gameObjects.get(0);
            return;
        }


        // START Add all game components START
        // =========
        obj1 = new GameObject("Obj 1", new Transform(new Vector2f(100, 100), new Vector2f(100, 100)), 0)
                .addComponent(new SpriteRenderer().setColor(new Vector4f(0, 1, 1, 1)))
                .addComponent(new RigidBody());
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Obj 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 0)
            .addComponent(
                    new SpriteRenderer().setSprite(
                            new Sprite().setTexture(
                                    AssetPool.getTexture("assets/textures/test/testImage.png"))));

        this.addGameObjectToScene(obj2);

        // END Add all game components END
        // =========

    }

    private void loadResources() {
        AssetPool.getShader("default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16,16,81,0));
        AssetPool.getTexture("assets/textures/test/testImage.png");
    }

    @Override
    public void update(float dt) {
        this.camera().adjustProjection();
        MouseListener.getOrhoX();
//        System.out.println("FPS: " + ((1.0f / dt)));
        mouseControls.update(dt);
        for(GameObject go: this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i <  sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            Vector2f[] texCoords = sprite.getTexCoords();
            int id = sprite.getTexId();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y )) {
                GameObject object = Prefabs.generateSpriteObjec(sprite, spriteWidth, spriteHeight);
                mouseControls.pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }


        ImGui.text("Some random text");
        ImGui.end();
    }
}
