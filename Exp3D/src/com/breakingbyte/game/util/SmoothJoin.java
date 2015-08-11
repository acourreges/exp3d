package com.breakingbyte.game.util;

import com.breakingbyte.game.util.SmoothJoin.Interpolator;
import com.breakingbyte.wrap.shared.Timer;

public class SmoothJoin {
    
    public enum Interpolator {
        ASYMPTOTIC,
        LINEAR,
        
        LINEAR_TIMED,
        
        SINUSOIDAL,
        SINUSOIDAL_SLOW_START,
        
        QUADRATIC_START,
        QUADRATIC_END,
        QUADRATIC_START_END,
        
        BACK_START,
        BACK_END,
        BACK_START_END,
        
        BOUNCE,
        
        ELASTIC
    }
    
    protected Interpolator interpolator;
    
    private InnerJoin[] joins;
    private int nbComponents; 
    
    public SmoothJoin() {
        this(1);
    }
    
    public SmoothJoin(int nbComponents) {
        this.nbComponents = nbComponents;
        joins = new InnerJoin[nbComponents];
        for (int i = 0; i < nbComponents; i++) {
            joins[i] = new InnerJoin(this);
        }
        interpolator = Interpolator.ASYMPTOTIC;
    }
    
    public SmoothJoin setInterpolator(Interpolator i) {
        interpolator = i;
        return this;
    }
    
    public float backAmplitude = 5.70158f;
    
    protected float elasticAmplitude = 0f;
    protected float elasticDamping = 0f;
    
    /*
    protected boolean elasticAmplitudeSet = false;
    
    public void setElasticAmplitude(float value) {
        elasticAmplitude = value;
        elasticAmplitudeSet = true;
    }
    
    protected boolean elasticDampingSet = false;
    
    public void setElasticDamping(float value) {
        elasticDamping = value;
        elasticDampingSet = true;
    }
    */
    
    public void setElasticValues(float amplitude, float damping) {
        elasticAmplitude = amplitude;
        elasticDamping = damping;
    }
    
    public void setBack(float amplitude) {
        backAmplitude = amplitude; // 1.70158f;
    }
    
    public float get() {
        return joins[0].getValue();
    }
    
    public float get(int index) {
        return joins[index].getValue();
    }
    
    public void init(float value) {
        joins[0].initAt(value);
    }
    
    public void initAt(int index, float value) {
        joins[index].initAt(value);
    }
   
    public void setTarget(float targetValue, float speed, float delay) {
        joins[0].setTarget(targetValue, speed, delay);
    }
    
    public void setTarget(float targetValue, float speed) {
        joins[0].setTarget(targetValue, speed, 0);
    }
    
    public void setTargetAt(int index, float targetValue, float speed, float delay) {
        joins[index].setTarget(targetValue, speed, delay);
    }
    
    public void setTargetAt(int index, float targetValue, float speed) {
        joins[index].setTarget(targetValue, speed, 0);
    }
    
    //Returns true if value changed
    public boolean update() {
        boolean result = false;
        for (int i = 0; i < nbComponents; i++) {
            result |= joins[i].update();
        }
        return result;
    }
    
    //Some utils
    public void init(float x, float y, float z, float w){
        joins[0].initAt(x);
        joins[1].initAt(y);
        joins[2].initAt(z);
        joins[3].initAt(w);
    }
    
    public void setTarget(float x, float y, float z, float w, float speed, float delay){
        joins[0].setTarget(x, speed, delay);
        joins[1].setTarget(y, speed, delay);
        joins[2].setTarget(z, speed, delay);
        joins[3].setTarget(w, speed, delay);
    }
    
    public void setTarget(float x, float y, float z, float w, float speed){
        joins[0].setTarget(x, speed, 0);
        joins[1].setTarget(y, speed, 0);
        joins[2].setTarget(z, speed, 0);
        joins[3].setTarget(w, speed, 0);
    }

}

class InnerJoin {
    
    SmoothJoin parent;
    
    private float initialValue;
    private float value;
    private float targetValue;
    private float speed;
    
    private float time;
    
    private float delay = 0;
    
    private final float almostZero = 0.001f;
    
    public InnerJoin(SmoothJoin parent) {
        this.parent = parent;
        initAt(0);
    }
    
    public float getValue() {
        return value;
    }
    
    public void initAt(float value){
        this.initialValue = value;
        this.value = value;
        this.time = 0;
    }
    
    public void setTarget(float targetValue, float speed, float delay) {
        this.targetValue = targetValue;
        this.speed = speed;
        this.delay = delay;
    }
    
    //Returns true if value changed
    public boolean update() {
        
        if (delay > 0) {
            delay -= Timer.delta;
            return true;
        }
        
        switch (parent.interpolator) {
            case ASYMPTOTIC:
            {
                float difference = targetValue - value;
                float absDifference = Math.abs(difference);
                
                if (absDifference <= almostZero) {
                    return false;
                }
                
                float grow = speed * absDifference * Timer.delta;
                if (grow > absDifference) grow = absDifference;            
                value += (difference < 0? -grow : grow); 
                return true;
            }

            case LINEAR:
            {
                float difference = targetValue - initialValue;
                if (difference == 0) return false;

                value += (difference > 0)? speed * Timer.delta : -speed * Timer.delta;
                if ( (difference >= 0 && value >= targetValue) || (difference < 0 && value <= targetValue) ) 
                {
                        value = targetValue;
                        return false;
                }
                return true;
            }

            default:
                if (time == speed) {
                    value = targetValue;
                    return false;
                }
                
                time += Timer.delta;
                if (time >= speed) {
                    time = speed;
                    value = targetValue;
                    return true;
                }
                //float difference = targetValue - initialValue;
                //float progress = time / speed;
                value = initialValue + (targetValue - initialValue) * getEquationProgress( parent.interpolator, time / speed );
                return true;  
                
        }

    }
    
    /**
     * Returns a normalized progress-value calculated form the time elapsed.
     * Some equations based on the Java-universal-tween-engine library, 
     * itself based on Robert Penner's work http://robertpenner.com/easing/
     * @return the progress value
     */
    @SuppressWarnings("incomplete-switch")
    private float getEquationProgress(Interpolator interpolator, float progress) {
        
        float s;
        
        switch (interpolator) {
            
            case LINEAR_TIMED:
                return progress;
            
            case SINUSOIDAL:
                return (float)Math.sin(progress * MathUtil.HALF_PI);
                
            case SINUSOIDAL_SLOW_START:
                return 0.5f * (1 + (float)Math.sin( (progress * MathUtil.PI ) - MathUtil.HALF_PI) );
            
            case QUADRATIC_START:
                return progress * progress;
                
            case QUADRATIC_END:
                return -progress * (progress - 2);
                
            case QUADRATIC_START_END:
                if ( (progress *= 2) < 1) return 0.5f * progress * progress;
                return -0.5f * ((--progress) * (progress - 2) - 1);
            
            case BACK_START:
                s = parent.backAmplitude;
                return progress * progress * ((s+1)*progress - s);
                
            case BACK_END:
                s = parent.backAmplitude;
                return (progress-=1) * progress * ((s+1)*progress + s) + 1;
 
            case BACK_START_END:
                s = parent.backAmplitude;
                if ((progress*=2) < 1) return 0.5f*(progress*progress*(((s*=(1.525f))+1)*progress - s));
                return 0.5f*((progress-=2)*progress*(((s*=(1.525f))+1)*progress + s) + 2);
                
            case BOUNCE:
                if (progress < (1 / 2.75f)) {
                        return 7.5625f * progress * progress;
                } else if (progress < (2 / 2.75f)) {
                        return 7.5625f * (progress -= (1.5f/2.75f)) * progress + 0.75f;
                } else if (progress < (2.5f / 2.75f)) {
                        return 7.5625f * (progress -= (2.25f/2.75f)) * progress + 0.9375f;
                } else {
                        return 7.5625f * (progress -= (2.625f/2.75f)) * progress + 0.984375f;
                }
                
            case ELASTIC:
                /*
                float t = progress;
                if (t == 0) return 0; 
                if (t == 1) return 1; 
                if (!parent.elasticDampingSet) parent.elasticDamping = 0.3f;
                float s;
                if (!parent.elasticAmplitudeSet || parent.elasticAmplitude < 1) { 
                    parent.elasticAmplitude = 1;
                    s = parent.elasticDamping / 4; 
                } else {
                    s = parent.elasticDamping / (MathUtil.TWO_PI) * (float)Math.asin( 1 / parent.elasticAmplitude);
                }
                return parent.elasticAmplitude * (float)Math.pow(2,-10*t) * (float)Math.sin( (t-s)*(MathUtil.TWO_PI) / parent.elasticDamping ) + 1;
                */
                float t = progress;
                if (t == 0) return 0; 
                if (t == 1) return 1; 
                if ( parent.elasticAmplitude <= 1 ) { 
                    parent.elasticAmplitude = 1;
                    s = parent.elasticDamping / 4; 
                } else {
                    s = parent.elasticDamping / (MathUtil.TWO_PI) * (float)Math.asin( 1 / parent.elasticAmplitude);
                }
                return parent.elasticAmplitude * (float)Math.pow(2,-14*t) * (float)Math.sin( (t-s)*(MathUtil.TWO_PI) / parent.elasticDamping ) + 1;
                

        }
        
        return 0f;
    }
    
}

