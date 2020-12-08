package com.github.terefang.gea.idtech.wad;

import com.github.terefang.gea.GeaWriter;

import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

public class WadWriter implements GeaWriter<WadFile, WadFileEntry>
{
    public static WadWriter instance() { return new WadWriter(); }
    public static void packFromDirectory(File _base, int _cf, File _dest) throws IOException
    {
        List<File> _files = WadUtil.listFile(_base);
        packFromDirectory(_base, _files, _cf, _dest);
    }

    public static void packFromDirectory(File _base, List<File> _files, int _cf, File _dest) throws IOException
    {
        int _noffset = _base.getAbsolutePath().length();

        WadWriter _pw = WadWriter.create(_cf, _dest);

        for (File _file : _files)
        {
            String _name = _file.getAbsolutePath().substring(_noffset);
            _pw.writeEntry(_name, _file);
        }
        _pw.close();
    }

    public static void packFrom(WadFile _pak) throws IOException
    {
        WadWriter _pw = create(0, _pak.getFilepath());
        _pw.write(_pak);
        _pw.close();
    }

    public static WadWriter create(int _cf, String _dest) throws IOException
    {
        return create(_cf, new File(_dest));
    }

    public static WadWriter create(int _cf, File _dest) throws IOException {
        _dest.delete();
        WadWriter _pw = new WadWriter();
        _pw.setCompression(_cf);
        _pw.setDestinationFile(new RandomAccessFile(_dest, "rw"));
        _pw.setOffset(WadUtil._ENTRY_OFFSET);
        _pw.setFileCount(0);
        _pw.setFileEntriesBuffer(new ByteArrayOutputStream());
        return _pw;
    }

    public void writeEntry(String _name, byte[] _content) throws IOException
    {
        this.getDestinationFile().seek(this.getOffset());

        this.getDestinationFile().write(_content);

        this.writeEntry(_name, (int) this.getOffset(), _content.length);

        this.addOffset(_content.length);
        this.addOffset((32-(this.getOffset()%16)));
        this.setFileCount(this.getFileCount()+1);
    }

    public void write(WadFile _content) throws IOException
    {
        for(Map.Entry<String, WadFileEntry> _entry : _content.getFileEntries().entrySet())
            this.writeEntry(_entry.getValue());
    }

    public void writeEntry(WadFileEntry _entry) throws IOException
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

    public void writeEntry(String _name, int _offset, int _size) throws IOException
    {
        this.getFileEntriesBuffer().write(WadUtil.intToByteLittleEndian((int) _offset));
        this.getFileEntriesBuffer().write(WadUtil.intToByteLittleEndian((int) _size));
        this.getFileEntriesBuffer().write(nameEntryArray(_name));
    }

    public void close() throws IOException
    {
        this.getDestinationFile().seek(this.getOffset());
        this.getDestinationFile().write(this.getFileEntriesBuffer().toByteArray());

        this.getDestinationFile().seek(0L);
        switch (this.getCompression())
        {
            case 'I':
                this.getDestinationFile().write(WadUtil._HEADER_TYPE_IWAD);
                break;
            case 'P':
            default:
                this.getDestinationFile().write(WadUtil._HEADER_TYPE_PWAD);
                break;
        }
        this.getDestinationFile().write(WadUtil.intToByteLittleEndian(this.getFileCount()));
        this.getDestinationFile().write(WadUtil.intToByteLittleEndian((int) this.getOffset()));
        this.getDestinationFile().write(WadUtil.intToByteLittleEndian((int) (System.currentTimeMillis()/1000)));
        this.getDestinationFile().close();
    }

    public void addOffset(long l)
    {
        this.offset += l;
    }

    ByteArrayOutputStream fileEntriesBuffer;
    int fileCount;
    long offset;
    File destination;
    int compression;
    RandomAccessFile destinationFile;

    public ByteArrayOutputStream getFileEntriesBuffer() {
        return fileEntriesBuffer;
    }

    public void setFileEntriesBuffer(ByteArrayOutputStream fileEntriesBuffer) {
        this.fileEntriesBuffer = fileEntriesBuffer;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public RandomAccessFile getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(RandomAccessFile destinationFile) {
        this.destinationFile = destinationFile;
    }

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

    static byte[] nameEntryArray(String _fileName)
    {
        _fileName = _fileName.toUpperCase();

        if(_fileName.indexOf('/')>=0) _fileName = _fileName.substring(_fileName.lastIndexOf('/')+1);
        if(_fileName.indexOf('.')>=0) _fileName = _fileName.substring(0, _fileName.indexOf('.'));

        byte[] _entry = new byte[WadUtil._NAME_SIZE];

        if(_fileName.length()>WadUtil._NAME_SIZE) _fileName = _fileName.substring(0, WadUtil._NAME_SIZE);

        for (int _i = 0; _i < _fileName.length(); _i++) {
            _entry[_i] = (byte) _fileName.charAt(_i);
        }
        return _entry;
    }
}
