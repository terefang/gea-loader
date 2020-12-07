import com.github.terefang.gea.q2.pk3.*;

import java.io.File;
import java.io.IOException;

public class TestPk3 {
    public static void main(String[] args) throws IOException {
        Pk3Loader _Pk3Loader = Pk3Loader.instance();
        File _srcdir = new File("./gea-core");

        File _target = new File("./target/test-none.pk3");
        _target.getParentFile().mkdirs();
        File _xtarget = new File(_target.getParentFile(), _target.getName()+".d");

        Pk3Writer.packFromDirectory(_srcdir, Pk3Util.COMRESS_FLATE, _target);
        _Pk3Loader.explode(_target, _xtarget);
    }
}
