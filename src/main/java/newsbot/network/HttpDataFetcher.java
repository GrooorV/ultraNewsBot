package newsbot.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpDataFetcher implements DataFetcher {

    @Override
    public InputStream fetch(String url) throws IOException, URISyntaxException {
        // Логика, которая раньше была внутри LentaNewsProvider
        return new URI(url).toURL().openStream();
    }
}