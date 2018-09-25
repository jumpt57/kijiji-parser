package com.github.jumpt57.kijiji.engine.worker;

import org.jsoup.nodes.Document;

public interface Worker<T> {

    T load();

    T process(final Document document);

}
