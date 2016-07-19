package android.galgame.white_album_2.wa2;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.widget.player.HuPlayer;

public class VideoActivity extends AppCompatActivity implements HuPlayer.OnHuplayerListener {
    private HuPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video);
        player = new HuPlayer(this);
        player.setOnHuplayerListener(this);
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run()
            {
                player.setOrientationLock(true);
                //player.setCover();

                player.setPath("http://o9dupqosi.bkt.clouddn.com/wa2_op.mp4");
                player.setTitle("OP");
                player.createComplete();
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPlayerCreate() {

    }

    @Override
    public void onPrepared(IMediaPlayer mp) {

    }

    @Override
    public void onError(IMediaPlayer mp, int what, int extra) {

    }

    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                //do something when buffering start!
//                Toast.makeText(this,"缓冲开始",Toast.LENGTH_SHORT).show();
                findViewById(R.id.video_text).setVisibility(View.VISIBLE);
                ( (TextView) findViewById(R.id.video_text)).setText("缓冲开始");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                //do something when buffering end
                ( (TextView) findViewById(R.id.video_text)).append("\n缓冲结束");
                postDelayed();
                break;
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                //download speed
                //((TextView) findViewById(R.id.tv_speed)).setText(Formatter.formatFileSize(getApplicationContext(),extra)+"/s");
                ( (TextView) findViewById(R.id.video_text)).append("\n下载速度："+extra);
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                //do something when video rendering
                // findViewById(R.id.tv_speed).setVisibility(View.GONE);
                ( (TextView) findViewById(R.id.video_text)).append("\n开始播放");
                postDelayed();
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                break;
        }
    }

    private void postDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.video_text).setVisibility(View.GONE);
            }
        }, 1000);
    }

    @Override
    public void onChange(boolean isShowing) {

    }

    @Override
    public void onComplete(IMediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }
}
