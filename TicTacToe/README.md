# Tic Tac Toe Progression
## folders
### base
base game implementation
### starter
base local players and AIs
### udp0
simple udp implementation
### udp1
- packet loss tolerant udp connection with retry
- introduced a fault injection class
  - The fault injector is a server/client that passes messages back and forth between the existing client and gui, a man-in-the-middle.
  - The fault injector for the next stage introduces the fault that makes the previous stage unsuitable.
  - For instance, in packet loss we randomly don't forward packets.
### udp2
- packet loss tolerant with retry
- corruption tolerant with TCP checksums
- expanded fault injection capability
  - we randomly scramble the bytes
### udp3
- message ids for matching retries
- message id history for rejecting retries
- expanded fault injection capability
  - we late send packets by swapping a packet with its retry
### util
utilities and base structures for packing and transporting data
