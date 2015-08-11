package com.breakingbyte.game.util;

import java.util.ArrayList;

import com.breakingbyte.wrap.Log;

/**
 * To avoid GC, maintain a pool of available objects.
 */


public class ObjectPool {
    
    public static interface Constructor {
        public Poolable newObject();
    }
    
    private static String TAG = "ObjectPool";
    
    private ArrayList<Poolable> pool;
    
    private int currentCapacity;
    
    private Constructor constructor;
    
    public ObjectPool(int initialCapacity, Constructor constructor) {
        this.currentCapacity = initialCapacity;
        this.constructor = constructor;
        pool = new ArrayList<Poolable>(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            pool.add(constructor.newObject());
        }
    }
    
    public Poolable getFreeInstance(){
        
        if (pool.size() == 0) {
            //Pool is empty, we need to create more entities
            currentCapacity++;
            Log.d(TAG, "Pool empty! " + constructor.newObject().getClass().getName() + " Increments to " + Integer.toString(currentCapacity) );
            pool.add(constructor.newObject());
        }
        
        Poolable entity = (Poolable)pool.get(0);        
        pool.remove(0);
      
        entity.resetState(); //Needed only if default init values changed while 'entity' was waiting in the pool
        
        return entity;        
    }
    
    //Return an instance to the pool
    public void returnToPool(Poolable t){
        
        t.resetState();
        pool.add(t);
    }

    
}

/* Original way it was done, unfortunately, cannot be translated in Javascriot for GWT... */
/*
public class ObjectPool<T extends Poolable> {
    
    private static String TAG = "ObjectPool";

    private Class<T> runtimeClass;
    
    private ArrayList<T> pool;
    
    private int currentCapacity;
    
//    
//     * Initializes a new object pool.
//     * There is no easy way to determine the class of T so unfortunately 
//     * we have to explicitly provide it to the constructor. 
//     * @param c Class of the pool elements (T.class for ObjectPool<T>)
//     
    public ObjectPool(Class<T> c) {
        this(c, 0);
    }
    
//    
//     * Initializes a new entity pool.
//     * @param c Class of the pool elements (T.class for EntityPool<T>)
//     * @param initialCapacity initial number of elements in the pool
//     
    public ObjectPool(Class<T> c, int initialCapacity) {
        pool = new ArrayList<T>();
        currentCapacity = initialCapacity;
        runtimeClass = c;
        for (int i = 0; i < initialCapacity; i++) {
            pool.add(createNewPoolMember());
        }
    }   
    
    //Create a *runtype* instance of the Poolable object
    private T createNewPoolMember() {
        try {
            T object = runtimeClass.newInstance();
            object.resetState();
            return object;            
        } catch (Exception e) {
            Log.e(TAG, "Could not add new to the pool!" + runtimeClass.getName(), e);
            e.printStackTrace();
        }
        return null;
    }
    
    //Grab an available instance from the pool
    // @SuppressWarnings("unchecked")
    public T getFreeInstance(){
        
        if (pool.size() == 0) {
            //Pool is empty, we need to create more entities
            currentCapacity++;
            Log.d(TAG, "Pool empty! " + runtimeClass.getName() + " Increments to " + Integer.toString(currentCapacity) );
            pool.add(createNewPoolMember());
        }
        
        T entity = (T)pool.get(0);        
        pool.remove(0);
      
        entity.resetState(); //Needed only if default init values changed while 'entity' was waiting in the pool
        
        return (T)entity;        
    }
    
    //Return an instance to the pool
    public void returnToPool(T t){
        
        t.resetState();
        pool.add(t);
    }
}
*/   

