package com.mediator.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.mediator.R;
import com.mediator.helpers.Oju;
import com.mediator.model.TVShow;
import com.mediator.model.tmdb.TMDBSeason;
import com.mediator.model.tmdb.TMDbTVResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 11/06/15.
 */
public class FragmentSuggestEpisodesDialog extends DialogFragment {

    List<String> labels;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TVShow tvShow = (TVShow) getArguments().getSerializable(TVShow.class.getName());
        TMDbTVResult tmdbTVResult = (TMDbTVResult) getArguments().getSerializable(TMDbTVResult.class.getName());

        labels = buildLabels(tvShow, tmdbTVResult);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_suggest_episodes)
                .setItems(labels.toArray(new String[]{}), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, labels.get(which));
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                });
        return builder.create();
    }

    private List<String> buildLabels(TVShow tvShow, TMDbTVResult tmdbTVResult) {
        List<String> labels = new ArrayList<>();

        for (int seasonNumber = tvShow.lastSeasonNumber(); seasonNumber <= tmdbTVResult.getNumberOfSeasons(); seasonNumber++) {
            TMDBSeason season = tmdbTVResult.getSeason(seasonNumber);
            String seasonString = Oju.right("00" + String.valueOf(seasonNumber), 2);

            for (int episodeNumber = tvShow.lastEpisodeNumber(seasonNumber) + 1; episodeNumber <= season.getEpisodeCount(); episodeNumber++) {
                String episodeString = Oju.right("00" + String.valueOf(episodeNumber), 2);

                labels.add(tvShow.getTitle() + " S" + seasonString + "E" + episodeString);
            }
        }

        return labels;
    }
}