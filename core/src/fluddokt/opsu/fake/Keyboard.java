package fluddokt.opsu.fake;

public class Keyboard extends Input{

	public static final int KEY_NONE = 0;

	public static boolean isRepeatEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	public static String getKeyName(int keycode) {
		return keycode>0?com.badlogic.gdx.Input.Keys.toString(keycode):"Null";
	}

	public static int getKeyIndex(String value) {
		return com.badlogic.gdx.Input.Keys.valueOf(value);
	}

}
