package com.github.terefang.gea.godot.pck;

import com.github.terefang.gea.GeaUtil;

public class PckUtil extends GeaUtil
{
    static final int _HEADER_SIZE_V1 = (20+64+4);
    static final int _HEADER_SIZE_V2 = (20+64+4+12);
    static final byte[] _HEADER_MAGIC = {'G', 'D', 'P', 'C'};
    // ENTRY = uint32(len), StringZ[len], uint64(offset), uint64(size), byte[16](md5sum)
    // len is 4-byte align!
}
