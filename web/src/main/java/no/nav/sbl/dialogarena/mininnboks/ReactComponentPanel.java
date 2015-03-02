package no.nav.sbl.dialogarena.mininnboks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.HashMap;
import java.util.Map;

public class ReactComponentPanel extends Panel {

    private String componentName;
    private Map<String, Object> props;
    private ObjectMapper objectMapper = new ObjectMapper();

    private WebMarkupContainer reactContainer;

    public ReactComponentPanel(String id, String componentName) {
        this(id, componentName, new HashMap<String, Object>());
    }

    public ReactComponentPanel(String id, String componentName, Map<String, Object> props) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        this.reactContainer = new WebMarkupContainer("reactContainer");
        this.componentName = componentName;
        this.props = withPanelReference(props);

        add(reactContainer);
    }

    private Map<String, Object> withPanelReference(Map<String, Object> props) {
        props.put("reactContainer", reactContainer.getMarkupId());
        return props;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(onDomReadyHeaderItem());
    }

    private OnDomReadyHeaderItem onDomReadyHeaderItem() {
        try {
            String js = String.format(
                    "window.NAVJS.React.render(window.NAVJS.React.createElement(window.NAVJS.Components.%s, %s), document.getElementById('%s'));",
                    componentName,
                    objectMapper.writeValueAsString(props),
                    reactContainer.getMarkupId());

            return OnDomReadyHeaderItem.forScript(js);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Feil ved serialisering av props: " + props, e);
        }
    }
}

