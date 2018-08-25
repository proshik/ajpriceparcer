package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.exception.ProviderNotFoundException;
import ru.proshik.applepricebot.exception.ProviderUpdateEnableException;
import ru.proshik.applepricebot.model.ProviderRestOut;
import ru.proshik.applepricebot.repository.ProviderRepository;
import ru.proshik.applepricebot.repository.model.Provider;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/provider")
public class ProviderController {

    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderController(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @GetMapping
    public List<ProviderRestOut> list() {
        List<Provider> providers = providerRepository.findAll();

        return providers.stream()
                .map(this::transform)
                .sorted(Comparator.comparing(ProviderRestOut::getId))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "{providerId}")
    public ProviderRestOut providerProvider(@PathVariable("providerId") Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found by id=" + providerId));

        return transform(provider);
    }

    @PutMapping(value = "{providerId}")
    public void changeEnableValue(@PathVariable("providerId") Long providerId,
                                  @RequestParam(value = "enabled") boolean enabled) {
        int result = providerRepository.updateEnableValue(providerId, enabled);
        if (result != 1) {
            throw new ProviderUpdateEnableException("Error on update provider with id=" + providerId);
        }
    }

    private ProviderRestOut transform(Provider provider) {
        return ProviderRestOut.builder()
                .id(provider.getId())
                .title(provider.getTitle())
                .providerType(provider.getType())
                .url(provider.getUrl())
                .enabled(provider.isEnabled())
                .build();
    }

}
