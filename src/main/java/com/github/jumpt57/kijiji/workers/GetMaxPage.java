package com.github.jumpt57.kijiji.workers;

import com.github.jumpt57.kijiji.engine.executor.SyncExecutor;
import com.github.jumpt57.kijiji.engine.worker.Loader;
import com.github.jumpt57.kijiji.utils.LinksHelper;
import org.jsoup.nodes.Document;

public class GetMaxPage extends Loader<Integer> {

    private GetMaxPage(String url) {
        super(url);
    }

    @Override
    public Integer process(Document document) {
        return Integer.parseInt(
                document.select("div.pagination > span.selected").text().trim()
        );
    }

    public static Integer launchTask() {
        var executor = new SyncExecutor<Integer>();
        return executor.start(new GetMaxPage(LinksHelper.MAGIC_PAGE.getValue()));
    }

}
