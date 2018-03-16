Mininnboks
================

## Forberedelser for å kjøre

* Installer node.js, finnes på *F:\F2990\Felles Filer\3 Forvaltningsseksjonen\3.4 Kontor for brukerdialog\Portaler og SBL forvaltning\7. Teknisk\Programmer\nodejs*. Ikke benytt nyeste versjon, men hold deg til 0.10.*-versjoner - det bygger iallefall OK  med v0.10.17 og v0.10.26.

* Kjør følgende kommandoer i en terminal, (2a, 2b, 2c, 2d fra [denne confluence siden](http://confluence.adeo.no/display/AURA/Karma)):

```
npm config set https-proxy "https://155.55.60.117:8088"
npm config set proxy "http://155.55.60.117:8088/"
npm config set registry "http://registry.npmjs.org/"
npm config set strict-ssl false
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

##Tips

* Marker node_modules mappen som ekskludert i IntelliJ. Høyreklikk på mappen, velg "Mark Directory As" og "Excluded".