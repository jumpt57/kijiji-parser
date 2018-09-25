package com.github.jumpt57.kijiji.engine.worker;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public abstract class Loader<T> implements Worker<T> {

    private static final Logger LOGGER = Logger.getLogger(Loader.class.getName());
    private static final String HTTP = "http";

    protected final String url;

    protected Loader(String url) {
        this.url = url;
    }

    @Override
    public T load() {
        LOGGER.info(format("Starting worker %s on thread %s", this.getClass().getSimpleName(),
                Thread.currentThread().getName()));
        try {
            if (url.contains(HTTP)) {
                return process(Jsoup.connect(url).get());
            } else {
                File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(url)).getFile());
                return process(Jsoup.parse(file, "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.SEVERE, format("---------------> Error for url %s", url));
        return null;
    }

}
