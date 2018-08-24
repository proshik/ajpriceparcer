package ru.proshik.applepricebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.proshik.applepricebot.repository.AssortmentRepository;

@Service
public class AssortmentService {

    @Autowired
    private AssortmentRepository assortmentRepository;



}

