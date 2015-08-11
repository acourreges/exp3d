package com.breakingbyte.game.util;


public class MathUtil {
    
    public static final float PI = (float)Math.PI; //3.1415927f;
    
    public static final float TWO_PI = PI * 2f;
    public static final float HALF_PI = PI / 2f;
    
    static public final float radiansToDegrees = 180f / PI;
    static public final float degreesToRadians = PI / 180f;

    /**
     * Returns a random integer in the range [min, max]
     * @param min Minimum value of the range
     * @param max Maximum value of the range
     * @return Random integer in [min, max]
     */
    static public int getRandomInt(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    
    static public int roundedUpInt(float f) {
        return (int)Math.ceil(f);
    }
    
    /**
     * Given a point O(oriX,oriY) and a vector V(oriDX,oriDY), 
     * with V origin in O and V unit vector, tells how much we
     * need to rotate V around O so that V points to Target.
     * @param oriX O x coordinate
     * @param oriY O y coordinate
     * @param oriDX V x coordinates
     * @param oriDY V y coordinates
     * @param targetX Target x coordinate
     * @param targetY Target y coordinate
     * @return Angle to rotate around O so V points to Target
     */
    static public float getAngleBetween(
            float oriX, float oriY, 
            float oriDX, float oriDY,
            float targetX, float targetY) 
    {
        float cos = oriDX;
        float sin = oriDY;
        
        float currentAngle = (float)Math.acos(cos);
        if (sin < 0) currentAngle = -currentAngle;
        
        return getAngleBetween(
                oriX, oriY,
                currentAngle,
                targetX, targetY);
    }
    
    /**
     * Given a point O(oriX,oriY) and a rotation angle around O 
     * oriAngle, tells how much rotation we still need to add to
     * oriAngle so we are "looking at the target" from O.
     * @param oriX O x coordinate
     * @param oriY O y coordinate
     * @param oriAngle initial angle of rotation around O, within [-2PI,PI]
     * @param targetX Target x coordinate
     * @param targetY Target y coordinate
     * @return Angle to rotate around O so V points to Target
     */
    static public float getAngleBetween(
            float oriX, float oriY, 
            float oriAngle,
            float targetX, float targetY) 
    {       
        float currentAngle = oriAngle;
        
        //Normalized vector to target
        float vectorToTargetX = targetX - oriX;
        float vectorToTargetY = targetY - oriY;
        
        float normSquare = vectorToTargetX * vectorToTargetX + vectorToTargetY * vectorToTargetY;
        if (normSquare == 0f) {
            //Target and origin are the same.
            return 0f;
        } 
        float norm = (float)Math.sqrt(normSquare);
    
        vectorToTargetX /= norm;
        vectorToTargetY /= norm;
        
        //calculate target angle
        float cos = vectorToTargetX;
        float sin = vectorToTargetY;
        
        float targetAngle = (float)Math.acos(cos);
        if (sin < 0) targetAngle = -targetAngle; 

        //How much should we rotate?
        float angleBetweenTargetAndDirection = targetAngle - currentAngle;
        if (angleBetweenTargetAndDirection < 0 && angleBetweenTargetAndDirection < -Math.PI) {
            angleBetweenTargetAndDirection += 2*Math.PI;
        } else if (angleBetweenTargetAndDirection > 0 && angleBetweenTargetAndDirection > Math.PI) {
            angleBetweenTargetAndDirection -= 2*Math.PI;
        }
        
        return angleBetweenTargetAndDirection;
    }
    
    static public float getCyclicValue(float minRange, float maxRange, float t) {
        float range = maxRange - minRange;
        return minRange + ( range * 0.5f ) + ( range * 0.5f * (float)Math.cos(t) );
    }
    
    static public float getCirclePosition(float centerX, float centerY, float radius, float angleBegin, float angleEnd, int index, int totalNumber, boolean returnX )
    {
        float angle = angleBegin + (angleEnd - angleBegin) * (index / (float)(totalNumber - 1));
        if (returnX) return centerX + radius * (float) Math.cos(angle * degreesToRadians);
        else return centerY + radius * (float) Math.sin(angle * degreesToRadians);
    }

}



