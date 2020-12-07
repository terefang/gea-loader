package com.github.terefang.gea.q2.pak;

import com.github.terefang.gea.GeaWriter;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

public class PakWriter implements GeaWriter<PakFile, PakFileEntry>
{
    public static PakWriter instance() { return new PakWriter(); }
    public static void packFromDirectory(File _base, int _cf, File _dest) throws IOException
    {
        List<File> _files = PakUtil.listFile(_base);
        packFromDirectory(_base, _files, _cf, _dest);
    }

    public static void packFromDirectory(File _base, List<File> _files, int _cf, File _dest) throws IOException
    {
        int _noffset = _base.getAbsolutePath().length();

        PakWriter _pw = PakWriter.create(_cf, _dest);

        for (File _file : _files)
        {
            String _name = _file.getAbsolutePath().substring(_noffset);
            _pw.writeEntry(_name, _file);
        }
        _pw.close();
    }

    public static void packFrom(PakFile _pak) throws IOException
    {
        PakWriter _pw = create(_pak.getCompression(), _pak.getFilepath());
        _pw.write(_pak);
        _pw.close();
    }

    public static PakWriter create(int _cf, String _dest) throws IOException
    {
        return create(_cf, new File(_dest));
    }

    public static PakWriter create(int _cf, File _dest) throws IOException {
        _dest.delete();
        PakWriter _pw = new PakWriter();
        _pw.setCompression(_cf);
        _pw.setDestinationFile(new RandomAccessFile(_dest, "rw"));
        _pw.setOffset(PakUtil._ENTRY_OFFSET);
        _pw.setFileCount(0);
        _pw.setFileEntriesBuffer(new ByteArrayOutputStream());
        return _pw;
    }

    public void writeEntry(String _name, byte[] _content) throws IOException
    {
        this.getDestinationFile().seek(this.getOffset());

        if(this.getCompression()!=PakUtil.COMRESS_NONE && _content.length>0)
        {
            ByteArrayOutputStream _baos = new ByteArrayOutputStream();
            OutputStream _os = PakUtil.wrap(_baos, this.getCompression());
            _os.write(_content);
            _os.flush();
            _os.close();
            _content = _baos.toByteArray();
        }
        this.getDestinationFile().write(_content);

        this.writeEntryTable(_name, (int) this.getOffset(), _content.length);

        this.addOffset(_content.length);
        this.addOffset((32-(this.getOffset()%16)));
        this.setFileCount(this.getFileCount()+1);
    }

    public void write(PakFile _content) throws IOException
    {
        for(Map.Entry<String, PakFileEntry> _entry : _content.getFileEntries().entrySet())
            this.writeEntry(_entry.getValue());
    }

    public void writeEntry(PakFileEntry _entry) throws IOException
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

    public void writeEntryTable(String _name, int _offset, int _size) throws IOException
    {
        this.getFileEntriesBuffer().write(nameEntryArray(_name));
        this.getFileEntriesBuffer().write(PakUtil.intToByteLittleEndian((int) _offset));
        this.getFileEntriesBuffer().write(PakUtil.intToByteLittleEndian((int) _size));
    }

    public void close() throws IOException
    {
        this.getDestinationFile().seek(this.getOffset());
        this.getDestinationFile().write(this.getFileEntriesBuffer().toByteArray());

        this.getDestinationFile().seek(0L);
        switch (this.getCompression())
        {
            case PakUtil.COMRESS_LZ4:
                this.getDestinationFile().write(PakUtil._HEADER_PACK_LZ4);
                break;
            case PakUtil.COMRESS_XZ:
                this.getDestinationFile().write(PakUtil._HEADER_PACK_XZ);
                break;
            case PakUtil.COMRESS_BZLIB:
                this.getDestinationFile().write(PakUtil._HEADER_PACK_BZLIB);
                break;
            case PakUtil.COMRESS_FLATE:
                this.getDestinationFile().write(PakUtil._HEADER_PACK_ZLIB);
                break;
            case PakUtil.COMRESS_NONE:
            default:
                this.getDestinationFile().write(PakUtil._HEADER_PACK);
                break;
        }
        this.getDestinationFile().write(PakUtil.intToByteLittleEndian((int) this.getOffset()));
        this.getDestinationFile().write(PakUtil.intToByteLittleEndian(this.getFileCount() * PakUtil._HEADER_ENTRY));
        this.getDestinationFile().write(PakUtil.intToByteLittleEndian((int) (System.currentTimeMillis()/1000)));
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
        if(_fileName.startsWith("/")) _fileName = _fileName.substring(1);

        byte[] _entry = new byte[PakUtil._NAME_SIZE];

        if(_fileName.length()>55) _fileName = _fileName.substring(_fileName.length()-55);

        for (int _i = 0; _i < _fileName.length(); _i++) {
            _entry[_i] = (byte) _fileName.charAt(_i);
        }
        return _entry;
    }
}
