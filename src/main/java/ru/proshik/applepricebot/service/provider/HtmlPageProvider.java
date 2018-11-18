package ru.proshik.applepricebot.service.provider;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.exception.ProviderParseException;

import java.io.IOException;

@Component
public class HtmlPageProvider {

    public HtmlPage provide(String url) {
        WebClient client = new WebClient();

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        client.setCssErrorHandler(new SilentCssErrorHandler());

        try {
            return client.getPage(url);
        } catch (IOException | FailingHttpStatusCodeException e) {
            throw new ProviderParseException(e);
        }
    }

}
