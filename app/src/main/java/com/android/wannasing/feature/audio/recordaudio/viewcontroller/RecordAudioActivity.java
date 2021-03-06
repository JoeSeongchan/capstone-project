package com.android.wannasing.feature.audio.recordaudio.viewcontroller;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.android.wannasing.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordAudioActivity extends AppCompatActivity {

  public static final String FROM_SET_UPLOAD_ACTIVITY_USER_ID_TAG = "FROM_SET_UPLOAD_ACTIVITY_USER_ID_TAG";
  //firebase & id
  private FirebaseStorage storage = FirebaseStorage.getInstance();

  private MediaRecorder recorder;
  private String filePath;
  private ImageButton record;
  private TextView recording;
  private boolean isRecording = false;    // 현재 녹음 상태를 확인하기 위함.
  private String userid = "IOxZt7qFPxdE93PNtElzSztmYpP2";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record_audio);
    userid = getIntent().getStringExtra(FROM_SET_UPLOAD_ACTIVITY_USER_ID_TAG);
    permissionCheck();
    init();
    click();
  }

  private void init() {
    record = findViewById(R.id.recordingAudio_btn_record);
    recording = findViewById(R.id.recordingAudio_tv_status);
  }

  private void click() {
    record.setOnClickListener(view -> {
      if (isRecording == false) {
        recordAudio();

        record.setImageDrawable(
            ResourcesCompat
                .getDrawable(getResources(), R.drawable.show_my_profile_stop_icon, null));
        recording.setText("녹음중..");
        recording.setTextColor(Color.parseColor("#FF0000"));
        isRecording = true;
      } else {
        stopRecording();
        record.setImageDrawable(
            ResourcesCompat
                .getDrawable(getResources(), R.drawable.audio_record_icon, null));
        recording.setText("녹음준비");
        recording.setTextColor(Color.parseColor("#8C8C8C"));
        isRecording = false;
      }
    });
  }

  private void recordAudio() {
    setupAudio();
    recorder = new MediaRecorder();
    recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 어디에서 음성 데이터를 받을 것인지
    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 압축 형식 설정
    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    recorder.setOutputFile(filePath);

    try {
      recorder.prepare();
      recorder.start();

      Toast.makeText(this, "녹음 시작됨.", Toast.LENGTH_SHORT).show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setupAudio() {
    ContextWrapper cw = new ContextWrapper(getApplicationContext());
    File directory = cw.getDir("recordDir", Context.MODE_PRIVATE);
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File file = new File(directory, timeStamp + ".mp4");
    this.filePath = file.getAbsolutePath();
    Log.d("MainActivity_R", " - setupStorage" +
        "\ntarget path : " + filePath + "\n---");
  }


  private void stopRecording() {
    if (recorder != null) {
      recorder.stop();
      recorder.release();
      recorder = null;
      Toast.makeText(this, "녹음 중지 및 업로드 중..", Toast.LENGTH_SHORT).show();

      Uri fileUri = Uri.fromFile(new File(filePath));
      Log.d("upload", "뭐야이건" + fileUri);

      StorageReference s_ref = storage.getReference();
      StorageReference r_ref = s_ref.child(userid + "/" + fileUri.getLastPathSegment());
      Log.d("upload", "뭐야이건" + fileUri.getLastPathSegment());
      UploadTask uploadTask = r_ref.putFile((fileUri));
      uploadTask.addOnFailureListener(exception -> Log.d("upload", exception.getMessage()))
          .addOnSuccessListener(taskSnapshot -> {
          });
    }
  }

  public void permissionCheck() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
              Manifest.permission.RECORD_AUDIO}, 1);
    }
  }


}
