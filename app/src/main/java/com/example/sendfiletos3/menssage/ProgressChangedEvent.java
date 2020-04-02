package com.example.sendfiletos3.menssage;

public class ProgressChangedEvent {
    public final int id;
    public final long bytesCurrent;
    public final long bytesTotal;

    public ProgressChangedEvent(int id, long bytesCurrent, long bytesTotal) {
        this.id = id;
        this.bytesCurrent = bytesCurrent;
        this.bytesTotal = bytesTotal;
    }
}
