package com.github.terefang.gea;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeaUtil {
    public static final int COMRESS_NONE = 0;
    public static final int COMRESS_FLATE = 1;
    public static final int COMRESS_BZLIB = 2;
    public static final int COMRESS_XZ = 3;
    public static final int COMRESS_LZ4 = 4;
    public static final int COMRESS_ZSTD = 5;

    public static byte[] intToByteLittleEndian(int value) {
        return new byte[]{
                (byte) (value),
                (byte) (value >>> 8),
                (byte) (value >>> 16),
                (byte) (value >>> 24)};
    }

    public static byte[] longToByteLittleEndian(long value) {
        return new byte[]{
                (byte) (value),
                (byte) (value >>> 8),
                (byte) (value >>> 16),
                (byte) (value >>> 24),
                (byte) (value >>> 32),
                (byte) (value >>> 40),
                (byte) (value >>> 48),
                (byte) (value >>> 56)
        };
    }

    public static byte[] intToByteBigEndian(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static byte[] longToByteBigEndian(long value) {
        return new byte[]{
                (byte) (value >>> 56),
                (byte) (value >>> 48),
                (byte) (value >>> 40),
                (byte) (value >>> 32),
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static int readIntLittleEndian(byte[] array, int offset) {
        return array[3 + offset] << 24
                | (array[2 + offset] & 0xff) << 16
                | (array[1 + offset] & 0xff) << 8
                | (array[0 + offset] & 0xff);
    }

    public static int readIntLittleEndian(byte[] array) {
        return readIntLittleEndian(array, 0);
    }

    public static int readIntBigEndian(byte[] array, int offset) {
        return (array[offset] & 255) << 24 | (array[1 + offset] & 255) << 16
                | (array[2 + offset] & 255) << 8 | array[3 + offset] & 255;
    }

    public static int readIntBigEndian(byte[] array) {
        return readIntBigEndian(array, 0);
    }

    public static boolean compare(byte[] bytes, String header) {
        return compare(bytes, header.getBytes());
    }

    public static boolean compare(byte[] bytes, byte[] header) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == header[i]) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static List<File> listFile(File _base)
    {
        List<File> _files = new ArrayList();
        if(!_base.exists()) return _files;
        for (File _child : _base.listFiles())
        {
            listFile(_child, _files);
        }
        return _files;
    }

    public static void listFile(File _base, List<File> _files)
    {
        if(!_base.exists()) return;

        if (!_base.isDirectory())
        {
            _files.add(_base);
        } 
        else 
        {
            for (File _child : _base.listFiles()) 
            {
                listFile(_child, _files);
            }
        }
    }

    public static InputStream wrap(InputStream _in, int _cf) throws IOException {
        if(_cf==GeaUtil.COMRESS_FLATE)
        {
            return new DeflateCompressorInputStream(_in);
        }
        else
        if(_cf==GeaUtil.COMRESS_BZLIB)
        {
            return new BZip2CompressorInputStream(_in);
        }
        else
        if(_cf==GeaUtil.COMRESS_LZ4)
        {
            return new BlockLZ4CompressorInputStream(_in);
        }
        else
        if(_cf==GeaUtil.COMRESS_XZ)
        {
            return new XZCompressorInputStream(_in);
        }
        else
        if(_cf==GeaUtil.COMRESS_ZSTD)
        {
            return new ZstdCompressorInputStream(_in);
        }
        return _in;
    }

    public static OutputStream wrap(OutputStream _out, int _cf) throws IOException {
        if(_cf==GeaUtil.COMRESS_FLATE)
        {
            return new DeflateCompressorOutputStream(_out);
        }
        else
        if(_cf==GeaUtil.COMRESS_BZLIB)
        {
            return new BZip2CompressorOutputStream(_out);
        }
        else
        if(_cf==GeaUtil.COMRESS_LZ4)
        {
            return new BlockLZ4CompressorOutputStream(_out);
        }
        else
        if(_cf==GeaUtil.COMRESS_XZ)
        {
            return new XZCompressorOutputStream(_out);
        }
        else
        if(_cf==GeaUtil.COMRESS_ZSTD)
        {
            return new ZstdCompressorOutputStream(_out);
        }
        return _out;
    }

}
