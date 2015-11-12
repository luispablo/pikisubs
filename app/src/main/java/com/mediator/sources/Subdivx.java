package com.mediator.sources;

import com.mediator.model.Subtitle;
import com.mediator.model.VideoEntry;
import com.mediator.retrofit.RetrofitServiceSubdivx;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;

/**
 * Created by luispablo on 10/04/15.
 */
public class Subdivx implements SubtitlesSource {

    @Override
    public List<Subtitle> search(VideoEntry videoEntry) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://www.subdivx.com/")
                .setConverter(new SimpleXMLConverter())
                .build();

        RetrofitServiceSubdivx service = restAdapter.create(RetrofitServiceSubdivx.class);

        return service.search(videoEntry.suggestedSearchText()).getChannel().getItems();
    }

    @Override
    public String getName() {
        return "subdivx";
    }

    public static String findRealLink(Subtitle subtitle) {
        String realLink = null;

        try {
            Document doc = Jsoup.connect(subtitle.getLink()).get();

            realLink = doc.select("a.link1").get(0).attr("href");
            /*
            Document countdownPage = Jsoup.connect(countdownPageURL).get();

            Elements elements = countdownPage.select("a");

            for (Element node : elements) {
                if (node.text().contains("link directo")) {
                    realLink = node.attr("href");
                }
            }
            */
        } catch (IOException e) {
            Logger.e(e);
        }

        return realLink;
    }
}