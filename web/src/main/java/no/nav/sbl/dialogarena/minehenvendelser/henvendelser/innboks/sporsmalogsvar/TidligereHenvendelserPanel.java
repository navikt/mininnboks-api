package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar;


import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.MeldingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Meldingstype.SPORSMAL;

public class TidligereHenvendelserPanel extends Panel {
    public TidligereHenvendelserPanel(String id) {
        super(id);
        add(new PropertyListView<MeldingVM>("tidligereHenvendelser") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                item.add(new Label("dato-og-avsender",
                        new AbstractReadOnlyModel<String>() {
                            @Override
                            public String getObject() {
                                MeldingVM meldingVM = item.getModelObject();
                                String dato = DateTimeFormat.forPattern("dd.MM.yyyy, HH:mm:ss")
                                        .withLocale(Locale.getDefault())
                                        .print(meldingVM.melding.opprettet);
                                String avsender = meldingVM.avType(SPORSMAL).getObject() ? "sendte du" : "sendte NAV";

                                return dato + " " + avsender;
                            }
                        }));
                item.add(new Label("melding.overskrift"));
                item.add(new Label("melding.fritekst"));
            }
        });
    }


}
