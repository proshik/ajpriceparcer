package ru.proshik.applepriceparcer;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import java.io.IOException;
import java.util.List;

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

            System.out.println(h2.asText());

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

}
