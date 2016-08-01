import React, { PropTypes as PT } from 'react';

function Feilmelding({ tittel, children }) {
    return (
        <div className="panel panel-ramme">
            <h1 className="hode hode sidetittel hode-dekorert hode-feil">
                {tittel}
            </h1>
            {children}
        </div>
    );
}

Feilmelding.propTypes = {
    tittel: PT.oneOfType([PT.string, PT.node]).isRequired,
    children: PT.node
};

export default Feilmelding;
