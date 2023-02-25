package fluddokt.opsu.fake;

import com.badlogic.gdx.files.FileHandle;

import java.io.InputStream;

public class FileHandleInputStreamFactory implements InputStreamFactory {

	private FileHandle file;

	public FileHandleInputStreamFactory(FileHandle file) {
		this.file = file;
	}

	@Override
	public InputStream getNewInputStream() {
		return file.read(0x4000);
	}

}
