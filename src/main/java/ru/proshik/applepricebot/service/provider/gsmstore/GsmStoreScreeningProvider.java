package ru.proshik.applepricebot.service.provider.gsmstore;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.service.provider.HtmlPageProvider;
import ru.proshik.applepricebot.service.provider.ScreeningProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Component
@Qualifier("gsmStoreScreeningProvider")
public class GsmStoreScreeningProvider implements ScreeningProvider {

    private static final Logger LOG = Logger.getLogger(GsmStoreScreeningProvider.class);

    private final HtmlPageProvider htmlPageProvider;

    private final ProductBuilder productBuilder = new ProductBuilder();

    @Autowired
    public GsmStoreScreeningProvider(HtmlPageProvider htmlPageProvider) {
        this.htmlPageProvider = htmlPageProvider;
    }

    @Override
    public List<Product> screening(Provider provider) {
        return screening(provider, Arrays.asList(ProductType.values()));
    }

    @Override
    public List<Product> screening(Provider provider, ProductType productType) {
        return screening(provider, List.of(productType));
    }

    @Override
    public List<Product> screening(Provider provider, List<ProductType> productTypes) {
        LOG.info("ScreeningProvider has started for " + provider.getTitle());

        Map<ProductType, List<ProductTypePointer>> groupByProductType = getProductTypePointers().stream()
                .collect(Collectors.groupingBy(ProductTypePointer::getProductType, mapping(o -> o, toList())));

        List<ProductTypePointer> selectedProductPointers = productTypes.stream()
                .flatMap(productType -> Optional.ofNullable(groupByProductType.get(productType)).stream()
                        .flatMap(Collection::stream))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Pair<HtmlPage, ProductTypePointer>> preparedData = new ArrayList<>();
        for (ProductTypePointer ptp : selectedProductPointers) {
            try {
                HtmlPage page = htmlPageProvider.provide(provider.getUrl() + ptp.getUrlPath());

                preparedData.add(new ImmutablePair<>(page, ptp));
            } catch (ProviderParseException e) {
                LOG.error("Error on get page from gsm-store for url" + provider.getUrl() + ptp.getUrlPath(), e);
            }
        }

        List<Product> products = new ArrayList<>();
        for (Pair<HtmlPage, ProductTypePointer> element : preparedData) {
            products.addAll(productBuilder.build(element.getLeft(), element.getRight()));
        }

        LOG.debug("ScreeningProvider has ended for " + provider.getTitle());

        return products;
    }

    private List<ProductTypePointer> getProductTypePointers() {
        return Arrays.asList(
                // iPhone
                new ProductTypePointer(ProductType.IPHONE_X, "/telefony/telefony-apple-iphone/iphone-x/"),
                new ProductTypePointer(ProductType.IPHONE_8, "/telefony/telefony-apple-iphone/iphone-8/"),
                new ProductTypePointer(ProductType.IPHONE_8_PLUS, "/telefony/telefony-apple-iphone/iphone-8-plus/"),
                new ProductTypePointer(ProductType.IPHONE_7, "/telefony/telefony-apple-iphone/iphone-7?PAGE_SIZE=100"),
                new ProductTypePointer(ProductType.IPHONE_7_PLUS, "/telefony/telefony-apple-iphone/iphone-7-plus/"),
                new ProductTypePointer(ProductType.IPHONE_6S, "/telefony/telefony-apple-iphone/iphone-6s/"),
                new ProductTypePointer(ProductType.IPHONE_6S_PLUS, "/telefony/telefony-apple-iphone/iphone-6s-plus-/"),
                new ProductTypePointer(ProductType.IPHONE_6, "/telefony/telefony-apple-iphone/iphone-6/"),
                new ProductTypePointer(ProductType.IPHONE_SE, "/telefony/telefony-apple-iphone/iphone-se/"),
                new ProductTypePointer(ProductType.IPHONE_XS, "/telefony/telefony-apple-iphone/iphone-xs/"),
                new ProductTypePointer(ProductType.IPHONE_XS_MAX, "/telefony/telefony-apple-iphone/iphone-xs-max/"),
                new ProductTypePointer(ProductType.IPHONE_XS_MAX, "/telefony/telefony-apple-iphone/iphone-xs-max-dual-sim/"),
                new ProductTypePointer(ProductType.IPHONE_XR, "/telefony/telefony-apple-iphone/iphone-xr-/"),
                new ProductTypePointer(ProductType.IPHONE_11, "/telefony/telefony-apple-iphone/iphone-11/"),
                new ProductTypePointer(ProductType.IPHONE_11_PRO, "/telefony/telefony-apple-iphone/iphone-11-pro/"),
                new ProductTypePointer(ProductType.IPHONE_11_PRO_MAX, "/telefony/telefony-apple-iphone/iphone-11-pro-max/"));
    }

}
