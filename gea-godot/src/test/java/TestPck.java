import com.github.terefang.gea.godot.pck.PckFile;
import com.github.terefang.gea.godot.pck.PckFileEntry;
import com.github.terefang.gea.godot.pck.PckLoader;

import java.io.File;
import java.io.IOException;

public class TestPck {

    public static void main(String[] args) throws IOException
    {
        PckLoader _PckLoader = PckLoader.instance();
        PckFile _pckFile = _PckLoader.explode(new File("/opt/Wonderdraft/Wonderdraft.pck"), new File("./target/test.pck.d"));
        for(PckFileEntry _entry : _pckFile.getFileEntries().values())
        {
            System.err.println(_entry.getName());
        }
    }
}
