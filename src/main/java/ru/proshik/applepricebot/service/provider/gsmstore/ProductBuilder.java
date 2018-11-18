package ru.proshik.applepricebot.service.provider.gsmstore;

import com.gargoylesoftware.htmlunit.html.*;
import org.apache.log4j.Logger;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.utils.ProviderUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ProductBuilder {

    private static final Logger LOG = Logger.getLogger(ProductBuilder.class);

    private static final String IN_STOCK = "В наличии";
    private static final String OUT_STOCK = "Товар закончился";
    private static final String WAIT_STOCK = " Товар ожидается";

    private static Pattern TITLE_PATTERN = Pattern.compile(".*(gb|Gb|GB)");

    List<Product> build(HtmlPage page, ProductTypePointer ptp) {
        List<Product> products = new ArrayList<>();

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
                        title = ptp.getProductType().getValue();
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
                    LOG.warn("Not found price for productType=" + ptp.getProductType(), e);
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
                    .productType(ptp.getProductType())
                    .parameters(ProviderUtils.paramsToString(params))
                    .build();

            products.add(product);
        }

        return products;
    }
}
