package newsbot.engine;

import newsbot.news.NewsStory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class UserSession {
    private boolean started;


    private Instant lastNewsCheckTime;

    private List<String> pendingNewsLinks;
    private int pendingNewsIndex;

    public UserSession() {
        this.started = false;
        this.pendingNewsLinks = new ArrayList<>();
        this.pendingNewsIndex = 0;
        this.lastNewsCheckTime = null;
    }

    public boolean isStarted() { return started; }
    public void markStarted() { started = true; }
    public void unmarkStarted() { started = false; }

    public Instant getLastNewsCheckTime() { return lastNewsCheckTime; }
    public void setLastNewsCheckTime(Instant time) {this.lastNewsCheckTime = time; }

    public boolean hasPendingNews() {
        return pendingNewsLinks != null && pendingNewsIndex < pendingNewsLinks.size();
    }


    public void setPendingNews(List<NewsStory> news) {
        this.pendingNewsLinks = news.stream().map(NewsStory::link).toList();
        this.pendingNewsIndex = 0;
    }


    public void clearPendingNews() {
        this.pendingNewsLinks.clear();
        this.pendingNewsIndex = 0;
    }

    public List<String> getNextChunkIds(int chunkSize) {
        if (!hasPendingNews()) {
            return List.of();
        }

        int from = pendingNewsIndex;
        int to = Math.min(pendingNewsIndex + chunkSize, pendingNewsLinks.size());

        this.pendingNewsIndex = to;

        return pendingNewsLinks.subList(from, to);
    }
}
