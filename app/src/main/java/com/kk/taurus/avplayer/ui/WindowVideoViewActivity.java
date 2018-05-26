package com.kk.taurus.avplayer.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.cover.CloseCover;
import com.kk.taurus.avplayer.play.DataInter;
import com.kk.taurus.avplayer.play.ReceiverGroupManager;
import com.kk.taurus.playerbase.AVPlayer;
import com.kk.taurus.playerbase.assist.OnAVPlayerEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.window.FloatWindowParams;
import com.kk.taurus.playerbase.window.WindowVideoView;

public class WindowVideoViewActivity extends AppCompatActivity {

    Button mActiveWindow;

    WindowVideoView mWindowVideoView;

    DataSource mDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_video_view);
        mActiveWindow = findViewById(R.id.btn_active_window);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int SW = getResources().getDisplayMetrics().widthPixels;
        int width = (int) (SW * 0.7f);
        int height = width * 9/16;
        mWindowVideoView = new WindowVideoView(this,
                new FloatWindowParams()
                        .setX(100)
                        .setY(100)
                        .setWidth(width)
                        .setHeight(height)
                        .setGravity(Gravity.TOP | Gravity.LEFT));

        mWindowVideoView.setBackgroundColor(Color.BLACK);
        mWindowVideoView.setElevationShadow(50);

        mWindowVideoView.setEventHandler(eventHandler);

        ReceiverGroup receiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(this);
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CLOSE_COVER, new CloseCover(this));
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_SCREEN_SWITCH_ENABLE, false);

        mWindowVideoView.setReceiverGroup(receiverGroup);

        mDataSource = new DataSource();
        mDataSource.setData("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4");
        mDataSource.setTitle("神奇的珊瑚");

    }

    private OnAVPlayerEventHandler eventHandler = new OnAVPlayerEventHandler(){
        @Override
        public void onAssistHandle(AVPlayer assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode){
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mWindowVideoView.stop();
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_CLOSE:
                    mWindowVideoView.close();
                    mActiveWindow.setText(R.string.open_window_video_view);
                    break;
            }
        }
    };

    public void activeWindowVideoView(View view){
        if(mWindowVideoView.isWindowShow()){
            mActiveWindow.setText(R.string.open_window_video_view);
            mWindowVideoView.close();
        }else{
            mActiveWindow.setText(R.string.close_window_video_view);
            mWindowVideoView.show();
            mWindowVideoView.setDataSource(mDataSource);
            mWindowVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowVideoView.close();
        mWindowVideoView.stopPlayback();
    }
}
