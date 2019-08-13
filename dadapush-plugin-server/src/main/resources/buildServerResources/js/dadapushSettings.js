
Dadapush = {};

Dadapush.SettingsForm = OO.extend(BS.AbstractPasswordForm, {
    setupEventHandlers: function() {
        var that = this;
        $('testConnection').on('click', this.testConnection.bindAsEventListener(this));

        this.setUpdateStateHandlers({
            updateState: function() {
                that.storeInSession();
            },
            saveState: function() {
                that.submitSettings();
            }
        });
    },

    /** This method required for teamcity javascript events support (data changed and etc) */
    storeInSession: function() {
        $("submitSettings").value = 'storeInSession';
        BS.PasswordFormSaver.save(this, this.formElement().action, BS.StoreInSessionListener);
    },

    submitSettings: function() {
        $("submitSettings").value = 'store';
        this.removeUpdateStateHandlers();
        BS.PasswordFormSaver.save(this, this.formElement().action,
            OO.extend(BS.ErrorsAwareListener, this.createErrorListener()));
        return false;
    },

    createErrorListener: function() {
        var that = this;
        return {
            onEmptyChannelTokenError: function(elem) {
                $("errorChannelToken").innerHTML = elem.firstChild.nodeValue;
                that.highlightErrorField($("channelToken"));
            },
            onEmptyBasePathError: function(elem) {
                $("errorBasePath").innerHTML = elem.firstChild.nodeValue;
                that.highlightErrorField($("basePath"));
            },
            onCompleteSave: function(form, responseXML, err) {
                BS.ErrorsAwareListener.onCompleteSave(form, responseXML, err);
                if (!err) {
                    BS.XMLResponse.processRedirect(responseXML);
                } else {
                    that.setupEventHandlers();
                }
            }
        }
    }
});
