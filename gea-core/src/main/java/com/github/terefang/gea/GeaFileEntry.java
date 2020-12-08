package com.github.terefang.gea;

import java.io.File;

public class GeaFileEntry {
    String name;
    long offset;
    long size;
    long realSize;
    int compression;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getRealSize() {
        return realSize;
    }

    public void setRealSize(long realSize) {
        this.realSize = realSize;
    }

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    File localFile;

    public File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }
}
