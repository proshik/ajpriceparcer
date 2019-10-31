package ru.proshik.applepricebot.service.provider.gsmstore;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.repository.model.ProviderType;
import ru.proshik.applepricebot.service.provider.HtmlPageProvider;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class GsmStoreScreeningProviderTest {

    private static final String DEFAULT_URL = "https://test.test";

    private static final Provider PROVIDER = Provider.builder()
            .title("gsm-store.ru")
            .url(DEFAULT_URL)
            .enabled(true)
            .type(ProviderType.GSM_STORE)
            .build();

    @Mock
    private HtmlPageProvider htmlPageProvider;

    @InjectMocks
    private GsmStoreScreeningProvider screeningProvider;

    public GsmStoreScreeningProviderTest() {
    }

    @Ignore
    @Test
    public void provideByProductTypeIphoneXsSuccess() throws Exception {
        // init mock
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("gsm_store_iphone_xs.html"));

        // invoke the method
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XS);

        // check
        Assert.assertEquals(22, products.size());
    }

    private HtmlPage getPreparedHtmlPage(String pageName) throws Exception {
        // init data
        String text = IOUtils.toString(
                getClass().getClassLoader().getResourceAsStream("provider_data/" + pageName),
                Charset.defaultCharset());
        StringWebResponse response = new StringWebResponse(text, new URL(DEFAULT_URL));
        return HTMLParser.parseHtml(response, new WebClient().getCurrentWindow());
    }
}
