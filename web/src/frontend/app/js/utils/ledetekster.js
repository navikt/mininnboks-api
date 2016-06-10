function replaceToken(tekst, token, arg) {
    const re = new RegExp(`\\{${token}\\}`, 'g');
    return tekst.replace(re, arg);
}

const hentTekst = (tekster) => (key, args) => {
    let tekst = tekster[key];
    if (tekst) {
        if (args) {
            Object.keys(args).forEach(argKey => {
                tekst = replaceToken(tekst, argKey, args[argKey]);
            });
        }
        return tekst;
    }
    return key;
};

export default hentTekst;
