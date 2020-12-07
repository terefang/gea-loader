package com.github.terefang.gea.q2.pak;

import com.github.terefang.gea.GeaFile;
import java.io.*;

public class PakFile extends GeaFile<PakFileEntry>
{
    int compression;

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public InputStream getFileStream(PakFileEntry _entry) throws IOException
    {
        if(_entry.getSize()==0) return null;

        byte[] _buffer = new byte[_entry.getSize()];

        RandomAccessFile _raf = new RandomAccessFile(new File(this.getFilepath()), "r");
        _raf.seek(_entry.getOffset());
        _raf.read(_buffer);
        _raf.close();

        ByteArrayInputStream _bais = new ByteArrayInputStream(_buffer);

        return PakUtil.wrap(_bais, _entry.getCompression());
    }

    @Override
    public void close() throws IOException
    {
    }
}
