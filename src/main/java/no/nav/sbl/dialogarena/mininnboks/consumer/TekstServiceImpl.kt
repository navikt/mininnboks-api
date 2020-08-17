package no.nav.sbl.dialogarena.mininnboks.consumer;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

public class TekstServiceImpl implements TekstService {

    private final Map<String, String> tekster = new HashMap<>();

    public TekstServiceImpl() {
        lastTekster();
    }

    @SneakyThrows
    private void lastTekster() {
        Enumeration<URL> resources = TekstServiceImpl.class.getClassLoader().getResources("tekster/mininnboks");
        Collections.list(resources).stream().map(URL::getFile).map(File::new).forEach(this::lastTekster);
    }

    @SneakyThrows
    private void lastTekster(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(this::lastTekster);
        } else {
            tekster.put(finnKey(file), FileUtils.readFileToString(file, "UTF-8").trim());
        }
    }

    private String finnKey(File tekstFil) {
        String fileName = tekstFil.getName();
        Matcher matcher = Pattern.compile("(.*)_nb.(txt|html)").matcher(fileName);
        if (!matcher.find()) {
            throw new IllegalStateException(fileName);
        }
        return matcher.group(1);
    }

    @Override
    public String hentTekst(String key) {
        return ofNullable(tekster.get(key)).orElseThrow(() -> new MissingResourceException("mangler tekst for key=" + key, key, key));
    }

    @Override
    public Map<String, String> hentTekster() {
        return tekster;
    }
}
