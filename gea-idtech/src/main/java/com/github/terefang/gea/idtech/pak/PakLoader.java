package com.github.terefang.gea.idtech.pak;

import com.github.terefang.gea.GeaLoader;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PakLoader implements GeaLoader<PakFile>
{
    public static PakLoader instance() { return new PakLoader(); }

    public PakFile explode(File _path, File _dest) throws IOException
    {
        PakFile _pak = this.load(_path);
        for(PakFileEntry _entry : _pak.getFileEntries().values())
        {
            File _fn = new File(_dest, _entry.getName());
            _fn.getParentFile().mkdirs();
            _entry.setLocalFile(_fn);
            FileOutputStream _fh = new FileOutputStream(_fn);
            if(_entry.getSize()>0)
            {
                IOUtils.copy(_pak.getFileStream(_entry), _fh, 8192);
            }
            else
            {
                IOUtils.closeQuietly(_fh);
            }
        }
        return _pak;
    }

    public PakFile load(File _path) throws IOException
    {
        PakFile _pak = new PakFile();
        _pak.setFilepath(_path.getAbsolutePath());

        RandomAccessFile _raf = new RandomAccessFile(_path, "r");

        int _compression = PakUtil.COMRESS_NONE;
        byte[] _header = new byte[PakUtil._HEADER_SIZE];
        _raf.read(_header);

        if (PakUtil.compare(PakUtil._HEADER_PACK, _header))
        {
            _compression = PakUtil.COMRESS_NONE;
        }
        else
        if (PakUtil.compare(PakUtil._HEADER_PACK_ZLIB, _header))
        {
            _compression = PakUtil.COMRESS_FLATE;
        }
        else
        if (PakUtil.compare(PakUtil._HEADER_PACK_BZLIB, _header))
        {
            _compression = PakUtil.COMRESS_BZLIB;
        }
        else
        if (PakUtil.compare(PakUtil._HEADER_PACK_LZ4, _header))
        {
            _compression = PakUtil.COMRESS_LZ4;
        }
        else
        if (PakUtil.compare(PakUtil._HEADER_PACK_XZ, _header))
        {
            _compression = PakUtil.COMRESS_XZ;
        }
        else
        {
            throw new IllegalArgumentException("Invalid .pak file.");
        }
        _pak.setCompression(_compression);
        _pak.setOffset(PakUtil.readIntLittleEndian(_header, 4));
        _pak.setSize(PakUtil.readIntLittleEndian(_header, 8));
        readEntries(_pak, _raf, _compression);

        _raf.close();

        return _pak;
    }

    private static void readEntries(PakFile _pak, RandomAccessFile _raf, int _cf) throws IOException
    {
        long _offset = _pak.getOffset();
        int _fileCount = (int) (_pak.getSize() / 64);

        for (int i = 0; i < _fileCount; i++) {
            PakFileEntry entry = readEntry(_raf, _offset + PakUtil._HEADER_ENTRY * i, _cf);
            _pak.addFileEntry(entry);
        }
    }

    private static PakFileEntry readEntry(RandomAccessFile _raf, long _offset, int _cf) throws IOException {
        PakFileEntry _entry = new PakFileEntry();
        byte[] _headerEntry = new byte[PakUtil._HEADER_ENTRY];
        _raf.seek(_offset);
        _raf.read(_headerEntry);

        _entry.setCompression(_cf);
        String _name = new String(_headerEntry);
        _name = _name.substring(0, _name.indexOf('\0'));
        _entry.setName(_name);
        _entry.setOffset(PakUtil.readIntLittleEndian(_headerEntry, _headerEntry.length - 8));
        _entry.setSize(PakUtil.readIntLittleEndian(_headerEntry, _headerEntry.length - 4));
        _entry.setRealSize(-1);

        return _entry;
    }
}
