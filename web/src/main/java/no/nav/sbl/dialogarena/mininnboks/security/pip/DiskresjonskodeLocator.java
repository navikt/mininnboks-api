package no.nav.sbl.dialogarena.mininnboks.security.pip;

import no.nav.modig.security.tilgangskontroll.policy.request.attributes.SubjectAttribute;
import no.nav.sbl.dialogarena.mininnboks.consumer.DiskresjonskodeService;
import org.apache.commons.lang3.Validate;
import org.jboss.security.xacml.locators.attrib.StorageAttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.attr.StringAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;

import java.net.URI;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.mininnboks.config.ApplicationContext.CONTEXT;

public class DiskresjonskodeLocator extends StorageAttributeLocator {

    private static final URI STRING_TYPE_URI = URI.create("http://www.w3.org/2001/XMLSchema#string");
    private static final URI PERSON_FODELSELSNUMMER = URI.create("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
    private static final URI SUBJECT = URI.create(SubjectAttribute.ACCESS_SUBJECT.getURN());

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId,
                                          URI issuer, URI subjectCategory, EvaluationCtx context,
                                          int designatorType) {
        if (!ids.contains(attributeId)) {
            return new EvaluationResult(BagAttribute.createEmptyBag(attributeId));
        }
        String fNr = getSubstituteValue(attributeType, context);
        return new EvaluationResult(new BagAttribute(attributeType, asList(new StringAttribute(getDiskresjonskode(fNr)))));
    }

    private String getDiskresjonskode(String resourceValue) {
        return CONTEXT.getBean(DiskresjonskodeService.class).getDiskresjonskode(resourceValue);
    }

    @Override
    protected String getSubstituteValue(URI attributeType, EvaluationCtx context) {

        EvaluationResult evalResult = context.getSubjectAttribute(STRING_TYPE_URI, PERSON_FODELSELSNUMMER, SUBJECT);
        String fNr = (String) this.getAttributeValue(evalResult, attributeType);
        Validate.notBlank(fNr);
        return fNr;
    }
}
