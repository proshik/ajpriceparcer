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
import java.util.*;
import java.util.stream.Collectors;

public class AjProvider implements Provider {

    private static final Logger LOG = Logger.getLogger(AjProvider.class);

    private static final String TITLE = "AJ";
    private static final String URL = "http://aj.ru";

    private WebClient client = new WebClient();

    @Override
    public Shop getShop() {
        return new Shop(TITLE, URL);
    }

    @Override
    public Assortment screening() throws ProviderParseException {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page;
        try {
            page = client.getPage(URL);
        } catch (IOException e) {
            throw new ProviderParseException("Error on priceAssortment page from aj.ru", e);
        }

        return screening(page);
    }


    private Assortment screening(HtmlPage page) {
        Map<ProductType, List<Product>> result = new HashMap<>();

        HtmlElement ulContainer = page.getFirstByXPath("//ul[@class='container']");
        Iterable<DomElement> childElements = ulContainer.getChildElements();
        for (DomElement parentElem : childElements) {
            HtmlElement article = parentElem.getFirstByXPath("./article");
            String aClass = article.getAttribute("class");

            ProductType productType = defineProductType(aClass);

            if (productType == null) {
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
                        price = BigDecimal.ZERO;
                        LOG.warn("Not found price for item=" + title);
                    }
                }
                if (ili.getFirstChild().getNodeValue() == null) {
                    continue;
                }
                String changedTitle = ili.getFirstChild().getNodeValue().substring(0, ili.getFirstChild().getNodeValue().length() - 3);
                Map<String, String> params = extractParameters(changedTitle);
//                if (productType == ProductType.IPHONE && params.get("GB") != null) {
//                    changedTitle = changedTitle.replace(params.get("GB"), "").trim();
//                }
                items.add(new Item(changedTitle, price, params));
            }
            result.computeIfAbsent(productType, pt -> new ArrayList<>())
                    .add(new Product(title, description, items));
        }

        return new Assortment(LocalDateTime.now(), result);
    }

    private Map<String, String> extractParameters(String title) {
        Map<String, String> result = new HashMap<>();

        List<String> values = Arrays.asList(title.split(" "));
        for (String v : values) {
            if (v.toUpperCase().contains("GB")) {
                result.put("GB", v);
            }
        }
        return result;
    }

    private ProductType defineProductType(String aClass) {
        String original = aClass.toUpperCase();
        if (original.contains("IPHONE")) {
            return ProductType.IPHONE;
        } else if (original.contains("IPAD")) {
            return ProductType.IPAD;
        } else if (original.contains("IMAC")) {
            return ProductType.IMAC;
        } else if (original.contains("MACBOOK")) {
            return ProductType.MACBOOK_PRO;
        } else if (original.contains("MAC_MINI")) {
            return ProductType.MAC_MINI;
        } else {
            return null;
        }
    }

//    public static void main(String[] args) throws IOException, ProviderParseException {
//        WebClient client = new WebClient();
//        client.getOptions().setCssEnabled(false);
//        client.getOptions().setJavaScriptEnabled(false);
//        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
//
//        HtmlPage page = client.getPage(Paths.get("aj.html").toUri().toURL());
////
////        HtmlPage page;
////        try {
////            page = client.getPage(URL);
////        } catch (IOException e) {
////            throw new ProviderParseException("Error on priceAssortment page from aj.ru", e);
////        }
//
//        AjProvider apP = new AjProvider();
//        Assortment screening = apP.screening(page);
//
//        System.out.println(screening);
//    }

}
