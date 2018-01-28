package ru.proshik.applepriceparcer.provider2.gsmstore;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.model2.*;
import ru.proshik.applepriceparcer.provider2.Provider;
import ru.proshik.applepriceparcer.provider2.ProviderUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GsmStoreProvider implements Provider {

    private static final Logger LOG = Logger.getLogger(ru.proshik.applepriceparcer.provider2.gsmstore.GsmStoreProvider.class);

    public static final String TITLE = "GSM-STORE.ru";
    public static final String URL = "http://gsm-store.ru";

    private static final String IN_STOCK = "В наличии";
    private static final String OUT_STOCK = "Товар закончился";

    private WebClient client = new WebClient();

    public GsmStoreProvider() {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public Fetch screening() {

        List<Product> products = new ArrayList<>();
        for (ProductTypePointer ptp : productTypeClassHolder()) {
            HtmlPage page;
            try {
                page = client.getPage(URL + ptp.urlPath);
            } catch (IOException e) {
                LOG.error("Error on get page from gsm-store.ru for url" + URL + ptp.urlPath);
                continue;
            }

            List<HtmlElement> byXPath = page.getByXPath("//li[@class='product-block']");
            for (HtmlElement li : byXPath) {

                String title = ptp.productType.getValue();
                String description = null;
                Boolean presence = null;
                BigDecimal price = null;
                Map<String, String> params = null;

                // extract presence
                HtmlDivision statusDiv = li.getFirstByXPath(".//div[@class='status-inner']");
                HtmlSpan presenceElem = statusDiv.getFirstByXPath(".//span");
                if (presenceElem != null) {
                    if (presenceElem.asText().equals(IN_STOCK)) {
                        presence = true;
                    } else if (presenceElem.asText().equals(OUT_STOCK))
                        presence = false;
                }
                // extract description
                HtmlStrong strongDescriptionPreElem = li.getFirstByXPath(".//strong[@class='title']");
                if (strongDescriptionPreElem != null) {
                    HtmlElement descriptionElement = strongDescriptionPreElem.getFirstByXPath(".//a");
                    description = descriptionElement != null ? descriptionElement.asText() : null;
                }
                // extract price
                HtmlSpan spanPriceElem = li.getFirstByXPath(".//span[@class='price']");
                if (spanPriceElem != null) {
                    try {
                        if (spanPriceElem.asText() != null) {
                            price = new BigDecimal(spanPriceElem.asText().replaceAll("\\D+", ""));
                        }
                    } catch (Exception e) {
                        LOG.warn("Not found price for productType=" + ptp.productType);
                    }

                }
                // extract parameters
                if (description != null) {
                    params = ProviderUtils.extractParameters(description);
                }

                products.add(new Product(title, description, presence, price, ptp.productType, params));
            }
        }

        List<Assortment> assortments = new ArrayList<>();
//        products.sort(Comparator.comparing(Product::getDescription));
        assortments.add(new Assortment(AssortmentType.IPHONE.getValue(), null, AssortmentType.IPHONE, products));

//        printAssortments(assortments);

        return new Fetch(LocalDateTime.now(), assortments);
    }


    private List<ProductTypePointer> productTypeClassHolder() {
        return Arrays.asList(
                // iPhone
                new ProductTypePointer(ProductType.IPHONE_X, "/telefony/telefony-apple-iphone/iphone-x/"),
                new ProductTypePointer(ProductType.IPHONE_8, "/telefony/telefony-apple-iphone/iphone-8/"),
                new ProductTypePointer(ProductType.IPHONE_8_PLUS, "/telefony/telefony-apple-iphone/iphone-8-plus/"),
                new ProductTypePointer(ProductType.IPHONE_7, "/telefony/telefony-apple-iphone/iphone-7?PAGE_SIZE=100"),
                new ProductTypePointer(ProductType.IPHONE_7_PLUS, "/telefony/telefony-apple-iphone/iphone-7-plus/"),
                new ProductTypePointer(ProductType.IPHONE_6S, "/telefony/telefony-apple-iphone/iphone-6s/"),
                new ProductTypePointer(ProductType.IPHONE_6S_PLUS, "/telefony/telefony-apple-iphone/iphone-6s-plus-/"),
                new ProductTypePointer(ProductType.IPHONE_6, "/telefony/telefony-apple-iphone/iphone-6/"),
                new ProductTypePointer(ProductType.IPHONE_SE, "/telefony/telefony-apple-iphone/iphone-se/"));
    }

    private class ProductTypePointer {
        ProductType productType;
        String urlPath;

        ProductTypePointer(ProductType productType, String urlPath) {
            this.productType = productType;
            this.urlPath = urlPath;
        }

    }

    /**
     * Run
     */
//    public static void main(String[] args) {
//        GsmStoreProvider gsmStoreProvider = new GsmStoreProvider();
//        gsmStoreProvider.screening();
//    }

}
