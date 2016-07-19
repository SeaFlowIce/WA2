package tv.danmaku.ijk.media.widget.player;
import android.app.Activity;
import tv.danmaku.ijk.media.widget.IjkVideoView;
import android.widget.SeekBar;
import android.media.AudioManager;
import android.view.View;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.util.DisplayMetrics;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import tv.danmaku.ijk.media.interfaces.IRenderView;
import android.view.WindowManager;
import android.util.Log;
import android.view.Surface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.content.res.Configuration;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.TextView;
import tv.danmaku.ijk.media.R;
import tv.danmaku.ijk.media.activity.HuPlayerActivity;
import android.transition.Visibility;


/**
 * Created by tcking on 15/10/27.
 */
public class HuPlayer implements IMediaPlayer.OnCompletionListener,IMediaPlayer.OnPreparedListener,IMediaPlayer.OnVideoSizeChangedListener,
IMediaPlayer.OnBufferingUpdateListener,IMediaPlayer.OnErrorListener,IMediaPlayer.OnInfoListener,IMediaPlayer.OnSeekCompleteListener
{

  
    /**
     * 可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
     */
    public static final String SCALETYPE_FITPARENT="fitParent";
    /**
     * 可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
     */
    public static final String SCALETYPE_FILLPARENT="fillParent";
    /**
     * 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
     */
    public static final String SCALETYPE_WRAPCONTENT="wrapContent";
    /**
     * 不剪裁,非等比例拉伸画面填满整个View
     */
    public static final String SCALETYPE_FITXY="fitXY";
    /**
     * 不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
     */
    public static final String SCALETYPE_16_9="16:9";
    /**
     * 不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
     */
    public static final String SCALETYPE_4_3="4:3";
    /**
     * handle的5种不同情况
     */
    private static final int MESSAGE_SHOW_PROGRESS = 1;//显示过程
    private static final int MESSAGE_FADE_OUT = 2;//超时淡出
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;//跳转
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;//隐藏中间图标
    private static final int MESSAGE_RESTART_PLAY = 5;//重播
    /**
     * 播放器的不同状态
     */
    private int STATUS_ERROR=-1;
    private int STATUS_IDLE=0;
    private int STATUS_PREPARED=1;
    private int STATUS_LOADING=2;
    private int STATUS_PLAYING=3;
    private int STATUS_PAUSE=4;
    private int STATUS_COMPLETED=5;
    /**
     * 播放器当前的状态
     */
    private int status=STATUS_IDLE;//当前状态
    
    private final Activity activity;
    private IjkVideoView videoView;//
    private SeekBar seekBar;//
    private final AudioManager audioManager;
    private final int mMaxVolume;
    private boolean playerSupport;//是否支持播放
    private String url;//视频url
    private BindView v;
    private long pauseTime;//暂停时间
    private boolean isLive = false;//是否为直播
    private OrientationEventListener orientationEventListener;
    final private int initHeight;//竖屏高度
    private int defaultTimeout=5000;//默认超时时间
    private int screenWidthPixels;//屏幕宽度像素

    private View liveBox;
    
    private boolean isShowing;//控制器是否显示
    private boolean portrait;//是否是竖屏
    private float brightness=-1;//亮度
    private int volume=-1;//声音
    private long newPosition = -1;
    private long defaultRetryTime=5000;//默认重试时间

    private int currentPosition;//当前播放进度
    private boolean fullScreenOnly;//是否只允许全屏播放
    
    private long duration;//视频时长
    private boolean instantSeeking;//是否立即跳转
    private boolean isDragging;//是否可拖拽
    
    private OnHuplayerListener listener;
    
    private boolean mOrientationLock=false;//方向锁定
    public void setOrientationLock(boolean isLock){
        this.mOrientationLock=isLock;
    }
    
    
    
    
    @SuppressWarnings("HandlerLeak")
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FADE_OUT:
                    hide(false);
                    break;
                case MESSAGE_HIDE_CENTER_BOX:
                    v.id(R.id.app_video_volume_box).gone();
                    v.id(R.id.app_video_brightness_box).gone();
                    v.id(R.id.app_video_fastForward_box).gone();
                    break;
                case MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShowing) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                        updatePausePlay();
                    }
                    break;
                case MESSAGE_RESTART_PLAY:
                    // play(url);
                    start();
                    break;
            }
        }
    };
    
    
    //************
    // 实现接口函数
    //************
    
    
    @Override
    public void onPrepared(IMediaPlayer mp)
    {
        showLoading(false);
        
        if(listener!=null)
            listener.onPrepared(mp);
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent)
    {
        if(listener!=null)
        listener.onBufferingUpdate(mp,percent);
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp)
    {
        if(listener!=null)
            listener.onSeekComplete(mp);
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den)
    {
        // TODO: Implement this method
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra)
    {
        
        statusChange(STATUS_ERROR);
        showThumb(true);
        showLoading(false);
        if(listener!=null)
            listener.onError(mp,what,extra);
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra)
    {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START://缓存开始
                statusChange(STATUS_LOADING);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲结束
                statusChange(STATUS_PLAYING);
                break;
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH://
                //显示 下载速度
                //Toaster.show("download rate:" + extra);
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START://开始播放
                statusChange(STATUS_PLAYING);
                break;
        }
        if(listener!=null)
            listener.onInfo(what,extra);
        return true;
    }


    @Override
    public void onCompletion(IMediaPlayer mp)
    {
        statusChange(STATUS_COMPLETED);
        updateStartPlay();
        if(listener!=null)
            listener.onComplete(mp);
    }
    
    private final View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View p) {
            if (p.getId() == R.id.img_huplayer_fullscreen) {
                toggleFullScreen();
            } else if (p.getId() == R.id.img_huplayer_play) {
                doPauseResume();
                show(defaultTimeout);
            }else if (p.getId() == R.id.img_huplayet_start) {
                if(status==STATUS_IDLE){
                    showThumb(false);
                    showCover(false);
                    showLoading(true);
                    showControllerView(false);
                    videoView.start();
                }else if(status==STATUS_ERROR){
                    showThumb(false);
                    showCover(false);
                    showLoading(true);
                    showControllerView(false);
                    //setPath(url);
                   
                    //videoView.seekTo(currentPosition);
                    videoView.resume();
                    
                    videoView.start();
                }else if(status==STATUS_COMPLETED){
                    showThumb(false);
                    showCover(false);
                    showLoading(true);
                    showControllerView(false);
                    videoView.resume();
                    videoView.start();
                }else{
                    //doPauseResume();
                    // showThumb(false);
                }
                
            } else if (p.getId() == R.id.img_huplayer_back) {
                if (!fullScreenOnly && !portrait) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    activity.finish();
                }
            }
        }
    };
    
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;
            v.id(R.id.app_video_status).gone();//移动时隐藏掉状态image
            int newPosition = (int) ((duration * progress*1.0) / 1000);
            String time = generateTime(newPosition);
            if (instantSeeking){
                videoView.seekTo(newPosition);
            }
            v.id(R.id.tv_huplayer_currentTime).text(time);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            show(3600000);
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            if (instantSeeking){
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!instantSeeking){

                videoView.seekTo((int) ((duration * seekBar.getProgress()*1.0) / 1000));
            }
            show(defaultTimeout);
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isDragging = false;
            handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
        }
    };
    
    
    
    
    //***************
    //     构造器
    //***************
    public HuPlayer(final Activity activity) {

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport=true;
        } catch (Throwable e) {
            Log.e("GiraffePlayer", "loadLibraries error", e);
        }
        this.activity=activity;
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
      
        //init view
        initControllerView();

        //
        
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnSeekCompleteListener(this);
        videoView.setOnBufferingUpdateListener(this);

        //
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //手势
        final GestureDetector gestureDetector = new GestureDetector(activity, new PlayerGestureListener());

        //
        liveBox.setClickable(true);
        liveBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (gestureDetector.onTouchEvent(motionEvent))
                        return true;

                    // 处理手势结束
                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            endGesture();
                            break;
                    }

                    return false;
                }
            });


        orientationEventListener = new OrientationEventListener(activity) {
            @Override
            public void onOrientationChanged(int orientation) {
                /*if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                 //竖屏
                 if (portrait) {
                 activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                 orientationEventListener.disable();
                 }
                 } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                 if (!portrait) {
                 activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                 orientationEventListener.disable();
                 }
                 }*/


                if(!mOrientationLock){
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    portrait=getScreenOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    orientationEventListener.disable();
                }/*else if(portrait&&((orientation >= 60 && orientation < 150)||(orientation>=210&&orientation<300))){

                 activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                 portrait=getScreenOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                 orientationEventListener.disable();
                 }*/


            }
        };
        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        portrait=getScreenOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        initHeight=activity.findViewById(R.id.app_video_box).getLayoutParams().height;
        hideAll();
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.not_support));
        }
       
    }
    
    
    //初始化控制器布局
    private void initControllerView(){
        v=new BindView(activity);
        videoView = (IjkVideoView) activity.findViewById(R.id.video_view);
   
        seekBar = (SeekBar) activity.findViewById(R.id.sb_huplayer_seek);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        v.id(R.id.img_huplayer_play).clicked(onClickListener);
        v.id(R.id.img_huplayer_fullscreen).clicked(onClickListener);
        v.id(R.id.img_huplayer_back).clicked(onClickListener);
        v.id(R.id.img_huplayet_start).clicked(onClickListener);
       
        showLoading(true);
        
        liveBox = activity.findViewById(R.id.app_video_box);
        
    }
    
    
    

    //继续与暂停互相转换
    private void doPauseResume() {
        if (status==STATUS_COMPLETED) {//播放完成
            v.id(R.id.img_huplayet_start).gone();
            videoView.seekTo(0);
            videoView.start();
        } else if (videoView.isPlaying()) {//正在播放
            statusChange(STATUS_PAUSE);
            videoView.pause();
        } else {//暂停
            videoView.start();
        }
        updatePausePlay();
        updateStartPlay();
    }

    //****************
    //    播放器
    //****************

    /**
     * 设置视频路径
     * @param url
     */
    public void setPath(String url) {
        this.url = url;
        if (playerSupport) {
            //v.id(R.id.app_video_loading).visible();
            videoView.setVideoPath(url);
            //videoView.start();
        }
    }
    
    /**
     * 设置title
     * @param title
     */
    public void setTitle(CharSequence title) {
        v.id(R.id.tv_huplayer_title).text(title);
    }
    
    /**
     * 设置播放器封面
     * @param coverId
     */
    public void setCover(int coverId){
        v.id(R.id.img_huplayer_cover).image(coverId);
    }

    /**
     * try to play when error(only for live video)
     * @param defaultRetryTime millisecond,0 will stop retry,default is 5000 millisecond
     */
    public void setDefaultRetryTime(long defaultRetryTime) {
        this.defaultRetryTime = defaultRetryTime;
    }
    
    /**
     * 设置是否只能全屏播放
     * @param fullScreenOnly
     */
    public void setFullScreenOnly(boolean fullScreenOnly) {
        this.fullScreenOnly = fullScreenOnly;
        tryFullScreen(fullScreenOnly);
        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
    
    /**
     * 实现接口
     */
    public HuPlayer setOnHuplayerListener(OnHuplayerListener listener){
        this.listener=listener;
        return this;
    }
    
    
    /**
     * 播放器创建完成,此时显示播放按钮
     */
    public void createComplete(){
        showControllerView(false);
        showCover(true);
        showThumb(true);
        showLoading(false);
        videoView.setVisibility(View.VISIBLE);
        listener.onPlayerCreate();
    }
    
    
    /**
     * 开始播放
     */
    public void start() {
        videoView.start();
        status=STATUS_PLAYING;
        updatePausePlay();
        updateStartPlay();
    }

    /**
     * 暂停播放
     */
    public void pause() {
        videoView.pause();
        status=STATUS_PAUSE;
        updatePausePlay();
        updateStartPlay();
    }
    
    /**
     * 隐藏
     * @param force
     *        isShowing
     */
    public void hide(boolean force) {
        if (force || isShowing) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            showControllerView(false);
            isShowing = false;
            if(listener!=null)
                listener.onChange(false);
        }
    }
    
    /**
     * 显示
     */
    public void show() {
        show(0);
    }
    
    /**
     * 显示
     * @param timeout
     */
    public void show(int timeout) {
        if (!isShowing) {
            v.id(R.id.ll_huplayer_top_box).visible();
            if (!isLive) {
                showBottomBox(true);
            }
            if (!fullScreenOnly) {
                v.id(R.id.img_huplayer_fullscreen).visible();
            }
            isShowing = true;
            if(listener!=null)
                listener.onChange(true);
            //onControlPanelVisibilityChangeListener.change(true);
        }
        updatePausePlay();
        updateStartPlay();
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        handler.removeMessages(MESSAGE_FADE_OUT);
        if (timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_FADE_OUT), timeout);
        }
    }

    



    
    
    
    
    

  

    
    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            handler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
        handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);

    }

    private void statusChange(int newStatus) {
        status=newStatus;
        if (!isLive && newStatus==STATUS_COMPLETED) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            hideAll();
            v.id(R.id.img_huplayet_start).visible();
            
        }else if (newStatus == STATUS_ERROR) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            hideAll();
            if (isLive) {
                showStatus(activity.getResources().getString(R.string.small_problem));
                if (defaultRetryTime>0) {
                    handler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, defaultRetryTime);
                }
            } else {
                updateStartPlay();
                
                //showStatus(activity.getResources().getString(R.string.small_problem));
            }
        } else if(newStatus==STATUS_LOADING){
            showCover(false);
            showLoading(true);
        } else if (newStatus == STATUS_PLAYING) {
            showCover(false);
            showLoading(false);
        }

    }

    

    public void onPause() {
        pauseTime=System.currentTimeMillis();
        show(0);//把系统状态栏显示出来
        if (status==STATUS_PLAYING) {
            videoView.pause();
            if (!isLive) {
                currentPosition = videoView.getCurrentPosition();
            }
        }
    }

    public void onResume() {
        pauseTime=0;
        if (status==STATUS_PLAYING) {
            if (isLive) {
                videoView.seekTo(0);
            } else {
                if (currentPosition>0) {
                    videoView.seekTo(currentPosition);
                }
            }
            videoView.start();
        }
    }

    public void onConfigurationChanged(final Configuration newConfig) {
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }

    private void doOnConfigurationChanged(final boolean portrait) {
        if (videoView != null && !fullScreenOnly) {
           
            handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tryFullScreen(!portrait);
                        if (portrait) {
                            v.id(R.id.app_video_box).height(initHeight, false);
                        } else {
                            int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
                            int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
                            v.id(R.id.app_video_box).height(Math.min(heightPixels,widthPixels), false);
                        }
                        updateFullScreenButton();
                        
                    }
                });
            //orientationEventListener.enable();
        }
    }

    

    public void onDestroy() {
        orientationEventListener.disable();
        handler.removeCallbacksAndMessages(null);
        videoView.stopPlayback();
    }


    private void showStatus(String statusText) {
        v.id(R.id.app_video_status).visible();
        v.id(R.id.app_video_status_text).text(statusText);
    }

    

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    private int getScreenOrientation() {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
            || rotation == Surface.ROTATION_180) && height > width ||
            (rotation == Surface.ROTATION_90
            || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        hide(true);

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        // 显示
        v.id(R.id.app_video_volume_icon).image(i==0?R.drawable.ic_volume_off_white_36dp:R.drawable.ic_volume_up_white_36dp);
        v.id(R.id.app_video_brightness_box).gone();
        v.id(R.id.app_video_volume_box).visible();
        v.id(R.id.app_video_volume_box).visible();
        v.id(R.id.app_video_volume).text(s).visible();
    }

    private void onProgressSlide(float percent) {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);


        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition=0;
            delta=-position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            v.id(R.id.app_video_fastForward_box).visible();
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            v.id(R.id.app_video_fastForward).text(text + "s");
            v.id(R.id.app_video_fastForward_target).text(generateTime(newPosition)+"/");
            v.id(R.id.app_video_fastForward_all).text(generateTime(duration));
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f){
                brightness = 0.50f;
            }else if (brightness < 0.01f){
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(),"brightness:"+brightness+",percent:"+ percent);
        v.id(R.id.app_video_brightness_box).visible();
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f){
            lpa.screenBrightness = 1.0f;
        }else if (lpa.screenBrightness < 0.01f){
            lpa.screenBrightness = 0.01f;
        }
        v.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100))+"%");
        activity.getWindow().setAttributes(lpa);

    }

   

    
    

    

    /**
     * <pre>
     *     fitParent:可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
     *     fillParent:可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
     *     wrapContent:将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
     *     fitXY:不剪裁,非等比例拉伸画面填满整个View
     *     16:9:不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
     *     4:3:不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
     * </pre>
     * @param scaleType
     */
    public void setScaleType(String scaleType) {
        if (SCALETYPE_FITPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        }else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        }else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
        }else if (SCALETYPE_FITXY.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
        }else if (SCALETYPE_16_9.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
        }else if (SCALETYPE_4_3.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
        }
    }

    /**
     * 是否显示左上导航图标(一般有actionbar or appToolbar时需要隐藏)
     * @param show
     */
    public void setShowNavIcon(boolean show) {
        v.id(R.id.img_huplayer_back).visibility(show ? View.VISIBLE : View.GONE);
    }

    

    //
    public boolean onBackPressed() {
        if (!fullScreenOnly && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||getScreenOrientation()==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }


    
    //********************//
    //      控制器 UI
    //********************//
 
    
    //更新进度条
    private long setProgress() {
        if (isDragging){
            return 0;
        }

        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        this.duration = duration;
        v.id(R.id.tv_huplayer_currentTime).text(generateTime(position));
        v.id(R.id.tv_huplayer_endTime).text(generateTime(this.duration));
        return position;
    }
    
    //隐藏actionbar
    private void tryFullScreen(boolean fullScreen) {
        if(activity instanceof Activity){
            android.app.ActionBar supportActionBar = (activity).getActionBar();
            if (supportActionBar != null)
            {
                if (fullScreen)
                {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }else 
        if (activity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }

    //隐藏通知栏
    private void setFullScreen(boolean fullScreen) {
        if (activity != null) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }
    
    //隐藏所有控制器布局
    private void hideAll() {
        showControllerView(false);//隐藏顶部和底部栏
        showThumb(false);
        
    
      
        //v.id(R.id.app_video_loading).gone();
       
        v.id(R.id.app_video_status).gone();
       // showBottomControl(false);
        if(listener!=null)
            listener.onChange(false);
       
    }
    
   
    //设置控制器布局是否隐藏(顶部栏和底部栏)
    private void showControllerView(boolean isShow){
        if(isShow){
            v.id(R.id.rl_huplayer_bottom_box).visible();
            v.id(R.id.ll_huplayer_top_box).visible();
        }else{
            v.id(R.id.rl_huplayer_bottom_box).invisible();
            v.id(R.id.ll_huplayer_top_box).invisible();
        }
    }
    
    //是否显示封面
    private void showCover(boolean isShow){
        if(isShow){
            v.id(R.id.rl_huplayer_root_cover).visible();
        }else{
            v.id(R.id.rl_huplayer_root_cover).invisible();
        }
    }
    
    //是否显示thumb
    private void showThumb(boolean isShow){
        if(isShow){
            v.id(R.id.img_huplayet_start).visible();
        }else{
            v.id(R.id.img_huplayet_start).invisible();
        }
    }
 
    //是否显示加载等待条
    private void showLoading(boolean isShow){
        v.id(R.id.pb_huplayer_loading).visibility(isShow?View.VISIBLE:View.GONE);
    }
    
    //是否显示顶部导航栏
    private void showTopBox(boolean isShow){
        v.id(R.id.ll_huplayer_top_box).visibility(isShow?View.VISIBLE:View.GONE);
    }
    //是否显示底部栏
    private void showBottomBox(boolean isShow){
        v.id(R.id.rl_huplayer_bottom_box).visibility(isShow?View.VISIBLE:View.GONE);
    }
    
    
    //更新全屏按钮图标
    private void updateFullScreenButton() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||getScreenOrientation()==ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            v.id(R.id.img_huplayer_fullscreen).image(R.drawable.ic_fullscreen_exit_white_24dp);
        } else {
            v.id(R.id.img_huplayer_fullscreen).image(R.drawable.ic_fullscreen_white_24dp);
        }
    }
    
    private void updatePausePlay() {//更新暂停播放按钮图标
        if (videoView.isPlaying()) {
            v.id(R.id.img_huplayer_play).image(R.drawable.bili_player_play_can_pause);
        } else {
            v.id(R.id.img_huplayer_play).image(R.drawable.bili_player_play_can_play);
        }
    }
    
    private void updateStartPlay(){//更新中间按钮图标
        if(status==STATUS_PLAYING){
            v.id(R.id.img_huplayet_start).image(R.drawable.huplayer_click_video_pause_selector);
        }else if(status==STATUS_ERROR){
            v.id(R.id.img_huplayet_start).image(R.drawable.huplayer_click_video_error_selector);
        }else{
            v.id(R.id.img_huplayet_start).image(R.drawable.huplayer_click_video_play_selector);
        }
    }
    
    
    
    
    //**************//
    //  手势响应类
    //**************//
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //videoView.toggleAspectRatio();
            if(status==STATUS_PLAYING){
                pause();
            }else if(status==STATUS_PAUSE){
                start();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);

        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl=mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
                if (!isLive) {
                    onProgressSlide(-deltaX / videoView.getWidth());
                }
            } else {
                float percent = deltaY / videoView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }


            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        //单击确定
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if (isShowing) {
                 hide(false);
            } else {
                 show(defaultTimeout);
            }
            return true;
        }

        
        
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isShowing) {
               // hide(false);
            } else {
               // show(defaultTimeout);
            }
            return true;
        }
    }

    /**
     * is player support this device
     * @return
     */
    public boolean isPlayerSupport() {
        return playerSupport;
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying() {
        return videoView!=null?videoView.isPlaying():false;
    }

    /**
     *
     */
    public void stop(){
        videoView.stopPlayback();
    }

    /**
     * seekTo position
     * @param msec  millisecond
     */
    public HuPlayer seekTo(int msec, boolean showControlPanle){
        videoView.seekTo(msec);
        if (showControlPanle) {
            show(defaultTimeout);
        }
        return this;
    }

    public HuPlayer forward(float percent) {
        if (isLive || percent>1 || percent<-1) {
            return this;
        }
        onProgressSlide(percent);
        showBottomBox(true);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        endGesture();
        return this;
    }

    public int getCurrentPosition(){
        return videoView.getCurrentPosition();
    }

    /**
     * get video duration
     * @return
     */
    public int getDuration(){
        return videoView.getDuration();
    }

    public HuPlayer playInFullScreen(boolean fullScreen){
        if (fullScreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            updateFullScreenButton();
        }
        return this;
    }

    public void toggleFullScreen(){
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        updateFullScreenButton();
    }

   
    

    

    /**
     * set is live (can't seek forward)
     * @param isLive
     * @return
     */
    public HuPlayer live(boolean isLive) {
        this.isLive = isLive;
        return this;
    }

    public HuPlayer toggleAspectRatio(){
        if (videoView != null) {
            videoView.toggleAspectRatio();
        }
        return this;
    }

   
    
    
    
    
    //************//
    //    接口
    //************//
    public interface OnHuplayerListener{
        
        void onPlayerCreate();
        
        void onPrepared(IMediaPlayer mp);
        
        void onError(IMediaPlayer mp,int what, int extra) ;
        
        void onInfo(int what, int extra);
        
        void onChange(boolean isShowing);
        
        void onComplete(IMediaPlayer mp);
        
        void onSeekComplete(IMediaPlayer mp);
        
        void onBufferingUpdate(IMediaPlayer mp, int percent);
    }
   
    
    
    
    
    
    
}
