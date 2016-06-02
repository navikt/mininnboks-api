import React, { PropTypes as pt } from 'react';
import { injectIntl, intlShape } from 'rect-intl';

const getCMSKey = (erKvittering, key) => {
    if (!erKvittering) {
        return `traadvisning.send-svar.bekreftelse.${key}`;
    }
    return `send-sporsmal.bekreftelse.${key}`;
};

const finnesEnonicNokkel = (enonicKey, formatMessage) => {
    const FINNES_IKKE = 'finnesikke';
    return formatMessage({ id: enonicKey, defaultMessage: FINNES_IKKE }) !== FINNES_IKKE;
};

class Epost extends React.Component {

    render() {
        const { erKvittering, linkClass, className, intl: { formatMessage } } = this.props;
        const TPSFeil = !finnesEnonicNokkel('bruker.epost', formatMessage); 
        const epost = formatMessage({ id: 'bruker.epost' });

        if (TPSFeil) {
            return <p>{formatMessage({ id: 'send-sporsmal.bekreftelse.kunne-ikke-hente-epost' })}</p>;
        }

        const infoTekst = epost ?
            formatMessage({ id: getCMSKey(erKvittering, 'du-mottar-epost') }) :
            formatMessage({ id: getCMSKey(erKvittering, 'du-kan-motta-epost') });

        const epostTekst = epost ?
            <span>{epost}{' '}
                <a href={formatMessage({ id: 'brukerprofil.link' })} className={`${linkClass} lopendetekst`}>
                    {formatMessage({ id: getCMSKey(erKvittering, 'endre-epostadresse') })}
                </a>
            </span> :
            <a href={formatMessage({ id: 'brukerprofil.link' })} className={`${linkClass} lopendetekst`}>
                {formatMessage({ id: getCMSKey(erKvittering, 'registrer-epostadresse') })}
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
    intl: intlShape.isRequired
};

export default injectIntl(Epost);
