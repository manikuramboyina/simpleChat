(function(){
"use strict";

/** Controllers */
angular.module('sseChat.controllers', ['sseChat.services','sseChat.directives']).
    controller('ChatCtrl', function ($scope, $http, chatModel,$filter) {

        $scope.msgs = [];
        $scope.inputText = "";
        //Let the server take the authenticated user upon a chat request
        $scope.chooseUsers = chatModel.getFriends('user');
        $scope.chooseFriends = $scope.chooseUsers;
        //var myInterval = setInterval(alertFunc,3000);

        $scope.updateFriendList = function (onlineUsers) {
            $scope.$apply(function () {
                $scope.chooseUsers = JSON.parse(onlineUsers.data);
            });
        };

        //function alertFunc(){
        //    if(!confirm("Your session seems to be inactive Ok to logout/cancel to stay in"))
        //    {clearInterval(myInterval);
        //        $scope.chatFeed.close();
        //    }
        //}

        /** change current friend, restart EventSource connection */
        $scope.setCurrentChatFriend = function (friend) {
            console.log(friend.name);
            console.log(friend.value);
            $scope.currentChatFriend = friend;
            //$scope.chatFeed.close();
            $scope.msgs = [];
        };

        $scope.setCurrentUser = function (user) {
            console.log(user.name);
            console.log(user.value);
            $scope.user = user;
            //$scope.chatFeed.close();
            $scope.msgs = [];
            $scope.listen();
        };


        $scope.$on('$destroy', function () {
            alert("close");
            console.log("left");
        });


        /** posting chat text to server */
        $scope.submitMsg = function () {
            if (!$scope.currentChatFriend || !$scope.user) {
                $scope.msgs.push({
                    text: 'You got disconnected or you have not selected a friend',
                    fromUser: {name: 'Admin', value: 'Admin'},
                    time: (new Date()).toUTCString(),
                    toFriend: {name: 'offline', value: 'offline'}
                });
            } else {
                $http.post("/chat", {
                    text: $scope.inputText, fromUser: $scope.user,
                    time: (new Date()).toUTCString(), toFriend: $scope.currentChatFriend
                });
            }
            $scope.inputText = [];

        };

        /** handle incoming messages: add to messages array */
        $scope.addMsg = function (msg) {
            $scope.$apply(function () {
                $scope.msgs.push(msg);
            });
        };


        $scope.triggerChange = function (data) {
            var eventData = JSON.parse(data.data);
            if (eventData.eventName === "chatMessage")
                $scope.addMsg(eventData.data);
            else if (eventData.eventName === "chatList")
                $scope.updateFriendList(eventData.data);
        }

        /** start listening on messages from selected room */
        $scope.listen = function () {
            $scope.chatFeed = new EventSource("/chatFeed/" + $scope.user.value);
            $scope.chatFeed.addEventListener("message", $scope.triggerChange, false);
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



