package no.nav.sbl.dialogarena.minehenvendelser.components;

import org.apache.wicket.model.CompoundPropertyModel;

public class SporsmalOgSvarModel extends CompoundPropertyModel<SporsmalOgSvarVM> {

    public SporsmalOgSvarModel(SporsmalOgSvarVM sos) {
        super(sos);
    }

    public void setTema(String tema) {
        SporsmalOgSvarVM sos = getObject();
        sos.tema = tema;
        setObject(sos);
    }

    public void setFritekst(String fritekst) {
        SporsmalOgSvarVM sos = getObject();
        sos.fritekst = fritekst;
        setObject(sos);
    }
}
