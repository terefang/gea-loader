package com.github.terefang.gea;

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.IOException;

public interface GeaLoader<T>
{
    T explode(File _path, File _dest) throws IOException;

    T load(File _path) throws IOException;
}
