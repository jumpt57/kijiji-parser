package com.github.jumpt57.kijiji.workers;

import com.github.jumpt57.kijiji.engine.executor.AsyncExecutor;
import com.github.jumpt57.kijiji.engine.executor.SyncExecutor;
import com.github.jumpt57.kijiji.engine.worker.Loader;
import com.github.jumpt57.kijiji.engine.worker.Worker;
import com.github.jumpt57.kijiji.models.Offer;
import com.github.jumpt57.kijiji.utils.LinksHelper;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class GetOfferByPage extends Loader<Offer> {

    private static final Logger LOGGER = Logger.getLogger(GetOfferByPage.class.getName());

    public static String API_KEY = "";

    public static boolean CALL_GEO_API = true;

    private static final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(API_KEY)
            .build();

    GetOfferByPage(String url) {
        super(url);
    }

    @Override
    public Offer process(Document document) {
        Offer offer = new Offer();

        offer.url = this.url;
        offer.title = document.select("h1[class*='title-']").text();
        offer.price = document.select("span[class*='currentPrice-'] > span").attr("content");
        offer.address = document.select("span[class*='address-']").text().substring(1).trim();
        offer.aptId = document.select("input[class*='input-']").attr("value");

        ofNullable(document.select("div[class*='datePosted-'] > time").attr("title"))
                .ifPresentOrElse(
                        datePosted -> offer.date = datePosted,
                        () -> offer.date = document.select("div[class*='datePosted-'] > span").attr("title")
                );

        ofNullable(document.select("div[role='presentation']"))
                .ifPresent(elements -> {
                    Set<String> pictures = elements.stream()
                            .map(element -> element.select("img").attr("src").trim())
                            .collect(Collectors.toSet());
                    offer.pictures.addAll(pictures);
                });

        geoCodingAddress(offer.address)
                .ifPresent(latLng -> {
                    offer.lat = latLng.lat;
                    offer.lng = latLng.lng;
                });

        return offer;
    }

    private Optional<LatLng> geoCodingAddress(String address) {
        try {
            if (CALL_GEO_API) {
                return ofNullable(GeocodingApi.geocode(context, address).await())
                        .map(geoCodingResults -> geoCodingResults[0])
                        .map(geoCodingResult -> geoCodingResult.geometry.location);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return empty();
    }

    public static Collection<Offer> launchTask(Collection<String> urls) {
        Set<Worker<Offer>> workers = urls.stream()
                .map(url -> new GetOfferByPage(LinksHelper.BASE_URL.getValue() + url))
                .collect(Collectors.toSet());
        var pool = new SyncExecutor<Offer>();
        return pool.start(workers);
    }

    public static void launchTask(Collection<String> urls, Consumer<Offer> action) {
        Set<Worker<Offer>> workers = urls.stream()
                .map(url -> new GetOfferByPage(LinksHelper.BASE_URL.getValue() + url))
                .collect(Collectors.toSet());
        var pool = new AsyncExecutor<Offer>();
        pool.start(workers, action);
    }

}
