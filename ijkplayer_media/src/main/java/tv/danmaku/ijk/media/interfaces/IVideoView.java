package tv.danmaku.ijk.media.interfaces;
import tv.danmaku.ijk.media.interfaces.IRenderView;
import android.widget.TableLayout;
import android.net.Uri;
import tv.danmaku.ijk.media.interfaces.IMediaController;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public interface IVideoView
{
	
	/**
	 * 设置渲染视图
	 * @param 
	 *        renderView
	 */
	void setRenderView(IRenderView renderView);
	
	void setRender(int render);
	
	void setHudView(TableLayout tableLayout) ;
	
	/**
	 * 设置视频的路径
	 * @param
	 *        path
	 */
	void setVideoPath(String path) ;
	
	/**
	 * 设置视频的路径
	 * @param
	 *        uri
	 */
	void setVideoURI(Uri uri);
	
	void stopPlayback();
	
	void setMediaController(IMediaController controller);

	void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) ;

	void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) ;
	
	void setOnErrorListener(IMediaPlayer.OnErrorListener l) ;

	void setOnInfoListener(IMediaPlayer.OnInfoListener l);

	void releaseWithoutStop();

	void release(boolean cleartargetstate);

	void suspend();

	void resume();

	IMediaPlayer createPlayer(int playerType);

	boolean isBackgroundPlayEnabled() ;

	void enterBackground();

	void stopBackgroundPlay();

	void showMediaInfo() ;

	ITrackInfo[] getTrackInfo();

	void selectTrack(int stream) ;

	void deselectTrack(int stream);

	int getSelectedTrack(int trackType) ;


}
