package ru.proshik.applepricebot.service.provider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.utils.ProviderUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.proshik.applepricebot.utils.ProviderUtils.extractParameters;

public class AjScreeningProvider implements ScreeningProvider {

    private static final Logger LOG = Logger.getLogger(AjScreeningProvider.class);

    private static class ProductTypeInfo {
        ProductType productType;
        String xPathToItems;
        String titleRegExp;

        ProductTypeInfo(ProductType productType, String xPathToItems, String titleRegExp) {
            this.productType = productType;
            this.xPathToItems = xPathToItems;
            this.titleRegExp = titleRegExp;
        }
    }

    private static List<ProductTypeInfo> PRODUCT_TYPE_INFOS = new ArrayList<>();

    static {
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_SE, "//li[@id='iphone5se']/article[contains(@class, 'iphone5se')]/ul/li", "iPhone SE.*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_6S, "//li[@id='iphone'][7]/article[contains(@class, 'iphone6s')]/ul/li", "iPhone 6s.*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_7, "//li[@id='iphone'][6]/article[contains(@class, 'iphone7')]/ul/li", "iPhone 7(?!.*Plus).*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_7_PLUS, "//li[@id='iphone'][6]/article[contains(@class, 'iphone7')]/ul/li", "iPhone 7 Plus.*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_8, "//li[@id='iphone'][5]/article[contains(@class, 'iphone8')]/ul/li", "iPhone 8(?!.*Plus).*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_8_PLUS, "//li[@id='iphone'][5]/article[contains(@class, 'iphone8')]/ul/li", "iPhone 8 Plus.*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_X, "//li[@id='iphone'][3]/article[contains(@class, 'iphoneX')]/ul/li", "iPhone X.*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_XS, "//li[@id='iphone']/article[contains(@class, 'iphoneXS')]/ul/li", "iPhone XS(?!.*Max).*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_XS_MAX, "//li[@id='iphone']/article[contains(@class, 'iphoneXS')]/ul/li", "iPhone XS Max.*"));
        PRODUCT_TYPE_INFOS.add(new ProductTypeInfo(ProductType.IPHONE_XR, "//li[@id='iphone'][2]/article[contains(@class, 'iphoneXR')]/ul/li", "iPhone XR.*"));
    }

    @Override
    public List<Product> screening(Provider provider) throws ProviderParseException {
        LOG.info("ScreeningProvider has started for " + provider.getTitle());

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setUseInsecureSSL(true);

        HtmlPage page;
        try {
            page = client.getPage(provider.getUrl());
        } catch (IOException e) {
            throw new ProviderParseException("Error on get page from aj.ru", e);
        }

        List<Product> products = new ArrayList<>();
        for (ProductTypeInfo pti : PRODUCT_TYPE_INFOS) {
            Pattern p = Pattern.compile(pti.titleRegExp);

            List<HtmlElement> elements = page.getByXPath(pti.xPathToItems);
            String description = elements.get(0).asText();
            for (HtmlElement element : elements.stream().skip(1).collect(Collectors.toList())) {
                // if line is null or empty then skip that element
                if (StringUtils.isEmpty(element.asText().trim())) {
                    continue;
                }

                String title = element.getFirstChild()
                        .getNodeValue().substring(0, element.getFirstChild().getNodeValue().length() - 3);
                // if title not match to regExp the skip elemtn
                if (!p.matcher(title).matches()) {
                    // if this element not equals title from holder
                    continue;
                }

                // try to find a price
                List<HtmlSpan> spans = element.getByXPath("./span");
                BigDecimal price = null;
                for (HtmlSpan span : spans) {
                    try {
                        price = new BigDecimal(span.asText().replace(" ", ""));
                    } catch (Exception e) {
                        LOG.debug("price not found for type=" + pti.productType + " and xPath=" + pti.xPathToItems);
                    }
                }

                // build price object
                Product product = Product.builder()
                        .title(title)
                        .description(description)
                        .price(price)
                        .productType(pti.productType)
                        .parameters(ProviderUtils.paramsToString(extractParameters(element.asText())))
                        .build();
                products.add(product);
            }
        }

        LOG.info("ScreeningProvider has ended for " + provider.getTitle());

        return products;
    }

    /**
     * Run
     */
//    public static void main(String[] args) throws ProviderParseException {
//        AjScreeningProvider apP = new AjScreeningProvider();
//        List<Product> screening = apP.screening(new Provider(null, null, "aj.ru", "https://aj.ru", true, AJ));
//
//        System.out.println(screening);
//    }

}