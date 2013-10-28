package no.nav.sbl.dialogarena.minehenvendelser.sendsporsmal;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.minehenvendelser.innboks.Innboks;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

public class SamtykkePanel extends Panel {

//    @Inject
//    PersonService personService;
//
//    @Inject
//    Brukerkontekst brukerkontekst;

    public SamtykkePanel(String id, final SideNavigerer sideNavigerer) {
        super(id);

//        Person person = personService.hentPerson(brukerkontekst.getBrukerId());

        final ModigModalWindow infotekstModal = new ModigModalWindow("infotekst-modal");
        infotekstModal.setContent(new SamtykkeInfotekstPanel(infotekstModal.getContentId()));
        infotekstModal.setMinimalWidth(600);
        infotekstModal.setResizable(false);
        infotekstModal.setAutoSize(true);
        add(infotekstModal);

        add(new AjaxLink<Void>("les-mer") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                infotekstModal.show(target);
            }
        });

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
