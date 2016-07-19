package tv.danmaku.ijk.media.widget.controller;
import android.widget.MediaController;
import tv.danmaku.ijk.media.interfaces.IMediaController;
import android.view.View;
import tv.danmaku.ijk.media.interfaces.IMediaPlayerControl;
import android.content.Context;

public class MMediaController  extends MediaController implements IMediaController
{

    @Override
    public void showOnce(View view)
    {
        // TODO: Implement this method
    }
    

   


    MediaPlayerControl mplayer;
    
    public MMediaController(Context context){
        super(context);
    }
    
    @Override
    public void hide()
    {
        super.hide();
    }

    @Override
    public boolean isShowing()
    {
        return super.isShowing();
    }

    @Override
    public void setAnchorView(View view)
    {
        // TODO: Implement this method
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        // TODO: Implement this method
    }

    @Override
    public void show(int timeout)
    {
        super.show(timeout);
    }

    @Override
    public void show()
    {
        super.show();
    }
    

   

    public void setMediaPlayer(IMediaPlayerControl player)
    {
        // TODO: Implement this method
	}
    
}
