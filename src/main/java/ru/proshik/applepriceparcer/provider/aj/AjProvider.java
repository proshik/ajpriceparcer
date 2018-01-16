package ru.proshik.applepriceparcer.provider.aj;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.model.*;
import ru.proshik.applepriceparcer.provider.Provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AjProvider implements Provider {

    private static final Logger LOG = Logger.getLogger(AjProvider.class);

    private static final String TITLE = "AJ";
    private static final String URL = "http://aj.ru";

    @Override
    public Shop getShop() {
        return new Shop(TITLE, URL);
    }

    @Override
    public Assortment screening() throws ProviderParseException {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page;
        try {
            page = client.getPage(URL);
        } catch (IOException e) {
            throw new ProviderParseException("Error on priceAssortment page from aj.ru", e);
        }

        HtmlElement ulContainer = page.getFirstByXPath("//ul[@class='container']");

        List<Product> products = new ArrayList<>();

        Iterable<DomElement> childElements = ulContainer.getChildElements();
        for (DomElement parentElem : childElements) {

            HtmlElement article = parentElem.getFirstByXPath("./article");

            // TODO: 11.01.2018 saparate logic by extract data by type
            String aClass = article.getAttribute("class");
            if (!aClass.contains("iphone")) {
                continue;
            }

            HtmlElement h2 = article.getFirstByXPath("./h2");

            if (h2 == null) {
                //pass items with struct without h2(header of goods)
                continue;
            }

            String title;
            String description = null;

            if (h2.getChildNodes().size() == 1) {
                title = h2.asText();
            } else {
                DomNodeList<DomNode> childNodes = h2.getChildNodes();
                title = childNodes.stream()
                        .map(DomNode::asText)
                        .filter(value -> !value.equals("\r\n"))
                        .collect(Collectors.joining(" "));
            }

            List<Item> items = new ArrayList<>();

            Iterable<DomElement> insideLi = ((HtmlElement) article.getFirstByXPath("./ul")).getChildElements();
            for (DomElement ili : insideLi) {
                List<HtmlSpan> spans = ili.getByXPath("./span");
                if (spans.isEmpty()) {
                    if (ili.getFirstChild() != null && ili.getFirstChild().getNodeValue() != null) {
                        description = ili.getFirstChild().asText();
                    }
                    continue;
                }

                BigDecimal price = null;
                for (HtmlSpan span : spans) {
                    try {
                        price = new BigDecimal(span.asText().replace(" ", ""));
                    } catch (Exception e) {
                        LOG.warn("Not found price for item=" + title);
                    }
                }
                String changedTitle = ili.getFirstChild().getNodeValue().substring(0, ili.getFirstChild().getNodeValue().length() - 3);
                items.add(new Item(changedTitle, price));
            }
            products.add(new Product(title, description, items));
        }

        return new Assortment(LocalDateTime.now(), Map.of(ProductType.IPHONE, products));
    }


}
