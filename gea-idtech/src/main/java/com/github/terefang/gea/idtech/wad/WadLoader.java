package com.github.terefang.gea.idtech.wad;

import com.github.terefang.gea.GeaLoader;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WadLoader implements GeaLoader<WadFile>
{
    public static WadLoader instance() { return new WadLoader(); }

    public WadFile explode(File _path, File _dest) throws IOException
    {
        WadFile _pak = load(_path);
        for(WadFileEntry _entry : _pak.getFileEntries().values())
        {
            File _fn = new File(_dest, _entry.getName());
            _fn.getParentFile().mkdirs();
            _entry.setLocalFile(_fn);
            FileOutputStream _fh = new FileOutputStream(_fn);
            IOUtils.copy(_pak.getFileStream(_entry), _fh, 8192);
        }
        return _pak;
    }

    public WadFile load(File _path) throws IOException {

        WadFile _Wad = new WadFile();
        _Wad.setFilepath(_path.getAbsolutePath());

        RandomAccessFile _raf = new RandomAccessFile(_path, "r");

        int _compression = 0;
        byte[] _header = new byte[WadUtil._HEADER_SIZE];
        _raf.read(_header);

        if (WadUtil.compare(WadUtil._HEADER_TYPE_PWAD, _header))
        {
            _compression = 'P';
        }
        else
        if (WadUtil.compare(WadUtil._HEADER_TYPE_IWAD, _header))
        {
            _compression = 'I';
        }
        else
        {
            throw new IllegalArgumentException("Invalid .wad file.");
        }
        _Wad.setSize(WadUtil.readIntLittleEndian(_header, 4));
        _Wad.setOffset(WadUtil.readIntLittleEndian(_header, 8));
        readEntries(_Wad, _raf, _compression);

        _raf.close();

        return _Wad;
    }

    private static void readEntries(WadFile _Wad, RandomAccessFile _raf, int _cf) throws IOException
    {
        long _offset = _Wad.getOffset();
        int _fileCount = (int) _Wad.getSize();

        for (int i = 0; i < _fileCount; i++) {
            WadFileEntry entry = readEntry(_raf, _offset + WadUtil._HEADER_ENTRY * i, _cf);
            _Wad.addFileEntry(entry);
        }
    }

    private static WadFileEntry readEntry(RandomAccessFile _raf, long _offset, int _cf) throws IOException {
        WadFileEntry _entry = new WadFileEntry();
        byte[] _headerEntry = new byte[WadUtil._HEADER_ENTRY];
        _raf.seek(_offset);
        _raf.read(_headerEntry);

        _entry.setCompression(_cf);
        _entry.setOffset(WadUtil.readIntLittleEndian(_headerEntry, 0));
        _entry.setSize(WadUtil.readIntLittleEndian(_headerEntry, 4));
        _entry.setRealSize(-1);

        StringBuilder _sb = new StringBuilder();
        for(int _i=8; _i<16; _i++)
        {
            if(_headerEntry[_i]==0) break;
            _sb.append((char)_headerEntry[_i]);
        }
        _entry.setName(_sb.toString());

        return _entry;
    }
}
