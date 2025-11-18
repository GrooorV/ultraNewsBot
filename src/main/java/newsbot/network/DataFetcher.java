package newsbot.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;


public interface DataFetcher {
    InputStream fetch(String url) throws IOException, URISyntaxException;
}