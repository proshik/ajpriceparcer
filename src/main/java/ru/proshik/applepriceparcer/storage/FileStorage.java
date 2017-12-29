package ru.proshik.applepriceparcer.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import ru.proshik.applepriceparcer.exception.SystemErrorException;
import ru.proshik.applepriceparcer.model.goods.AjAssortment;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileStorage {

    private static final String HOME_DIR = System.getProperty("user.home");

    private ObjectMapper mapper;

    private Path path = Paths.get(HOME_DIR, ".app/aj.json");

    public FileStorage() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    }

    public void save(AjAssortment ajAssortment) {
        checkOnExistFile();

        List<AjAssortment> existsAssortment = getExistsAssortment();

        existsAssortment.add(ajAssortment);

        String result;
        try {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(existsAssortment);
        } catch (JsonProcessingException e) {
            throw new SystemErrorException("Error read save file to UserHome directory");
        }

        try {
            ;
            Files.write(path, new String(result.getBytes(), Charset.forName("UTF-8")).getBytes());
        } catch (IOException e) {
            throw new SystemErrorException("Error read save file to UserHome directory");
        }
    }


    public List<AjAssortment> read() {
        checkOnExistFile();

        return getExistsAssortment();
    }

    private List<AjAssortment> getExistsAssortment() {
        List<AjAssortment> ajAssortments = new ArrayList<>();

        try {
            byte[] bytes = Files.readAllBytes(path);
            if (bytes.length != 0) {
                AjAssortment[] arrays = mapper.readValue(bytes, AjAssortment[].class);
                ajAssortments.addAll(Arrays.asList(arrays));
            }
        } catch (IOException e) {
            throw new SystemErrorException("Error read save file to UserHome directory");
        }

        return ajAssortments;
    }

    private void checkOnExistFile() {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                throw new SystemErrorException("Error on crate file in UserHome directory", e);
            }
        }
    }

}
