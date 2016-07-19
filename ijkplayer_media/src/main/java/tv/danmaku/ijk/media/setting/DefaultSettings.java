package tv.danmaku.ijk.media.setting;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import tv.danmaku.ijk.media.R;

public class DefaultSettings implements Settings{
    private Context mAppContext;
    private SharedPreferences mSharedPreferences;

    public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;



    //是否允许后台播放
	@Override
    public boolean getEnableBackgroundPlay() {
        return true;
    }

	@Override
    public int getPlayer() {
        String key = mAppContext.getString(R.string.pref_key_player);
        String value = mSharedPreferences.getString(key, "");
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

	@Override
    public boolean getUsingMediaCodec() {
        return false;
    }

	@Override
    public boolean getUsingMediaCodecAutoRotate() {
        return false;
    }

	@Override
    public boolean getUsingOpenSLES() {
        return false;
    }

	@Override
    public String getPixelFormat() {
        return "";
    }

	@Override
    public boolean getEnableNoView() {
        return false;
    }

	@Override
    public boolean getEnableSurfaceView() {
        return false;
    }

	@Override
    public boolean getEnableTextureView() {
        return false;
    }

	@Override
    public boolean getEnableDetachedSurfaceTextureView() {
        return false;
    }

	@Override
    public String getLastDirectory() {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        return mSharedPreferences.getString(key, "/");
    }

	@Override
    public void setLastDirectory(String path) {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        mSharedPreferences.edit().putString(key, path).apply();
    }
}
