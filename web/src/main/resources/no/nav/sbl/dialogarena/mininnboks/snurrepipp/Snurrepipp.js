var React = require('react');
var format = require('string-format');

module.exports = React.createClass({
    getDefaultProps: function () {
        return {
            storrelse: 128,
            farge: 'graa'
        };
    },
    render: function () {
        var src = format('/mininnboks/build/img/ajaxloader/{}/loader_{}_{}.gif', this.props.farge, this.props.farge, this.props.storrelse);
        return (
            <div className="innboks-container snurrepipp">
                <img src={src} />
            </div>
        );
    }
});