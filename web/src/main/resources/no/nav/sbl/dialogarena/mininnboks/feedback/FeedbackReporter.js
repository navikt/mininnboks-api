function FeedbackReporter(update) {
    this.update = update;
    this.errors = {};
}
FeedbackReporter.prototype.error = function (ref, errorMessages) {
    if (typeof errorMessages === 'string' || !errorMessages.hasOwnProperty("length")) {
        errorMessages = [errorMessages];
    }
    this.errors[ref] = errorMessages;
    this.update(getAllErrorMessages(this.errors));
};
FeedbackReporter.prototype.ok = function (ref) {
    this.errors[ref] = [];
    this.update(getAllErrorMessages(this.errors));

};

FeedbackReporter.prototype.numberOfErrors = function() {
    return getAllErrorMessages(this.errors).length;
};

FeedbackReporter.prototype.get = function(ref) {
    return this.errors[ref] || [];
};

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