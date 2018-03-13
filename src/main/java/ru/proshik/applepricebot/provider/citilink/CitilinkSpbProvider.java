package ru.proshik.applepricebot.provider.citilink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.provider.Provider;
import ru.proshik.applepricebot.storage.model.AssortmentType;
import ru.proshik.applepricebot.storage.model.Fetch;
import ru.proshik.applepricebot.storage.model.Product;
import ru.proshik.applepricebot.storage.model.ProductType;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.proshik.applepricebot.utils.ProviderUtils.extractGBSolid;
import static ru.proshik.applepricebot.utils.ProviderUtils.groupExtractor;

public class CitilinkSpbProvider implements Provider {

    private static final Logger LOG = Logger.getLogger(CitilinkSpbProvider.class);

    public static final String URL = "http://citilink.ru";
    public static final String TITLE = "CitilinkSpb";
    private ObjectMapper objectMapper = new ObjectMapper();

    private WebClient client = new WebClient();

    public CitilinkSpbProvider() {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.addRequestHeader("_space", "spb_cl%3A");
    }

    @Override
    public Fetch screening() throws ProviderParseException {
        LOG.info("Screening has started for " + this.TITLE);

        String queryPath = "/search/?menu_id=100008&text=%s&available=1";
        List<Product> products = new ArrayList<>();

        for (ProductTypePointer productTypePointer : productTypeClassHolder()) {

            HtmlPage page = openPageByQueryText(queryPath, productTypePointer.queryText);
            List<HtmlElement> data = page.getByXPath("//*[@id=\"subcategoryList\"]/div/div/div[*]");
            for (HtmlElement item : data) {
                String info = item.getAttribute("data-params");
                if (info == null || info.equals("")) {
                    continue;
                }
                try {
                    CitilinkProductObject object = objectMapper.readValue(info, CitilinkProductObject.class);
                    ArrayList<String> productData = groupExtractor(object.getShortName(),productTypePointer.liTitleRegexp);
                    if (productData.size()==4 || !object.getPrice().equals(null))
                    {
                        String title = String.format("%s %s", productData.get(3), productData.get(2) );
                        String description = productData.get(0);
                        String paramsData = productData.get(2);
                        Map<String, String> params = extractGBSolid(paramsData);
                        products.add(new Product(title, description, Boolean.TRUE, new BigDecimal(object.getPrice()), productTypePointer.assortmentType, productTypePointer.productType, params));
                    }
                    else {
                        LOG.error("wrong extract data for " + object.getShortName());
                    }
                } catch (IOException e) {
                    LOG.error(String.format(this.URL+queryPath,productTypePointer.queryText));
                    e.printStackTrace();
                }

            }
        }
       LOG.info("Screening has ended for " + TITLE);
       return new Fetch(LocalDateTime.now(), products);
    }

    private HtmlPage openPageByQueryText(String queryPath, String queryText)
    {
        HtmlPage page;
        try {
            page = client.getPage( String.format(this.URL+queryPath,queryText));
        }
        catch (IOException e) {
            LOG.error(this.URL);
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
                new ProductTypePointer(AssortmentType.IPHONE, ProductType.IPHONE_SE, "iphone%20SE", "(iPhone SE).(\\d+Gb),(.*),(.*)")
        );
    }

    private class ProductTypePointer {
        AssortmentType assortmentType;
        ProductType productType;
        String queryText;
        String liTitleRegexp;

        ProductTypePointer(AssortmentType assortmentType, ProductType productType, String queryText,
                           String liTitleRegexp) {
            this.assortmentType = assortmentType;
            this.productType = productType;
            this.queryText = queryText;
            this.liTitleRegexp = liTitleRegexp;
        }
    }

    /**
     * Fun
     */
    public static void main(String[] args) throws ProviderParseException {
        CitilinkSpbProvider citilinkSpbProvider = new CitilinkSpbProvider();
        Fetch screening = citilinkSpbProvider.screening();
    }

}