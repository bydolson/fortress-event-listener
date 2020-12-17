pragma solidity ^0.4.24;

contract EventRegistry {

    struct Event {
        string eventName;
        string eventType;
    }

    Event[] events;

    function addEvent(string eventName, string eventType) external {
        names.push(Event(eventName, eventType));

        emit EventAdded(names.length - 1, eventName, eventType);
    }

    event EventAdded(uint256 id, string eventName, string eventType);
}
