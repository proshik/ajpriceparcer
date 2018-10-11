package ru.proshik.applepricebot.service.provider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.apache.log4j.Logger;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.utils.ProviderUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IStoreSpbScreeningProvider implements ScreeningProvider {

    private static final Logger LOG = Logger.getLogger(GsmStoreScreeningProvider.class);

    private static List<Pattern> TITLE_PATTERNS = Arrays.asList(Pattern.compile(".*GB"), Pattern.compile(".*Gb"));

    private WebClient client = new WebClient();

    public IStoreSpbScreeningProvider() {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.setCssErrorHandler(new CustomErrorHandler());
    }

    @Override
    public List<Product> screening(Provider provider) {
        LOG.info("ScreeningProvider has started for " + provider.getTitle());

        List<Product> products = new ArrayList<>();

        for (ProductTypePointer ptp : productTypeClassHolder()) {
            HtmlPage page;
            try {
                page = client.getPage(provider.getUrl() + ptp.urlPath);
            } catch (IOException e) {
                LOG.error("Error on get page from istorespb.ru for url" + provider.getUrl() + ptp.urlPath);
                continue;
            }

            List<HtmlElement> byXPath = page.getByXPath("//div[@class='block_product']");
            for (HtmlElement li : byXPath) {

                String title = null;
                String description = null;
                BigDecimal price = null;
                Map<String, String> params = null;

                // extract description
                HtmlElement descriptionElement = li.getFirstByXPath(".//div[@class='name']");
                if (descriptionElement != null) {
                    description = descriptionElement.asText();
                    if (description != null) {
                        for (Pattern p : TITLE_PATTERNS) {
                            Matcher matcher = p.matcher(description);
                            if (matcher.find()) {
                                title = matcher.group();
                                description = description.replace(title, "").trim();
                                if (title.contains("Apple")) {
                                    title = title.replace("Apple", "").trim();
                                }
                                break;
                            }
                        }
                        if (title == null) {
                            title = ptp.productType.getValue();
                        }
                    }
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
                if (title != null) {
                    params = ProviderUtils.extractParameters(title);
                }
                Product product = Product.builder()
                                .title(title)
                                .description(description)
                                .price(price)
                                .productType(ptp.productType)
                                .parameters(ProviderUtils.paramsToString(params))
                                .build();

                products.add(product);
            }
        }

        LOG.info("ScreeningProvider ended for " + provider.getTitle());

        return products;
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
//        IStoreSpbScreeningProvider gsmStoreProvider = new IStoreSpbScreeningProvider();
//        gsmStoreProvider.screening();
//    }

}
