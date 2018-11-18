package ru.proshik.applepricebot.service.provider;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.repository.model.ProviderType;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AjScreeningProviderTest {

    private static final String DEFAULT_URL = "https://test.test";

    private static final Provider PROVIDER = Provider.builder()
            .title("aj.ru")
            .url(DEFAULT_URL)
            .enabled(true)
            .type(ProviderType.AJ)
            .build();

    private HtmlPage default_html_page = getPreparedHtmlPage();

    @Mock
    private HtmlPageProvider htmlPageProvider;

    @InjectMocks
    private AjScreeningProvider screeningProvider;

    public AjScreeningProviderTest() throws Exception {
    }

    @Before
    public void init() {
        // mock
        when(htmlPageProvider.provide(DEFAULT_URL)).thenReturn(default_html_page);
    }

    @Test
    public void provideSuccess() {
        // call the method
        List<Product> products = screeningProvider.screening(PROVIDER);

        // check
        assertEquals(28, products.size());
    }

    @Test
    public void provideByProductTypesSuccess() {
        // call the method
        List<Product> products = screeningProvider.screening(PROVIDER, List.of(ProductType.IPHONE_XS, ProductType.IPHONE_XS_MAX));

        // check
        assertEquals(9, products.size());
    }

    @Test
    public void provideByProductTypeIphoneXs() {
        // call the method
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XS);

        assertEquals(3, products.size());

        Product on64Gb = products.get(0);

        assertEquals(new BigDecimal(74500), on64Gb.getPrice());
        assertEquals("iPhone XS 64GB", on64Gb.getTitle());
        assertEquals("золотой/серебристый/«серый космос»", on64Gb.getDescription());
        assertEquals("{\"GB\":\"64GB\"}", on64Gb.getParameters());
    }

    @Test
    public void provideByProductTypeIphoneXsMax() {
        // call the method
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XS_MAX);

        assertEquals(6, products.size());

        // check iPhone XS Max 64GB (с 2 sim)
        Product on64Gb = products.get(3);

        assertEquals(new BigDecimal(85000), on64Gb.getPrice());
        assertEquals("iPhone XS Max 64GB (с 2 sim)", on64Gb.getTitle());
        assertEquals("золотой/серебристый/«серый космос»", on64Gb.getDescription());
        assertEquals("{\"GB\":\"64GB\"}", on64Gb.getParameters());
    }

    @Test
    public void provideByProductTypeIphoneXr() {
        // call the method
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XR);

        assertEquals(6, products.size());

        // check iPhone XR 256GB
        Product on64Gb = products.get(2);

        assertEquals(new BigDecimal(71500), on64Gb.getPrice());
        assertEquals("iPhone XR 256GB", on64Gb.getTitle());
        assertEquals("желтый/коралловый/красный/черный/синий/белый", on64Gb.getDescription());
        assertEquals("{\"GB\":\"256GB\"}", on64Gb.getParameters());
    }

    private HtmlPage getPreparedHtmlPage() throws Exception {
        // init data
        String text = IOUtils.toString(
                getClass().getClassLoader().getResourceAsStream("provider_data/aj_ru.html"),
                Charset.defaultCharset());
        StringWebResponse response = new StringWebResponse(text, new URL(DEFAULT_URL));
        return HTMLParser.parseHtml(response, new WebClient().getCurrentWindow());
    }
}
