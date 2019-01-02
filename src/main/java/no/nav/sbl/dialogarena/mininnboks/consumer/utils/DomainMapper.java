package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class DomainMapper<S, T> implements Function<S, T> {
    static class Mapper<S, T> {
        final Predicate<S> brukMapper;
        final BiFunction<S, T, T> mapper;
        final Boolean breakAfterMapping;

        public Mapper(Predicate<S> brukMapper, BiFunction<S, T, T> mapper) {
            this(brukMapper, mapper, false);
        }

        public Mapper(Predicate<S> brukMapper, BiFunction<S, T, T> mapper, Boolean breakAfterMapping) {
            this.brukMapper = brukMapper;
            this.mapper = mapper;
            this.breakAfterMapping = breakAfterMapping;
        }
    }

    private List<Mapper<S, T>> mappers;

    public DomainMapper() {
        this.mappers = new LinkedList<>();
    }

    public void registerMapper(Mapper<S, T> mapper) {
        this.mappers.add(mapper);
    }

    public void registerMapper(Predicate<S> predicate, BiFunction<S, T, T> mapper) {
        this.mappers.add(new Mapper<>(predicate, mapper));
    }

    @Override
    public T apply(S xmlValue) {
        T value = null;

        for (Mapper<S, T> mapper : mappers) {
            if (mapper.brukMapper.test(xmlValue)) {
                value = mapper.mapper.apply(xmlValue, value);
                if (mapper.breakAfterMapping) {
                    break;
                }
            }
        }
        return value;
    }
}
