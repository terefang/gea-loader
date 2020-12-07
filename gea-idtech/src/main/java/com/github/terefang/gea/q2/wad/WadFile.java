package com.github.terefang.gea.q2.wad;

import com.github.terefang.gea.GeaFile;

import java.io.*;

public class WadFile extends GeaFile<WadFileEntry>
{
    public InputStream getFileStream(WadFileEntry _entry) throws IOException
    {
        byte[] _buffer = new byte[_entry.getSize()];

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
