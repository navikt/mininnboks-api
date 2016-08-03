import { PropTypes as PT } from 'react';
export const emptyShape = PT.shape({});

export const storeShape = (dataShape) => PT.shape({
    status: PT.string,
    data: PT.oneOfType([PT.arrayOf(dataShape), dataShape, emptyShape])
});

export const meldingShape = PT.shape({
    id: PT.string.isRequired,
    traadId: PT.string.isRequired,
    fritekst: PT.string.isRequired
});

export const traadShape = PT.shape({
    traadId: PT.string.isRequired,
    meldinger: PT.arrayOf(meldingShape).isRequired,
    nyeste: meldingShape.isRequired,
    eldste: meldingShape.isRequired,
    kanBesvares: PT.bool.isRequired,
    avsluttet: PT.bool.isRequired
});
