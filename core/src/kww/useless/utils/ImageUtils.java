package kww.useless.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;

public class ImageUtils {
    public static int getMaxTextureSize()
    {
        IntBuffer buffer = BufferUtils.newIntBuffer(16);
        Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buffer);
        //kww: todo: proper logging solution
        System.out.println("Maximum supported texture size: " + buffer.get(0));
        return buffer.get(0);
    }
}
