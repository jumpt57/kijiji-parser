package com.github.jumpt57.kijiji;

import com.github.jumpt57.kijiji.workers.GetMaxPage;
import com.github.jumpt57.kijiji.workers.GetOfferByPage;
import com.github.jumpt57.kijiji.workers.GetOffersByPage;

import java.util.logging.Logger;

import static java.lang.String.format;

public class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Warming up crawler...");

        GetOfferByPage.API_KEY = args[0];

        long startTime = System.currentTimeMillis();

        var numberOfPages = GetMaxPage.launchTask();

        LOGGER.info("Crawler is ready");

        var urls = GetOffersByPage.launchTask(numberOfPages);

        LOGGER.info(format("%s links to retrieve", String.valueOf(urls.size())));

        GetOfferByPage.launchTask(urls, offer ->
            LOGGER.info(offer.title)
        );

        long endTime = System.currentTimeMillis();
        LOGGER.info(format("That took %s milliseconds", (endTime - startTime)));
    }

}


