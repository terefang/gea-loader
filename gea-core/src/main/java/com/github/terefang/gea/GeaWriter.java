package com.github.terefang.gea;

import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Map;

public interface GeaWriter<T,E>
{
    void writeEntry(String _name, byte[] _content) throws IOException;

    void write(T _contents) throws IOException;

    void writeEntry(E _content) throws IOException;

    void writeEntry(String _name, File _content) throws IOException;

    void writeEntry(String _name, InputStream _content) throws IOException;

    void close() throws IOException;

    int getCompression();

    void setCompression(int compression);

    File getDestination();

    void setDestination(File destination);

}
