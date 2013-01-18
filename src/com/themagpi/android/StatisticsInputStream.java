package com.themagpi.android;

import java.io.IOException;
import java.io.InputStream;

public class StatisticsInputStream extends InputStream {
    private InputStream source;
    private long bytesRead;
 
    public StatisticsInputStream (InputStream source) {
        this.source = source;
    }
 
    public int read() throws IOException {
        int value = source.read();
        bytesRead++;
        return value;
    }
  
    public long getBytesRead() {
        return bytesRead;
    }
}