package no.nav.sbl.dialogarena.minehenvendelser.utils;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;

public class URLParsingMultiLineLabel extends MultiLineLabel {

    public URLParsingMultiLineLabel(String id) {
        super(id);
    }

    public URLParsingMultiLineLabel(String id, String label) {
        super(id, label);
    }

    public URLParsingMultiLineLabel(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        setDefaultModelObject(urlToLinkTags(getDefaultModelObjectAsString()));
        super.onComponentTagBody(markupStream, openTag);
    }

    public static String urlToLinkTags(String text) {
        return text.replaceAll("(?:(?:ht|f)tp(?:s?)://|~/|/)?(?:\\w+:\\w+@)?(?:(?:[-\\w]+\\.)+(?:com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum|travel|[a-z]{2}))(?::[\\d]{1,5})?(?:(?:(?:/(?:[-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|/)+|\\?|#)?(?:(?:\\?(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=(?:[-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)(?:&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=(?:[-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*(?:#(?:[-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?", "<a href=\"$0\">$0</a>");
    }
}
