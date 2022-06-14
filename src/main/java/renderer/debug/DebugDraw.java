package renderer.debug;

import assetPool.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.Shader;
import window.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 500;

    private static List<Line2D> lines = new ArrayList<>();
    // 6 floats per vertex, 2 verties per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("debugLine2D.glsl");
    private static int DEFAULT_LIFETIME = 240; // 10 frames
    private static Vector3f DEFAULT_COLOR = new Vector3f(0, 1,0);
    private static float LINE_STROKE = 2.0f;

    private static int vaoID;
    private static int vboID;

    private static boolean started = false;

    public static void start() {
        // Generate the VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);


        // create the vbo and buffer the memory
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Enable the vertex array attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(LINE_STROKE);

        started = true;
    }

    public static void beginFrame() {
        if (!started) {
            start();
        }

        // Remove deadlines
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if (lines.size() <= 0 ) return;
        int index = 0;
        for (Line2D line : lines) {
            for (int i=0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();


                // Load position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;


                // load color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;

                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0 , lines.size() * 6 * 2));

        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());


        // bind the vao
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // draw the batch
        glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);


        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detatch();
    }

    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from, to, DEFAULT_COLOR, DEFAULT_LIFETIME);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, DEFAULT_LIFETIME);
    }

    public static  void addLine2D(Vector2f from , Vector2f to, Vector3f color, int lifeTime) {
        if (lines.size() >= MAX_LINES) {
            lines.remove(0);
        }
        DebugDraw.lines.add(new Line2D(from, to, color, lifeTime));
    }
}
