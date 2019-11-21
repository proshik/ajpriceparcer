package ru.proshik.applepricebot.service.provider.gsmstore;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.repository.model.ProviderType;
import ru.proshik.applepricebot.service.provider.HtmlPageProvider;
import ru.proshik.applepricebot.service.provider.ScreeningProviderTest;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class GsmStoreScreeningProviderTest extends ScreeningProviderTest {

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

    @Test
    public void provideByProductTypeIphoneXsSuccess() throws Exception {
        // init mock
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("gsm_store_iphone_xs.htm"));

        // invoke the method
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XS);

        // check
        Assert.assertEquals(28, products.size());
    }

    @Test
    public void provideByProductTypeIphone11Success() throws Exception {
        // init mock
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("gsm_store_iphone_11.htm"));

        // invoke the method
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_11);

        // check
        Assert.assertEquals(51, products.size());
    }



}
