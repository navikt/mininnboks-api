import React, { PropTypes as pt } from 'react';

const getCMSKey = (erKvittering, key) => {
    if (!erKvittering) {
        return `traadvisning.send-svar.bekreftelse.${key}`;
    }
    return `send-sporsmal.bekreftelse.${key}`;
};

class Epost extends React.Component {

    render() {
        const { resources, erKvittering, linkClass, className } = this.props;
        const TPSFeil = !resources.hasKey('bruker.epost');
        const epost = resources.get('bruker.epost');

        if (TPSFeil) {
            return <p>{resources.get('send-sporsmal.bekreftelse.kunne-ikke-hente-epost')}</p>;
        }

        const infoTekst = epost ?
            resources.get(getCMSKey(erKvittering, 'du-mottar-epost')) :
            resources.get(getCMSKey(erKvittering, 'du-kan-motta-epost'));

        const epostTekst = epost ?
            <span>{epost}{' '}
                <a href={resources.get('brukerprofil.link')} className={`${linkClass} lopendetekst`}>
                    {resources.get(getCMSKey(erKvittering, 'endre-epostadresse'))}
                </a>
            </span> :
            <a href={resources.get('brukerprofil.link')} className={`${linkClass} lopendetekst`}>
                {resources.get(getCMSKey(erKvittering, 'registrer-epostadresse'))}
            </a>;

        return (
            <p className={className}>{epostTekst} {infoTekst}</p>
        );
    }
}

Epost.defaultProps = {
    erKvittering: false,
    className: '',
    linkClass: ''
};

Epost.propTypes = {
    erKvittering: pt.bool,
    className: pt.string,
    linkClass: pt.string,
    resources: pt.object
};

export default Epost;
