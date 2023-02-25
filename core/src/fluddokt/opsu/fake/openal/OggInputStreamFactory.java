package fluddokt.opsu.fake.openal;

import fluddokt.opsu.fake.InputStreamFactory;

import java.io.IOException;

public class OggInputStreamFactory implements AudioInputStreamFactory {

	InputStreamFactory in;
	
	public OggInputStreamFactory(InputStreamFactory in) {
		this.in = in;
	}

	@Override
	public AudioInputStream2 getNewAudioInputStream() throws IOException {
		return new OggInputStream(in.getNewInputStream());
	}

}
