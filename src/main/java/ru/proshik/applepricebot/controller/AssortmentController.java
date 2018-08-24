package ru.proshik.applepricebot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.proshik.applepricebot.repository.model.Assortment;
import ru.proshik.applepricebot.repository.model.ProductType;
import ru.proshik.applepricebot.repository.model.ProviderType;
import ru.proshik.applepricebot.service.ScreeningService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/assortment")
public class AssortmentController {

    private final ScreeningService screeningService;

    @Autowired
    public AssortmentController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @GetMapping
    public List<Assortment> assortment(@RequestParam(required = false, defaultValue = "false") boolean store) {
        return screeningService.provideProducts(store);
    }

    // TODO: 10.08.2018
    @GetMapping("filter")
    public List<Assortment> filter(@RequestParam(required = false) ProviderType providerType,
                                   @RequestParam(required = false) ProductType productType) {

        return Collections.emptyList();
    }

}
