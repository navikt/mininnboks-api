package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain;


import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import org.apache.commons.collections15.Transformer;


public final class MHSak {

    public enum SaksStatus { PAABEGYNT, UNDER_ARBEID }

    private String header;
    private String beskrivelse;
    private SaksStatus saksStatus;

    private MHSak() {}

    public static MHSak transformToIPSak(Sak sak) {
        return ipSakTransformer.transform(sak);
    }

    public String getHeader() {
        return header;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public SaksStatus getSaksStatus() {
        return saksStatus;
    }

    private static Transformer<Sak, MHSak> ipSakTransformer = new Transformer<Sak, MHSak>() {

        @Override
        public MHSak transform(Sak sak) {
            MHSak mhsak = new MHSak();
            mhsak.beskrivelse = sak.getTema().getKodeRef();
            mhsak.header = sak.getSaksId();
            mhsak.saksStatus = evaluateStatus(sak);
            return mhsak;
        }

    };

    private static SaksStatus evaluateStatus(Sak sak) {
        return SaksStatus.PAABEGYNT;
    }

}
