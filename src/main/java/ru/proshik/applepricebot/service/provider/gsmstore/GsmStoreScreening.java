package ru.proshik.applepricebot.service.provider.gsmstore;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.log4j.Logger;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.service.provider.Screening;
import ru.proshik.applepricebot.utils.ProviderUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GsmStoreScreening implements Screening {

    private static final Logger LOG = Logger.getLogger(GsmStoreScreening.class);

    private static final String IN_STOCK = "В наличии";
    private static final String OUT_STOCK = "Товар закончился";

    private static Pattern TITLE_PATTERN = Pattern.compile(".*Gb");

    private WebClient client = new WebClient();

    public GsmStoreScreening() {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public List<Product> screening(Provider provider) {
        LOG.info("Screening has started for " + provider.getTitle());

        List<Product> products = new ArrayList<>();

        for (ProductTypePointer ptp : productTypeClassHolder()) {
            HtmlPage page;
            try {
                page = client.getPage(provider.getUrl() + ptp.urlPath);
            } catch (IOException e) {
                LOG.error("Error on get page from gsm-store.ru for url" + provider.getUrl() + ptp.urlPath);
                continue;
            }

            List<HtmlElement> byXPath = page.getByXPath("//li[@class='product-block']");
            for (HtmlElement li : byXPath) {

                String title = null;
                String description = null;
                Boolean available = null;
                BigDecimal price = null;
                Map<String, String> params = null;

                // extract presence
                HtmlDivision statusDiv = li.getFirstByXPath(".//div[@class='status-inner']");
                HtmlSpan presenceElem = statusDiv.getFirstByXPath(".//span");
                if (presenceElem != null) {
                    if (presenceElem.asText().equals(IN_STOCK)) {
                        available = true;
                    } else if (presenceElem.asText().equals(OUT_STOCK))
                        available = false;
                }
                // extract description
                HtmlStrong strongDescriptionPreElem = li.getFirstByXPath(".//strong[@class='title']");
                if (strongDescriptionPreElem != null) {
                    HtmlElement descriptionElement = strongDescriptionPreElem.getFirstByXPath(".//a");
                    description = descriptionElement != null ? descriptionElement.asText() : null;
                    if (description != null) {
                        Matcher matcher = TITLE_PATTERN.matcher(description);
                        if (matcher.find()) {
                            String preTitle = matcher.group();
                            description = description.replace(preTitle, "").trim();
                            title = preTitle.replace("Apple", "").trim();
                        } else {
                            title = ptp.productType.getValue();
                        }
                    }
                }
                // extract price
                HtmlSpan spanPriceElem = li.getFirstByXPath(".//span[@class='price']");
                if (spanPriceElem != null) {
                    try {
                        if (spanPriceElem.asText() != null) {
                            price = new BigDecimal(spanPriceElem.asText().replaceAll("\\D+", ""));
                        }
                    } catch (Exception e) {
                        LOG.warn("Not found price for productType=" + ptp.productType, e);
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
                        .available(available)
                        .productType(ptp.productType)
                        .parameters(ProviderUtils.paramsToString(params))
                        .build();

                products.add(product);
            }
        }

        LOG.info("Screening has ended for " + provider.getTitle());

        return products;
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
                new ProductTypePointer(ProductType.IPHONE_SE, "/telefony/telefony-apple-iphone/iphone-se/"),
                new ProductTypePointer(ProductType.IPHONE_XS, "/telefony/telefony-apple-iphone/iphone-xs/"),
                new ProductTypePointer(ProductType.IPHONE_XS_MAX, "/telefony/telefony-apple-iphone/iphone-xs-max/"));
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
//        GsmStoreScreening gsmStoreProvider = new GsmStoreScreening();
//        List<Product> screening = gsmStoreProvider.screening(new Provider(null, null, "gsm-store", "https://gsm-store.ru", true, GSM_STORE));
////        screening.stream().filter(product -> product.getProductType() == ProductType.IPHONE_XS).collect(Collectors.toList());
//        System.out.println(screening);
//    }

}
