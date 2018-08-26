package ru.proshik.applepricebot.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.proshik.applepricebot.exception.ProviderParseException;
import ru.proshik.applepricebot.repository.AssortmentRepository;
import ru.proshik.applepricebot.repository.ProviderRepository;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.FetchType;
import ru.proshik.applepricebot.repository.model.Product;
import ru.proshik.applepricebot.repository.model.Provider;
import ru.proshik.applepricebot.service.provider.ProviderResolver;
import ru.proshik.applepricebot.service.provider.Screening;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScreeningService {

    private static final Logger LOG = Logger.getLogger(ScreeningService.class);

    private final ProviderResolver providerResolver;

    private final ProviderRepository providerRepository;

    private final AssortmentRepository assortmentRepository;

    @Autowired
    public ScreeningService(ProviderResolver providerResolver,
                            AssortmentRepository assortmentRepository,
                            ProviderRepository providerRepository) {
        this.providerResolver = providerResolver;
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
                    Screening screening = providerResolver.resolve(provider);

                    List<Product> screeningResult = screening.screening(provider);

                    productByShopType.put(provider.getId(), screeningResult);
                }
            } catch (ProviderParseException e) {
                LOG.error("Screening the provider with id: " + provider.getId());
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
