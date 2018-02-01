package ru.proshik.applepriceparcer.provider2.istorespb;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.apache.log4j.Logger;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;
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

public class IStoreSpbProvider implements Provider {

    private static final Logger LOG = Logger.getLogger(ru.proshik.applepriceparcer.provider2.gsmstore.GsmStoreProvider.class);

    public static final String TITLE = "ISTORESPB.ru";
    public static final String URL = "http://istorespb.ru";

    private WebClient client = new WebClient();

    public IStoreSpbProvider() {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.setCssErrorHandler(new CustomErrorHandler());
    }

    @Override
    public Fetch screening() {
        LOG.info("Screening has started for " + TITLE);

        List<Product> products = new ArrayList<>();
        for (ProductTypePointer ptp : productTypeClassHolder()) {
            HtmlPage page;
            try {
                page = client.getPage(URL + ptp.urlPath);
            } catch (IOException e) {
                LOG.error("Error on get page from istorespb.ru for url" + URL + ptp.urlPath);
                continue;
            }

            List<HtmlElement> byXPath = page.getByXPath("//div[@class='block_product']");
            for (HtmlElement li : byXPath) {

                String title = ptp.productType.getValue();
                String description = null;
                BigDecimal price = null;
                Map<String, String> params = null;

                // extract description
                HtmlElement descriptionElement = li.getFirstByXPath(".//div[@class='name']");
                if (descriptionElement != null) {
                    description = descriptionElement.asText();
                }
                // extract price
                HtmlElement divPriceElem = li.getFirstByXPath(".//div[@class='jshop_price']");
                if (divPriceElem != null) {
                    HtmlSpan spanPriceElem = divPriceElem.getFirstByXPath(".//span");
                    if (spanPriceElem != null) {
                        try {
                            price = new BigDecimal(spanPriceElem.asText().replaceAll("\\D.\\D+", ""));
                        } catch (Exception e) {
                            LOG.warn("Not found price for productType=" + ptp.productType);
                        }

                    }
                }
                // extract parameters
                if (description != null) {
                    params = ProviderUtils.extractParameters(description);
                }

                products.add(new Product(title, description, null, price, ptp.productType, params));
            }
        }

        List<Assortment> assortments = new ArrayList<>();
//        products.sort(Comparator.comparing(Product::getDescription));
        assortments.add(new Assortment(AssortmentType.IPHONE.getValue(), null, AssortmentType.IPHONE, products));

//        printAssortments(assortments);

        LOG.info("Screening has ended for " + TITLE);

        return new Fetch(LocalDateTime.now(), assortments);
    }


    private List<ProductTypePointer> productTypeClassHolder() {
        return Arrays.asList(
                // iPhone
                new ProductTypePointer(ProductType.IPHONE_X, "/iphone-x"),
                new ProductTypePointer(ProductType.IPHONE_8, "/iphone-8"),
                new ProductTypePointer(ProductType.IPHONE_8_PLUS, "/iphone-8-plus"),
                new ProductTypePointer(ProductType.IPHONE_7, "/iphone-7"),
                new ProductTypePointer(ProductType.IPHONE_7_PLUS, "/iphone-7-plus"),
                new ProductTypePointer(ProductType.IPHONE_6S, "/iphone-6-s"),
                new ProductTypePointer(ProductType.IPHONE_6S_PLUS, "/iphone-6s-plus"),
                new ProductTypePointer(ProductType.IPHONE_6, "/iphone-6"),
                new ProductTypePointer(ProductType.IPHONE_SE, "/iphone-se"));
    }

    private class ProductTypePointer {
        ProductType productType;
        String urlPath;

        ProductTypePointer(ProductType productType, String urlPath) {
            this.productType = productType;
            this.urlPath = urlPath;
        }

    }

    private class CustomErrorHandler implements ErrorHandler {

        @Override
        public void warning(CSSParseException e) throws CSSException {

        }

        @Override
        public void error(CSSParseException e) throws CSSException {

        }

        @Override
        public void fatalError(CSSParseException e) throws CSSException {

        }
    }

    /**
     * Run
     */
//    public static void main(String[] args) {
//        IStoreSpbProvider gsmStoreProvider = new IStoreSpbProvider();
//        gsmStoreProvider.screening();
//    }


}
