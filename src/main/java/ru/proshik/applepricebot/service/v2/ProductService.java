package ru.proshik.applepricebot.service.v2;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.provider.Provider;
import ru.proshik.applepricebot.provider.ProviderFactory;
import ru.proshik.applepricebot.repository.ProductRepository;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ShopType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private static final Logger LOG = Logger.getLogger(ProductService.class);

    @Autowired
    private ProviderFactory providerFactory;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Map<ShopType, List<Product>> provideProducts() {
        Map<ShopType, Provider> providers = providerFactory.providers();

        Map<ShopType, List<Product>> result = new HashMap<>();
        for (Map.Entry<ShopType, Provider> entry : providers.entrySet()) {
            try {
                if (entry.getKey().isEnabled()) {


                    List<Product> products = entry.getValue().screening();
                    result.put(entry.getKey(), products);
                }
            } catch (ProviderParseException e) {
                LOG.error("Screening the shop: " + entry.getKey());
            }
        }

        result.values().stream()
                .flatMap(Collection::stream)
                .forEach(p -> productRepository.save(p));

        return result;
    }
}
