package tv.danmaku.ijk.media.setting;
import android.content.Context;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

public interface Settings
{

	public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;
	
	boolean getEnableBackgroundPlay();
	
	int getPlayer();
	
	boolean getUsingMediaCodec() ;
	
    boolean getUsingMediaCodecAutoRotate() ;

    boolean getUsingOpenSLES() ;

    String getPixelFormat();

    boolean getEnableNoView() ;
	
    boolean getEnableSurfaceView() ;

    boolean getEnableTextureView() ;

    boolean getEnableDetachedSurfaceTextureView();

    String getLastDirectory() ;

    void setLastDirectory(String path);
	
	
}
