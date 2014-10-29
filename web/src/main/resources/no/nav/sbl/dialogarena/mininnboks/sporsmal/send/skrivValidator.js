(function () {
    "use strict";

    var SkrivValidator = function (config) {
        this.config = config;
        this.$form = $(config.form);
        this.$form.on('blur', 'input,textarea', this.validate.bind(this));
        this.$form.on('change', 'input', this.validate.bind(this));
        this.$form.on('keyup', 'textarea', this.validate.bind(this));
    };

    $.extend(SkrivValidator.prototype, {
        validateAll: function(){
            this.$form.find('input,textarea').each(function(index, element){
                this.validate({currentTarget: element});
            }.bind(this));
        },
        validate: function (event) {
            var $el = $(event.currentTarget);
            if (event.type === 'keyup' && (event.keyCode === 9 || event.keyCode == 32)) {
                return;
            }
            if ($el.is('input[type=checkbox]')) {
                this.validateCheckbox($el);
            } else if ($el.is('textarea')) {
                this.validateTextArea($el);
            }
        },
        validateCheckbox: function ($el) {
            if (!$el.prop('checked')) {
                $el.next('label').addClass('validation-error');
                this.showErrorMessage(this.config.checkboxErrorMessage);
            } else {
                $el.next('label').removeClass('validation-error');
                this.hideErrorMessage(this.config.checkboxErrorMessage);
            }
        },
        validateTextArea: function ($el) {
            if ($el.val().length === 0 || $el.val() === this.config.textareaPlaceholder) {
                $el.addClass('validation-error')
                    .nextAll('label').addClass('validation-error');
                this.showErrorMessage(this.config.textareaErrorMessage);
            } else {
                $el.removeClass('validation-error')
                    .nextAll('label').removeClass('validation-error');
                this.hideErrorMessage(this.config.textareaErrorMessage);
            }
        },
        showErrorMessage: function(message) {

        },
        hideErrorMessage: function(message){

        }
    });

    window.SkrivFormValidator = SkrivValidator;
})();