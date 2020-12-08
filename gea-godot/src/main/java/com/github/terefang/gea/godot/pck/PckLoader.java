package com.github.terefang.gea.godot.pck;

import com.github.terefang.gea.GeaLoader;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PckLoader implements GeaLoader<PckFile>
{
    public static PckLoader instance() { return new PckLoader(); }

    public PckFile explode(File _path, File _dest) throws IOException
    {
        PckFile _Pck = this.load(_path);
        for(PckFileEntry _entry : _Pck.getFileEntries().values())
        {
            File _fn = new File(_dest, _entry.getName());
            _fn.getParentFile().mkdirs();
            _entry.setLocalFile(_fn);
            FileOutputStream _fh = new FileOutputStream(_fn);
            if(_entry.getSize()>0)
            {
                IOUtils.copy(_Pck.getFileStream(_entry), _fh, 8192);
            }
            else
            {
                IOUtils.closeQuietly(_fh);
            }
        }
        return _Pck;
    }

    public PckFile load(File _path) throws IOException
    {
        PckFile _Pck = new PckFile();
        _Pck.setFilepath(_path.getAbsolutePath());
        _Pck.setOffset(0);

        RandomAccessFile _raf = new RandomAccessFile(_path, "r");

        byte[] _header = new byte[PckUtil._HEADER_SIZE_V1];
        _raf.read(_header);

        if (!PckUtil.compare(PckUtil._HEADER_MAGIC, _header))
        {
            throw new IllegalArgumentException("Invalid .pck file.");
        }

        _Pck.setFormat(PckUtil.readIntLittleEndian(_header, 4));
        // we know format 1 (godot 3.x) and 2 (4.x)
        // v1 = I I I I I I16 I"
        if(_Pck.getFormat()==2)
        {
            _raf.seek(0L);
            _header = new byte[PckUtil._HEADER_SIZE_V2];
            _raf.read(_header);
        }
        _Pck.setMajor(PckUtil.readIntLittleEndian(_header, 8));
        _Pck.setMinor(PckUtil.readIntLittleEndian(_header, 12));
        _Pck.setPatch(PckUtil.readIntLittleEndian(_header, 16));
        // v2 = I I I I I I L I16 I"
        if(_Pck.getFormat()==2)
        {
            _Pck.setFlags(PckUtil.readIntLittleEndian(_header, 20));
            _Pck.setOffset(PckUtil.readLongLittleEndian(_header, 24));
        }

        int _count = PckUtil.readIntLittleEndian(_header, _header.length-4);

        if (_Pck.getFlags()!=0)
        {
            throw new IllegalArgumentException("encrypted .pck file.");
        }

        readEntries(_Pck, _raf, _count);

        _raf.close();

        return _Pck;
    }

    private static void readEntries(PckFile _Pck, RandomAccessFile _raf, int _fileCount) throws IOException
    {
        long _offset = PckUtil._HEADER_SIZE_V1;
        if(_Pck.getFormat()==2)
        {
            _offset = PckUtil._HEADER_SIZE_V2;
        }
        _raf.seek(_offset);

        for (int i = 0; i < _fileCount; i++) {
            PckFileEntry _entry = readEntry(_Pck, _raf);
            if(_entry.getOffset()!=0L)
                _Pck.addFileEntry(_entry);
        }
    }

    private static PckFileEntry readEntry(PckFile _Pck, RandomAccessFile _raf) throws IOException
    {
        PckFileEntry _entry = new PckFileEntry();
        _entry.setCompression(0);
        _entry.setRealSize(-1L);

        int _slen = PckUtil.intSwap(_raf.readInt());
        byte[] _buf = new byte[_slen];
        _raf.read(_buf);
        String _fname = new String(_buf);
        if(_fname.indexOf(0)>0) _fname = _fname.substring(0, _fname.indexOf(0));
        if(_fname.startsWith("res://")) _fname = _fname.substring(6);
        _entry.setName(_fname);

        _entry.setOffset(PckUtil.longSwap(_raf.readLong()));
        _entry.setSize(PckUtil.longSwap(_raf.readLong()));

        _buf = new byte[16];
        _raf.read(_buf);
        _entry.setMd5sum(_buf);

        if(_Pck.getFormat()==2)
        {
            _entry.setFlags(PckUtil.intSwap(_raf.readInt()));
            if(_entry.getOffset()>0L)
                _entry.setOffset(_entry.getOffset()+_Pck.getOffset());
        }

        return _entry;
    }
}
