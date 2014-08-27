package no.nav.sbl.dialogarena.mininnboks.security;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;

import javax.inject.Inject;

import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;


public class Authorization {
    public static String DISCRETION_CODE = "urn:nav:ikt:tilgangskontroll:xacml:resource:discretion-code";
    public static String ACTION_INNSENDING = "innsending";

    @Inject
    private EnforcementPoint pep;

    public boolean harTilgangTilInnsending(Integer diskresjonskode) {
        PolicyRequest policyRequest = forRequest(resourceId(SubjectHandler.getSubjectHandler().getUid()),
                actionId(ACTION_INNSENDING),
                resourceAttribute(Authorization.DISCRETION_CODE, "" + diskresjonskode));
        try {
            pep.assertAccess(policyRequest);
            return true;
        } catch (AuthorizationException ex) {
            return false;
        }
    }
}
