package ru.proshik.applepricebot.service.provider;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.Charset;

public class ScreeningProviderTest {

    private static final String DEFAULT_URL = "https://test.test";

    protected HtmlPage getPreparedHtmlPage(String pageName) throws Exception {
        // init data
        String text = IOUtils.toString(
                getClass().getClassLoader().getResourceAsStream("provider_data/" + pageName),
                Charset.defaultCharset());
        StringWebResponse response = new StringWebResponse(text, new URL(DEFAULT_URL));

        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);

        return HTMLParser.parseHtml(response, webClient.getCurrentWindow());
    }

}
