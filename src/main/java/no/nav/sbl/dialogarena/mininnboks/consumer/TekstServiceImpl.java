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
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = contextClassLoader.getResources("tekster/mininnboks");
//        Enumeration<URL> resources2 = contextClassLoader.getResources("tekster/mininnboks/svar_nb.txt");

        Collections.list(resources).stream().map(URL::getFile).map(File::new).forEach(this::lastTekster);

        Collections.list(resources).forEach(r -> {
            System.out.println(r);
        });
    }

    @SneakyThrows
    private void lastTekster(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(this::lastTekster);
        } else {
            String name = file.getName();
            Pattern compile = Pattern.compile("(.*)_nb.(txt|html)");
            Matcher matcher = compile.matcher(name);
            if (!matcher.find()) {
                throw new IllegalStateException(name);
            }
            String substring = matcher.group(1);
            tekster.put(substring, FileUtils.readFileToString(file, "UTF-8").trim());
        }
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
