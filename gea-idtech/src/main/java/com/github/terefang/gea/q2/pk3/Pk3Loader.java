package com.github.terefang.gea.q2.pk3;

import com.github.terefang.gea.GeaLoader;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

public class Pk3Loader implements GeaLoader<Pk3File>
{
    public static Pk3Loader instance() { return new Pk3Loader(); }

    public Pk3File explode(File _path, File _dest) throws IOException
    {
        Pk3File _pak = this.load(_path);
        for(Pk3FileEntry _entry : _pak.getFileEntries().values())
        {
            File _fn = new File(_dest, _entry.getName());
            _fn.getParentFile().mkdirs();
            _entry.setLocalFile(_fn);
            FileOutputStream _fh = new FileOutputStream(_fn);
            IOUtils.copy(_pak.getFileStream(_entry), _fh, 8192);
        }
        return _pak;
    }

    public Pk3File load(File _path) throws IOException
    {
        Pk3File _pak = new Pk3File();
        _pak.setFilepath(_path.getAbsolutePath());
        _pak.setZipFile(new ZipFile(_path));
        //_pak.setCompression(_compression);
        _pak.setOffset(-1);
        _pak.setSize(-1);

        readEntries(_pak);

        return _pak;
    }

    private static void readEntries(Pk3File _pak) throws IOException
    {
        Enumeration<ZipArchiveEntry> _zentries = _pak.getZipFile().getEntries();
        while(_zentries.hasMoreElements())
        {
            ZipArchiveEntry _zentry = _zentries.nextElement();
            Pk3FileEntry _entry = new Pk3FileEntry();
            _entry._zipEntry = _zentry;
            _entry.setName(_zentry.getName());
            _entry.setRealSize((int) _zentry.getSize());
            _pak.addFileEntry(_entry);
        }
    }

}
