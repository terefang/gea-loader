package com.github.terefang.gea;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GeaFile<T extends GeaFileEntry>
{
    long size;
    long offset;
    String filepath;
    Map<String, T> fileEntries = new LinkedHashMap<String, T>();

    public GeaFile() {
        super();
    }

    public GeaFile(String filepath) {
        this();
        this.filepath = filepath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Map<String, T> getFileEntries() {
        return fileEntries;
    }

    public void setFileEntries(Map<String, T> fileEntries) {
        this.fileEntries = fileEntries;
    }

    public T getFileEntry(String _entry)
    {
        return this.fileEntries.get(_entry);
    }

    public void addFileEntry(String _path, T _entry)
    {
        this.fileEntries.put(_path, _entry);
    }

    public void addFileEntry(T _entry)
    {
        this.fileEntries.put(_entry.getName(), _entry);
    }

    public InputStream getFileStream(String _path) throws IOException {
        return this.getFileStream(this.getFileEntry(_path));
    }

    public abstract InputStream getFileStream(T _entry) throws IOException;

    public abstract void close() throws IOException;
}
