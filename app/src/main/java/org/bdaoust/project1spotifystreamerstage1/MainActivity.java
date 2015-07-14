package org.bdaoust.project1spotifystreamerstage1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity{

    private ArtistListAdapter artistListAdapter;
    private HashMap<String, Bitmap> thumbnailsCache;
    private SpotifyService spotifyService;
    private Context context;
    private Toast toast;
    private List<Artist> artists;

    private final static String ARTIST_NAME_KEY = "ARTIST_NAME";
    private final static String ARTIST_ID_KEY = "ARTIST_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpotifyApi spotifyApi;
        EditText artistQuery;
        ListView artistSearchResults;
        ArtistNameTextWatch artistNameTextWatch;

        setContentView(R.layout.activity_main);

        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();
        thumbnailsCache = new HashMap<>();
        context = this;

        artistQuery = (EditText)findViewById(R.id.artistQuery);
        artistNameTextWatch = new ArtistNameTextWatch();
        artistQuery.addTextChangedListener(artistNameTextWatch);

        artists = new ArrayList<>();
        artistListAdapter = new ArtistListAdapter();
        artistSearchResults = (ListView)findViewById(R.id.artistSearchResults);
        artistSearchResults.setAdapter(artistListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class ArtistListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return artists.size();
        }

        @Override
        public Object getItem(int position) {
            return artists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View artistView;
            TextView artistName;
            final ImageView artistIcon;
            Image preferedArtistImage;

            if(convertView == null) {
                artistView = getLayoutInflater().inflate(R.layout.artist_list_item, null);
            }
            else {
                artistView = convertView;
            }

            artistName = (TextView)artistView.findViewById(R.id.artistName);
            artistIcon = (ImageView)artistView.findViewById(R.id.artistIcon);

            artistName.setText(artists.get(position).name);
            artistIcon.setImageBitmap(null);

            preferedArtistImage = Tools.findPreferedSizeImage(artists.get(position).images, 200);
            if(preferedArtistImage !=null) {
                if(thumbnailsCache.containsKey(preferedArtistImage.url)){
                    artistIcon.setImageBitmap(thumbnailsCache.get(preferedArtistImage.url));
                }
                else {
                    ThumbnailDownloader thumbnailDownloader = new ThumbnailDownloader(preferedArtistImage.url) {
                        @Override
                        public void onThumbnailDowloaded(String thumbnailPath, Bitmap bitmap) {
                            thumbnailsCache.put(thumbnailPath,bitmap);
                            artistIcon.setImageBitmap(bitmap);
                        }
                    };
                    thumbnailDownloader.download();
                }
            }

            artistView.setTag(artists.get(position));

            artistView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Artist artist;
                    Intent intent;

                    artist = (Artist)v.getTag();
                    intent = new Intent(context,TopTracksActivity.class);
                    intent.putExtra(ARTIST_NAME_KEY, artist.name);
                    intent.putExtra(ARTIST_ID_KEY, artist.id);

                    startActivity(intent);
                }
            });

            return artistView;
        }
    }




    private class ArtistNameTextWatch implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(toast != null){
                toast.cancel();
            }
            thumbnailsCache.clear();
        }

        @Override
        public void onTextChanged(CharSequence artist, int start, int before, int count) {
            spotifyService.searchArtists(artist.toString(), new Callback<ArtistsPager>() {

                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    artists = artistsPager.artists.items;
                    artistListAdapter.notifyDataSetChanged();

                    if(artists.size() == 0){
                        toast  = Toast.makeText(context, getText(R.string.search_result_no_artists_found), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    artists.clear();
                    artistListAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

}
