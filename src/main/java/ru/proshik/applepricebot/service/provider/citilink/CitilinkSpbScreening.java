package ru.proshik.applepricebot.service.provider.citilink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.service.provider.Screening;
import ru.proshik.applepricebot.utils.ProviderUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.proshik.applepricebot.utils.ProviderUtils.extractGBSolid;
import static ru.proshik.applepricebot.utils.ProviderUtils.groupExtractor;


public class CitilinkSpbScreening implements Screening {

    private static final Logger LOG = Logger.getLogger(CitilinkSpbScreening.class);

    private WebClient client = new WebClient();

    private ObjectMapper objectMapper = new ObjectMapper();

    public CitilinkSpbScreening() {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.addRequestHeader("_space", "spb_cl%3A");
    }

    @Override
    public List<Product> screening(Provider provider) {
        LOG.info("Screening has started for " + provider.getTitle());

        String queryPath = "/search/?menu_id=100008&text=%s&available=1";

        LocalDateTime fetchTime = LocalDateTime.now();

        List<Product> products = new ArrayList<>();

        for (ProductTypePointer productTypePointer : productTypeClassHolder()) {
            HtmlPage page = openPageByQueryText(provider.getUrl(), queryPath, productTypePointer.queryText);

            List<HtmlElement> data = page.getByXPath("//*[@id=\"subcategoryList\"]/div/div/div[*]");
            for (HtmlElement item : data) {
                String info = item.getAttribute("data-params");
                if (info == null || info.equals("")) {
                    continue;
                }
                try {
                    CitilinkProduct object = objectMapper.readValue(info, CitilinkProduct.class);
                    ArrayList<String> productData = groupExtractor(object.getShortName(), productTypePointer.liTitleRegexp);

                    String title = String.format("%s %s", productData.get(3), productData.get(2));
                    String description = productData.get(0);
                    String paramsData = productData.get(2);
                    Map<String, String> params = extractGBSolid(paramsData);

                    Product product = Product.builder()
                            .title(title)
                            .description(description)
                            .price(new BigDecimal(object.getPrice()))
                            .productType(productTypePointer.productType)
                            .parameters(ProviderUtils.paramsToString(params))
                            .build();

                    products.add(product);
                } catch (IOException e) {
                    LOG.error(String.format(provider.getUrl() + queryPath, productTypePointer.queryText));
                    e.printStackTrace();
                }

            }
        }
        LOG.info("Screening has ended for " + provider.getTitle());

        return products;
    }

    private HtmlPage openPageByQueryText(String url, String queryPath, String queryText) {
        HtmlPage page;
        try {
            page = client.getPage(String.format(url + queryPath, queryText));
        } catch (IOException e) {
            LOG.error(url);
            e.printStackTrace();
            return null;
        }
        return page;
    }

    private List<ProductTypePointer> productTypeClassHolder() {
        return Arrays.asList(
                // iPhone
//              new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_X, "iphoneX", "iPhone X.*"),
//                new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_8, "iphone8", "iPhone 8(?!.*Plus).*"),
//                new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_8_PLUS, "iphone8", "iPhone 8 Plus.*"),
//                new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_7, "iphone7", "iPhone 7(?!.*Plus).*"),
//                new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_7_PLUS, "iphone7", "iPhone 7 Plus.*"),
//                new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_7, "iphone7red", "iPhone 7.*"),
//                new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_6S, "iphone6s", "iPhone 6s.*"),
//                new ProductTypePointer(AssortmentType.IPHONE, ProductTypes.IPHONE_6, "iphone6", "iPhone 6.*"),
                new ProductTypePointer(ProductType.IPHONE_SE, "iphone%20SE", "(iPhone SE).(\\d+Gb),(.*),(.*)")
        );
    }

    private class ProductTypePointer {

        ProductType productType;
        String queryText;
        String liTitleRegexp;

        ProductTypePointer(ProductType productType, String queryText, String liTitleRegexp) {
            this.productType = productType;
            this.queryText = queryText;
            this.liTitleRegexp = liTitleRegexp;
        }
    }

    /**
     * Fun
     */
//    public static void main(String[] args) {
//        CitilinkSpbScreening citilinkSpbProvider = new CitilinkSpbScreening();
//        citilinkSpbProvider.screening();
//    }

}