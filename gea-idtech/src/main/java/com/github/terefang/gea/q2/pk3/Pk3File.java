package com.github.terefang.gea.q2.pk3;

import com.github.terefang.gea.GeaFile;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.*;

public class Pk3File extends GeaFile<Pk3FileEntry>
{
    int compression;

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    ZipFile zipFile;

    public ZipFile getZipFile() {
        return zipFile;
    }

    public void setZipFile(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    public InputStream getFileStream(Pk3FileEntry _entry) throws IOException
    {
        return this.zipFile.getInputStream(_entry._zipEntry);
    }

    @Override
    public void close() throws IOException
    {
        zipFile.close();
    }
}
