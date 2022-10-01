package demo1;

public class NetWork <I,O>{
    private final I inputStream;
    private final O outputSream;

    public NetWork(I inputStream, O outputSream) {
        this.inputStream = inputStream;
        this.outputSream = outputSream;
    }

    public I getInputStream(){
        return inputStream;
    }

    public O getOutputStream(){
        return outputSream;
    }
}
