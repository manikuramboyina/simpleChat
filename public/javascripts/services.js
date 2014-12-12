(function(){"use strict";

/** chatModel service, provides chat rooms (could as well be loaded from server) */
angular.module('sseChat.services', ['sseChat.controllers']).service('chatModel', function () {
    var getFriends = function (user) {
        return [ {name: 'saurav', value: 'saurav'}, {name: 'siva', value: 'siva'},
            {name: 'sakthi', value: 'sakthi'}, {name: 'milan', value: 'milan'},
            {name: 'luke', value: 'luke'} ];
    };
    return { getFriends: getFriends };
});
})();