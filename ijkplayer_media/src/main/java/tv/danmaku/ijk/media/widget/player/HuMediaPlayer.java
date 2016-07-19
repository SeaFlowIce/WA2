package tv.danmaku.ijk.media.widget.player;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class HuMediaPlayer implements IMediaPlayer.OnPreparedListener ,IMediaPlayer.OnCompletionListener ,IMediaPlayer.OnBufferingUpdateListener,IMediaPlayer.OnSeekCompleteListener ,
                                      IMediaPlayer. OnVideoSizeChangedListener ,IMediaPlayer.OnErrorListener ,IMediaPlayer.OnInfoListener 
{

    public IMediaPlayer mediaPlayer;
    
    
    @Override
    public void onPrepared(IMediaPlayer mp)
    {
        // TODO: Implement this method
    }

    @Override
    public void onCompletion(IMediaPlayer mp)
    {
        // TODO: Implement this method
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent)
    {
        // TODO: Implement this method
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp)
    {
        // TODO: Implement this method
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den)
    {
        // TODO: Implement this method
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra)
    {
        // TODO: Implement this method
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra)
    {
        // TODO: Implement this method
        return false;
    }

}
