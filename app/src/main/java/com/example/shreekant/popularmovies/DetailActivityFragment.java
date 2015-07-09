package com.example.shreekant.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        MovieData movieData = intent.getParcelableExtra(String.valueOf(R.string.movie_data_key));

        ((TextView) rootView.findViewById(R.id.movie_original_title))
                            .setText(movieData.getOriginalTitle());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
        String url = movieData.getPosterPath();
        Picasso.with(rootView.getContext())
                .load(url)
                .placeholder(R.raw.image_placeholder)
                .into(imageView);

        ((TextView) rootView.findViewById(R.id.movie_release_date))
                            .setText(movieData.getReleaseDate());
        ((TextView) rootView.findViewById(R.id.movie_vote_average))
                            .setText(movieData.getVoteAverage());
        ((TextView) rootView.findViewById(R.id.movie_overview))
                            .setText(movieData.getOverview());

        return rootView;
    }
}
