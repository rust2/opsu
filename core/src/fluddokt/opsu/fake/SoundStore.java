package fluddokt.opsu.fake;

public class SoundStore {

	private static Music music;
	public static SoundStore single;

	public static SoundStore get() {
		if (single == null)
			single = new SoundStore();
		return single;
	}

	public void setMusicVolume(float musicVolume) {
		if(music != null){
			music.setMusicVolume(musicVolume);
		}
	}

	public void setMusic(Music music2) {
		SoundStore.music = music2;
	}

	public void setMusicPitch(float pitch) {
		music.setPitch(pitch);
	}

	public boolean soundWorks() {
		// FIXME: 18.02.2023 kww: this have to return true if the sound system works,
		//  otherwise (e.g. no audio playback device) false
		return true;
	}

}
