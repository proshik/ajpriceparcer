package ru.proshik.applepriceparcer.service.aj;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import ru.proshik.applepriceparcer.model.goods.Assortment;
import ru.proshik.applepriceparcer.model.goods.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AjReader {

    public List<Assortment> printAjPrices() {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page;
        try {
            page = client.getPage("http://aj.ru");
        } catch (IOException e) {
            System.out.println("ERROR on extract page from aj.ru");
            throw new RuntimeException("Error on read page from aj.ru");
        }

        HtmlElement ulContainer = page.getFirstByXPath("//ul[@class='container']");

        List<Assortment> assortment = new ArrayList<>();

        Iterable<DomElement> childElements = ulContainer.getChildElements();
        for (DomElement parentElem : childElements) {

            HtmlElement article = parentElem.getFirstByXPath("./article");

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
                        System.out.println("Not found price for item=" + title);
                    }
                }
                String changedTitle = ili.getFirstChild().getNodeValue().substring(0, ili.getFirstChild().getNodeValue().length()-3);
                items.add(new Item(changedTitle, price));
            }
            assortment.add(new Assortment(title, description, items));
        }

        return assortment;
    }

}
