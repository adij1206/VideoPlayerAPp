package com.aditya.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.android.volley.Request;
import com.android.volley.Response;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    private RecyclerView recyclerView;
    private VideoRecyclerViewAdapter videoRecyclerViewAdapter;
    private EditText urlEditText;
    private Button playBtn;


    //Removing Api Key,use when it is required
    public static final String URL = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&key=AIzaSyDgilcbQ-4p3ZzIyCHoluFY60sTULuvOW0";
    String videoURL = "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4";

    SimpleExoPlayerView simpleExoPlayerView;
    SimpleExoPlayer exoPlayer;

    List<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = (EditText) findViewById(R.id.url_edit_txt);
        playBtn = (Button)findViewById(R.id.play_btn);

        simpleExoPlayerView =  findViewById(R.id.pv_main);
        recyclerView = findViewById(R.id.recylerview);

        videoList = new ArrayList<>();

        playVideo();
        networkrequest();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlFromEdittext = urlEditText.getText().toString();
                playVideofrmUrl(urlFromEdittext);
            }
        });

        //This Method is to change the Input Action Button Functionality,we have to edit in xml code also.
        urlEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(EditorInfo.IME_ACTION_SEARCH==actionId){
                    Toast.makeText(MainActivity.this, "Searching....", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void networkrequest() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try{
                            Log.d("Aditya", "onResponse: "+response);
                            JSONObject res = new JSONObject(response);
                            for(int i=0;i<5;i++) {
                                String title = res.getJSONArray("items").getJSONObject(i).getJSONObject("snippet").getString("title");
                                String id = res.getJSONArray("items").getJSONObject(i).getString("id");
                                Video video = new Video(title,id);
                                videoList.add(video);

                            }
                            Log.d("As", videoList.get(1).getId());
                        }catch (JSONException e){
                                e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(videoList,this);
        recyclerView.setAdapter(videoRecyclerViewAdapter);
        videoRecyclerViewAdapter.notifyDataSetChanged();

        //This is new feature in which we can swipe or drag and drop in recyclerview
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT|ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(videoList,from,to);
                videoRecyclerViewAdapter.notifyItemMoved(from,to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                videoList.remove(viewHolder.getAdapterPosition());
                videoRecyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        helper.attachToRecyclerView(recyclerView);

    }
    public void playVideo(){
        try{
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            Uri videouri = Uri.parse(videoURL);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            simpleExoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e("TAG", "Error : " + e.toString());
        }
    }

    public void playVideofrmUrl(String url){
                //exoPlayer.release();
        exoPlayer.stop();
        exoPlayer.seekTo(0L);
        try{
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            Uri videouri = Uri.parse(url);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            simpleExoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e("TAG", "Error : " + e.toString());
        }

    }
}
