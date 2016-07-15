import React, { PropTypes as pt } from 'react'
import { Dokumenter, Hurtignavigering } from 'react-dokumentvisning';

const offset = [-300, -80];

class Dokumentvinsing extends React.Component {
    componentDidMount() {
        setTimeout(() => document.querySelector('.kulemeny li input') && document.querySelector('.kulemeny li input').classList.add('active'), 0);
    }

    render() {
        const { dokumentmetadata, journalpostmetadata } = this.props;

        return (
            <section className="dokumenter">
                <Hurtignavigering dokumentmetadata={dokumentmetadata} navigeringsknappOffset={offset} />
                <Dokumenter
                  journalpostId={journalpostmetadata.journalpostId}
                  dokumentmetadata={dokumentmetadata}
                />
            </section>
        );
    }
}

Dokumentvinsing.propTypes = {
    dokumentmetadata: pt.array.isRequired,
    journalpostmetadata: pt.object.isRequired
};

export default Dokumentvinsing;
