Mininnboks
================

## Forberedelser for å kjøre

* Sett følgende i .npmrc filen din

```
registry=https://repo.adeo.no/repository/npm-public/
strict-ssl=false
url.https://.insteadof=git://
```

* Kjør `maven clean install` for å laste ned alle JS-avhengigheter og bygge JS-modulene (hvis du starter maven i en terminal, må den ha støtte for GIT).

* Alternativt og anbefalt, kan du installere gulp globalt (`npm install gulp -g`) og så kjøre `npm install && gulp` fra /web.

* Hent ned [mininnboks-tekster](http://stash.devillo.no/projects/TEKST/repos/mininnboks-tekster/browse) og følg instruksjonene i README.

## Utvikling

* Under utvikling kjøres `gulp dev` fra web-katalogen (/web). Forandringer i koden vil da automatisk bli bygd inn og lagt i `target` mappen.

## Test

Tester kan kjøres på to måter:

1. `gulp test`
2. `mvn test`, evt. `mvn clean install`

## Tips

* Marker node_modules mappen som ekskludert i IntelliJ. Høyreklikk på mappen, velg "Mark Directory As" og "Excluded".