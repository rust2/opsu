package kww.useless.math;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Matrix4ex extends Matrix4 {
    static final Quaternion quat = new Quaternion();
    public Matrix4 moveTo(float x, float y, float z)
    {
        this.val[M03] = x;
        this.val[M13] = y;
        this.val[M23] = z;
        return this;
    }

    public Matrix4 rotateTo(Vector3 axis, float degrees)
    {
        quat.set(axis, degrees);
        return this.rotateTo(quat.x, quat.y, quat.z, quat.w);
    }

    public Matrix4 rotateTo(float quaternionX, float quaternionY, float quaternionZ, float quaternionW)
    {
        final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
        final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
        final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
        final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

        val[M00] = 1f - (yy + zz);
        val[M01] = xy - wz;
        val[M02] = xz + wy;

        val[M10] = xy + wz;
        val[M11] = 1f - (xx + zz);
        val[M12] = yz - wx;

        val[M20] = xz - wy;
        val[M21] = yz + wx;
        val[M22] = 1f - (xx + yy);

        return this;
    }
}
