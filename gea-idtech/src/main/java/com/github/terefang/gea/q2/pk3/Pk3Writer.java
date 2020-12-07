package com.github.terefang.gea.q2.pk3;

import com.github.terefang.gea.GeaWriter;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

public class Pk3Writer implements GeaWriter<Pk3File, Pk3FileEntry>
{
    public static Pk3Writer instance() { return new Pk3Writer(); }
    public static void packFromDirectory(File _base, int _cf, File _dest) throws IOException
    {
        List<File> _files = Pk3Util.listFile(_base);
        packFromDirectory(_base, _files, _cf, _dest);
    }

    public static void packFromDirectory(File _base, List<File> _files, int _cf, File _dest) throws IOException
    {
        int _noffset = _base.getAbsolutePath().length();

        Pk3Writer _pw = Pk3Writer.create(_cf, _dest);
        for (File _file : _files)
        {
            String _name = _file.getAbsolutePath().substring(_noffset);
            _pw.writeEntry(_name, _file);
        }
        _pw.close();
    }

    public static void packFrom(Pk3File _pak) throws IOException
    {
        Pk3Writer _pw = create(_pak.getCompression(), _pak.getFilepath());
        _pw.write(_pak);
        _pw.close();
    }

    public static Pk3Writer create(int _cf, String _dest) throws IOException
    {
        return create(_cf, new File(_dest));
    }

    public static Pk3Writer create(int _cf, File _dest) throws IOException {
        _dest.delete();
        Pk3Writer _pw = new Pk3Writer();
        _pw.setCompression(_cf);
        _pw.setDestination(_dest);
        return _pw;
    }

    ZipArchiveOutputStream _out;

    public void writeEntry(String _name, byte[] _content) throws IOException
    {
        if(_name.startsWith("/")) _name = _name.substring(1);

        if(_out==null)
        {
            _out = new ZipArchiveOutputStream(this.getDestination());
            _out.setLevel(9);
            switch(this.getCompression())
            {
                case Pk3Util.COMRESS_NONE:
                    _out.setMethod(ZipMethod.STORED.getCode()); break;
                case Pk3Util.COMRESS_XZ:
                    _out.setMethod(ZipMethod.XZ.getCode()); break;
                case Pk3Util.COMRESS_BZLIB:
                    _out.setMethod(ZipMethod.BZIP2.getCode()); break;
                case Pk3Util.COMRESS_FLATE:
                default:
                    _out.setMethod(ZipMethod.DEFLATED.getCode()); break;
            }
        }
        ZipArchiveEntry _zentry = new ZipArchiveEntry(_name);
        switch(this.getCompression())
        {
            case Pk3Util.COMRESS_NONE:
                _zentry.setMethod(ZipMethod.STORED.getCode()); break;
            case Pk3Util.COMRESS_XZ:
                _zentry.setMethod(ZipMethod.XZ.getCode()); break;
            case Pk3Util.COMRESS_BZLIB:
                _zentry.setMethod(ZipMethod.BZIP2.getCode()); break;
            case Pk3Util.COMRESS_FLATE:
            default:
                _zentry.setMethod(ZipMethod.DEFLATED.getCode()); break;
        }
        _out.putArchiveEntry(_zentry);
        _out.write(_content);
        _out.closeArchiveEntry();
    }

    public void write(Pk3File _content) throws IOException
    {
        for(Map.Entry<String, Pk3FileEntry> _entry : _content.getFileEntries().entrySet())
            this.writeEntry(_entry.getValue());
    }

    public void writeEntry(Pk3FileEntry _entry) throws IOException
    {
        this.writeEntry(_entry.getName(), _entry.getLocalFile());
    }

    public void writeEntry(String _name, File _content) throws IOException
    {
        this.writeEntry(_name, new FileInputStream(_content));
    }

    public void writeEntry(String _name, InputStream _content) throws IOException
    {
        this.writeEntry(_name, IOUtils.toByteArray(_content));
    }

    public void close() throws IOException
    {
        if(_out!=null)
        {
            _out.finish();
            _out.close();
        }
    }

    File destination;
    int compression;

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public File getDestination() {
        return destination;
    }

    public void setDestination(File destination) {
        this.destination = destination;
    }

}
