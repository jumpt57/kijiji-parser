package com.github.jumpt57.kijiji.workers;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.jumpt57.kijiji.engine.executor.SyncExecutor;
import com.github.jumpt57.kijiji.engine.worker.Loader;
import com.github.jumpt57.kijiji.engine.worker.Worker;
import com.github.jumpt57.kijiji.utils.LinksHelper;
import org.jsoup.nodes.Document;

import static java.lang.String.format;

public class GetOffersByPage extends Loader<Collection<String>> {

    private GetOffersByPage(String url) {
        super(url);
    }

    @Override
    public Collection<String> process(Document document) {
        return document.select("a.title.enable-search-navigation-flag").stream()
                .map(element -> element.attr("href"))
                .collect(Collectors.toSet());
    }

    public static Collection<String> launchTask(Integer numberOfPages) {
        Set<Worker<Collection<String>>> workers = IntStream.range(1, numberOfPages)
                .mapToObj(operand -> new GetOffersByPage(String.format(LinksHelper.TEMPLATE_PAGE.getValue(), operand)))
                .collect(Collectors.toSet());

        var pool = new SyncExecutor<Collection<String>>();

        return pool.start(workers).stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
