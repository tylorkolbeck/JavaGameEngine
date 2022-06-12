package jade;

import jade.component.Sprite;
import jade.component.SpriteRenderer;
import jade.gameObject.GameObject;
import jade.transform.Transform;
import org.joml.Vector2f;

public class Prefabs {
    public static GameObject generateSpriteObjec(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = new GameObject("Sprite Object_gen",
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);
        SpriteRenderer renderer = new SpriteRenderer().setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
