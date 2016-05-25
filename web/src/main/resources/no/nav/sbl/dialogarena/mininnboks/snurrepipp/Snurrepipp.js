import React from 'react/addons';
import format from 'string-format';

class Snurrepipp extends React.Component {

    render () {
        var src = format('/mininnboks/build/img/ajaxloader/{}/loader_{}_{}.gif', this.props.farge, this.props.farge, this.props.storrelse);
        return (
            <div className="snurrepipp">
                <img src={src} />
            </div>
        );
    }
};

Snurrepipp.defaultProps = {
    storrelse: 128,
    farge: 'graa'
};

export default Snurrepipp;