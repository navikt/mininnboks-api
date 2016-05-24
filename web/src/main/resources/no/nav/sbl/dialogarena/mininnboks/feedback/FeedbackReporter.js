import React from 'react/addons';

function FeedbackReporter(update) {
    this.update = update;
    this.errors = {};
}
FeedbackReporter.prototype.error = function (ref, errorMessages) {
    if (typeof errorMessages === 'string' || !errorMessages.hasOwnProperty("length")) {
        errorMessages = [errorMessages];
    }
    this.errors[ref] = errorMessages;
    this.update($.extend({}, this.errors));
};
FeedbackReporter.prototype.ok = function (ref) {
    this.errors[ref] = [];
    this.update($.extend({}, this.errors));
};

FeedbackReporter.prototype.numberOfErrors = function () {
    return getAllErrorMessages(this.errors).length;
};

FeedbackReporter.prototype.get = function (ref) {
    return this.errors[ref] || [];
};

FeedbackReporter.prototype.getErrorElementsForComponent = function (ref, elementType, idSuffix) {
    var messages = {};
    messages[ref] = this.get(ref);
    return toErrorElements(elementType, messages, idSuffix);
};

FeedbackReporter.prototype.getErrorMessageIdForComponent = function (ref, idSuffix) {
    idSuffix = idSuffix || '';
    if (this.get(ref).length !== 0) {
        return 'error-' + ref + idSuffix;
    }
    return '';
};

FeedbackReporter.prototype.getAllErrorElements = function (elementType) {
    return toErrorElements(elementType, this.errors);
};

function toErrorElements(elementType, errors, idSuffix) {
    idSuffix = idSuffix || '';
    var elementTypeComponents = (elementType || 'span.validation-message').split(".");
    var tagType = elementTypeComponents[0];
    var tagClass = '';

    if (elementTypeComponents.length > 1) {
        tagClass = elementTypeComponents[1];
    }
    var elements = [];
    for (var key in errors) {
        if (errors.hasOwnProperty(key)) {
            var errorElements = errors[key].map(function (errorMessage) {
                return React.createElement(tagType, {
                    "className": tagClass,
                    "id": 'error-' + key + idSuffix,
                    "role": 'alert',
                    "aria-live": 'assertive',
                    "aria-atomic": true

                }, errorMessage);
            });
            elements = elements.concat(errorElements);
        }
    }
    return React.addons.createFragment({elements: elements});
}

function getAllErrorMessages(errors) {
    var msg = [];
    for (var key in errors) {
        if (errors.hasOwnProperty(key)) {
            msg = msg.concat(errors[key]);
        }
    }
    return msg;
}

module.exports = FeedbackReporter;