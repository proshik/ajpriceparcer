package ru.proshik.applepricebot.service.provider;

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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AjScreeningProviderTest extends ScreeningProviderTest {

    private static final String DEFAULT_URL = "https://test.test";

    private static final Provider PROVIDER = Provider.builder()
            .title("aj.ru")
            .url(DEFAULT_URL)
            .enabled(true)
            .type(ProviderType.AJ)
            .build();

    @Mock
    private HtmlPageProvider htmlPageProvider;

    @InjectMocks
    private AjScreeningProvider screeningProvider;

    @Test
    public void provideSuccessAllFor2018YearSuccess() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2018.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER);

        // then
        assertEquals(28, products.size());
    }

    @Test
    public void provideByIphoneXsAndIphoneXsMaxFor2018YearSuccess() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2018.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER, List.of(ProductType.IPHONE_XS, ProductType.IPHONE_XS_MAX));

        // then
        assertEquals(9, products.size());
    }

    @Test
    public void provideByIphoneXsAndIphoneXsMaxFor2019YearSuccess() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2019.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER, List.of(ProductType.IPHONE_XS, ProductType.IPHONE_XS_MAX));

        // then
        assertEquals(8, products.size());
    }

    @Test
    public void provideByProductTypeIphoneXsFor2018Success() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2018.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XS);

        // then
        assertEquals(3, products.size());

        Product on64Gb = products.get(0);

        assertEquals(new BigDecimal(74500), on64Gb.getPrice());
        assertEquals("iPhone XS 64GB", on64Gb.getTitle());
        assertEquals("золотой/серебристый/«серый космос»", on64Gb.getDescription());
        assertEquals("{\"GB\":\"64GB\"}", on64Gb.getParameters());
    }

    @Test
    public void provideByProductTypeIphoneXsMaxFor2018Success() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2018.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XS_MAX);

        // then
        assertEquals(6, products.size());

        // then iPhone XS Max 64GB (с 2 sim)
        Product on64Gb = products.get(3);

        assertEquals(new BigDecimal(85000), on64Gb.getPrice());
        assertEquals("iPhone XS Max 64GB (с 2 sim)", on64Gb.getTitle());
        assertEquals("золотой/серебристый/«серый космос»", on64Gb.getDescription());
        assertEquals("{\"GB\":\"64GB\"}", on64Gb.getParameters());
    }

    @Test
    public void provideByProductTypeIphoneXrFor2019Success() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2018.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER, ProductType.IPHONE_XR);

        assertEquals(6, products.size());

        // then iPhone XR 256GB
        Product on64Gb = products.get(2);

        assertEquals(new BigDecimal(71500), on64Gb.getPrice());
        assertEquals("iPhone XR 256GB", on64Gb.getTitle());
        assertEquals("желтый/коралловый/красный/черный/синий/белый", on64Gb.getDescription());
        assertEquals("{\"GB\":\"256GB\"}", on64Gb.getParameters());
    }

    @Test
    public void provideSuccessAllFor2019YearSuccess() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2019.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER);

        // then
        assertEquals(34, products.size());
    }

    @Test
    public void provideSuccessIphone11For2019Success() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2019.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER, List.of(ProductType.IPHONE_11));

        // then
        assertEquals(6, products.size());
    }

    @Test
    public void provideSuccessIphoneProAndMaxFor2019Success() throws Exception {
        // given
        when(htmlPageProvider.provide(anyString())).thenReturn(getPreparedHtmlPage("aj_ru_2019.html"));

        // when
        List<Product> products = screeningProvider.screening(PROVIDER, List.of(ProductType.IPHONE_11_PRO, ProductType.IPHONE_11_PRO_MAX));

        // then
        assertEquals(12, products.size());
    }

}
