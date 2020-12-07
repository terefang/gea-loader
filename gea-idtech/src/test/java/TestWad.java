import com.github.terefang.gea.q2.wad.*;

import java.io.File;
import java.io.IOException;

public class TestWad
{
    public static void main(String[] args) throws IOException
    {
        WadLoader _PakLoader = WadLoader.instance();
        File _srcdir = new File("./gea-core");

        File _target = new File("./target/test-none.wad");
        _target.getParentFile().mkdirs();
        File _xtarget = new File(_target.getParentFile(), _target.getName()+".d");

        WadWriter.packFromDirectory(_srcdir, WadUtil.COMRESS_NONE, _target);
        _PakLoader.explode(_target, _xtarget);
    }
}
