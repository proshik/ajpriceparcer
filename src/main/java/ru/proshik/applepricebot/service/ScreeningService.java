package ru.proshik.applepricebot.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.AssortmentRepository;
import ru.proshik.applepricebot.repository.ProviderRepository;
import ru.proshik.applepricebot.repository.model.*;
import ru.proshik.applepricebot.service.provider.ScreeningProvider;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScreeningService {

    private static final Logger LOG = Logger.getLogger(ScreeningService.class);

//    private final ProviderResolver providerResolver;

    private final ProviderRepository providerRepository;

    private final AssortmentRepository assortmentRepository;

    @Autowired
    private Map<ProviderType, ScreeningProvider> screeningProviders;

    @Autowired
    public ScreeningService(
//            ProviderResolver providerResolver,
                            AssortmentRepository assortmentRepository,
                            ProviderRepository providerRepository) {
//        this.providerResolver = providerResolver;
        this.assortmentRepository = assortmentRepository;
        this.providerRepository = providerRepository;
    }

    @Transactional
    public List<Assortment> provideProducts(FetchType fetchType, boolean store) {
        List<Provider> providers = providerRepository.findAll();

        Map<Long, Provider> byProviderId = providers.stream()
                .collect(Collectors.toMap(Provider::getId, o -> o));

        Map<Long, List<Product>> productByShopType = new HashMap<>();
        for (Provider provider : providers) {
            try {
                if (provider.isEnabled()) {
                    ScreeningProvider screeningProvider = screeningProviders.get(provider.getType());

                    List<Product> screeningResult = screeningProvider.screening(provider);

                    productByShopType.put(provider.getId(), screeningResult);
                }
            } catch (ProviderParseException e) {
                LOG.error("ScreeningProvider the provider with id: " + provider.getId(), e);
            }
        }

        List<Assortment> result = new ArrayList<>();
        for (Map.Entry<Long, List<Product>> entry : productByShopType.entrySet()) {
            Assortment assortment = Assortment.builder()
                    .createdDate(ZonedDateTime.now())
                    .fetchType(fetchType)
                    .fetchDate(LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime())
                    .provider(byProviderId.get(entry.getKey()))
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
