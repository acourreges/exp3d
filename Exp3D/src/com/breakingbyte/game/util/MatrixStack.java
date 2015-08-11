package com.breakingbyte.game.util;

public class MatrixStack {
    
    public int capacity;
    
    Matrix4[] stack;
    int currentMatrixIndex;

    public MatrixStack(int maxCapacity) {
        capacity = maxCapacity;
        stack = new Matrix4[capacity];
        for (int i = 0; i < capacity; i++) {
            stack[i] = new Matrix4();
        }
        currentMatrixIndex = 0;
    }
    
    public Matrix4 getCurrent() {
        return stack[currentMatrixIndex];
    }
    
    public void push() {
        stack[currentMatrixIndex+1].set(stack[currentMatrixIndex]);
        currentMatrixIndex++;
    }
    
    public void pop() {
        currentMatrixIndex--;
    }
    
}
