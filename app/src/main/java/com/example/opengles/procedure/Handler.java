package com.example.opengles.procedure;


public abstract class Handler {
    protected static final String TAG = Handler.class.getSimpleName();
    protected Handler nextHandler;

    public void setNextHandler(Handler nextHandler){
        this.nextHandler = nextHandler;
    }
    public abstract void handler(ProgramBean bean);
}
