package kww.useless.math;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class Vector2I implements Serializable {
    private static final long serialVersionUID = 913902788239530931L;

    public final static Vector2I X = new Vector2I(1, 0);
    public final static Vector2I Y = new Vector2I(0, 1);
    public final static Vector2I Zero = new Vector2I(0, 0);

    /** the x-component of this vector **/
    public float x;
    /** the y-component of this vector **/
    public float y;

    /** Constructs a new vector at (0,0) */
    public Vector2I() {}

    /**
     * Constructs a vector with the given components
     *
     * @param x The x-component
     * @param y The y-component
     */
    public Vector2I(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a vector from the given vector
     *
     * @param v The vector
     */
    public Vector2I(Vector2I v)
    {
        set(v);
    }

    public Vector2I set (Vector2I v) {
        x = v.x;
        y = v.y;
        return this;
    }
}
