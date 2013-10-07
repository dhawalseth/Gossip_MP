Distributed Group Membership:

The design implemented for this machine problem is as follows: 
Each node maintains a local membership list, which is implemented using Concurrent Hash Maps to ensure synchronization. Two threads are spawned at each node- one (as ClientHeartBeatSender) for sending the local list to other nodes and the second (as ServerHeartBeatListener) for receiving lists from other nodes. The ClientHeartBeatSender thread checks for failures, updates the local list and increments its own heartbeat count just before marshaling into an object and sending over the network using UDP protocol to a few randomly selected members from the group. The timeliness requirement of 5s is met by gossiping every 1ms to randomly half of the group members. At each node, on receiving a list, the ServerHeartBeatListener thread merges the local list with the received list by looking for new nodes and updating new heart beat counts of old nodes. 
We have also created Unit Tests: testIncreaseOwnHeartBeat, testServerMergeAdd, testServerMerge, testClientUpdateNoFailures, testClientUpdateWithFailures. These help in verifying the basic functionality of merging and updating list.


---------------INSTRUCTIONS BELOW--------------------------------------------
Prepare:
1)Select a contact node and find its ip address (ifconfig)
2)make a IpAddressList.txt file in /tmp/ on each computer and have the contact node ip address on the first line
--------
Start:
1)On the contact node run "java -jar gossip.java <session name>", where session name will be part of the key in the log files located in /tmp/machine.<ip address>.log.

2)On the contact node type "contact true" to set the computer as the contact node, it will be able to join using its old list.

3)For each of the computers repeat step 1!
-----------
Voluntary Leave/Join:
Each computer has a connected/disconnected state. It initially starts with connected
To disconnect: Type "leave"
To Connect to contact node: Type "join"
-----------
To Simulate dropped packets:
Type "drop true <int>" where int is the percentage you want dropped.
Type "drop false" to stop dropping packets
----------
To set session name:
Type "session <session name" where session name will help you search the keys in the logs located in /tmp/machine.<ip address>.log

 
