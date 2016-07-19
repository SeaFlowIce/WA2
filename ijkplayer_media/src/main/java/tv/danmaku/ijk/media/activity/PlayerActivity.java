package tv.danmaku.ijk.media.activity;
import android.app.Activity;
import android.os.Bundle;
import tv.danmaku.ijk.media.R;
import tv.danmaku.ijk.media.widget.IjkVideoView;
import android.widget.MediaController;
import tv.danmaku.ijk.media.widget.AndroidMediaController;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import tv.danmaku.ijk.media.widget.controller.MMediaController;

public class PlayerActivity extends Activity
{

	private IjkVideoView mVideoView;
	//private IjkMediaController mMediaController;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_player);
        
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        
		mVideoView=(IjkVideoView)findViewById(R.id.video_view);
		//mMediaController=(IjkMediaController)findViewById(R.id.media_controller);
		player();
	}
	
	void player(){
		mVideoView.setVideoPath("/sdcard/test.mp4");
		//TestMediaController mc=new TestMediaController(this);
		AndroidMediaController mc=new AndroidMediaController(this,false);
        mVideoView.setMediaController(mc);
		mVideoView.start();
	}

    @Override
    protected void onPause()
    {
        if(mVideoView!=null){
            mVideoView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        if(mVideoView!=null){
            mVideoView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }*/
        IjkMediaPlayer.native_profileEnd();
    }
    
	
    
    
	
}
