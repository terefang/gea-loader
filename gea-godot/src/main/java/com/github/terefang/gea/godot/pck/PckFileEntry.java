package com.github.terefang.gea.godot.pck;

import com.github.terefang.gea.GeaFileEntry;

public class PckFileEntry extends GeaFileEntry
{
    byte[] md5sum;

    public byte[] getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(byte[] md5sum) {
        this.md5sum = md5sum;
    }

    int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
