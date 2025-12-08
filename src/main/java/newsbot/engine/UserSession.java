package newsbot.engine;

import newsbot.news.NewsStory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
        this.pendingNewsLinks = news.stream().map(NewsStory::link).collect(Collectors.toCollection(ArrayList::new));
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


    public List<String> getPendingNewsLinks() {
        return new ArrayList<>(this.pendingNewsLinks);
    }

    public int getPendingNewsIndex() {
        return this.pendingNewsIndex;
    }

    public void setPendingNewsLinks(List<String> links) {
        this.pendingNewsLinks = (links == null) ? new ArrayList<>() : new ArrayList<>(links);
    }

    public void setPendingNewsIndex(int index) {
        this.pendingNewsIndex = index;
    }
}
