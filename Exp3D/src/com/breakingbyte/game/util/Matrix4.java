package com.breakingbyte.game.util;

public class Matrix4 {

    // Shamelessly borrowed from the libgdx Matrix4 class
    
    public static final int M00 = 0;// 0;
    public static final int M01 = 4;// 1;
    public static final int M02 = 8;// 2;
    public static final int M03 = 12;// 3;
    public static final int M10 = 1;// 4;
    public static final int M11 = 5;// 5;
    public static final int M12 = 9;// 6;
    public static final int M13 = 13;// 7;
    public static final int M20 = 2;// 8;
    public static final int M21 = 6;// 9;
    public static final int M22 = 10;// 10;
    public static final int M23 = 14;// 11;
    public static final int M30 = 3;// 12;
    public static final int M31 = 7;// 13;
    public static final int M32 = 11;// 14;
    public static final int M33 = 15;// 15;
    
    public static final float tmp[] = new float[16];
    public final float val[] = new float[16];
    
    public Matrix4 () {
        val[M00] = 1f;
        val[M11] = 1f;
        val[M22] = 1f;
        val[M33] = 1f;
    }
    
    /** Constructs a matrix from the given matrix.
    *
    * @param matrix The matrix to copy. (This matrix is not modified) */
    public Matrix4 (Matrix4 matrix) {
        this.set(matrix);
    }
    
    /** Constructs a matrix from the given float array. The array must have at least 16 elements; the first 16 will be copied.
    * @param values The float array to copy. Remember that this matrix is in <a
    * href="http://en.wikipedia.org/wiki/Row-major_order">column major</a> order. (The float array is not modified) */
    public Matrix4 (float[] values) {
        this.set(values);
    }
    
    public Matrix4 set (Matrix4 matrix) {
        return this.set(matrix.val);
    }
    
    public Matrix4 set (float[] values) {
        //This is heavy in GWT, so just unroll it
        //System.arraycopy(values, 0, val, 0, val.length);
        val[M00] = values[M00];
        val[M01] = values[M01];
        val[M02] = values[M02];
        val[M03] = values[M03];
        val[M10] = values[M10];
        val[M11] = values[M11];
        val[M12] = values[M12];
        val[M13] = values[M13];
        val[M20] = values[M20];
        val[M21] = values[M21];
        val[M22] = values[M22];
        val[M23] = values[M23];
        val[M30] = values[M30];
        val[M31] = values[M31];
        val[M32] = values[M32];
        val[M33] = values[M33];
        
        return this;
    }
    
    public Matrix4 cpy () {
        return new Matrix4(this);
    }
    
    public static final float tmp2[] = new float[16];
    public static final void mul(float[] mata, float[] matb) {
        tmp2[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20] + mata[M03] * matb[M30];
        tmp2[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21] + mata[M03] * matb[M31];
        tmp2[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22] + mata[M03] * matb[M32];
        tmp2[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02] * matb[M23] + mata[M03] * matb[M33];
        tmp2[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20] + mata[M13] * matb[M30];
        tmp2[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21] + mata[M13] * matb[M31];
        tmp2[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22] + mata[M13] * matb[M32];
        tmp2[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12] * matb[M23] + mata[M13] * matb[M33];
        tmp2[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20] + mata[M23] * matb[M30];
        tmp2[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21] + mata[M23] * matb[M31];
        tmp2[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22] + mata[M23] * matb[M32];
        tmp2[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22] * matb[M23] + mata[M23] * matb[M33];
        tmp2[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32] * matb[M20] + mata[M33] * matb[M30];
        tmp2[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32] * matb[M21] + mata[M33] * matb[M31];
        tmp2[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32] * matb[M22] + mata[M33] * matb[M32];
        tmp2[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32] * matb[M23] + mata[M33] * matb[M33];
        
        //This is heavy in GWT, so just unroll it
        //System.arraycopy(tmp2, 0, mata, 0, mata.length);
        mata[M00] = tmp2[M00];
        mata[M01] = tmp2[M01];
        mata[M02] = tmp2[M02];
        mata[M03] = tmp2[M03];
        mata[M10] = tmp2[M10];
        mata[M11] = tmp2[M11];
        mata[M12] = tmp2[M12];
        mata[M13] = tmp2[M13];
        mata[M20] = tmp2[M20];
        mata[M21] = tmp2[M21];
        mata[M22] = tmp2[M22];
        mata[M23] = tmp2[M23];
        mata[M30] = tmp2[M30];
        mata[M31] = tmp2[M31];
        mata[M32] = tmp2[M32];
        mata[M33] = tmp2[M33];
}
    
    public Matrix4 trn (float x, float y, float z) {
        val[M03] += x;
        val[M13] += y;
        val[M23] += z;
        return this;
    }
    
    public Matrix4 mul (Matrix4 matrix) {
        mul(val, matrix.val);
        return this;
    }
    
    public Matrix4 idt () {
        val[M00] = 1;
        val[M01] = 0;
        val[M02] = 0;
        val[M03] = 0;
        val[M10] = 0;
        val[M11] = 1;
        val[M12] = 0;
        val[M13] = 0;
        val[M20] = 0;
        val[M21] = 0;
        val[M22] = 1;
        val[M23] = 0;
        val[M30] = 0;
        val[M31] = 0;
        val[M32] = 0;
        val[M33] = 1;
        return this;
    }
    
    public Matrix4 setToProjection (float near, float far, float fov, float aspectRatio) {
        idt();
        float l_fd = (float)(1.0 / Math.tan((fov * (Math.PI / 180)) / 2.0));
        float l_a1 = (far + near) / (near - far);
        float l_a2 = (2 * far * near) / (near - far);
        val[M00] = l_fd / aspectRatio;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = l_fd;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = l_a1;
        val[M32] = -1;
        val[M03] = 0;
        val[M13] = 0;
        val[M23] = l_a2;
        val[M33] = 0;
        
        return this;
    }
    
    public Matrix4 setToOrtho2D (float x, float y, float width, float height) {
        setToOrtho(x, x + width, y, y + height, 0, 1);
        return this;
    }
    
    public Matrix4 setToOrtho2D (float x, float y, float width, float height, float near, float far) {
        setToOrtho(x, x + width, y, y + height, near, far);
        return this;
    }
    
    public Matrix4 setToOrtho (float left, float right, float bottom, float top, float near, float far) {

        this.idt();
        float x_orth = 2 / (right - left);
        float y_orth = 2 / (top - bottom);
        float z_orth = -2 / (far - near);
        
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);
        
        val[M00] = x_orth;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = y_orth;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = z_orth;
        val[M32] = 0;
        val[M03] = tx;
        val[M13] = ty;
        val[M23] = tz;
        val[M33] = 1;
        
        return this;
    }

    public Matrix4 setToTranslation (float x, float y, float z) {
        idt();
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return this;
    }
    
    /*
    public Matrix4 setToRotation (float axisX, float axisY, float axisZ, float angle) {
        idt();
        if (angle == 0) return this;
        return set(quat.set(tmpV.set(axisX, axisY, axisZ), angle));
    }
    */
    
    @SuppressWarnings("unused")
    public Matrix4 setToRotation(float[] val, float rotX, float rotY, float rotZ, float angle) {
        
        if (true) {
        final float l_ang = (float)Math.toRadians(angle);
        float len = rotX * rotX + rotY * rotY + rotZ * rotZ;
        len = (float)Math.sqrt(len);
        
        final float x = rotX / len;
        final float y = rotY / len;
        final float z = rotZ / len;
        
        final float c = (float)Math.cos(l_ang);
        final float s = (float)Math.sin(l_ang);
        
        val[M00] = (x*x) * (1-c)+c;
        val[M01] = x*y * (1-c)-z*s;
        val[M02] = x*z * (1-c)+y*s;
        val[M03] = 0;
        val[M10] = y*x * (1-c)+z*s;
        val[M11] = (y*y) * (1-c)+c;
        val[M12] = y*z * (1-c)-x*s;
        val[M13] = 0;
        val[M20] = x*z * (1-c)-y*s;
        val[M21] = y*z * (1-c)+x*s;
        val[M22] = (z*z) * (1-c)+c;
        val[M23] = 0;
        val[M30] = 0;
        val[M31] = 0;
        val[M32] = 0;
        val[M33] = 1;
        return this;
        }
        else {
        
        //quaternion
        float l_ang = (float)Math.toRadians(angle);
        float l_sin = (float)Math.sin(l_ang / 2);
        float l_cos = (float)Math.cos(l_ang / 2);
        float x = rotX * l_sin;
        float y = rotY * l_sin;
        float z = rotZ * l_sin;
        float w = l_cos;
        float len = x * x + y * y + z * z + w * w;
        len = (float)Math.sqrt(len);
        w /= len;
        x /= len;
        y /= len;
        z /= len;
        // Compute quaternion factors
        float l_xx = x * x;
        float l_xy = x * y;
        float l_xz = x * z;
        float l_xw = x * w;
        float l_yy = y * y;
        float l_yz = y * z;
        float l_yw = y * w;
        float l_zz = z * z;
        float l_zw = z * w;
        // Set matrix from quaternion
        val[M00] = 1 - 2 * (l_yy + l_zz);
        val[M01] = 2 * (l_xy - l_zw);
        val[M02] = 2 * (l_xz + l_yw);
        val[M03] = 0;
        val[M10] = 2 * (l_xy + l_zw);
        val[M11] = 1 - 2 * (l_xx + l_zz);
        val[M12] = 2 * (l_yz - l_xw);
        val[M13] = 0;
        val[M20] = 2 * (l_xz - l_yw);
        val[M21] = 2 * (l_yz + l_xw);
        val[M22] = 1 - 2 * (l_xx + l_yy);
        val[M23] = 0;
        val[M30] = 0;
        val[M31] = 0;
        val[M32] = 0;
        val[M33] = 1;
        return this;
        }
    }
    
    public Matrix4 setToScaling (float x, float y, float z) {
        idt();
        val[M00] = x;
        val[M11] = y;
        val[M22] = z;
        return this;
    }
    
    public Matrix4 scl (float x, float y, float z) {
        val[M00] *= x;
        val[M11] *= y;
        val[M22] *= z;
        return this;
    }
    
    //public Matrix4 rotate()
    
    public Matrix4 scaleGL (float scaleX, float scaleY, float scaleZ) {
        tmp[M00] = scaleX;
        tmp[M01] = 0;
        tmp[M02] = 0;
        tmp[M03] = 0;
        tmp[M10] = 0;
        tmp[M11] = scaleY;
        tmp[M12] = 0;
        tmp[M13] = 0;
        tmp[M20] = 0;
        tmp[M21] = 0;
        tmp[M22] = scaleZ;
        tmp[M23] = 0;
        tmp[M30] = 0;
        tmp[M31] = 0;
        tmp[M32] = 0;
        tmp[M33] = 1;
        
        mul(val, tmp);
        return this;
    }
    
    public Matrix4 translateGL (float x, float y, float z) {
        tmp[M00] = 1;
        tmp[M01] = 0;
        tmp[M02] = 0;
        tmp[M03] = x;
        tmp[M10] = 0;
        tmp[M11] = 1;
        tmp[M12] = 0;
        tmp[M13] = y;
        tmp[M20] = 0;
        tmp[M21] = 0;
        tmp[M22] = 1;
        tmp[M23] = z;
        tmp[M30] = 0;
        tmp[M31] = 0;
        tmp[M32] = 0;
        tmp[M33] = 1;
        
        mul(val, tmp);
        return this;
    }
    
    public Matrix4 rotateGL (float angle, float x, float y, float z) {
        setToRotation(tmp, x, y, z, angle);
        mul(val, tmp);
        return this;
    }
    

}
