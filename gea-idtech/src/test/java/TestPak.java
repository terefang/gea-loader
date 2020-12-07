import com.github.terefang.gea.q2.pak.PakLoader;
import com.github.terefang.gea.q2.pak.PakUtil;
import com.github.terefang.gea.q2.pak.PakWriter;

import java.io.File;
import java.io.IOException;

public class TestPak {
    public static void main(String[] args) throws IOException {
        PakLoader _PakLoader = PakLoader.instance();
        File _srcdir = new File("./gea-core");

        File _target = new File("./target/test-none.pak");
        _target.getParentFile().mkdirs();
        File _xtarget = new File(_target.getParentFile(), _target.getName()+".d");

        PakWriter.packFromDirectory(_srcdir, PakUtil.COMRESS_NONE, _target);
        _PakLoader.explode(_target, _xtarget);

        _target = new File("./target/test-gzlib.pak");
        _target.getParentFile().mkdirs();
        _xtarget = new File(_target.getParentFile(), _target.getName()+".d");
        PakWriter.packFromDirectory(_srcdir, PakUtil.COMRESS_FLATE, _target);
        _PakLoader.explode(_target, _xtarget);

        _target = new File("./target/test-bzlib.pak");
        _target.getParentFile().mkdirs();
        _xtarget = new File(_target.getParentFile(), _target.getName()+".d");
        PakWriter.packFromDirectory(_srcdir, PakUtil.COMRESS_BZLIB, _target);
        _PakLoader.explode(_target, _xtarget);

        _target = new File("./target/test-lz4.pak");
        _target.getParentFile().mkdirs();
        _xtarget = new File(_target.getParentFile(), _target.getName()+".d");
        PakWriter.packFromDirectory(_srcdir, PakUtil.COMRESS_LZ4, _target);
        _PakLoader.explode(_target, _xtarget);

        _target = new File("./target/test-xz.pak");
        _target.getParentFile().mkdirs();
        _xtarget = new File(_target.getParentFile(), _target.getName()+".d");
        PakWriter.packFromDirectory(_srcdir, PakUtil.COMRESS_XZ, _target);
        _PakLoader.explode(_target, _xtarget);
    }
}
