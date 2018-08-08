package ru.proshik.applepricebot.provider.aj;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.apache.log4j.Logger;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.provider.Provider;
import ru.proshik.applepricebot.storage.model.AssortmentType;
import ru.proshik.applepricebot.storage.model.Product;
import ru.proshik.applepricebot.storage.model.ProductType;
import ru.proshik.applepricebot.utils.ProviderUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.proshik.applepricebot.utils.ProviderUtils.extractParameters;

public class AjProvider implements Provider {

    private static final Logger LOG = Logger.getLogger(AjProvider.class);

    public static final String URL = "http://aj.ru";
    public static final String TITLE = "AJ";

    @Override
    public List<ru.proshik.applepricebot.repository.model.Product> screening() throws ProviderParseException {
        LOG.info("Screening has started for " + TITLE);

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page;
        try {
            page = client.getPage(URL);
        } catch (IOException e) {
            throw new ProviderParseException("Error on priceAssortment page from aj.ru", e);
        }

        List<ru.proshik.applepricebot.repository.model.Product> newProducts = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        Map<AssortmentType, List<ProductTypePointer>> groupByAssortmentType = productTypeClassHolder().stream()
                .collect(Collectors.groupingBy(o -> o.assortmentType));
        for (Map.Entry<AssortmentType, List<ProductTypePointer>> entry : groupByAssortmentType.entrySet()) {

            for (ProductTypePointer ptp : entry.getValue()) {

                Pattern p = Pattern.compile(ptp.liTitleRegexp);

                List<HtmlElement> articles = page.getByXPath("//article[@class='" + ptp.articleClass + "']");
                for (HtmlElement article : articles) {
//                HtmlElement article = page.getFirstByXPath("//article[@class='" + ptp.articleClass + "']");
                    HtmlElement h2 = article.getFirstByXPath("./h2");
                    if (h2 == null) {
                        //pass items with struct without h2(header of goods)
                        continue;
                    }

                    String description = null;
                    for (DomElement ili : ((HtmlElement) article.getFirstByXPath("./ul")).getChildElements()) {
                        List<HtmlSpan> spans = ili.getByXPath("./span");
                        if (spans.isEmpty()) {
                            if (ili.getFirstChild() != null && ili.getFirstChild().getNodeValue() != null) {
                                description = ili.getFirstChild().asText();
                            }
                            // if not a description, then not and info
                            continue;
                        }

                        BigDecimal price = null;
                        for (HtmlSpan span : spans) {
                            try {
                                price = new BigDecimal(span.asText().replace(" ", ""));
                            } catch (Exception e) {
//                            price = BigDecimal.ZERO;
                            }
                        }
                        if (ili.getFirstChild().getNodeValue() == null) {
                            continue;
                        }

                        String title = ili.getFirstChild()
                                .getNodeValue().substring(0, ili.getFirstChild().getNodeValue().length() - 3);
                        if (!p.matcher(title).matches()) {
                            // if this element not equals title from holder
                            continue;
                        }

                        Map<String, String> params = extractParameters(title);

                        if (price == null) {
                            LOG.warn("Not found price for assortmentType=" + ptp.assortmentType
                                    + ", productType=" + ptp.productType);
                        }

                        ru.proshik.applepricebot.repository.model.Product product =
                                ru.proshik.applepricebot.repository.model.Product.builder()
                                        .title(title)
                                        .description(description)
                                        .price(price)
                                        .productType(ptp.productType)
                                        .parameters(ProviderUtils.paramsToString(params))
                                        .build();

                        newProducts.add(product);
                        products.add(new Product(title, description, null, price, ptp.assortmentType, ptp.productType, params));
                    }
                }
            }
        }

//        printAssortments(assortments);

        LOG.info("Screening has ended for " + TITLE);

        return newProducts;
//        return new Fetch(LocalDateTime.now(), assortment);
    }

    private List<ProductTypePointer> productTypeClassHolder() {
        return Arrays.asList(
                // iPhone
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_X, "iphoneX", "iPhone X.*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_8, "iphone8", "iPhone 8(?!.*Plus).*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_8_PLUS, "iphone8", "iPhone 8 Plus.*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_7, "iphone7", "iPhone 7(?!.*Plus).*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_7_PLUS, "iphone7", "iPhone 7 Plus.*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_7, "iphone7red", "iPhone 7.*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_6S, "iphone6s", "iPhone 6s.*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_6, "iphone6", "iPhone 6.*"),
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_SE, "iphone5se", "iPhone SE.*")
                // MacBook Pro
//                new ProductTypePointer(AssortmentType.MACBOOK_PRO, ProductTypes.MACBOOK_PRO_2017, "MacBookPro2017", "13\".*"),
//                new ProductTypePointer(AssortmentType.MACBOOK_PRO, ProductTypes.MACBOOK_PRO_2017, "MacBookPro2017", "15\".*"),
//                new ProductTypePointer(AssortmentType.MACBOOK_PRO, ProductTypes.MACBOOK_PRO_2016, "MacBookPro2016", "15\".*"),
//                // iMac
//                new ProductTypePointer(AssortmentType.IMAC, ProductTypes.IMAC_21_5, "new_imac5K-2017", "21\".*"),
//                new ProductTypePointer(AssortmentType.IMAC, ProductTypes.IMAC_27, "new_imac5K-2017", "27\" Retina 5K(?!.*8-Core).*"),
//                new ProductTypePointer(AssortmentType.IMAC, ProductTypes.IMAC_21_5, "new_imac5K", "21.5\".*"),
//                new ProductTypePointer(AssortmentType.IMAC, ProductTypes.IMAC_27, "new_imac5K", "27\" Retina 5K.*")
        );
    }

    private class ProductTypePointer {
        AssortmentType assortmentType;
        ProductType productType;
        String articleClass;
        String liTitleRegexp;

        ProductTypePointer(AssortmentType assortmentType, ProductType productType, String articleClass,
                           String liTitleRegexp) {
            this.assortmentType = assortmentType;
            this.productType = productType;
            this.articleClass = articleClass;
            this.liTitleRegexp = liTitleRegexp;
        }
    }

    /**
     * Fun
     */
    public static void main(String[] args) throws ProviderParseException {
        AjProvider apP = new AjProvider();
        apP.screening();
    }

/**
 * Run from file
 */
//    public static void main(String[] args) throws IOException, ProviderParseException {
//        WebClient client = new WebClient();
//        client.getOptions().setCssEnabled(false);
//        client.getOptions().setJavaScriptEnabled(false);
//        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
//
//        HtmlPage page = client.getPage(Paths.get("Aj.html").toUri().toURL());
////
////        HtmlPage page;
////        try {
////            page = client.getPage(URL);
////        } catch (IOException e) {
////            throw new ProviderParseException("Error on priceAssortment page from Aj.ru", e);
////        }
//
//        AjProvider apP = new AjProvider();
//        Fetch screening = apP.screening(page);
//
//        System.out.println(screening);
//    }
}