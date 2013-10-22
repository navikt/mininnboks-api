package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.consumer.Person;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.person.service.PersonService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.security.Brukerkontekst;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;

public class SamtykkePanel extends Panel {

    @Inject
    PersonService personService;

    @Inject
    Brukerkontekst brukerkontekst;

    public SamtykkePanel(String id, final SideNavigerer sideNavigerer) {
        super(id);

        Person person = personService.hentPerson(brukerkontekst.getBrukerId());

        add(new AjaxLink("samtykker") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                sideNavigerer.neste();
                target.add(SamtykkePanel.this.getParent());
            }
        });

        add(new BookmarkablePageLink<>("avbryt", Innboks.class));
    }
}
