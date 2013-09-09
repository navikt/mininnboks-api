package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar;


import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.HenvendelseVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Henvendelsetype.SPORSMAL;

public class TidligereHenvendelserPanel extends Panel {
    public TidligereHenvendelserPanel(String id) {
        super(id);
        add(new PropertyListView<HenvendelseVM>("tidligereHenvendelser") {
            @Override
            protected void populateItem(final ListItem<HenvendelseVM> item) {
                item.add(new Label("dato-og-avsender",
                        new AbstractReadOnlyModel<String>() {
                            @Override
                            public String getObject() {
                                HenvendelseVM henvendelseVM = item.getModelObject();
                                String dato = DateTimeFormat.forPattern("dd.MM.yyyy, HH:mm:ss")
                                        .withLocale(Locale.getDefault())
                                        .print(henvendelseVM.henvendelse.opprettet);
                                String avsender = henvendelseVM.avType(SPORSMAL).getObject() ? "sendte du" : "sendte NAV";

                                return dato + " " + avsender;
                            }
                        }));
                item.add(new Label("henvendelse.overskrift"));
                item.add(new Label("henvendelse.fritekst"));
            }
        });
    }


}
