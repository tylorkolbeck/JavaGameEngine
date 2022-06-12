package assetPool;

import jade.component.Spritesheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File("assets/shaders/" + resourceName);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(file.getAbsolutePath());
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File( resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture().init(file.getAbsolutePath());
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet " + resourceName + " and it has not been added to the asset pool";
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
}
