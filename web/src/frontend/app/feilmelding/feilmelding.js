import React, { PropTypes as PT } from 'react';
import { Rammepanel } from 'nav-react-design/dist/panel';

function Feilmelding({ tittel, children }) {
    return (
        <Rammepanel className="text-center">
            <h1 className="hode hode-innholdstittel hode-dekorert hode-feil">
                {tittel}
            </h1>
            {children}
        </Rammepanel>
    );
}

Feilmelding.propTypes = {
    tittel: PT.oneOfType([PT.string, PT.node]).isRequired,
    children: PT.node
};

export default Feilmelding;
