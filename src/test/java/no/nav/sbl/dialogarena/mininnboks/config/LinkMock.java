package no.nav.sbl.dialogarena.mininnboks.config;

import static no.nav.sbl.dialogarena.mininnboks.provider.LinkService.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class LinkMock {

    public static void setup() {
        setProperty(MININNBOKS_LINK_PROPERTY,"/", PUBLIC);
        setProperty(SAKSOVERSIKT_LINK_PROPERTY,"/", PUBLIC);
        setProperty(TEMAVELGER_LINK_PROPERTY,"/", PUBLIC);
        setProperty(BRUKERPROFIL_LINK_PROPERTY,"/", PUBLIC);
    }

}

