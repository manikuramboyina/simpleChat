(function(){
"use strict";

/** Controllers */
angular.module('sseChat.controllers', ['sseChat.services','sseChat.directives']).
    controller('ChatCtrl', function ($scope, $http, chatModel,$filter) {

        $scope.msgs = [];
        $scope.inputText = "";
        //Let the server take the authenticated user upon a chat request
        $scope.user = [];
        $scope.currentChatFriend = [];
        $scope.chooseUsers = chatModel.getFriends();
        $scope.chooseFriends = $scope.chooseUsers;

        /** change current friend, restart EventSource connection */
        $scope.setCurrentChatFriend = function (friend) {
            console.log(friend.name);
            console.log(friend.value);
            $scope.currentChatFriend = friend;
            //$scope.chatFeed.close();
            $scope.msgs = [];
            $scope.listen();
        };

        $scope.setCurrentUser = function (user) {
            console.log(user.name);
            console.log(user.value);
            $scope.user = user;
            //$scope.chatFeed.close();
            $scope.msgs = [];
            if(!$scope.currentChatFriend)
            $scope.listen();
        };


        /** posting chat text to server */
        $scope.submitMsg = function () {
            if(!$scope.currentChatFriend || !$scope.user)
            $http.post("/chat", { text: $scope.inputText, chatUser: $scope.user,
                time: (new Date()).toUTCString(), friend: $scope.currentChatFriend.value });
            else
            $scope.msgs = [{text: 'You got disconnected or friend is offline', chatUser: {name:'Admin',value:'Admin'},
                time: (new Date()).toUTCString(), friend: 'offline'}];
            $scope.inputText = [];
        };

        /** handle incoming messages: add to messages array */
        $scope.addMsg = function (msg) {
            $scope.$apply(function () { $scope.msgs.push(JSON.parse(msg.data)); });
        };

        /** start listening on messages from selected room */
        $scope.listen = function () {
            $scope.chatFeed = new EventSource("/chatFeed/" + $scope.user.value);
            $scope.chatFeed.addEventListener("message", $scope.addMsg, false);
        };

        //$scope.listen();

        /**on text area submit */
        $scope.checkForSubmit = function($event) {
            if($event.keyCode == 13 && !$event.shiftKey) {
                this.submitMsg();
            }

        }
    });
})();



