package com.example.opengles.procedure;


public abstract class GLHandler {
    protected static final String TAG = GLHandler.class.getSimpleName();
    protected GLHandler nextHandler;

    public void setNextHandler(GLHandler nextHandler){
        this.nextHandler = nextHandler;
    }
    public abstract void handler(ProgramBean bean);
}
