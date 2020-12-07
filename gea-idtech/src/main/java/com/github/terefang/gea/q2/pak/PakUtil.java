package com.github.terefang.gea.q2.pak;

import com.github.terefang.gea.GeaUtil;


public class PakUtil extends GeaUtil
{
    static final int _HEADER_SIZE = 12;
    static final int _ENTRY_OFFSET = 64;
    static final int _HEADER_ENTRY = 64; // StringZ[56], uint32, uint32
    static final byte[] _HEADER_PACK = {'P', 'A', 'C', 'K'};
    static final byte[] _HEADER_PACK_ZLIB = {'Z', 'P', 'A', 'K'};
    static final byte[] _HEADER_PACK_BZLIB = {'B', 'P', 'A', 'K'};
    static final byte[] _HEADER_PACK_LZ4 = {'4', 'P', 'A', 'K'};
    static final byte[] _HEADER_PACK_XZ = {'X', 'P', 'A', 'K'};
    static final int _NAME_SIZE = 56;
}
