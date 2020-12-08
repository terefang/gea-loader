package com.github.terefang.gea.godot.pck;

import com.github.terefang.gea.GeaFile;

import java.io.*;

public class PckFile extends GeaFile<PckFileEntry>
{
    int compression;
    int format;
    int major;
    int minor;
    int patch;
    int flags;

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public InputStream getFileStream(PckFileEntry _entry) throws IOException
    {
        if(_entry.getSize()==0) return null;

        byte[] _buffer = new byte[(int) _entry.getSize()];

        RandomAccessFile _raf = new RandomAccessFile(new File(this.getFilepath()), "r");
        _raf.seek(_entry.getOffset());
        _raf.read(_buffer);
        _raf.close();

        return new ByteArrayInputStream(_buffer);
    }

    @Override
    public void close() throws IOException
    {
    }
}
