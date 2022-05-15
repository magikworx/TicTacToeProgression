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
### udp2
- packet loss tolerant with retry
- corruption tolerant with TCP checksums
- expanded fault injection capability
### udp3
- next up
  - message ids / origin info for keeping for rejecting retries
  - potentially out of order message detection
### util
utilities and base structures for packing and transporting data
