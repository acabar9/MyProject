package org.minic.destinaionwaker;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class Alarm extends AppCompatActivity {

    Vibrator vibrator;
    MediaPlayer mPlayer;
    DBHelperSettingManager dbHSM;
    AudioManager audioManager;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        //잠금화면에서 액티비티 띄우기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);


//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent intent = new Intent(this, Alarm.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification.Builder builder = new Notification.Builder(this);

//        builder.setFullScreenIntent(pendingIntent, true);
//        builder.setWhen(System.currentTimeMillis());    //알람시간
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_map)); //큰그림
//        builder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm);   //작은그림
//        builder.setTicker("--알람--");  //알람 발생시 잠깐 나오는 텍스트(실제 디바이스에서는 나옴)
//        builder.setContentTitle("목적지 도착");   // 제목
//        builder.setContentText("도착하셨습니다");    //내용

        dbHSM = new DBHelperSettingManager(getApplicationContext(), "SETTING_SQLITE.db", null, 1);

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {    //무음
            if (dbHSM.playBellOnVibeMode()==1){
                playBell();
                playViberate();
            }else{
                playViberate();
            }
        }
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){   //진동
            if (dbHSM.playBellOnVibeMode()==1){
                playBell();
                playViberate();
            }else{
                playViberate();
            }
        }
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){    //벨
            playBell();
            playViberate();
        }


        Button cancle = (Button) findViewById(R.id.cancleButton);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //알람 발생시 진동, 사운드
//        builder.setContentIntent(pendingIntent);   // 알람 눌렀을 때 실행할 작업 인텐트 설정
//        builder.setAutoCancel(true);    //알람 터치시 자동으로 삭제할 것인지 설정
//        builder.setNumber(999);     //확인하지 않은 알림 갯수 설정
//        notificationManager.notify(0, builder.build());
//
//        builder.addAction(android.R.drawable.ic_dialog_map, "", pendingIntent);
//        builder.setAutoCancel(true);
//        notificationManager.notify(0, builder.build());

        //화면 아예 껐을때도 깨우기
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire(3000);
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }


    public void playBell(){
        mPlayer = new MediaPlayer();         // 객체생성
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); //TYPE_RINGTONE

        try {
            // 이렇게 URI 객체를 그대로 삽입해줘야한다.
            //인터넷에서 url.toString() 으로 하는것이 보이는데 해보니까 안된다 -_-;
            mPlayer.setDataSource(this,alert);
            // 출력방식(재생시 사용할 방식)을 설정한다. STREAM_RING 은 외장 스피커로,
            // STREAM_VOICE_CALL 은 전화-수신 스피커를 사용한다.
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM); //STREAM_RING
            mPlayer.setLooping(true);  // 반복여부 지정
            mPlayer.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();   // 실행 시작
    }

    public void playViberate(){
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final long vibratePattern[] = {500, 1000};
        vibrator.vibrate(vibratePattern, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
        Toast.makeText(this, "알람이 해제되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void stopAlarm(){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            if (dbHSM.playBellOnVibeMode()==1){
                vibrator.cancel();
                mPlayer.stop();
                mPlayer.release();
            }else{
                vibrator.cancel();
            }
        }
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            if (dbHSM.playBellOnVibeMode()==1){
                vibrator.cancel();
                mPlayer.stop();
                mPlayer.release();
            }else{
                vibrator.cancel();
            }
        }
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            vibrator.cancel();
            mPlayer.stop();         // 이 방식은 미디어를 멈추는것이고
            mPlayer.release();        // 이 방식은 메모리에서 해체시키는 방법이다

        }
        finish();
    }
}
