package ru.proshik.applepriceparcer.provider.screener.aj;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.log4j.Logger;
import ru.proshik.applepriceparcer.provider.model.*;
import ru.proshik.applepriceparcer.provider.screener.Screener;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AjScreener implements Screener {

    private static final Logger LOG = Logger.getLogger(AjScreener.class);

    private static final String URL = "http://aj.ru";

    @Override
    public Shop supplier() {
        return new Shop("AJ", URL);
    }

    @Override
    public Assortment screening() {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page;
        try {
            page = client.getPage(URL);
        } catch (IOException e) {
            LOG.error("ERROR on extract page from aj.ru");
            throw new RuntimeException("Error on read page from aj.ru");
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
                        LOG.error("Not found price for item=" + title);
                    }
                }
                String changedTitle = ili.getFirstChild().getNodeValue().substring(0, ili.getFirstChild().getNodeValue().length() - 3);
                items.add(new Item(changedTitle, price));
            }
            products.add(new Product(title, ProductType.IPHONE, description, items));
        }

        return new Assortment(LocalDateTime.now(), products);
    }

    public static String buildAssortmentOut(Assortment assortment) {
        StringBuilder out = new StringBuilder("Date: " + assortment.getCreatedDate().toString() + "\n");

        for (Product p : assortment.getProducts()) {
            out.append("### ").append(p.getTitle()).append(" ###").append("\n");
            for (Item i : p.getItems()) {
                out.append(i.getTitle()).append(" - ").append(i.getPrice()).append("\n");
            }
            out.append("\r\n");
        }

        return out.toString();
    }

}
