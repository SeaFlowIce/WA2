package tv.danmaku.ijk.media.interfaces;

public interface IMediaPlayerControl
{
	void start();

	void pause();

	int getDuration();

	int getCurrentPosition();

	void seekTo(int pos);

	boolean isPlaying();

	int getBufferPercentage();
	
	/**
	**/
	boolean canPause();

	boolean canSeekBackward();

	boolean canSeekForward();
}
