package ru.proshik.applepriceparcer;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {
        Application app = new Application();

        app.printAjPrices();
    }

    private void printAjPrices() {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page;
        try {
            page = client.getPage("http://aj.ru");
        } catch (IOException e) {
            System.out.println("ERROR on extract page from aj.ru");
            e.printStackTrace();
            return;
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

            if (h2.getChildNodes().size() == 1) {
                System.out.println(h2.asText());
            } else {
                DomNodeList<DomNode> childNodes = h2.getChildNodes();
                String text = childNodes.stream()
                        .map(DomNode::asText)
                        .filter(value -> !value.equals("\r\n"))
                        .collect(Collectors.joining(" "));
                System.out.println(text);
            }

            HtmlElement ul = article.getFirstByXPath("./ul");
            if (ul != null) {
                Iterable<DomElement> insideLi = ul.getChildElements();
                for (DomElement ili : insideLi) {
                    List<HtmlSpan> spans = ili.getByXPath("./span");
                    if (spans.isEmpty()) {
                        if (ili.getFirstChild() != null && ili.getFirstChild().getNodeValue() != null) {
                            System.out.println(ili.getFirstChild().asText());
                        }
                        continue;
                    }

                    if (ili.getFirstChild().getNodeValue() != null) {
                        StringBuilder value = new StringBuilder(ili.getFirstChild().getNodeValue());
                        for (HtmlSpan span : spans) {
                            if (span != null) {
                                value.append(" ").append(span.asText());
                            } else {
                                if (ili.getFirstChild() != null && ili.getFirstChild().getNodeValue() != null) {
                                    value.append(" ").append(ili.getFirstChild().asText());
                                }
                            }
                        }
                        System.out.println(value);
                    } else {
                        // TODO: 25.12.2017 тут обработать "MacBook 12", проверяя дочерние через  ili.getChildNodes()
                        continue;
                    }

                }
                System.out.println();
            }
        }

    }

    class Assortment {
        private String title;
        private String description;
        private List<Item> items;

        public Assortment(String title, String description, List<Item> items) {
            this.title = title;
            this.description = description;
            this.items = items;
        }


    }

    class Item {
        private String title;
        private BigDecimal price;

        public Item(String title, BigDecimal price) {
            this.title = title;
            this.price = price;
        }

        public String getTitle() {
            return title;
        }

        public BigDecimal getPrice() {
            return price;
        }
    }

}
