Distributed Group Membership:

The design implemented for this machine problem is as follows: 
Each node maintains a local membership list, which is implemented using Concurrent Hash Maps to ensure synchronization. Two threads are spawned at each node- one (as ClientHeartBeatSender) for sending the local list to other nodes and the second (as ServerHeartBeatListener) for receiving lists from other nodes. The ClientHeartBeatSender thread checks for failures, updates the local list and increments its own heartbeat count just before marshaling into an object and sending over the network using UDP protocol to a few randomly selected members from the group. The timeliness requirement of 5s is met by gossiping every 1ms to randomly half of the group members. At each node, on receiving a list, the ServerHeartBeatListener thread merges the local list with the received list by looking for new nodes and updating new heart beat counts of old nodes. 
We have also created Unit Tests: testIncreaseOwnHeartBeat, testServerMergeAdd, testServerMerge, testClientUpdateNoFailures, testClientUpdateWithFailures. These help in verifying the basic functionality of merging and updating list.

