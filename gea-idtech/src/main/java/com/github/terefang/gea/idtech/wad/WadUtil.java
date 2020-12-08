package com.github.terefang.gea.idtech.wad;

import com.github.terefang.gea.GeaUtil;


public class WadUtil extends GeaUtil
{
    static final int _HEADER_SIZE = 12;
    static final int _ENTRY_OFFSET = 64;
    static final int _HEADER_ENTRY = 16; // uint32, uint32, StringZ[8]
    static final byte[] _HEADER_TYPE_PWAD = {'P', 'W', 'A', 'D'};
    static final byte[] _HEADER_TYPE_IWAD = {'I', 'W', 'A', 'D'};
    static final int _NAME_SIZE = 8;
}
