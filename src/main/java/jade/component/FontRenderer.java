package jade.component;

public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("FOUND FONT RENDERER");
        }
    }

    @Override
    public void update(float dt) {

    }
}
