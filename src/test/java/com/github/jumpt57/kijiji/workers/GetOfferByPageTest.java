package com.github.jumpt57.kijiji.workers;

import com.github.jumpt57.kijiji.engine.executor.AsyncExecutor;
import com.github.jumpt57.kijiji.engine.executor.SyncExecutor;
import com.github.jumpt57.kijiji.engine.worker.Loader;
import com.github.jumpt57.kijiji.engine.worker.Worker;
import com.github.jumpt57.kijiji.models.Offer;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class GetOfferByPageTest {

    private static final String URL_LOCAL = "kijiji-offer-spacious-one-bedroom.html";
    private static final String TITLE_TO_FIND = "Spacious one bedroom apartment corner Parc/Sherbrooke";

    @Test
    public void should_map_one_offer() {
        //GIVEN
        GetOfferByPage.CALL_GEO_API = false;
        Loader<Offer> worker = new GetOfferByPage(URL_LOCAL);
        //WHEN
        var pool = new SyncExecutor<Offer>();
        var offer = pool.start(worker);
        //THEN
        assertThat(offer)
                .isNotNull()
                .extracting(offer1 -> offer1.title)
                .contains(TITLE_TO_FIND);
    }

    @Test
    public void should_map_100_offers() {
        //GIVEN
        GetOfferByPage.CALL_GEO_API = false;
        Set<Worker<Offer>> workers = buildWorkers();
        //WHEN
        var pool = new SyncExecutor<Offer>();

        HashSet<Offer> offers = new HashSet<>(pool.start(workers));
        //THEN
        assertThat(offers)
                .isNotNull()
                .hasSize(100);
    }


    @Test
    public void should_map_one_offer_async() {
        //GIVEN
        GetOfferByPage.CALL_GEO_API = false;
        Loader<Offer> worker = new GetOfferByPage(URL_LOCAL);
        //WHEN
        var pool = new AsyncExecutor<Offer>();
        //THEN
        pool.start(worker, offer ->
                assertThat(offer)
                        .isNotNull()
                        .extracting(offer1 -> offer1.title)
                        .contains(TITLE_TO_FIND)
        );
    }

    @Test
    public void should_map_100_offers_async() {
        //GIVEN
        GetOfferByPage.CALL_GEO_API = false;
        Set<Worker<Offer>> workers = buildWorkers();
        //WHEN
        var pool = new AsyncExecutor<Offer>();
        //THEN
        pool.start(workers, offer ->
                assertThat(offer)
                        .isNotNull()
                        .extracting(offer1 -> offer1.title)
                        .contains(TITLE_TO_FIND)
        );
    }

    private Set<Worker<Offer>> buildWorkers() {
        return IntStream.range(0, 100)
                .mapToObj(integer -> URL_LOCAL)
                .map(GetOfferByPage::new)
                .collect(Collectors.toSet());
    }

}