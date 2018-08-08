package ru.proshik.applepricebot.service.v2;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.provider.Provider;
import ru.proshik.applepricebot.provider.ProviderFactory;
import ru.proshik.applepricebot.repository.AssortmentRepository;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.ShopType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScreeningService {

    private static final Logger LOG = Logger.getLogger(ScreeningService.class);

    private final ProviderFactory providerFactory;

    private final AssortmentRepository assortmentRepository;

    @Autowired
    public ScreeningService(ProviderFactory providerFactory, AssortmentRepository assortmentRepository) {
        this.providerFactory = providerFactory;
        this.assortmentRepository = assortmentRepository;
    }

    @Transactional
    public List<Assortment> provideProducts(boolean store) {
        Map<ShopType, Provider> providers = providerFactory.providers();

        Map<ShopType, List<Product>> productByShopType = new HashMap<>();
        for (Map.Entry<ShopType, Provider> entry : providers.entrySet()) {
            try {
                if (entry.getKey().isEnabled()) {
                    List<Product> products = entry.getValue().screening();
                    productByShopType.put(entry.getKey(), products);
                }
            } catch (ProviderParseException e) {
                LOG.error("Screening the shop: " + entry.getKey());
            }
        }

        List<Assortment> result = new ArrayList<>();
        for (Map.Entry<ShopType, List<Product>> entry : productByShopType.entrySet()) {
            Assortment assortment = Assortment.builder()
                    .createdDate(ZonedDateTime.now())
                    .fetchDate(LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime())
                    .shopType(entry.getKey())
                    .products(entry.getValue())
                    .build();

            entry.getValue().forEach(product -> product.setAssortment(assortment));

            if (store) {
                assortmentRepository.save(assortment);
            }

            result.add(assortment);
        }

        return result;
    }
}
